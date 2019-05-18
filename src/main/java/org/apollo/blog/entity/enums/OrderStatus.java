package org.apollo.blog.entity.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;

/**
 * Created by york on 2019/2/27.
 */
@ToString
public enum OrderStatus implements IEnum<Integer> {
	NOTPAY(0, "新建订单"),
	PAYED(10, "已支付"),
	WAIT_REFUND(20, "待退款"),
	REFUNDED(30, "已退款"),
	CLOSED(50, "已关闭");

	private Integer value;

	private String desc;


	OrderStatus(Integer value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	@JsonValue
	@Override
	public Integer getValue() {
		return value;
	}
}
