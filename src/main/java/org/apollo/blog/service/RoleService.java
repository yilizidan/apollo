package org.apollo.blog.service;

import org.apollo.blog.models.role.SaveRoleFO;
import org.apollo.blog.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RoleService extends IService<Role> {
    /**
     * 根据用户名查找其角色
     */
    List<Role> findRoleByUsername(String username);

    List<Role> findRoleByName(String name);

    Role selectRoleById(long id);

    List<Role> selectRoleAll();

    void insertRole(SaveRoleFO saveRoleFO);

    Object batchInsert(List<Role> roles);

    Object batchDelete(List<Role> roles);

    Object updateRole(Role role);

    Object deleteRole(long id);
}
