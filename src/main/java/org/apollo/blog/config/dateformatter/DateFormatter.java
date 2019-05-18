package org.apollo.blog.config.dateformatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Slf4j
public class DateFormatter implements Formatter<Date> {

	@Override
	public Date parse(String text, Locale locale) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d H:m:s");
		if (text.indexOf("-") > -1) {
			text = text.replaceAll("-", "/");
		}
		return sdf.parse(text);
	}

	@Override
	public String print(Date object, Locale locale) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d H:m:s");
		return sdf.format(object);
	}
}
