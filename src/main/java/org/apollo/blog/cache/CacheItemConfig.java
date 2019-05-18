package org.apollo.blog.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by jiangmin on 2018/4/7.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CacheItemConfig implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5937912220527802665L;
	/**
     * 缓存容器名称
     */
    private String name;
    /**
     * 缓存失效时间
     */
    private long expiryTimeSecond;
    /**
     * 当缓存存活时间达到低于此值时，主动刷新缓存
     */
    private Long preLoadTimeSecond;
}
