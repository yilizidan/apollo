package org.apollo.blog.models.personal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apollo.blog.config.LongJsonDeserializer;
import org.apollo.blog.config.LongJsonSerializer;

/**
 * @author 黄欣
 * @description 角色列表封装
 * @date 2019.03.27 11:41:27
 */
@ApiModel(description = "角色列表封装")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleListVO {

    /**
     * 角色Id
     */
    @ApiModelProperty(value = "角色Id", required = true)
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称", required = true)
    private String name;

    /**
     * 角色描述
     */
    @ApiModelProperty(value = "角色描述", required = true)
    private String description;

    /**
     * 角色权重
     */
    @ApiModelProperty(value = "角色权重", required = true)
    private Long weights;

    /**
     * 是否能够编辑
     */
    @ApiModelProperty(value = "是否能够编辑", required = true)
    private int edit;
}
