package org.apollo.blog.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据通用字段
 *
 * @author penwei
 * @date 2018/8/24 12:10:24
 */
@Data
@NoArgsConstructor
public abstract class BaseEntity implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 数据创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private Long gmtCreate;
	/**
	 * 数据修改时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Long gmtModified;
	/**
	 * 数据标记是否已删除 false:正常 true:已删除
	 */
	@TableLogic
	@TableField(fill = FieldFill.INSERT)
	private Boolean gmtRemove;
	/**
	 * 版本号
	 */
	@Version
	@TableField(fill = FieldFill.INSERT)
	private Integer gmtVersion;

}
