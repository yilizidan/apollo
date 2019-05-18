package org.apollo.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apollo.blog.entity.Role;
import org.apollo.blog.mapper.RoleMapper;
import org.apollo.blog.models.role.SaveRoleFO;
import org.apollo.blog.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apollo.blog.util.StringUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色
 */
@Service
@Transactional
@Slf4j
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public List<Role> findRoleByUsername(String username) {
        return baseMapper.findRoleByUsername(username);
    }

    @Override
    public List<Role> findRoleByName(String name) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(Role::getName, name);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public Role selectRoleById(long id) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    @Cacheable(value = "rolelist", unless = "#result==null")
    public List<Role> selectRoleAll() {
        System.out.println("未進入緩存！------------------》");
        return baseMapper.selectList(new QueryWrapper<>());
    }

    @Override
    @CacheEvict(value = "rolelist", allEntries = true)
    public void insertRole(SaveRoleFO saveRoleFO) {
        if (StringUtil.strIsEmpty(saveRoleFO.getName())) {
            throw new ApiException("角色名称不能为空！");
        }
        try {
            List<Role> list = ((RoleService) AopContext.currentProxy()).findRoleByName(saveRoleFO.getName());
            if (list != null && list.size() > 0) {
                if (saveRoleFO.getId() > 0) {
                    list.forEach(e -> {
                        if (e.getId() != saveRoleFO.getId()) {
                            throw new ApiException("角色名称重复！");
                        }
                    });
                } else {
                    throw new ApiException("角色名称重复！");
                }
            }
            if (saveRoleFO.getId() == 0) {
                baseMapper.insert(Role.builder()
                        .name(saveRoleFO.getName())
                        .description(saveRoleFO.getDescription())
                        .weights(0L)
                        .edit(0)
                        .build());
            } else {
                baseMapper.updateById(Role.builder()
                        .id(saveRoleFO.getId())
                        .name(saveRoleFO.getName())
                        .description(saveRoleFO.getDescription())
                        .build());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiException("添加失败！");
        }
    }

    @Override
    @CacheEvict(value = "rolelist", allEntries = true)
    public Object batchInsert(List<Role> roles) {
        if (roles == null || roles.size() == 0) {
            return 0;
        }
        roles.forEach(e -> {
            baseMapper.insert(e);
        });
        return roles.size();
    }

    @Override
    @CacheEvict(value = "rolelist", allEntries = true)
    public Object batchDelete(List<Role> roles) {
        if (roles == null || roles.size() == 0) {
            return 0;
        }
        List<Long> ids = roles.stream().map(e -> {
            return e.getId();
        }).collect(Collectors.toList());
        //Arrays.asList(18, 19, 20)
        return baseMapper.deleteBatchIds(ids);
    }

    @Override
    @CacheEvict(value = "rolelist", allEntries = true)
    public Object updateRole(Role role) {
        return baseMapper.updateById(role);
    }

    @Override
    @CacheEvict(value = "rolelist", allEntries = true)
    public Object deleteRole(long id) {
        return baseMapper.deleteById(id);
    }
}
