package org.apollo.blog.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.Lists;
import org.apollo.blog.config.dateformatter.DateFormatter;
import org.apollo.blog.config.dateformatter.LocalDateFormatter;
import org.apollo.blog.config.dateformatter.LocalDateTimeFormatter;
import org.apollo.blog.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author huangxin
 * @description mvc配置类
 * @date 2019/4/8 14:09:08
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Resource
	private TokenInterceptor tokenInterceptor;

	/**
	 * 功能描述:接口拦截器
	 *
	 * @author huangxin
	 * @date 2019/4/8 14:14:00
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//添加拦截器，拦截所有请求
		registry.addInterceptor(tokenInterceptor)
				//配置拦截策略
				.addPathPatterns("/api/**")
				//排除配置
				.excludePathPatterns(Lists.newArrayList(
						"/api/login",
						"/api/login",
						"/api/getGifCode",
						"/api/rememberMe",
						"/api/getPublicKey",
						"/api/registered",
						"/api/forgotpassword"
				));
	}

	/**
	 * 跨域请求支持
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedMethods("*").allowedOrigins("*").allowCredentials(true).allowedHeaders("*");
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		// 忽略不显示值为null的字段
		ObjectMapper om = new ObjectMapper();
		//输出json时间格式化（【月日时分秒】双数格式）
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDateTime.class,
				new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
		javaTimeModule.addSerializer(LocalDate.class,
				new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
		javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
		//输入json时间格式化（如果【月日时分秒】格式为双数，在前端为单数的情况下会异常；所以用单数格式）
		javaTimeModule.addDeserializer(LocalDateTime.class,
				new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy/M/d H:m:s")));
		javaTimeModule.addDeserializer(LocalDate.class,
				new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/M/d")));
		javaTimeModule.addDeserializer(LocalTime.class,
				new LocalTimeDeserializer(DateTimeFormatter.ofPattern("H:m:s")));

		om.registerModule(javaTimeModule).registerModule(new ParameterNamesModule());
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
		simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
		om.registerModule(simpleModule);
		// 忽略 transient 修饰的属性
		om.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
		// 解决：去除接口返回String类型带双引号的问题(converters顺序可能导致失效)
		converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
		converters.add(new MappingJackson2HttpMessageConverter(om));
	}

	/**
	 * 输入时间格式化
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatter(new DateFormatter());
		registry.addFormatter(new LocalDateFormatter());
		registry.addFormatter(new LocalDateTimeFormatter());
		registry.addConverterFactory(new UniversalEnumConverterFactory());
		registry.addConverterFactory(new IntegerEnumConverterFactory());
		registry.addConverterFactory(new IntegerStrEnumConverterFactory());
	}

	/**
	 * 发现如果继承了WebMvcConfigurationSupport，则在yml中配置的相关内容会失效。 需要重新指定静态资源
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/resources/")
				.addResourceLocations("classpath:/static/").addResourceLocations("classpath:/public/");
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	/**
	 * 配置servlet处理
	 */
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}