$(function () {
    var vue = new Vue({
        el: "#layout",//dom元素内使用vue
        data: {
            textarea: '[TOC]\n' +
            '\n' +
            '#### Disabled options\n' +
            '\n' +
            '- TeX (Based on KaTeX);\n' +
            '- Emoji;\n' +
            '- Task lists;\n' +
            '- HTML tags decode;\n' +
            '- Flowchart and Sequence Diagram;\n' +
            '\n' +
            '#### Editor.md directory\n' +
            '\n' +
            '    editor.md/\n' +
            '            lib/\n' +
            '            css/\n' +
            '            scss/\n' +
            '            tests/\n' +
            '            fonts/\n' +
            '            images/\n' +
            '            plugins/\n' +
            '            examples/\n' +
            '            languages/     \n' +
            '            editormd.js\n' +
            '            ...\n' +
            '\n' +
            '```html\n' +
            '&lt;!-- English --&gt;\n' +
            '&lt;script src="../dist/js/languages/en.js"&gt;&lt;/script&gt;\n' +
            '\n' +
            '&lt;!-- 繁體中文 --&gt;\n' +
            '&lt;script src="../dist/js/languages/zh-tw.js"&gt;&lt;/script&gt;\n' +
            '```'
        },
        created: function () {//vue初始化后加载
        },
        filters: {},
        methods: {},
        mounted: function () {
            var testEditor = editormd("simple-editormd", {
                width: "90%",
                height: 640,
                syncScrolling: "single",
                path: ".." + projectName + "/lib/"
            });
        }
    })
});