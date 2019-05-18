$(function () {
    var vue = new Vue({
        el: "#layout",//dom元素内使用vue
        data: {
            menuData: [
                {
                    url: '#basic',
                    des: 'Basic',
                    simple: true,
                    api: '',
                    data: []
                },
                {
                    url: '#customs',
                    des: '自定义 Customs',
                    simple: true,
                    api: '',
                    data: []
                },
                {
                    url: '#markdown-extras',
                    des: 'Markdown Extras',
                    simple: true,
                    api: '',
                    data: []
                },
                {
                    url: '#image-upload',
                    des: 'Image Upload',
                    simple: true,
                    api: '',
                    data: []
                },
                {
                    url: '#events',
                    des: '事件处理 Events handle',
                    simple: true,
                    api: '',
                    data: []
                },
                {
                    url: '',
                    des: '博客',
                    simple: false,
                    api: '',
                    data: [
                        {
                            url: projectName + '/markdown/api',
                            des: 'API'
                        },
                        {
                            url: projectName + '/markdown/blog',
                            des: '博客展示'
                        },
                        {
                            url: projectName + '/markdown/tag',
                            des: '标签'
                        },
                        {
                            url: projectName + '/markdown/picture',
                            des: '图片Base64'
                        },
                        {
                            url: projectName + '/api/mail/TemplateMail',
                            des: '发送模板邮件'
                        },
                        {
                            url: projectName + '/markdown/photoClip',
                            des: '截图'
                        },
                        {
                            url: projectName + '/portal',
                            des: '制作中的首页'
                        }
                    ]
                },
                {
                    url: '',
                    des: '退出',
                    simple: true,
                    api: projectName + '/logout',
                    data: []
                }
            ],
            contentData: [
                {
                    name: 'basic',
                    des: 'Basic',
                    top: false,
                    data: [
                        {
                            url: projectName + '/simple',
                            zhDes: '简单示例',
                            enDes: 'Simple example'
                        },
                        {
                            url: '/examples/full.htm',
                            zhDes: '完整示例',
                            enDes: 'Full example'
                        },
                        {
                            url: '/examples/use-requirejs.htm',
                            zhDes: '使用 Require.js',
                            enDes: 'Using Require.js'
                        },
                        {
                            url: '/examples/use-seajs.htm',
                            zhDes: '使用 Sea.js',
                            enDes: 'Using Seajs'
                        },
                        {
                            url: '/examples/use-zepto.htm',
                            zhDes: '使用 Zepto.js',
                            enDes: 'Using Zepto.js'
                        },
                        {
                            url: '/examples/form-get-value.htm',
                            zhDes: '表单取值',
                            enDes: 'Get textarea value in form'
                        },
                        {
                            url: '/examples/html-preview-markdown-to-html.htm',
                            zhDes: '非编辑时 Markdown 转 HTML 的显示处理',
                            enDes: 'Markdown to HTML for preview'
                        }
                    ]
                },
                {
                    name: 'customs',
                    des: '自定义 Customs',
                    top: true,
                    data: [
                        {
                            url: '/examples/custom-toolbar.htm',
                            zhDes: '自定义工具栏',
                            enDes: 'Custom toolbar'
                        },
                        {
                            url: '/examples/multi-languages.htm',
                            zhDes: '多语言',
                            enDes: 'Multi-languages for l18n'
                        },
                        {
                            url: '/examples/auto-height.htm',
                            zhDes: '自动高度',
                            enDes: 'Auto height'
                        },
                        {
                            url: '/examples/toolbar-auto-fixed.htm',
                            zhDes: '工具栏自动固定定位的开启与禁用',
                            enDes: 'Enable / disable toolbar auto fixed position.'
                        },
                        {
                            url: '/examples/dynamic-create-editormd.htm',
                            zhDes: '动态创建 Editor.md',
                            enDes: 'Dynamic create Editor.md'
                        },
                        {
                            url: '/examples/delay-renderer-preview.htm',
                            zhDes: '延迟解析和预览',
                            enDes: 'Delay Rerender & Preview'
                        },
                        {
                            url: '/examples/multi-editormd.htm',
                            zhDes: '多个 Editor.md 并存',
                            enDes: 'Multi Editor.md'
                        },
                        {
                            url: '/examples/goto-line.htm',
                            zhDes: '跳转到指定的行',
                            enDes: 'Goto line'
                        },
                        {
                            url: '/examples/readonly.htm',
                            zhDes: '只读模式',
                            enDes: 'Read only mode'
                        },
                        {
                            url: '/examples/themes.htm',
                            zhDes: '自定义编辑器样式主题',
                            enDes: 'Setting / change editor style theme'
                        },
                        {
                            url: '/examples/search-replace.htm',
                            zhDes: '搜索替换功能',
                            enDes: 'Search / Replace'
                        },
                        {
                            url: '/examples/code-fold.htm',
                            zhDes: '代码折叠功能',
                            enDes: 'Code fold'
                        },
                        {
                            url: '/examples/custom-keyboard-shortcuts.htm',
                            zhDes: '自定义键盘快捷键',
                            enDes: 'Custom keyboard shortcuts'
                        },
                        {
                            url: '/examples/custom-keyboard-shortcuts.htm',
                            zhDes: '自定义键盘快捷键',
                            enDes: 'Custom keyboard shortcuts'
                        },
                        {
                            url: '/examples/define-plugin.htm',
                            zhDes: '自定义插件',
                            enDes: 'Define extention plugins for Editor.md'
                        },
                        {
                            url: '/examples/manually-load-modules.htm',
                            zhDes: '手动加载依赖模块文件',
                            enDes: 'Manually loading dependent module files.'
                        },
                        {
                            url: '/examples/sync-scrolling.htm',
                            zhDes: '双向、单向或禁用同步滚动',
                            enDes: 'Bisync, Single, Disabled sync scrolling'
                        },
                        {
                            url: '/examples/external-use.htm',
                            zhDes: '外部使用工具栏的操作方法和对话框',
                            enDes: 'External use of toolbar handlers / modal dialog'
                        },
                        {
                            url: '/examples/resettings.htm',
                            zhDes: '加载完成且创建成功之后的重配置',
                            enDes: 'loaded resettings'
                        },
                        {
                            url: '/examples/change-mode.htm',
                            zhDes: '变身为代码编辑器',
                            enDes: 'Change mode become to the code editor'
                        },
                        {
                            url: '/examples/set-get-replace-selection.htm',
                            zhDes: '插入字符 / 设置和获取光标位置 / 设置、获取和替换选中的文本',
                            enDes: 'Insert value & Set / Get cursor & Set / Get / Replace selection'
                        },
                        {
                            url: '/examples/extends.htm',
                            zhDes: '扩展成员方法和属性',
                            enDes: 'Expanded of member methods and properties'
                        }
                    ]
                },
                {
                    name: 'markdown-extras',
                    des: 'Markdown Extras',
                    top: true,
                    data: [
                        {
                            url: '/examples/html-tags-decode.htm',
                            zhDes: '识别和（过滤）解析 HTML 标签',
                            enDes: 'HTML tags (fliter) decode'
                        },
                        {
                            url: '/examples/toc.htm',
                            zhDes: '自动生成目录(下拉菜单) ToC / ToCM',
                            enDes: 'Table of Contents (ToC)'
                        },
                        {
                            url: '/examples/task-lists.htm',
                            zhDes: 'GFM 任务列表',
                            enDes: 'Github Flavored Markdown (GFM) Task Lists'
                        },
                        {
                            url: '/examples/@links.htm',
                            zhDes: '@链接',
                            enDes: '@links'
                        },
                        {
                            url: '/examples/emoji.htm',
                            zhDes: 'Emoji / Twemoji 表情 / Font Awesome 图标',
                            enDes: 'Emoji / Twemoji / Font Awesome icons'
                        },
                        {
                            url: '/examples/katex.htm',
                            zhDes: '科学公式 TeX',
                            enDes: 'TeX / LaTeX (Based on KaTeX)'
                        },
                        {
                            url: '/examples/flowchart.htm',
                            zhDes: '流程图',
                            enDes: 'FlowChart example'
                        },
                        {
                            url: '/examples/sequence-diagram.htm',
                            zhDes: '时序图 / 序列图',
                            enDes: 'Sequence Diagram example'
                        },
                        {
                            url: '/examples/page-break.htm',
                            zhDes: '分页符',
                            enDes: 'Page break'
                        }
                    ]
                },
                {
                    name: 'image-upload',
                    des: 'Image Upload',
                    top: true,
                    data: [
                        {
                            url: '/examples/image-upload.htm',
                            zhDes: '图片上传',
                            enDes: 'Image upload'
                        },
                        {
                            url: '/examples/image-cross-domain-upload.htm',
                            zhDes: '图片跨域上传',
                            enDes: 'Image cross-domain upload'
                        }
                    ]
                },
                {
                    name: 'events',
                    des: '事件处理 Events handle',
                    top: true,
                    data: [
                        {
                            url: '/examples/on-off.htm',
                            zhDes: '事件注册和解除方法',
                            enDes: 'On / Off (bind/unbind) event handle'
                        },
                        {
                            url: '/examples/onload.htm',
                            zhDes: '加载完成事件处理',
                            enDes: 'Onload event handle example'
                        },
                        {
                            url: '/examples/onchange.htm',
                            zhDes: '变化监听处理',
                            enDes: 'Onchange event handle example'
                        },
                        {
                            url: '/examples/onfullscreen.htm',
                            zhDes: '全屏事件处理',
                            enDes: 'Onfullscreen / onfullscreenExit event handle example'
                        },
                        {
                            url: '/examples/onresize.htm',
                            zhDes: 'Onresize',
                            enDes: 'Onresize event handle'
                        },
                        {
                            url: '/examples/onpreviewing-onpreviewed.htm',
                            zhDes: 'Onpreviewing / Onpreviewed',
                            enDes: 'Onpreviewing / Onpreviewed event handle'
                        },
                        {
                            url: '/examples/onwatch-onunwatch.htm',
                            zhDes: 'Onwatch / Onunwatch',
                            enDes: 'Onwatch / Onunwatch event handle'
                        },
                        {
                            url: '/examples/onscroll-onpreviewscroll.htm',
                            zhDes: 'Onscroll / Onpreviewscroll',
                            enDes: 'Onscroll / Onpreviewscroll event handle'
                        }
                    ]
                }
            ],
            user_css: 'nav-item profile-info',
            user_show: false
        },
        created: function () {//vue初始化后加载
        },
        filters: {},
        methods: {
            currentClick: function (e, index) {
                var data = vue.menuData[index];
                var target = $("[name=" + data.url.slice(1) + "]");
                if (target.length) {
                    $("html, body").animate({
                        scrollTop: target.offset().top
                    }, 800);
                    return false;
                }
            },
            pClick: function (e, url) {
                window.location.href = url;
            },
            vMouseMove: function (e) {
                vue.user_css = 'nav-item profile-info on';
                vue.user_show = true;
            },
            vMouseOut: function (e) {
                vue.user_css = 'nav-item profile-info';
                vue.user_show = false;
            },
            logout: function (e) {
                window.location.href = projectName + '/logout';
            }
        },
        mounted: function () {

        }
    })
});