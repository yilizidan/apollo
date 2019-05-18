package org.apollo.blog.entity.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;

@ToString
public enum Area implements IEnum<Integer> {
    JAPAN(0, "日本"),
    CHINA(1, "中国"),
    OTHER(2, "其他");

    private Integer value;
    private String text;

    @Override
    @JsonValue
    public Integer getValue() {
        return value;
    }

    public static Area valueOf(Integer value) {
        Area[] areas = Area.values();
        for (Area area : areas) {
            if (area.getValue().equals(value)) {
                return area;
            }
        }
        throw new IllegalArgumentException("枚举参数错误");
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    Area(int value, String text) {
        this.value = value;
        this.text = text;
    }

}
