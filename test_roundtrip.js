// 本地测试：从 index.html 提取 <script>，在 Node 里跑往返转换测试
const fs = require('fs');
const html = fs.readFileSync('index.html', 'utf8');
const m = html.match(/<script>([\s\S]*)<\/script>/);
if (!m) { console.error('FAIL: no <script> block'); process.exit(1); }

// 浏览器环境桩
function mkEl() {
  return {
    style: {}, className: '', textContent: '', innerHTML: '', disabled: false,
    classList: { add() {}, remove() {}, contains() { return false; } },
    addEventListener() {}, appendChild() {},
    querySelector() { return mkEl(); },
  };
}
global.window = {};
global.document = {
  getElementById() { return mkEl(); },
  createElement() { return mkEl(); },
  addEventListener() {},
  querySelectorAll() { return []; },
  body: mkEl(),
};

eval(m[1]);
const W = global.window.MDWX;
if (!W) { console.error('FAIL: MDWX not exported'); process.exit(1); }

let pass = 0, fail = 0;
function check(name, cond, extra) {
  if (cond) { pass++; console.log('PASS', name); }
  else { fail++; console.log('FAIL', name, extra === undefined ? '' : extra); }
}

const sample = `---
title: "测试文档"
tags: [demo]
---

# 一级标题

这是一段 **加粗** 和 *斜体* 还有 ***粗斜体***、~~删除线~~、\`行内代码\` 以及 [链接](https://example.com)。

## 列表

- 项目一
- 项目二
  - 子项目A
  - 子项目B
- [x] 已完成任务
- [ ] 未完成任务

1. 第一步
2. 第二步

## 表格

| 名称 | 数量 | 备注 |
| :--- | ---: | :---: |
| 苹果 | 3 | 新鲜 |
| 香蕉 | 5 | 一般 |

## 代码

\`\`\`js
function hello() {
  return "world";
}
\`\`\`

## 引用

> 这是一段引用
> 第二行

> [!warning] 注意标题
> callout 内容

---

![图片alt](https://example.com/pic.png)

结尾段落。
`;

// 1. MD → blocks → MD 往返：两次解析结构应一致
const b1 = W.parseMd(sample);
const md2 = W.blocksToMd(b1);
const b2 = W.parseMd(md2);
check('MD→blocks→MD→blocks 结构一致', JSON.stringify(b1) === JSON.stringify(b2),
  '\n--- 第一次渲染 ---\n' + md2 + '\n--- 再解析 ---\n' + JSON.stringify(b2, null, 1).slice(0, 800));

// 2. MD → JSON 文档 → jsonToMdFiles 还原
const docJson = JSON.stringify({ format: 'mdwx-document', version: 1, source: 'markdown', blocks: b1 }, null, 2);
const r = W.jsonToMdFiles(docJson, '测试.md.json');
check('结构化JSON识别为文档', r.files.length === 1 && r.files[0].name.endsWith('.md'), JSON.stringify(r.files.map(f => f.name)));
const b3 = W.parseMd(r.files[0].content);
check('JSON→MD 还原后结构一致', JSON.stringify(b1) === JSON.stringify(b3));

// 3. isDocJson 判断
check('isDocJson 识别正确', W.isDocJson(JSON.parse(docJson)) === true && W.isDocJson({ a: 1 }) === false && W.isDocJson([1, 2]) === false);

// 4. MD → docx 字节是合法 ZIP
const z = W.mdToDocxBytes(sample);
check('MD→docx 生成ZIP', z[0] === 0x50 && z[1] === 0x4B);

// 5. 结构化 JSON → MD → docx 链路
const z2 = W.mdToDocxBytes(r.files[0].content);
check('JSON→MD→docx 生成ZIP', z2[0] === 0x50 && z2[1] === 0x4B);

// 6. Claude 对话导出 JSON 走原逻辑（不能被判成文档 JSON）；两条对话应附 Dataview 索引
const conv = (id, q) => ({ uuid: id, name: '对话' + id, chat_messages: [
  { uuid: id + 'a', sender: 'human', text: q, created_at: '2026-01-01T00:00:00Z' },
  { uuid: id + 'b', sender: 'assistant', text: '好的', parent_message_uuid: id + 'a', created_at: '2026-01-01T00:00:01Z' },
] });
const claude = JSON.stringify([conv('x', '你好'), conv('y', '在吗')]);
const rc = W.jsonToMdFiles(claude, 'conversations.json');
check('Claude导出JSON仍走对话逻辑', rc.files.some(f => f.content.includes('🧑 你')) && rc.files.some(f => f.name === '_索引.md'),
  rc.files.map(f => f.name).join(','));

// 7. 普通 JSON 走通用逻辑
const rg = W.jsonToMdFiles(JSON.stringify({ name: '配置', a: 1, list: [1, 2] }), 'cfg.json');
check('普通JSON走通用逻辑', rg.files.length === 1 && rg.files[0].content.includes('# 配置'));

// 8. 文档 JSON 数组 → 多个 md
const arr = JSON.stringify([JSON.parse(docJson), JSON.parse(docJson)]);
const ra = W.jsonToMdFiles(arr, 'multi.json');
check('文档JSON数组→多文件', ra.files.length === 2 && ra.files.every(f => f.name.endsWith('.md')), ra.files.map(f => f.name).join(','));

console.log(`\n结果: ${pass} 通过, ${fail} 失败`);
process.exit(fail ? 1 : 0);
