$(function () {
    var vue = new Vue({
        el: "#portal_vue",//dom元素内使用vue
        data: {
            chart_visitors: 'fa fa-refresh',
            chart_visits: 'fa fa-refresh',
        },
        created: function () {//vue初始化后加载
            this.$options.methods.init(this);
        },
        filters: {},
        methods: {
            init: function (that) {
                that.$options.methods.methodpost(that, 0, "/api/portal/lastweek", {});
                that.$options.methods.methodpost(that, 1, "/api/portal/lastmonth", {});

                window.setInterval(function () {
                    that.$options.methods.methodpost(that, 0, "/api/portal/lastweek", {}, function () {
                    }, true);
                    that.$options.methods.methodpost(that, 1, "/api/portal/lastmonth", {}, function () {
                    }, true);
                }, 1000 * 60);
            },
            iboxClick: function (e, index) {
                switch (index) {
                    case 0:
                        vue.chart_visitors = "fa fa-refresh fa-spin";
                        vue.$options.methods.methodpost(vue, 0, "/api/portal/lastweek", {}, function () {
                            vue.chart_visitors = "fa fa-refresh";
                        });
                        break;
                    case 1:
                        vue.chart_visits = "fa fa-refresh fa-spin";
                        vue.$options.methods.methodpost(vue, 1, "/api/portal/lastmonth", {}, function () {
                            vue.chart_visits = "fa fa-refresh";
                        });
                        break;
                    default:
                        break;
                }
            },
            /**
             * 对所有的请求结果集中处理
             */
            filldata: function (that, data, mode, callback) {
                switch (mode) {
                    case 0:
                        var jsData = data.resultData;
                        $('#morris-area-chart-number-visitors').empty();
                        $('#area-morris-chart-number-visitors').empty();
                        new Morris.Line({
                            element: 'morris-area-chart-number-visitors',
                            resize: true,
                            data: jsData,
                            xkey: 'time',
                            ykeys: ["peoplenop", "peoplept"],
                            labels: ["人数(UV)", "人次(PV)"],
                            lineColors: ['#81db55', '#3a91ff'],
                            hideHover: 'true',
                            ymin: "auto 0"
                        });
                        new Morris.Area({
                            element: 'area-morris-chart-number-visitors',
                            data: jsData,
                            xkey: "time",
                            ykeys: ["peoplenop", "peoplept"],
                            labels: ["人数(UV)", "人次(PV)"],
                            hideHover: "auto",
                            lineWidth: 2,
                            pointSize: 4,
                            lineColors: ['#81db55', '#3a91ff'],
                            fillOpacity: 0.5,
                            smooth: true
                        });
                        break;
                    case 1:
                        var jsData = data.resultData;
                        $('#morris-area-chart-visits').empty();
                        $('#area-morris-chart-visits').empty();
                        new Morris.Line({
                            element: 'morris-area-chart-visits',
                            resize: true,
                            data: jsData,
                            xkey: 'time',
                            ykeys: ["peoplenop", "peoplept"],
                            labels: ["人数(UV)", "人次(PV)"],
                            lineColors: ['#81db55', '#3a91ff'],
                            hideHover: 'true',
                            ymin: "auto 0"
                        });
                        new Morris.Area({
                            element: 'area-morris-chart-visits',
                            data: jsData,
                            xkey: "time",
                            ykeys: ["peoplenop", "peoplept"],
                            labels: ["人数(UV)", "人次(PV)"],
                            hideHover: "auto",
                            lineWidth: 2,
                            pointSize: 4,
                            lineColors: ['#81db55', '#3a91ff'],
                            fillOpacity: 0.5,
                            smooth: true
                        });
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
            }
        },
        mounted: function () {
        }
    })
});