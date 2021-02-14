package com.aifurion.oasystem.common.formVaild;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 20:57
 */
public class MapToList<T> {

	T data;
//	Map强转成List
	public List<Object> mapToList(T data){
		Map<Object, Object> map=(Map<Object, Object>) data;
		Collection<Object> collects=map.values();
		final int size=collects.size();
		List<Object> list=new ArrayList<Object>(collects);
		return list;
	}

}
