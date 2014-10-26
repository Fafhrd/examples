package com.greenbird.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

@Provider
public class DateParamConverterProvider implements ParamConverterProvider {

	@Override
	public  ParamConverter<Date> getConverter(Class type, Type genericType, Annotation[] annotations) {
		if (type.equals(Date.class)) {
			return (ParamConverter<Date>) new DateTimeParamConverter();
		} else {
			return null;
		}

	}

	private static class DateTimeParamConverter implements ParamConverter<Date> {
		@Override
		public Date fromString(String value) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return dateFormat.parse(value);
			} catch (ParseException e) {
				throw new IllegalArgumentException();
			}
		}

		@Override
		public String toString(Date value) {
			return value.toString();
		}

	}
}