package com.agnesstudio.mdwordjson

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    companion object {
        private const val REQ_FILE = 1001
        private const val PREFS = "mdwx_prefs"
        private const val KEY_LAUNCH_COUNT = "launch_count"
        private const val KEY_NEVER_REMIND = "never_remind"
        private const val REMIND_EVERY = 10
        private const val URL_AFDIAN = "https://afdian.com/a/Agnesxie"
        private const val URL_KOFI = "https://ko-fi.com/agnesxie"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
        }
        webView.addJavascriptInterface(AndroidBridge(this), "AndroidBridge")
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // 赞助等外部链接交给浏览器打开，本地 file:// 资源正常加载
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    openExternal(url)
                    return true
                }
                return false
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView,
                callback: ValueCallback<Array<Uri>>,
                params: FileChooserParams
            ): Boolean {
                filePathCallback?.onReceiveValue(null)
                filePathCallback = callback
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                    // 免费版：一次只转一个文件，不开启多选
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                    putExtra(
                        Intent.EXTRA_MIME_TYPES,
                        arrayOf(
                            "text/markdown", "text/plain",
                            "application/json", "text/json",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            "application/octet-stream"
                        )
                    )
                }
                return try {
                    startActivityForResult(intent, REQ_FILE)
                    true
                } catch (e: ActivityNotFoundException) {
                    filePathCallback = null
                    false
                }
            }
        }

        webView.loadUrl("file:///android_asset/index.html")
        maybeShowSponsorDialog()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_FILE) {
            val callback = filePathCallback ?: return
            filePathCallback = null
            if (resultCode != RESULT_OK || data == null) {
                callback.onReceiveValue(null)
                return
            }
            val uris = mutableListOf<Uri>()
            data.clipData?.let { clip ->
                for (i in 0 until clip.itemCount) uris.add(clip.getItemAt(i).uri)
            } ?: data.data?.let { uris.add(it) }
            callback.onReceiveValue(if (uris.isEmpty()) null else uris.toTypedArray())
        }
    }

    override fun onBackPressed() {
        if (this::webView.isInitialized && webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }

    private fun openExternal(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "没有可打开链接的应用", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------- 赞助提醒：每打开 REMIND_EVERY 次弹一次 ----------

    private fun maybeShowSponsorDialog() {
        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_NEVER_REMIND, false)) return
        val count = prefs.getInt(KEY_LAUNCH_COUNT, 0) + 1
        prefs.edit().putInt(KEY_LAUNCH_COUNT, count).apply()
        if (count % REMIND_EVERY != 0) return

        val options = arrayOf("⚡ 爱发电（微信 / 支付宝）", "☕ Ko-fi（PayPal / 信用卡）", "下次再说", "不再提醒")
        AlertDialog.Builder(this)
            .setTitle("支持作者 ☕")
            .setMessage("MD⇄Word⇄JSON 是完全免费的离线工具，所有转换都在本机完成。\n\n如果它帮到了你，欢迎请作者喝杯咖啡～")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> openExternal(URL_AFDIAN)
                    1 -> openExternal(URL_KOFI)
                    3 -> prefs.edit().putBoolean(KEY_NEVER_REMIND, true).apply()
                }
                dialog.dismiss()
            }
            .setNegativeButton("关闭", null)
            .show()
    }

    // ---------- JS 桥：把网页的下载转成系统「下载」目录里的真实文件 ----------

    class AndroidBridge(private val activity: MainActivity) {

        /** 网页端调用：AndroidBridge.saveFile(base64, fileName, mimeType)，返回是否成功 */
        @JavascriptInterface
        fun saveFile(base64Data: String, fileName: String, mimeType: String): Boolean {
            return try {
                val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                val saved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveViaMediaStore(bytes, fileName, mimeType)
                } else {
                    saveLegacy(bytes, fileName)
                }
                activity.runOnUiThread {
                    Toast.makeText(
                        activity,
                        if (saved) "已保存到「下载」：$fileName" else "保存失败：$fileName",
                        Toast.LENGTH_LONG
                    ).show()
                }
                saved
            } catch (e: Exception) {
                activity.runOnUiThread {
                    Toast.makeText(activity, "保存失败：${e.message}", Toast.LENGTH_LONG).show()
                }
                false
            }
        }

        private fun saveViaMediaStore(bytes: ByteArray, fileName: String, mimeType: String): Boolean {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, mimeType.ifBlank { "application/octet-stream" })
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = activity.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return false
            resolver.openOutputStream(uri)?.use { it.write(bytes) } ?: return false
            return true
        }

        @Suppress("DEPRECATION")
        private fun saveLegacy(bytes: ByteArray, fileName: String): Boolean {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                ?: return false
            dir.mkdirs()
            FileOutputStream(File(dir, fileName)).use { it.write(bytes) }
            return true
        }
    }
}
