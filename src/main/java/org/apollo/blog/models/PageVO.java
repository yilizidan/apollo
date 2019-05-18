package org.apollo.blog.models;

import java.io.Serializable;
import java.util.Collection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询返回信息
 *
 * @author: liuqianshun
 * @date: Created in 2018-08-26
 */
@ApiModel(description= "数据分页查询")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 总数
	 */
	@ApiModelProperty(value = "总条数")
	private long total;
	/**
	 * 总页数
	 */
	@ApiModelProperty(value = "总页数")
	private long pages;
	/**
	 * 每页显示数
	 */
	@ApiModelProperty(value = "每页条数(默认30)",example="30")
	private long size = 30;
	/**
	 * 当前页
	 */
	@ApiModelProperty(value = "当前页码（默认1）",example="1")
	private long current = 1;

	/**
	 * 返回数据
	 */
	@ApiModelProperty(value = "当页返回数据")
	private Collection<T> records;

}
