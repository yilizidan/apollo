package org.apollo.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apollo.blog.entity.User;
import org.apollo.blog.models.personal.PersonalInfoVO;

import java.util.List;

public interface UserService extends IService<User> {

    /**
     * 功能描述:添加人员缓存信息
     *
     * @author 黄欣
     * @date 2019.04.09 20:06:00
     */
    String getUserImage(Long userid);

    /**
     * 功能描述:修改人员缓存信息
     *
     * @author 黄欣
     * @date 2019.04.09 20:06:00
     */
    void updateUserImage(Long userid, String picSrc);

    /**
     * 功能描述:获取个人信息
     *
     * @author 黄欣
     * @date 2019.04.09 20:07:00
     */
    PersonalInfoVO getUserInfo(Long userid);

    List<User> findAllUser();

    List<User> seachAllUser(String... columns);

    User findByUsername(String username);

    Object insert(User user);

    Object update(User user, QueryWrapper<User> queryWrapper);

    Object updateByid(User user);

    Object delete(User user);
}
