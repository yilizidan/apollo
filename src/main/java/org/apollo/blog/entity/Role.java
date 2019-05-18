package org.apollo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apollo.blog.common.BaseEntity;
import org.springframework.beans.factory.annotation.Value;

@TableName("t_role")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    private String name;

    private String description;

    @Value("${weights:0}")
    private Long weights;

    @Value("${edit:0}")
    private int edit;
}
