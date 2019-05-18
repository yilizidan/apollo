package org.apollo.blog.common;

import com.baomidou.mybatisplus.extension.api.IErrorCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "返回响应数据")
public class ApiResult<T> {
    private Long timestamp = System.currentTimeMillis();
    /**
     * 消息CODE
     */
    @ApiModelProperty(value = "返回消息CODE", required = true)
    private Integer code;
    /**
     * 消息
     */
    @ApiModelProperty(value = "返回消息描述")
    private String msg;
    /**
     * 返回数据
     */
    @ApiModelProperty(value = "返回结果集")
    private T resultData;

    /**
     * 操作成功
     */
    public static final ApiResult<Object> SUCCESS = new ApiResult<>(MsgCode.SUCCESS);

    public ApiResult(final Integer code, final String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResult(final Integer code, final String msg, final T data) {
        this.code = code;
        this.msg = msg;
        this.resultData = data;
    }

    public ApiResult(final IErrorCode error) {
        this.code = Integer.parseInt(error.getCode());
        this.msg = error.getMsg();
    }

    public ApiResult(final IErrorCode error, final T data) {
        this(error);
        this.resultData = data;
    }

    /**
     * 操作成功并带数据
     */
    public static ApiResult<Object> sucess() {
        return new ApiResult<>(MsgCode.SUCCESS);

    }

    /**
     * 操作成功并带数据
     */
    public static <T> ApiResult<T> sucess(T resultData) {
        return new ApiResult<>(MsgCode.SUCCESS, resultData);

    }

    public static ApiResult<Object> fail(String msg) {
        ApiResult<Object> fail = new ApiResult<>(MsgCode.FAIL);
        fail.setMsg(msg);
        return fail;
    }

    public static ApiResult<Object> fail(IErrorCode msgCode) {
        return new ApiResult<>(msgCode);
    }
}
