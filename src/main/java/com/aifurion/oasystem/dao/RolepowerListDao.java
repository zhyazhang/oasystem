package com.aifurion.oasystem.dao;

import com.aifurion.oasystem.entity.role.Rolemenu;
import com.aifurion.oasystem.entity.role.Rolepowerlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/12 11:24
 */

@Repository
public interface RolepowerListDao extends JpaRepository<Rolepowerlist, Long> {
    //找所有的父菜单
    @Query("select new com.aifurion.oasystem.entity.role.Rolemenu(menu.menuId,menu.menuName,menu.menuUrl,menu.show,role.check,menu.parentId,menu.menuIcon,menu.sortId,menu.menuGrade) "
            + "from Rolepowerlist as role,SystemMenu as menu where role.menuId.menuId=menu.menuId "
            + "and menu.parentId=?1 and role.roleId.roleId=?2 order by menu.sortId")
    List<Rolemenu> findByParentAll(Long id, Long roleId);

    //找所有的子菜单
    @Query("select new com.aifurion.oasystem.entity.role.Rolemenu(menu.menuId,menu.menuName,menu.menuUrl,menu.show,role.check,menu.parentId,menu.menuIcon,menu.sortId,menu.menuGrade) "
            + "from Rolepowerlist as role,SystemMenu as menu where role.menuId.menuId=menu.menuId "
            + "and menu.parentId!=?1 and role.roleId.roleId=?2 order by menu.sortId")
    List<Rolemenu> findByParents(Long id, Long roleId);

    @Query("select po from Rolepowerlist as po where po.roleId.roleId=?1 and po.menuId.menuId=?2")
    Rolepowerlist findByRoleIdAndMenuId(Long roleId, Long menuId);

    //找所有可显示的父菜单
    @Query("select new com.aifurion.oasystem.entity.role.Rolemenu(menu.menuId,menu.menuName,menu.menuUrl,menu.show,role.check,menu.parentId,menu.menuIcon,menu.sortId,menu.menuGrade) "
            + "from Rolepowerlist as role,SystemMenu as menu where role.menuId.menuId=menu.menuId "
            + "and menu.parentId=?1 and role.roleId.roleId=?2 and menu.show=?3 and role.check=?4 order by menu.sortId")
    List<Rolemenu> findByParentDisplayAll(Long id, Long roleId, Boolean show, Boolean check);

    //找所有可显示的子菜单
    @Query("select new com.aifurion.oasystem.entity.role.Rolemenu(menu.menuId,menu.menuName,menu.menuUrl,menu.show,role.check,menu.parentId,menu.menuIcon,menu.sortId,menu.menuGrade) "
            + "from Rolepowerlist as role,SystemMenu as menu where role.menuId.menuId=menu.menuId "
            + "and menu.parentId!=?1 and role.roleId.roleId=?2 and menu.show=?3 and role.check=?4 order by menu.sortId")
    List<Rolemenu> findByParentsDisplay(Long id, Long roleId, Boolean show, Boolean check);

    //条件查找父菜单
    @Query("select new com.aifurion.oasystem.entity.role.Rolemenu(menu.menuId,menu.menuName,menu.menuUrl,menu.show,role.check,menu.parentId,menu.menuIcon,menu.sortId,menu.menuGrade) "
            + "from Rolepowerlist as role,SystemMenu as menu where role.menuId.menuId=menu.menuId "
            + "and menu.parentId=?1 and role.roleId.roleId=?2 and menu.show=?3 and role.check=?4 and menu.menuName like %?5% order by menu.sortId")
    List<Rolemenu> findName(Long id, Long roleId, Boolean show, Boolean check, String name);


}