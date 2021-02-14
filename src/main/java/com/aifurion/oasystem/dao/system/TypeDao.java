package com.aifurion.oasystem.dao.system;

import com.aifurion.oasystem.entity.system.SystemTypeList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 12:10
 */

@Repository
public interface TypeDao extends PagingAndSortingRepository<SystemTypeList, Long> {
    //根据模块名和名称查找唯一对象
	SystemTypeList findByTypeModelAndTypeName(String typeModel,String typeName);

	//根据模块名查找到类型集合
	List<SystemTypeList> findByTypeModel(String typeModel);

	List<SystemTypeList> findByTypeNameLikeOrTypeModelLike(String name,String name2);


	@Query("select type.typeName from SystemTypeList type where type.typeId=:id")
	String findname(@Param("id")Long id);

}
