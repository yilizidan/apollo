package org.apollo.blog.util;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Created by york on 2018/11/8.
 */
@Slf4j
public class AssertUtils extends Assert {

	public static void notNull(@Nullable Object object, String message) {
		if (object == null) {
			throw new ApiException(message);
		}
	}
}