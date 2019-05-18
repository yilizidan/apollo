package org.apollo.blog.config.dateformatter;

import org.springframework.format.Formatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateFormatter implements Formatter<LocalDate> {

	@Override
	public String print(LocalDate localDateTime, Locale locale) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
		return formatter.format(localDateTime);
	}

	@Override
	public LocalDate parse(String text, Locale locale) {
		if (text.indexOf("-") > -1) {
			text = text.replaceAll("-", "/");
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
		return LocalDate.parse(text, formatter);
	}
}
