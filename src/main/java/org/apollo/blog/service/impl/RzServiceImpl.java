package org.apollo.blog.service.impl;

import org.apollo.blog.mapper.RzMapper;
import org.apollo.blog.service.RzService;
import org.apollo.blog.entity.Rz;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RzServiceImpl extends ServiceImpl<RzMapper, Rz> implements RzService {

    @Override
    public int lastWeeknop(String time) {
        return baseMapper.lastWeeknop(time);
    }

    @Override
    public int lastWeekpt(String time) {
        return baseMapper.lastWeekpt(time);
    }
}