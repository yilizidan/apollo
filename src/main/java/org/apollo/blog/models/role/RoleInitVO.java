package org.apollo.blog.models.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apollo.blog.models.personal.RoleListVO;

import java.util.List;

/**
 * @author 黄欣
 * @description 角色树封装
 * @date 2019.03.27 14:46:27
 */
@ApiModel(description = "角色树封装")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoleInitVO {

    @ApiModelProperty(value = "角色列表数据集", required = true)
    List<RoleInfoListVO> roleData;

    @ApiModelProperty(value = "当前登陆人角色列表数据集", required = true)
    List<RoleListVO> qxData;
}
