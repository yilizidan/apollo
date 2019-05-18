package org.apollo.blog.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apollo.blog.entity.enums.PayMode;
import org.apollo.blog.common.BaseEntity;
import org.apollo.blog.entity.enums.OrderStatus;

/**
 * Created by york on 2019/2/27.
 */
@Data
@TableName("t_order")
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity {
    @TableId
    private Long id;

    private Long userId;

    private Long goodsId;

    private OrderStatus orderStatus;

    private PayMode payMode;

    private long price;

    private int currency;

    private long payPrice;

    private int payCurrency;
}
