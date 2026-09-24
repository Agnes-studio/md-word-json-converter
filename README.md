# MD ⇄ Word ⇄ JSON 离线互转工具

一个**单文件、纯离线**的格式互转小工具：Markdown、Word (.docx)、JSON 三种格式互相转换。整个工具就是一个 HTML 文件，双击就能用，不联网、不上传、不装任何东西。

## 📥 下载

**👉 [点我直接下载（ZIP，解压后双击 index.html 即可使用）](https://github.com/Agnes-studio/md-word-json-converter/archive/refs/heads/main.zip)**

只要单个 HTML 文件？打开 [index.html](https://github.com/Agnes-studio/md-word-json-converter/blob/main/index.html)，点页面**右上角的下载按钮**（⬇ Download raw file）即可。

## ✨ 特点

- **完全离线**：所有转换都在浏览器本地完成，文件不会离开你的电脑
- **单文件**：只有一个 `index.html`，拷走就能用，无需安装、无需依赖
- **三种格式 6 个方向全互转**：MD ⇄ Word、MD ⇄ JSON、Word ⇄ JSON，任意组合
- **JSON 可无损还原**：MD / Word 转出的 JSON 是结构化文档数据（标题/列表/表格都是字段），再转回 MD / Word 时原样还原，不会退化成纯文本
- **批量转换**：支持多选 / 拖入多个文件一次性转换，多个结果自动打包成 ZIP
- **拖拽识别**：文件拖进来自动识别格式，选好目标格式点一下即可
- **Claude 对话导出优化**：JSON 转换对 Claude 导出的对话文件（`conversations.json`）做了专门处理——自动理清分支对话主线、保留思考过程和附件内容、生成带 frontmatter 的 Obsidian 笔记（多条对话还附 Dataview 索引页）；普通 JSON 也能转，按结构生成 Markdown

## 🔀 支持的转换方向

| 原始 \ 目标 | Markdown | Word (.docx) | JSON |
|---|---|---|---|
| **Markdown** | — | ✅ 排版保留 | ✅ 结构化数据 |
| **Word (.docx)** | ✅ | — | ✅ 结构化数据 |
| **JSON** | ✅ 结构化 JSON 无损还原；Claude 对话 / 普通 JSON 智能排版 | ✅ 同上 | — |

## 🚀 使用方法

1. 用上面「📥 下载」里的链接拿到 `index.html`
2. 双击用浏览器打开（推荐新版 Edge / Chrome）
3. 左边选原始格式，右边选目标格式，把文件拖进来，点「开始转换」

## ⚠️ 注意事项

- Word → MD 只支持 `.docx`；老 `.doc` 请先用 Word 另存为 `.docx`
- Word 文档里的图片不会带入 MD（会标记为〔图片〕）
- 读取 `.docx` 需要较新的浏览器（用到 `DecompressionStream` API）

## 🛠 技术说明

纯原生 HTML / CSS / JavaScript 实现，零依赖：

- 内置 Markdown 解析器（标题、列表、表格、代码块、行内样式、YAML frontmatter 等）
- `.docx` 读写：手动构造 / 解析 OOXML + ZIP 结构，解压使用浏览器原生 `DecompressionStream`
- 转出的 `.docx` 可直接用 Word / WPS 打开
- MD / Word → JSON 使用统一的 `mdwx-document` 结构（`{format, version, source, blocks}`），转回时按结构渲染，保证往返一致

## 🧪 测试

仓库附带 Node 测试脚本（仅开发用，工具本身零依赖）：

```bash
node test_roundtrip.js
```

覆盖：MD ⇄ JSON 往返一致性、结构化 JSON 识别、docx 生成、Claude 对话导出兼容等 9 项。

## 📄 License

[MIT](LICENSE)

---

## English

A single-file, fully offline converter between **Markdown ⇄ Word (.docx) ⇄ JSON**. Just download `index.html` and open it in a modern browser — no install, no network, your files never leave your computer. Batch conversion and drag-and-drop supported. MIT licensed.
