package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.user.Dept;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/19 14:19
 */
public interface DeptService {


    Dept findOne(Long id);

    List<Dept> findAll();

    void deptManage(Model model);

    boolean addDept(Dept dept, String xg, BindingResult br, Model model);

    void changeDept(Long deptId, Model model);


    void readDept(Long deptId, Model model);

    void depTanDpositionChange(Long positionid, Long changedeptid, Long userid, Long deptid, Model model);

    void deletDept(Long deletedeptid);

    void deptManageChange(Long positionid, Long changedeptid, Long oldmanageid, Long newmanageid, Long deptid, Model model);



}
