package org.apollo.blog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apollo.blog.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    List<Role> findRoleByUsername(String username);
}
