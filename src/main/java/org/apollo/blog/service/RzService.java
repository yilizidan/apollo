package org.apollo.blog.service;

import org.apollo.blog.entity.Rz;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RzService extends IService<Rz> {
	int lastWeeknop(String time);

	int lastWeekpt(String time);
}