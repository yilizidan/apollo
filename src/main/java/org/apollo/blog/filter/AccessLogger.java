package org.apollo.blog.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Http请求参数及响应
 * @author penwei
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessLogger implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 218918758419354405L;
	/**
	 * 
	 */
	private String traceId;
	/**
	 * 用户Id
	 */
	private Long userId;
	/**
	 * 客户端请求IP
	 */
    private String ip;
    /**
	 * 客户端请求URL
	 */
    private String url;
    /**
   	 * 客户端请求类型，可以判断是否ajax
   	 */
    private String type;
    /**
	 * 客户端请求方法
	 */
    private String method;
    /**
	 * 客户端请求参数
	 */
    private String paramData;
    /**
	 * 客户端请求sessionId
	 */
    private String sessionId;
    /**
	 * 客户端请求时间戳
	 */
    private Long accessTime;
    /**
	 * 请求返回时间
	 */
    private String returnTime;
    /**
	 * 请求返回数据
	 */
    private String returnData;
    /**
	 * 请求状态CODE
	 */
    private String httpStatusCode;
    /**
	 * 请求内容类型
	 */
    private String httpContentType;
    /**
	 * 请求耗时
	 */
    private Integer useTime;
}
