# MD⇄Word⇄JSON 安卓 App 产品决策记录

记录时间：2026-11-17（与初代 App 开发同一 session）

## 已定决策（不要再重做）

1. **网页版不动**：GitHub 仓库 `Agnes-studio/md-word-json-converter` 的 `index.html` 保持原样，网页版**永远保留无限批量转换**。App 只是它的一个移植壳，网页端代码不做任何阉割。

2. **App 初代（v1.0.0）= 单文件转换**：一次只能转一个文件（文件选择框已去掉 multiple，系统选择器已关闭多选，页面文案已改为「一次一个」）。这是有意为之的产品限制，为后期收费留空间，**不是技术限制**。

3. **后期更新路线**：批量转换作为 App 的付费功能放出，采用**激活码解锁**模式（不走 Google Play 内购，不走微信/支付宝商户接入）。

4. **激活码的双重作用**：输入有效激活码后
   - 解锁无限批量转换（与网页版一致）
   - **同时永久关闭赞助提醒弹窗**（实现方式：校验通过后把 `never_remind=true` 和 `activated=true` 一起写入 SharedPreferences；赞助弹窗逻辑里已有 `KEY_NEVER_REMIND` 检查，激活时复用即可）

5. **赞助提醒现状**：每打开 10 次弹一次（`MainActivity.kt` 里 `REMIND_EVERY = 10`），带「爱发电 / Ko-fi / 下次再说 / 不再提醒」四个选项。

## 实现激活码时的技术要点（到时候直接用）

- 码的格式建议：开发者用私钥（如 Ed25519）对「用户标识 + 有效期」签名生成码，App 内置公钥离线验签，**无需服务器**。
- 解锁时改动点（都在 `MainActivity.kt`）：
  - `onShowFileChooser` 里 `EXTRA_ALLOW_MULTIPLE` 改为读 `activated` 状态
  - assets 里的 `index.html`：`input` 标签恢复 `multiple`，`addFiles` 的 `.slice(0,1)` 去掉（注意 App 用的是 assets 副本，网页原版不受此约束，到时需按当时网页版重新同步再改）
  - `maybeShowSponsorDialog` 开头加 `if (prefs.getBoolean("activated", false)) return`
- 卖码渠道候选：爱发电自动发货 / 面包多 / Gumroad（参考本 session 讨论）。
- 注意：assets/index.html 是网页版的拷贝 + 桥接补丁（`trigger()` 走 AndroidBridge 保存 + 单文件限制 + 文案调整）。网页版以后更新时，App 需要重新同步再打这两个补丁。
