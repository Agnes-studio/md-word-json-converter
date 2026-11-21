# MD ⇄ Word ⇄ JSON 离线互转工具

Markdown、Word (.docx)、JSON 三种格式互相转换，**完全离线**，文件不离开你的设备。
A fully offline converter between **Markdown ⇄ Word (.docx) ⇄ JSON**. Your files never leave your device.

---

## 🌐 网页版（电脑 / 手机浏览器通用）
## 🌐 Web Version (works in any desktop or mobile browser)

单文件、纯离线：整个工具就是一个 `index.html`，双击就能用，不联网、不上传、不装任何东西。
A single-file, fully offline tool: everything lives in one `index.html` — double-click to use, no network, no uploads, no installs.

**支持无限批量转换**：多选 / 拖入多个文件一次性转换，多个结果自动打包成 ZIP。
**Unlimited batch conversion**: select or drop multiple files at once; results are automatically packed into a ZIP.

### 📥 下载 / Download

**👉 [点我直接下载（ZIP，解压后双击 index.html 即可使用）](https://github.com/Agnes-studio/md-word-json-converter/archive/refs/heads/main.zip)**
**👉 [Click to download the ZIP (unzip and double-click index.html to use)](https://github.com/Agnes-studio/md-word-json-converter/archive/refs/heads/main.zip)**

只要单个 HTML 文件？打开 [index.html](https://github.com/Agnes-studio/md-word-json-converter/blob/main/index.html)，点页面**右上角的下载按钮**（⬇ Download raw file）即可。
Just want the single HTML file? Open [index.html](https://github.com/Agnes-studio/md-word-json-converter/blob/main/index.html) and click the **download button** (⬇ Download raw file) at the top right.

### 🚀 使用方法 / Usage

1. 用上面「📥 下载」里的链接拿到 `index.html`
   Get `index.html` via the download links above
2. 双击用浏览器打开（推荐新版 Edge / Chrome）
   Double-click to open it in a browser (recent Edge / Chrome recommended)
3. 左边选原始格式，右边选目标格式，把文件拖进来，点「开始转换」
   Pick the source format on the left, the target format on the right, drop your files in, and hit convert

---

## 🤖 安卓 App
## 🤖 Android App

网页版的原生安卓移植：同样的转换内核，完全离线，零广告零统计。
A native Android port of the web tool: same conversion engine, fully offline, no ads, no tracking.

### 📥 下载 / Download

**👉 [下载 APK（v1.0.0）](https://github.com/Agnes-studio/md-word-json-converter/raw/main/downloads/MD-Word-JSON-v1.0.0.apk)**

传到手机上点开即可安装（需允许「安装未知来源应用」）。
Transfer the APK to your phone and tap to install (you'll need to allow "install from unknown sources").

### ✨ 特点 / Features

- **完全离线**：所有转换都在本机完成，不需要联网权限
  **Fully offline**: all conversion happens on-device; no network permission needed
- **单文件转换**：一次转换一个文件，结果直接保存到系统「下载」文件夹
  **Single-file conversion**: convert one file at a time; results are saved straight to your Downloads folder
- **系统文件选择器**：支持 .md / .docx / .json，自动识别格式
  **System file picker**: supports .md / .docx / .json with automatic format detection
- **免费无限制**：赞助提醒每打开 10 次出现一次，可随时点「不再提醒」
  **Free with no limits**: a donation reminder appears every 10 launches and can be dismissed forever

### 🔧 源码 / Source Code

App 源码在 [`android-app/`](android-app/) 目录（Kotlin + WebView 壳，转换逻辑与网页版同一份 HTML）。
The app source lives in [`android-app/`](android-app/) (Kotlin + WebView shell around the same HTML engine as the web version).

用 Android Studio 打开 `android-app/` 即可构建。
Open `android-app/` in Android Studio to build.

---

## 🔀 支持的转换方向
## 🔀 Supported Conversions

| 原始 \ 目标 / From \ To | Markdown | Word (.docx) | JSON |
|---|---|---|---|
| **Markdown** | — | ✅ 排版保留 / Formatting kept | ✅ 结构化数据 / Structured data |
| **Word (.docx)** | ✅ | — | ✅ 结构化数据 / Structured data |
| **JSON** | ✅ 结构化 JSON 无损还原；Claude 对话 / 普通 JSON 智能排版 / Lossless restore; smart layout for Claude exports & plain JSON | ✅ 同上 / Same | — |

## ✨ 更多特点
## ✨ More Highlights

- **JSON 可无损还原**：MD / Word 转出的 JSON 是结构化文档数据（标题/列表/表格都是字段），再转回时原样还原，不会退化成纯文本
  **Lossless JSON round-trip**: JSON exported from MD / Word is structured document data (headings, lists, tables as fields), and converts back exactly — never degrades to plain text
- **Claude 对话导出优化**：JSON 转换对 Claude 导出的对话文件（`conversations.json`）做了专门处理——自动理清分支对话主线、保留思考过程和附件内容、生成带 frontmatter 的 Obsidian 笔记（多条对话还附 Dataview 索引页）
  **Optimized for Claude exports**: special handling for Claude's `conversations.json` — untangles branch threads, keeps thinking and attachments, and generates Obsidian notes with frontmatter (plus a Dataview index for multiple conversations)

## ⚠️ 注意事项
## ⚠️ Notes

- Word → MD 只支持 `.docx`；老 `.doc` 请先用 Word 另存为 `.docx`
  Word → MD only supports `.docx`; for legacy `.doc`, save as `.docx` in Word first
- Word 文档里的图片不会带入 MD（会标记为〔图片〕）
  Images in Word documents are not carried into MD (marked as 〔图片〕)
- 读取 `.docx` 需要较新的浏览器（用到 `DecompressionStream` API）
  Reading `.docx` requires a recent browser (uses the `DecompressionStream` API)

## 🛠 技术说明
## 🛠 Tech Notes

纯原生 HTML / CSS / JavaScript 实现，零依赖：
Pure HTML / CSS / JavaScript, zero dependencies:

- 内置 Markdown 解析器（标题、列表、表格、代码块、行内样式、YAML frontmatter 等）
  Built-in Markdown parser (headings, lists, tables, code blocks, inline styles, YAML frontmatter, etc.)
- `.docx` 读写：手动构造 / 解析 OOXML + ZIP 结构，解压使用浏览器原生 `DecompressionStream`
  `.docx` read/write: hand-built OOXML + ZIP handling, decompression via the native `DecompressionStream`
- 转出的 `.docx` 可直接用 Word / WPS 打开
  Generated `.docx` opens directly in Word / WPS
- MD / Word → JSON 使用统一的 `mdwx-document` 结构（`{format, version, source, blocks}`），转回时按结构渲染，保证往返一致
  MD / Word → JSON uses a unified `mdwx-document` structure (`{format, version, source, blocks}`) for lossless round-trips

## 🧪 测试
## 🧪 Tests

仓库附带 Node 测试脚本（仅开发用，工具本身零依赖）：
The repo ships a Node test script (development only — the tool itself has zero dependencies):

```bash
node test_roundtrip.js
```

覆盖：MD ⇄ JSON 往返一致性、结构化 JSON 识别、docx 生成、Claude 对话导出兼容等 9 项。
Covers: MD ⇄ JSON round-trip consistency, structured JSON detection, docx generation, Claude export compatibility — 9 tests in total.

## ☕ 支持我
## ☕ Support Me

如果这个工具帮到了你，可以请我喝杯咖啡（金额不限），谢谢！
If this tool helps you, consider buying me a coffee (any amount). Thank you!

- ⚡ [爱发电 Afdian](https://afdian.com/a/Agnesxie)（微信 / 支付宝 / WeChat / Alipay）
- ☕ [Ko-fi](https://ko-fi.com/agnesxie)（Credit card / PayPal）

## 📄 License

[MIT](LICENSE)
