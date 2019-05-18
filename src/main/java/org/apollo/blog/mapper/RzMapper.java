package org.apollo.blog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apollo.blog.entity.Rz;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface RzMapper extends BaseMapper<Rz> {

	int lastWeeknop(String time);

	int lastWeekpt(String time);
}