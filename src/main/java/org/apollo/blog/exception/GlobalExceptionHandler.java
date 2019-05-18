package org.apollo.blog.exception;

import com.baomidou.mybatisplus.extension.api.IErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apollo.blog.common.ApiResult;
import org.apollo.blog.common.MsgCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * <p>
 * 通用 Api Controller 全局异常处理
 * </p>
 *  extends ResponseEntityExceptionHandler
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 不满足@RequiresGuest注解时抛出的异常信息
     */
    private static final String GUEST_ONLY = "Attempting to perform a guest-only operation";



    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ApiResult<Object> missingParameter(MissingServletRequestParameterException e) {
        log.warn("请求参数缺失",e);
        return ApiResult.fail("请求参数缺失");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ApiResult<Object> missingParameter(HttpMessageNotReadableException e) {
        log.warn("请求参数错误",e);
        return ApiResult.fail("请求参数错误,接口解析参数失败");
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ApiResult<Object> methodNotSupported(HttpRequestMethodNotSupportedException e) {
        return ApiResult.fail("请确认访问接口方法[GET/POST]是否正确");
    }

    @ExceptionHandler(UnauthenticatedException.class)
    @ResponseBody
    public ApiResult<Object> error402(UnauthenticatedException e) {
        String eMsg = e.getMessage();
        if (StringUtils.startsWithIgnoreCase(eMsg,GUEST_ONLY)){
            return ApiResult.fail("只允许游客访问，若您已登录，请先退出登录");
        }else{
            return ApiResult.fail(MsgCode.UNAUTHENTICATE);
        }
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ApiResult<Object> error402(AuthenticationException e) {
        String eMsg = e.getMessage();
        if (StringUtils.isNotBlank(eMsg)){
            return ApiResult.fail(eMsg);
        }else{
            log.error("登录认证异常",e);
            return ApiResult.fail("登录认证异常");
        }
    }


    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public ApiResult<Object> error401(UnauthorizedException e) {
        return ApiResult.fail(MsgCode.UNAUTHORIZE);
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ApiResult<Object> bindException(BindException e) {
        BindingResult bindingResult =  e.getBindingResult();
        List<Object> jsonList = new ArrayList<>();
        bindingResult.getFieldErrors().stream().forEach(fieldError -> {
            Map<String, Object> jsonObject = new HashMap<>(2);
            jsonObject.put("field", fieldError.getField());
            jsonObject.put("msg", fieldError.getDefaultMessage());
            jsonList.add(jsonObject);
        });
        return new ApiResult<Object>(MsgCode.PARAMETER_EXCEPTION,jsonList);
    }


    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public ApiResult<Object> apiException (ApiException e) {
        /*
         * 业务逻辑异常
         */
        IErrorCode errorCode = e.getErrorCode();
        if (null != errorCode) {
            return ApiResult.fail(errorCode);
        }
        return ApiResult.fail(e.getMessage());
    }

    @ExceptionHandler(ShiroException.class)
    @ResponseBody
    public ApiResult<Object> handleShiroException(ShiroException e) {
        log.error("shiro执行出错",e);
        return ApiResult.fail(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ApiResult<Object> handleBadRequest(Exception e) {
        /**
         * 系统内部异常，打印异常栈
         */
        log.error("发现未知异常:"+e.getMessage(), e);
        return new ApiResult<Object>(MsgCode.ERROR);
    }
}
