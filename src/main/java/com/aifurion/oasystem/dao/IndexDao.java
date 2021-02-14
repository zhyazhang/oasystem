package com.aifurion.oasystem.dao;

import com.aifurion.oasystem.entity.system.SystemMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 11:26
 */

@Repository
public interface IndexDao extends JpaRepository<SystemMenu, Long> {

    // 查找一级菜单栏,且show是等于true
	List<SystemMenu> findByParentIdAndShowOrderBySortId(Long parentId, Boolean boo);

	// 查找二级菜单栏，且show是等于true
	List<SystemMenu> findByParentIdNotAndShowOrderBySortId(Long parentId, Boolean boo);

	// 查找一级菜单栏,
	List<SystemMenu> findByParentIdOrderBySortId(Long parentId);

	// 查找二级菜单栏
	List<SystemMenu> findByParentIdNotOrderBySortId(Long parentId);

	// 1、上移下移按钮先改变其他的排序值
	@Query("update SystemMenu menu set menu.sortId=(:sortId) where menu.parentId = :parentId and menu.sortId=(:sortId - :arithNum)")
	@Modifying
	int changeSortOtherId(@Param("sortId") Integer sortId, @Param("arithNum") Integer arithNum,
                     @Param("parentId") Long parentId);

	// 2、上移下移按钮先改变自己的排序值
	@Query("update SystemMenu menu set menu.sortId=(:sortId -:arithNum) where menu.menuId= :menuId")
	@Modifying
	int changeSortSelfId(@Param("sortId") Integer sortId, @Param("arithNum") Integer arithNum,
			@Param("menuId") Long menuId);

	@Query("update SystemMenu menu set menu.parentId=999 where menu.menuId= :menuId")
	@Modifying
	int deleteThis(@Param("menuId") Long menuId);

	List<SystemMenu> findByMenuNameLike(String name);

	@Query("select sy from SystemMenu as sy ")
	List<SystemMenu> findAll();

}
