package org.apollo.blog.models.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 黄欣
 * @description 角色封装
 * @date 2019.03.27 15:03:27
 */
@Data
@ApiModel(description = "角色封装")
public class SaveRoleFO {
    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色id", required = false)
    private Long id;

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称", required = true)
    @NotNull(message = "角色名称不能为空")
    private String name;

    /**
     * 角色描述
     */
    @ApiModelProperty(value = "角色描述", required = false)
    private String description;
}
