$(function () {

    var publicKey = null;
    /*let timerInterval = setInterval(() => {
    }, 100);*/
    AJAX({
        url: projectName + "/api/getPublicKey",
        sync: true,
        success: function (data) {
            if (strEquals(data.code,200)) {
                publicKey = data.resultData;
            } else {
                publicKey = null;
                swal(
                    '错误提示',
                    data.msg,
                    'error'
                )
            }
        }
    });

    if ($.cookie("ischeck")) {
        if ($.cookie("urd") == '' || $.cookie("psd") == '') {
            swal({
                title: '提示！',
                text: '登录时间已过期，请重新输入密码登录！',
                timer: 2000
            }).then(
                function () {
                },
                // handling the promise rejection
                function (dismiss) {
                    if (dismiss === 'timer') {
                    }
                }
            )
        } else {
            $("form input[name='username']").val($.cookie("urd"));
            AJAX({
                url: projectName + "/api/rememberMe",
                sync: false,
                success: function (data) {
                    if (strEquals(data.code,200)) {
                        if (!strIsEmpty(data.resultData.psd)) {
                            $("form input[name='rememberMe']").attr("checked", true);
                            $(".icheckbox_square-green").addClass("checked");
                            $("form input[name='password']").val(data.resultData.psd);
                        } else {
                            $("form input[name='rememberMe']").attr("checked", false);
                            $(".icheckbox_square-green").removeClass("checked");
                            $("form input[name='password']").val("");
                        }
                    }
                }
            });
        }
    } else {
        $("form input[name='rememberMe']").attr("checked", false);
        $(".icheckbox_square-green").removeClass("checked");
    }

    $("form").submit(function (envent) {
        envent.preventDefault();

        var username = $.trim($("form input[name='username']").val());
        var password = $.trim($("form input[name='password']").val());
        var rememberMe = $("form input[name='rememberMe']").is(':checked');
        if (strIsEmpty(username)) {
            return;
        }
        if (!(strEquals(username, "admin") || strEquals(username, "hx")) && !isMailAvailable(username)) {
            return;
        }
        if (strIsEmpty(password)) {
            return;
        }
        //存储30天
        /*if (rememberMe) {
            var b = new Base64();
            $.cookie('ischeck', 'true', {expires: 35});
            $.cookie('urd', b.encode(username), {expires: 30});
            $.cookie('psd', b.encode(password), {expires: 30});
        } else {
            $.cookie('ischeck', 'false', {expires: -1});
            $.cookie('urd', '', {expires: -1});
            $.cookie('psd', '', {expires: -1});
        }*/

        if (strIsEmpty(publicKey)) {
            swal(
                '错误提示',
                "系统异常！",
                'error'
            )
        }

        var encrypt = new JSEncrypt();
        encrypt.setPublicKey(publicKey);
        var m_password = encrypt.encrypt(password);

        var form = $(this);
        AJAX({
            url: projectName + "/api/login",
            sync: false,
            type: form.attr("method"),
            data: {
                username: username,
                password: encodeURI(m_password).replace(/\+/g, '%2B'),
                rememberMe: rememberMe
            },//form.serialize()
            success: function (data) {
                if (strEquals(data.code,200)) {
                    window.location.href = projectName + "/index"
                } else {
                    swal(
                        '错误提示',
                        data.msg,
                        'error'
                    )
                }
            }
        });
    });

});