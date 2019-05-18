(function ($) {

    $.alerts = {
        alert: function () {
            $.alerts._show();
        },
        _show: function () {
            var _html = "<div id='ibox-spinners'>\n" +
                "                        <div class='spiner-example'>\n" +
                "                            <div class='sk-spinner sk-spinner-wave'>\n" +
                "                                <div class='sk-rect1'></div>\n" +
                "                                <div class='sk-rect2'></div>\n" +
                "                                <div class='sk-rect3'></div>\n" +
                "                                <div class='sk-rect4'></div>\n" +
                "                                <div class='sk-rect5'></div>\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "                    </div>";

            //必须先将_html添加到body，再设置Css样式
            $("body").append(_html);
            GenerateCss();
        },
        _hide: function () {
            $("#ibox-spinners").remove();
        }
    };
    // Shortuct functions  
    spinnersPop = function () {
        if ($("#ibox-spinners").length == 0) {
            $.alerts.alert();
        }
    };

    spinnersClosePop = function () {
        if ($("#ibox-spinners").length > 0) {
            $.alerts._hide();
        }
    };

    //生成Css
    var GenerateCss = function () {
        var _height = document.documentElement.clientHeight; //屏幕高
        var boxHeight = $("#ibox-spinners").height();
        //让提示框居中
        $("#ibox-spinners").css({top: (_height - boxHeight) / 2 + "px", left: "50%", position: "fixed"});
    };
})(jQuery);