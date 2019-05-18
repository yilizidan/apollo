Array.prototype.ajax_add = function (val) {
    this.splice(0, 0, val);
};

Array.prototype.ajax_indexOf = function (val) {
    for (var i = 0; i < this.length; i++) {
        if (strEquals(this[i], val)) {
            return i;
        }
    }
    return -1;
};

Array.prototype.ajax_remove = function (val) {
    var index = this.ajax_indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};

var ajax_Array = [];

function AJAX(o) {
    var timestamp = new Date().getTime() + Math.random().toString(36).substr(2);
    var options = {
        type: o.type ? o.type : "POST",
        timeout: 60000,//设置请求超时时间(毫秒),此设置将覆盖全局设置。
        async: !(true === o.sync),//设置ajax异步请求或者同步请求(默认:true)同步请求将锁住浏览器，用户其他操作必须等待请求完成后才能进行
        dataType: o.datatype ? o.datatype : "JSON",
        timestamp: timestamp,
        success: function (data) {//请求成功时调用函数方法,
            onSuccess(o, data, options);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {//请求失败时调用方法函数
            if (o.error) {
                o.error(XMLHttpRequest, textStatus, errorThrown);
            } else {
                onFailure(XMLHttpRequest, textStatus, errorThrown, options);
            }
        },
        beforeSend: function () {//发送请求前调用函数
            if (o.beforeSend) {
                o.beforeSend(options);
            } else {
                onBeforeSend(options);
            }
        },
        complete: function () {//请求完成后回调函数(请求完成或失败时均可调用)
            if (o.complete) {
                o.complete(options);
            } else {
                onCompleted(options);
            }
        }
    };

    var url = o.url;
    var data = o.data;
    if (options.Type == 'GET' && data) {
        url = urlAppend(url, data);
        data = null;
    }
    options.data = data;
    options.url = url;

    var currentRequests = {};

    $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
        if (options.abortOnRetry) {
            if (currentRequests[options.url]) {
                currentRequests[options.url].abort();
            }
            currentRequests[options.url] = jqXHR;
        }
    });

    $.ajax(options);
}

function urlAppend(url, data) {
    url += "?";
    //遍历json对象的每个key/value对,p为key
    for (var key in data) {
        url += key + "=" + data[key] + "&";
    }
    url = url.substring(0, url.length - 1);
    return url;
}

function onBeforeSend(options) {
    if (options.async) {
        spinnersPop();
        ajax_Array.ajax_add(options.timestamp);
    }
}

function onSuccess(o, data, options) {
    if (options.async) {
        ajax_Array.ajax_remove(options.timestamp);
        if (strEquals(ajax_Array.length, 0)) {
            spinnersClosePop();
        }
    }
    //login is null
    if (!strEquals(data.code, 200)) {
        swal(
            '系统提示',
            data.msg,
            'error'
        );
        if (strEquals(data.code, 403)) {
            setTimeout(function () {
                if (!strEquals(top.location, self.location)) {
                    top.location.href = projectName + '/login';
                } else {
                    window.location.href = projectName + '/login';
                }
            }, 1000);
        }
    }
    if (o.success) {
        o.success(data);
    }
}

function onFailure(XMLHttpRequest, textStatus, errorThrown, options) {
    if (options.async) {
        ajax_Array.ajax_remove(options.timestamp);
        if (strEquals(ajax_Array.length, 0)) {
            spinnersClosePop();
        }
    }
    if (strEquals(XMLHttpRequest.status, 404)) {
        window.location.href = projectName + '/404';
    } else if (strEquals(XMLHttpRequest.status, 500)) {
        window.location.href = projectName + '/500';
    } else if (strEquals(XMLHttpRequest.status, 302)) {
        window.location.href = projectName + '/login';
    }
}

function onCompleted(options) {
}
