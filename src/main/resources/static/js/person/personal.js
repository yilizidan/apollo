$(function () {
    var vue = new Vue({
        el: "#persional_vue",//dom元素内使用vue
        data: {
            info: {
                address: "地球~中国~~",
                description: "该用户比较懒,什么也没有留下.",
                nickname: "",
                srcBase: "",
                username: ""
            }
        },
        created: function () {//vue初始化后加载
            this.$options.methods.init(this);
        },
        filters: {},
        methods: {
            init: function (that) {
                //获取个人信息
                that.$options.methods.methodpost(that, 0, "/api/init/personalinfo", {});
            },
            /**
             * 对所有的请求结果集中处理
             */
            filldata: function (that, data, mode, callback) {
                switch (mode) {
                    case 0:
                        that.info = data.resultData;
                        if (strIsEmpty(that.info.description)) {
                            that.info.description = "该用户比较懒,什么也没有留下.";
                        }
                        if (strIsEmpty(that.info.address)) {
                            that.info.address = "地球~中国~~";
                        }
                        break;
                    default:
                        break;
                }
                if (callback) {
                    callback();
                }
            },
            /**
             * 综合所有请求入口
             */
            methodpost: function (that, mode, url, json, callback) {
                AJAX({
                    url: projectName + url,
                    sync: false,
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