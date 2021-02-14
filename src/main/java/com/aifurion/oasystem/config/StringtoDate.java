package com.aifurion.oasystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 16:57
 */
@Configuration
public class StringtoDate implements Converter<String, Date> {

	   SimpleDateFormat sdf=new SimpleDateFormat();
	   private List<String> patterns = new ArrayList<>();

		@Override
		public Date convert(String source) {
			patterns.add("yyyy-MM-dd HH:mm:ss");
			patterns.add("yyyy-MM-dd HH:mm");
			   patterns.add("yyyy-MM-dd");
			   patterns.add("HH:mm");
			   patterns.add("yyyy-MM");
			Date date=null;
			for (String p : patterns) {
				try {
					sdf.applyPattern(p);
					date=sdf.parse(source);
					break;
				} catch (ParseException e) {}
			}
			if(date==null){
				throw new IllegalArgumentException("日期格式错误！[" + source + "]");
			}
			return date;
		}

		public void setPatterns(List<String> patterns) {
			this.patterns = patterns;
		}

}