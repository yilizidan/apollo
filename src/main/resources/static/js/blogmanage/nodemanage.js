$(function () {
    var vue = new Vue({
        el: "#nodeManager",//dom元素内使用vue
        data: {
            personList: null,
            topclass: 'boxli whiteact',
            faclass: 'fa fa-chevron-circle-down',
            treeShow: true,
            nodeData: null,
            chooseData: null,
            edit: true,
            refreshTips: 'fa fa-refresh'
        },
        created: function () {//vue初始化后加载
            this.$options.methods.init(this);
        },
        directives: {
            focus: {
                // 指令的定义
                inserted: function (el) {
                    el.focus()
                }
            }
        },
        filters: {
            dlfunc: function (val) {
                if (strIsEmpty(val)) {
                    return '未定义';
                }
                return val;
            }
        },
        methods: {
            init: function (that) {
                that.$options.methods.methodpost(that, 0, "/api/nodemanage/personInit", {});
            },
            divIClick: function (e) {
                $.each(vue.personList, function (i, d) {
                    d.boxclass = 'boxli';
                });
                vue.topclass = 'boxli whiteact';
            },
            itemClick: function (e, index) {
                vue.topclass = 'boxli';
                var data = vue.personList[index];
                $.each(vue.personList, function (i, d) {
                    d.boxclass = 'boxli';
                });
                data.boxclass = 'boxli whiteact';
                Vue.set(vue.personList, index, data);

                vue.chooseData = data;
                vue.edit = (data.edit == 0) ? true : false;

                vue.$options.methods.methodpost(vue, 1, "/api/nodemanage/nodeInit", {
                    userid: data.id
                });
            },
            faClick: function () {
                if (strEquals(vue.faclass, 'fa fa-chevron-circle-down')) {
                    vue.faclass = 'fa fa-chevron-circle-right';
                    vue.treeShow = false;
                } else {
                    vue.faclass = 'fa fa-chevron-circle-down';
                    vue.treeShow = true;
                }
            },
            checkClick: function (e, index, type, index1) {
                if (!vue.edit) {
                    return;
                }
                var data = vue.nodeData[index];
                switch (type) {
                    case 1:
                        if (strEquals(data.checkclass, 'fa fa-square-o')) {
                            data.checkclass = 'fa fa-check-square-o';
                            data.check = '0';

                            for (var j = 0; data.childlist && j < data.childlist.length; j++) {
                                data.childlist[j].check = '0';
                                data.childlist[j].checkclass = 'fa fa-check-square-o';
                            }
                        } else {
                            data.checkclass = 'fa fa-square-o';
                            data.check = '1';
                            for (var j = 0; data.childlist && j < data.childlist.length; j++) {
                                data.childlist[j].check = '1';
                                data.childlist[j].checkclass = 'fa fa-square-o';
                            }
                        }
                        break;
                    case 2:
                        var dt = data.childlist[index1];
                        if (strEquals(dt.checkclass, 'fa fa-square-o')) {
                            dt.checkclass = 'fa fa-check-square-o';
                            dt.check = '0';
                        } else {
                            dt.checkclass = 'fa fa-square-o';
                            dt.check = '1';
                        }
                        data.childlist[index1] = dt;

                        var uncheck = 0, checked = 0;
                        for (var j = 0; data.childlist && j < data.childlist.length; j++) {
                            if (strEquals(data.childlist[j].check, '0')) {
                                checked++;
                            } else {
                                uncheck++;
                            }
                        }
                        if (uncheck > 0 && checked == 0) {
                            data.checkclass = 'fa fa-square-o';
                            data.check = '1';
                        } else if (uncheck == 0) {
                            data.checkclass = 'fa fa-check-square-o';
                            data.check = '0';
                        } else {
                            data.checkclass = 'fa fa-minus-square-o';
                            data.check = '0';
                        }
                        break;
                    default:
                        break;
                }
                Vue.set(vue.nodeData, index, data);
            },
            saveClick: function (e) {
                if (!vue.chooseData) {
                    return;
                }
                vue.$options.methods.methodpost(vue, 2, "/api/nodemanage/saveNode", {
                    userid: vue.chooseData.id,
                    nodelist: JSON.stringify(vue.nodeData)
                });
            },
            refreshClick: function (e) {
                vue.refreshTips = 'fa fa-refresh fa-spin';
                vue.$options.methods.methodpost(vue, 0, "/api/nodemanage/personInit", {}, function () {
                    vue.refreshTips = 'fa fa-refresh';
                });
            },
            /**
             * 对所有的请求结果集中处理
             */
            filldata: function (that, data, mode, callback) {
                switch (mode) {
                    case 0:
                        var jsData = data.resultData;
                        for (var i = 0; i < jsData.length; i++) {
                            jsData[i].boxclass = 'boxli';
                        }
                        that.personList = jsData;
                        if (callback) {
                            callback();
                        }
                        break;
                    case 1:
                        var jsData = data.resultData;
                        for (var i = 0; i < jsData.length; i++) {
                            if (strEquals(jsData[i].check, '0')) {
                                var uncheck = 0, checked = 0;
                                for (var j = 0; jsData[i].childlist && j < jsData[i].childlist.length; j++) {
                                    if (strEquals(jsData[i].childlist[j].check, '0')) {
                                        checked++;
                                    } else {
                                        uncheck++;
                                    }
                                }
                                if (uncheck == 0) {
                                    jsData[i].checkclass = 'fa fa-check-square-o';
                                } else {
                                    jsData[i].checkclass = 'fa fa-minus-square-o';
                                }
                            } else {
                                jsData[i].checkclass = 'fa fa-square-o';
                            }
                            for (var j = 0; jsData[i].childlist && j < jsData[i].childlist.length; j++) {
                                if (strEquals(jsData[i].childlist[j].check, '0')) {
                                    jsData[i].childlist[j].checkclass = 'fa fa-check-square-o';
                                } else {
                                    jsData[i].childlist[j].checkclass = 'fa fa-square-o';
                                }
                            }
                        }
                        that.nodeData = jsData;

                        setTimeout(function () {
                            that.$options.methods.cssFunc();
                        }, 500);

                        break;
                    case 2:
                        swal(
                            '提示',
                            "保存完成！",
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
            },
            cssFunc: function () {
                // activate Nestable for list 2
                $('.dd').nestable({
                    group: 1
                });
            }
        },
        mounted: function () {
            $('.dd').nestable({
                group: 1
            });
        }
    })
});