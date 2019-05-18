package org.apollo.blog.config;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * Author: penwei
 * Create: 2018/8/25 13:11
 */
@Component
@Slf4j
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Long time = System.currentTimeMillis();
        setFieldValByName("gmtCreate", time, metaObject);
        setFieldValByName("gmtModified", time, metaObject);
        setFieldValByName("gmtRemove", false, metaObject);
        setFieldValByName("gmtVersion", 1, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName("gmtModified", System.currentTimeMillis(), metaObject);
    }
}