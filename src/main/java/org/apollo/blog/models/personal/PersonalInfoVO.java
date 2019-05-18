package org.apollo.blog.models.personal;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apollo.blog.config.LongJsonDeserializer;
import org.apollo.blog.config.LongJsonSerializer;

import java.util.List;

/**
 * 个人信息
 */
@ApiModel(description = "个人信息")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PersonalInfoVO {
    /**
     * 用户Id
     */
    @ApiModelProperty(value = "用户Id", required = true)
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    /**
     * 用户描述
     */
    @ApiModelProperty(value = "用户描述", required = true)
    private String description;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称", required = true)
    private String nickname;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址", required = true)
    private String address;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像", required = true)
    private String srcBase;

    /**
     * 角色列表
     */
    @ApiModelProperty(value = "角色列表", required = true)
    private List<RoleListVO> rolelist;

    /**
     * 节点数据
     */
    @ApiModelProperty(value = "节点数据", required = true)
    private JSONArray nodedata;

}
