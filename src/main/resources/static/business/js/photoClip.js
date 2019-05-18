$(function () {
    var vue = new Vue({
        el: "#photoclip",//dom元素内使用vue
        data: {
            dataURL: ''
        },
        created: function () {//vue初始化后加载
        },
        filters: {},
        methods: {
            savePhoto: function (e) {
                if (strIsEmpty(vue.dataURL)) {
                    swal(
                        '错误提示',
                        "请先截图！",
                        'error'
                    );
                    return;
                }
                const swalWithBootstrapButtons = Swal.mixin({
                    confirmButtonClass: 'btn btn-success',
                    cancelButtonClass: 'btn btn-danger',
                    buttonsStyling: false,
                });
                swalWithBootstrapButtons({
                    title: '上传头像?',
                    text: "你将修改你的个人头像!",
                    type: 'warning',
                    showCancelButton: true,
                    confirmButtonText: '上传头像',
                    cancelButtonText: '取消',
                    reverseButtons: true
                }).then((result) => {
                    if (result.value) {
                        vue.$options.methods.methodpost(vue, 0, "/api/edit/headPortrait", {
                            picsrc: encodeURI(vue.dataURL).replace(/\+/g, '%2B')
                        });
                    } else if (result.dismiss === Swal.DismissReason.cancel) {
                        swalWithBootstrapButtons(
                            '取消',
                            '已取消上传头像操作 :)',
                            'error'
                        )
                    }
                })
            },
            /**
             * 对所有的请求结果集中处理
             */
            filldata: function (that, data, mode, callback) {
                switch (mode) {
                    case 0:
                        swal(
                            '提示',
                            "头像修改成功！请刷新主页。",
                            'info'
                        );
                        break;
                    default:
                        break;
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
            var clipArea = new bjj.PhotoClip("#clipArea", {
                size: [260, 260],
                outputSize: [640, 640],
                file: "#file",
                view: "#view",
                ok: "#clipBtn",
                loadStart: function () {
                    console.log("照片读取中");
                },
                loadComplete: function () {
                    console.log("照片读取完成");
                },
                clipFinish: function (dataURL) {
                    vue.dataURL = dataURL;
                }
            });
            //clipArea.destroy();
        }
    })
});