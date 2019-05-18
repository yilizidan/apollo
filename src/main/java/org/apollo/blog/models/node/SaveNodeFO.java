package org.apollo.blog.models.node;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author 黄欣
 * @description 节点封装
 * @date 2019.03.27 15:03:27
 */
@Data
@ApiModel(description = "节点封装")
public class SaveNodeFO {
	/**
	 * 节点id
	 */
	@ApiModelProperty(value = "节点id", required = false)
	private Long nodeid;

	@ApiModelProperty(value = "父节点id", required = true)
	@NotBlank(message = "父节点id不能为空")
	private Long pnodeid;

	/**
	 * 节点名称
	 */
	@ApiModelProperty(value = "节点名称", required = true)
	@NotNull(message = "节点名称不能为空")
	private String nodename;

	/**
	 * 节点编码
	 */
	@ApiModelProperty(value = "节点编码", required = true)
	@NotNull(message = "节点编码不能为空")
	private String nodetype;

	/**
	 * 节点图标
	 */
	@ApiModelProperty(value = "节点图标", required = true)
	@NotNull(message = "节点图标不能为空")
	private String nodeicon;

	/**
	 * 节点关联地址
	 */
	@ApiModelProperty(value = "节点关联地址", required = true)
	@NotNull(message = "节点关联地址不能为空")
	private String nodeurl;

	/**
	 * 节点描述
	 */
	@ApiModelProperty(value = "节点描述", required = false)
	private String descripte;
}
