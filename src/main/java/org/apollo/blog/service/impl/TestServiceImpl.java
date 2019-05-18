package org.apollo.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apollo.blog.entity.Node;
import org.apollo.blog.entity.Test;
import org.apollo.blog.mapper.NodeMapper;
import org.apollo.blog.mapper.TestMapper;
import org.apollo.blog.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService {

    /**
     * 保存数据
     *
     * @param name    名称
     * @param integer integer
     * @return String
     */
    @Override
    public Long getData(String name, Integer integer) {
        Test test = Test.builder().name(name).uAge(integer).build();
        baseMapper.insert(test);
        return test.getId();
    }

    //select
    //update
    //delete
}
