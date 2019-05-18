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

@TableName("t_node")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Node extends BaseEntity {

    @TableId(value = "nodeid", type = IdType.ID_WORKER)
    private Long nodeid;

    /**
     * 父节点id
     */
    private Long pnodeid;

    /**
     * 权重 默认值 0
     */
    @Value("${weights:0}")
    private Integer weights;

    /**
     * 节点名称
     */
    private String nodename;

    /**
     * 节点类型
     */
    private String nodetype;

    /**
     * 节点描述
     */
    private String descripte;

    /**
     * 0 删除
     */
    private Integer isdelete;

    /**
     * 排序号
     */
    private Integer pxh;

    /**
     * 节点图标
     */
    private String nodeicon;

    /**
     * 节点地址
     */
    private String nodeurl;
}
