$(function () {

    $("form").submit(function (envent) {
        envent.preventDefault();

        var username = $.trim($("form input[name='username']").val());
        var password = $.trim($("form input[name='password']").val());
        var nextpassword = $.trim($("form input[name='nextpassword']").val());
        var vcode = $.trim($("form input[name='vcode']").val());
        if (strIsEmpty(username)) {
            swal(
                '提示',
                "请输入电子邮箱！",
                'warning'
            );
            return;
        }
        if (!isMailAvailable(username)) {
            swal(
                '提示',
                "请输入正确的电子邮箱！",
                'warning'
            );
            return;
        }
        if (strIsEmpty(password)) {
            swal(
                '提示',
                "请输入密码！",
                'warning'
            );
            return;
        }
        if (strIsEmpty(nextpassword)) {
            swal(
                '提示',
                "请确认密码！",
                'warning'
            );
            return;
        }
        if (!strEquals(password, nextpassword)) {
            swal(
                '提示',
                "两次输入的密码不一致！",
                'warning'
            );
            return;
        }
        if (strIsEmpty(vcode)) {
            swal(
                '提示',
                "请输入验证码！",
                'warning'
            );
            return;
        }
        var form = $(this);
        AJAX({
            url: projectName + "/api/registered",
            sync: false,
            type: form.attr("method"),
            data: {
                username: username,
                password: password,
                vcode: vcode
            },//form.serialize()
            success: function (data) {
                if (strEquals(data.code,200)) {
                    window.location.href = projectName + "/login"
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

function sendvcode(e) {
    e.preventDefault();
    e.stopPropagation();
    var username = $.trim($("form input[name='username']").val());
    if (strIsEmpty(username)) {
        swal(
            '提示',
            "请输入电子邮箱！",
            'warning'
        );
        return;
    }
    if (!isMailAvailable(username)) {
        swal(
            '提示',
            "请输入正确的电子邮箱！",
            'warning'
        );
        return;
    }
    AJAX({
        url: projectName + "/api/mail/sendvcode",
        sync: false,
        data: {
            username: username
        },
        success: function (data) {
            if (strEquals(data.code,200)) {
                swal({
                    title: '提示！',
                    text: '验证码已发送,请注意查收！',
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
                swal(
                    '错误提示',
                    data.msg,
                    'error'
                )
            }
        }
    });
}

function _protocol(e) {
    window.open('https://www.baidu.com/')
}