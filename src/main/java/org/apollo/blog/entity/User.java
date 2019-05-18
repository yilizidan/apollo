package org.apollo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apollo.blog.common.BaseEntity;

import javax.validation.constraints.NotBlank;

@TableName("t_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.ID_WORKER)
	private Long id;
	/**
	 * 用户名
	 */
	@NotBlank
	private String username;
	/**
	 * 登录密码
	 */
	@NotBlank
	private String password;
	/**
	 * 盐，用于密码加密
	 */
	@NotBlank
	private String salt;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 地址
	 */
	private String address;

	/**
	 * 禁止登录
	 */
	@TableField("is_disabled")
	private boolean disabled;

	/**
	 * 密码盐
	 */
	public String getCredentialsSalt() {
		return this.username + this.salt;
	}

}
