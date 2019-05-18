package org.apollo.blog.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apollo.blog.entity.Role;
import org.apollo.blog.entity.UserWeights;

import java.util.List;

/**
 * @author 黄欣
 * @description 服务类
 * @date 2019.04.13 15:01:13
 */
public interface UserWeightsService extends IService<UserWeights> {

    JSONArray getPersonalNodeTree(long id);

    List<Role> findRoleByUserID(long id);

    List<UserWeights> seachUserInfo();

}
