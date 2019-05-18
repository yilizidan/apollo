package org.apollo.blog.entity.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;

/**
 * Created by york on 2019/2/27.
 */
@ToString
public enum PayMode implements IEnum<Integer> {
	GOLD_PAY(1, "虚拟货币支付"),
	WX_PAY(2, "微信支付"),
	ALPAY_PAY(3, "支付宝支付");


	private Integer value;

	private String desc;


	PayMode(Integer value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	@JsonValue
	@Override
	public Integer getValue() {
		return value;
	}
}
