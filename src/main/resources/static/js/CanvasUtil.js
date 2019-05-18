/**
 * 根据img标签获取base64编码
 * @param img html的img标签
 * @param ext 图片格式
 * @returns {string}
 */
function getImageBase64(img, ext) {
    var canvas = document.createElement("canvas");   //创建canvas DOM元素，并设置其宽高和图片一样
    canvas.width = img.width;
    canvas.height = img.height;
    var ctx = canvas.getContext("2d");
    ctx.drawImage(img, 0, 0, img.width, img.height); //使用画布画图
    var dataURL = canvas.toDataURL("image/" + ext);  //返回的是一串Base64编码的URL并指定格式
    canvas = null; //释放
    return dataURL;
}

/**
 * 根据图片路径获取base64编码
 * @param url 图片路径
 * @param ext 图片格式
 * @param callback 结果回调
 */
function getUrlBase64(url, ext, callback) {
    var canvas = document.createElement("canvas");   //创建canvas DOM元素
    var ctx = canvas.getContext("2d");
    var img = new Image;
    img.crossOrigin = 'Anonymous';
    img.src = url;
    img.onload = function () {
        canvas.height = 60; //指定画板的高度,自定义
        canvas.width = 85; //指定画板的宽度，自定义
        ctx.drawImage(img, 0, 0, 60, 85); //参数可自定义
        var dataURL = canvas.toDataURL("image/" + ext);
        callback(this, dataURL); //回掉函数获取Base64编码
        canvas = null;
    };
}