$(function () {
    var vue = new Vue({
        el: "#nodeLinkManager",//dom元素内使用vue
        data: {
            personList: null,
            topclass: 'boxli whiteact',
            faclass: 'fa fa-chevron-circle-down',
            treeShow: true,
            nodeData: null,
            chooseData: null,
            pop: {
                nodeid: '',
                pnodeid: '',
                nodename: '',
                nodetype: "",
                nodeicon: "",
                nodeurl: "",
                descripte: ""
            },
            edit: true,
            popupsShow: false,
            refreshTips: 'fa fa-refresh',
            isadd: false,
            selectItem_flag: 'fa fa-eye',
            selectItem_title: '选择节点',
            oneTreeList: null
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
                that.$options.methods.methodpost(that, 0, "/api/nodelinkmanage/nodeLinkInit", {});
            },
            addTreeClick: function (e) {
                vue.$options.methods.methodpost(vue, 3, "/api/nodelinkmanage/nodeOneLink", {});
            },
            itemSelect: function (e, index) {
                var data = vue.oneTreeList[index];
                vue.pop.pnodeid = data.nodeid + "";
                vue.selectItem_flag = data.nodeicon;
                vue.selectItem_title = data.nodename;
            },
            editClick: function (e, index, index1) {
                var jsData;
                if (!strIsEmpty(index1)) {
                    var data = vue.nodeData[index];
                    jsData = data.childlist[index1];
                } else {
                    jsData = vue.nodeData[index];
                }

                vue.pop = {
                    nodeid: jsData.nodeid,
                    pnodeid: jsData.pnodeid,
                    nodename: jsData.nodename,
                    nodetype: jsData.nodetype,
                    nodeicon: jsData.nodeicon,
                    nodeurl: jsData.nodeurl,
                    descripte: jsData.descripte
                };
                vue.isadd = false;
                vue.selectItem_flag = 'fa fa-eye';
                vue.selectItem_title = '选择节点';
                vue.popupsShow = true;
            },
            deleteClick: function (e, index, index1) {
                var jsData;
                if (!strIsEmpty(index1)) {
                    var data = vue.nodeData[index];
                    jsData = data.childlist[index1];
                } else {
                    jsData = vue.nodeData[index];
                }
                Swal({
                    title: '你确定?',
                    text: "删除该节点!",
                    type: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: '删除',
                    cancelButtonText: '取消',
                }).then((result) => {
                    if (result.value) {
                        vue.$options.methods.methodpost(vue, 2, "/api/nodelinkmanage/delnNodeLink", {
                            nodeid: jsData.nodeid
                        });
                    }
                })
            },
            popcloseClick: function (e) {
                vue.pop = {
                    nodeid: '',
                    pnodeid: "",
                    nodename: '',
                    nodetype: "",
                    nodeicon: "",
                    nodeurl: "",
                    descripte: ""
                };
                vue.popupsShow = false;
            },
            popsaveClick: function (e) {
                var tcTK = function (value, descr) {
                    if (strIsEmpty(value)) {
                        Swal({
                            type: 'error',
                            title: '错误提示',
                            text: descr
                        });
                        return false;
                    }
                    return true;
                };

                if (!tcTK(vue.pop.nodename, '节点名称不能为空!')) {
                    return;
                }
                if (!tcTK(vue.pop.nodetype, '节点编码不能为空!')) {
                    return;
                }
                if (!tcTK(vue.pop.nodeicon, '节点图标不能为空!')) {
                    return;
                }
                if (strEquals(vue.pop.pnodeid, '0') && !tcTK(vue.pop.nodeurl, '节点地址不能为空!')) {
                    return;
                }

                if (strIsEmpty(vue.pop.nodeid)) {
                    vue.pop.nodeid = 0;
                }

                vue.$options.methods.methodpost(vue, 1, "/api/nodelinkmanage/nodeLinkEdit", vue.pop);
            },
            refreshClick: function (e) {
                vue.refreshTips = 'fa fa-refresh fa-spin';
                vue.$options.methods.methodpost(vue, 0, "/api/nodelinkmanage/nodeLinkInit", {}, function () {
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

                        if (callback) {
                            callback();
                        }
                        break;
                    case 1:
                        vue.popupsShow = false;
                        swal(
                            '提示',
                            "保存完成！",
                            'info'
                        );
                        setTimeout(function () {
                            that.$options.methods.methodpost(that, 0, "/api/nodelinkmanage/nodeLinkInit", {});
                        }, 1000);
                        break;
                    case 2:
                        swal(
                            '提示',
                            "删除成功！",
                            'info'
                        );
                        setTimeout(function () {
                            that.$options.methods.methodpost(that, 0, "/api/nodelinkmanage/nodeLinkInit", {});
                        }, 1000);
                        break;
                    case 3:
                        var json = [{nodeid: "0", nodename: "根节点", nodeicon: "fa fa-navicon"}];
                        that.oneTreeList = json.concat(data.resultData);
                        vue.pop = {
                            nodeid: '',
                            pnodeid: "",
                            nodename: '',
                            nodetype: "",
                            nodeicon: "",
                            nodeurl: "",
                            descripte: ""
                        };
                        vue.isadd = true;
                        vue.selectItem_flag = 'fa fa-eye';
                        vue.selectItem_title = '选择节点';
                        vue.popupsShow = true;
                        break;
                    default:
                        break;
                }
            },
            /**
             * 综合所有请求入口
             */
            methodpost: function (that, mode, url, json, callback, sync) {
                AJAX({
                    url: projectName + url,
                    sync: sync ? sync : false,
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
    });
});