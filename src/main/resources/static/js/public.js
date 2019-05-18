//获取路径
var pathName = window.document.location.pathname;
//截取，得到项目名称
var projectName = "";
if (typeof projectName != "undefined") {
    projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1)
}

function strIsEmpty(v) {
    return v === null || v === undefined || (v === '' ? true : false);
}

function strEquals(comval, v) {
    if (!!window.ActiveXObject || "ActiveXObject" in window) {
        return comval === v;
    } else {
        return Object.is(comval, v);
    }
}

function getCookie(name) {
    var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg))
        return unescape(arr[2]);
    else
        return null;
}

var phoneReg = /^1[3-578]\d{9}$/;

function isPhoneAvailable(phonevalue) {
    if (phoneReg.test(phonevalue)) {
        return true;
    } else {
        return false;
    }
}

var mailReg = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;

function isMailAvailable(mailvalue) {
    if (mailReg.test(mailvalue)) {
        return true;
    } else {
        return false;
    }
}

function isExitsFunction(funcName) {
    try {
        if (typeof(eval(funcName)) == "function") {
            return true;
        }
    } catch (e) {
    }
    return false;
}

// 动态加载css文件
function loadStyles(url) {
    var link = document.createElement("link");
    link.type = "text/css";
    link.rel = "stylesheet";
    link.href = url;
    document.getElementsByTagName("head")[0].appendChild(link);
}

// 动态加载css脚本
function loadStyleString(cssText) {
    var style = document.createElement("style");
    style.type = "text/css";
    try {
        // firefox、safari、chrome和Opera
        style.appendChild(document.createTextNode(cssText));
    } catch (ex) {
        // IE早期的浏览器 ,需要使用style元素的stylesheet属性的cssText属性
        style.styleSheet.cssText = cssText;
    }
    document.getElementsByTagName("head")[0].appendChild(style);
}

// 动态加载js脚本文件
function loadScript(url) {
    var script = document.createElement("script");
    script.type = "text/javascript";
    script.src = url;
    document.body.appendChild(script);
}

// 动态加载js脚本
function loadScriptString(code) {
    var script = document.createElement("script");
    script.type = "text/javascript";
    try {
        // firefox、safari、chrome和Opera
        script.appendChild(document.createTextNode(code));
    } catch (ex) {
        // IE早期的浏览器 ,需要使用script的text属性来指定javascript代码。
        script.text = code;
    }
    document.body.appendChild(script);
}

//传入图片路径，返回base64
function getBase64(img){
    function getBase64Image(img,width,height) {//width、height调用时传入具体像素值，控制大小 ,不传则默认图像大小
        var canvas = document.createElement("canvas");
        canvas.width = width ? width : img.width;
        canvas.height = height ? height : img.height;

        var ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
        return canvas.toDataURL();
    }
    var image = new Image();
    image.crossOrigin = '';
    image.src = img;
    var deferred=$.Deferred();
    if(img){
        image.onload =function (){
            deferred.resolve(getBase64Image(image));//将base64传给done上传处理
        };
        return deferred.promise();//问题要让onload完成后再return sessionStorage['imgTest']
    }
}
