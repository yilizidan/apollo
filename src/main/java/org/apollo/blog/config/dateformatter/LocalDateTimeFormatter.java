package org.apollo.blog.config.dateformatter;

import org.springframework.format.Formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {

	@Override
	public String print(LocalDateTime localDateTime, Locale locale) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d H:m:s");
		return formatter.format(localDateTime);
	}

	@Override
	public LocalDateTime parse(String text, Locale locale) {
		if (text.indexOf("-") > -1) {
			text = text.replaceAll("-", "/");
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d H:m:s");
		return LocalDateTime.parse(text, formatter);
	}
}
