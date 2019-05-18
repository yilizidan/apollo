package org.apollo.blog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 异常信息枚举
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ResponseCode {

    //**********  异常  ************
    SERVER_ERROR_CODE(-1, "系统异常！"),
    PARAM_ERROR_CODE(-12, "参数错误！"),
    NO_SUCH_INTERFACE(-18, "非法的接口调用！"),
    CODE_800001(901, "未知错误！"),
    CODE_800010(902, "未知的方法！"),
    OPERATION_DATABASE_EXCEPTION(903, "操作数据库异常"),
    SUEECESS(0, "成功！"),
    NOSUCH_DATA_ERROR(-2, "未查到数据"),
    REQUEST_ERROR(500, "请求异常"),
    EXISTS_ERROR(501, "已存在该账号，请重新注册账号！"),
    USERPWD_ERROR(502, "用户或密码错误！"),


    //**********  异常定义 100  ************

    NO_LOGIN(403, "登录认证失效，请重新登录！"),
    RuntimeException(100, "运行时异常"),
    NullPointerException(101, "空指针异常"),
    ClassCastException(102, "类型转换异常"),
    IOException(103, "IO异常"),
    NoSuchMethodException(104, "未知方法异常"),
    IndexOutOfBoundsException(105, "数组越界异常"),
    Exception_400(106, "400错误"),
    Exception_405(107, "405错误"),
    Exception_406(108, "406错误"),
    Exception_500(109, "500错误"),
    StackOverflowError(110, "栈溢出"),
    IllegalStateException(111, "无效状态异常"),
    DuplicateKeyException(112, "主键重复");


    //结果编码
    private int code;
    //结果信息
    private String msg;
}
