package org.apollo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apollo.blog.common.BaseEntity;

/**
 * @author 黄欣
 * @description 用户节点和角色权重
 * @date 2019.04.13 14:54:13
 */
@TableName("t_user_weights")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserWeights extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long userId;

    /**
     * 节点权重
     */
    private Long nodeWeights;

    /**
     * 角色权重
     */
    private Long roleId;
}
