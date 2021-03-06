package org.apollo.blog.config;

import com.baomidou.mybatisplus.core.enums.IEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * IEnum<Integer>枚举类型转换器：通过value值参数转为枚举类型
 * @author penwei
 *
 */
public class IntegerStrEnumConverterFactory implements ConverterFactory<String, IEnum<Integer>> {
 
    @SuppressWarnings("rawtypes")
	private static final Map<Class, Converter> converterMap = new WeakHashMap<>();
 
    @Override
    public <T extends IEnum<Integer>> Converter<String, T> getConverter(Class<T> targetType) {
        @SuppressWarnings("unchecked")
        Converter<String, T> result = converterMap.get(targetType);
        if(result == null) {
            result = new IntegerStrToEnum<T>(targetType);
            converterMap.put(targetType, result);
        }
        return result;
    }
 
    class IntegerStrToEnum<T extends IEnum<Integer>> implements Converter<String, T> {
        @SuppressWarnings("unused")
		private final Class<T> enumType;
        private Map<String, T> enumMap = new HashMap<>();
 
        public IntegerStrToEnum(Class<T> enumType) {
            this.enumType = enumType;
            T[] enums = enumType.getEnumConstants();
            for(T e : enums) {
                enumMap.put(e.getValue() + "", e);
            }
        }
 
 
        @Override
        public T convert(String source) {
            T result = enumMap.get(source);
            if(result == null) {
                throw new IllegalArgumentException("No element matches " + source);
            }
            return result;
        }
    }
}
 

