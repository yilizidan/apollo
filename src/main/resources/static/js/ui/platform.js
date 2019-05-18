$(function () {
    var vue = new Vue({
        el: "#protal_info_vue",//dom元素内使用vue
        data: {
            personinfo: {
                nickname: '一粒子弹',
                role: '超级管理员',
                img_src: null
            },
            routing: [
                {li: "", _class: "J_menuItem", isshow: true, src: projectName + '/photoClip', text: "修改头像"},
                {li: "", _class: "J_menuItem", isshow: true, src: projectName + '/profile', text: "个人资料"},
                {li: "", _class: "J_menuItem", isshow: true, src: '', text: "联系我们"},
                {li: "", _class: "J_menuItem", isshow: true, src: "", text: "信箱"},
                {li: "divider", _class: "", isshow: false, src: "", text: ""},
                {li: "", _class: "", isshow: true, src: projectName + '/logout', text: "安全退出"}
            ],
            nodelist: null
        },
        created: function () {//vue初始化后加载
            this.$options.methods.init(this);
        },
        filters: {},
        methods: {
            init: function (that) {
                that.$options.methods.methodpost(that, false, 0, '/api/init/personalinfo', {});

                that.$options.methods.methodpost(that, true, 1, '/api/init/personalPic', {});
            },
            analysis: function (arr, that) {
                for (var j = 0, len = arr.length; j < len; j++) {
                    if (!strIsEmpty(arr[j].nodeurl)) {
                        if (!(arr[j].nodeurl.indexOf("http") != -1 || arr[j].nodeurl.indexOf("https") != -1)) {
                            arr[j].nodeurl = projectName + arr[j].nodeurl;
                        }
                    }
                    if (strEquals(arr[j].child, '0')) {
                        that.$options.methods.analysis(arr[j].childlist, that);
                    }
                }
            },
            tyClick: function (e, index) {
                if (vue.routing[index].isshow && !strEquals(vue.routing[index].text, '安全退出')) {
                }
            },
            logout: function (e) {
                window.location.href = projectName + '/logout';
            },
            /**
             * 对所有的请求结果集中处理
             */
            filldata: function (that, data, mode, callback) {
                switch (mode) {
                    case 0:
                        that.personinfo.nickname = data.resultData.nickname;
                        var roles = data.resultData.rolelist;
                        if (roles.length > 0) {
                            that.personinfo.role = roles[0].name;
                        }
                        that.nodelist = data.resultData.nodedata;
                        that.$options.methods.analysis(that.nodelist, that);
                        loadScript('js/ui/hplus.min.js');
                        loadScript('js/ui/contabs.min.js');
                        break;
                    case 1:
                        var pic = data.resultData;
                        if (!strIsEmpty(pic)) {
                            /*
                            //pic为图片文件
                            getBase64(projectName + pic).then(function (base64) {
                                that.personinfo.img_src = base64;
                            }, function (err) {
                                console.log(err, "==============");//打印异常信息
                            });
                            */
                            that.personinfo.img_src = pic;
                        } else {
                            that.personinfo.img_src = null;
                        }
                        break;
                    default:
                        break;
                }
            },
            /**
             * 综合所有请求入口
             * @param async 是否有请求加载条
             * @param url 请求地址
             * @param json 参数
             * @param callback 回调函数
             */
            methodpost: function (that, async, mode, url, json, callback) {
                AJAX({
                    url: projectName + url,
                    sync: true,
                    async: async,
                    data: !json ? {} : json,
                    success: function (data) {
                        if (strIsEmpty(data)) {
                            swal(
                                '错误提示',
                                "请求异常！",
                                'error'
                            );
                            return;
                        }
                        if (strEquals(data.code, 200)) {
                            that.$options.methods.filldata(that, data, mode, callback);
                        } else {
                            swal(
                                '错误提示',
                                data.msg,
                                'error'
                            )
                        }
                    }
                });
            }
        },
        mounted: function () {

        }
    })
});