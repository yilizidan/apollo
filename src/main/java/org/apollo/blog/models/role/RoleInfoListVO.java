package org.apollo.blog.models.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apollo.blog.models.personal.RoleListVO;

/**
 * @author 黄欣
 * @description 角色列表信息封装
 * @date 2019.03.27 14:41:27
 */
@ApiModel(description = "角色列表信息封装")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleInfoListVO extends RoleListVO {
    /**
     * 是否能够删除
     */
    @ApiModelProperty(value = "是否能够删除", required = true)
    private int del;

}
