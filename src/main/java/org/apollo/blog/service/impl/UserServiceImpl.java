package org.apollo.blog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apollo.blog.service.UserWeightsService;
import org.apollo.blog.service.util.PersonCacheDataUtil;
import org.apollo.blog.entity.Role;
import org.apollo.blog.entity.User;
import org.apollo.blog.mapper.UserMapper;
import org.apollo.blog.models.personal.PersonalInfoVO;
import org.apollo.blog.models.personal.RoleListVO;
import org.apollo.blog.service.UserService;
import org.apollo.blog.util.BeanCopierUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

	@Resource
	private PersonCacheDataUtil personCacheDataUtil;
	@Resource
	private UserWeightsService userWeightsService;

	@Override
	public String getUserImage(Long userid) {
		return personCacheDataUtil.getPersonPic(userid);
	}

	@Override
	public void updateUserImage(Long userid, String picSrc) {
		personCacheDataUtil.uptPersonPic(userid, picSrc);
	}

	@Override
	public PersonalInfoVO getUserInfo(Long userid) {
		List<Role> roles = userWeightsService.findRoleByUserID(userid);
		List<RoleListVO> rolelist = roles.stream().map(e -> {
			RoleListVO roleListVO = new RoleListVO();
			BeanCopierUtils.copyProperties(e, roleListVO);
			return roleListVO;
		}).collect(Collectors.toList());
		JSONArray jsonArray = userWeightsService.getPersonalNodeTree(userid);
		User user = baseMapper.selectById(userid);
		return PersonalInfoVO.builder()
				.id(user.getId())
				.username(user.getUsername())
				.description(user.getDescription())
				.nickname(user.getNickname())
				.address(user.getAddress())
				.rolelist(rolelist)
				.nodedata(jsonArray)
				.srcBase(getUserImage(userid))
				.build();
	}

	@Override
	@Cacheable(value = "managerlist", sync = true)
	public List<User> findAllUser() {
		System.out.println("findAllUser 未進入緩存！------------------》findAllUser");
		return baseMapper.selectList(new QueryWrapper<>());
	}

	@Override
	public List<User> seachAllUser(String... columns) {
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.select(columns);
		return baseMapper.selectList(queryWrapper);
	}

	@Override
	public User findByUsername(String username) {
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("username", username);
		return baseMapper.selectOne(queryWrapper);
	}

	@Override
	@CacheEvict(value = "managerlist", allEntries = true)
	public Object insert(User user) {
		return baseMapper.insert(user);
	}

	@Override
	@CacheEvict(value = "managerlist", allEntries = true)
	public Object update(User user, QueryWrapper<User> queryWrapper) {
		return baseMapper.update(user, queryWrapper);
	}

	@Override
	@CacheEvict(value = "managerlist", allEntries = true)
	public Object updateByid(User user) {
		return baseMapper.updateById(user);
	}

	@Override
	@CacheEvict(value = "managerlist", allEntries = true)
	public Object delete(User user) {
		return baseMapper.deleteById(user.getId());
	}
}
