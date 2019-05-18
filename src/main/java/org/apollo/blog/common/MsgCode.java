package org.apollo.blog.common;

import com.baomidou.mybatisplus.extension.api.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MsgCode implements IErrorCode {
    SUCCESS("200", "成功"),
    FAIL("400","业务逻辑异常"),
    UNAUTHORIZE("401","未授权操作"),
    UNAUTHENTICATE("402","未登录认证"),
    NOT_FOUND("404","接口不存在"),
    PARAMETER_EXCEPTION("406","参数错误，请检查后重新输入"),
    ACCOUNT_DISABLED("405","账号已被禁用"),
    ERROR("500", "发生了未知的小错误，我们会尽快修复的!");

    private String code;
    private String msg;
}