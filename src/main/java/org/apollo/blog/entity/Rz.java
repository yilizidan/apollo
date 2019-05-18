package org.apollo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

@TableName("t_rz")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rz implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "zrid", type = IdType.ID_WORKER)
    private Long zrid;

    private Long userid;

    private Long loginTime;

    private String action;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 标记
     */
    private String remark;

    /**
     * IP
     */
    private String ip;

    /**
     * 登陆类型 1 pc登陆 2 移动端登陆
     */
    @Value("${login_type:1}")
    private Integer loginType;
}
