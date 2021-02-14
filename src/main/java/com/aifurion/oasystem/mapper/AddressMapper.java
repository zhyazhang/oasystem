package com.aifurion.oasystem.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 11:15
 */

@Mapper
public interface AddressMapper {

    	List<Map<String, Object>> allDirector(@Param("userId") Long userId, @Param("pinyin") String pinyin, @Param("outtype") String outtype, @Param("baseKey") String baseKey);


}
