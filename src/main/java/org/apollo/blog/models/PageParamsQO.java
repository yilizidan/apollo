package org.apollo.blog.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 分页查询参数
 *
 * @author: liuqianshun
 * @date: Created in 2018-08-24
 */
@Data
public class PageParamsQO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 当前页
	 */
	@ApiModelProperty(value = "当前页码",required = true,example="1")
	@NotNull
	public Integer current = 1;
	/**
	 * 每页显示条数
	 */
	@ApiModelProperty(value = "每页条目数",required = true,example="30")
	@NotNull
	public Integer size = 30;

	@ApiModelProperty(value = "",hidden = true)
    public int getOffset() {
        return (current-1)*size;
    }


}
