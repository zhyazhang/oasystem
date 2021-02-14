package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.system.SystemStatusList;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/19 10:41
 */
public interface StatusService {


    Iterable<SystemStatusList> findAll();

    List<SystemStatusList> findByStatusNameLikeOrStatusModelLike(String name, String name2);


    SystemStatusList findOne(Long id);


    void deleteStatus(Long id);

    void vaildForm(HttpServletRequest req,SystemStatusList menu, BindingResult br);

    SystemStatusList findByStatusModelAndStatusName(String statusModel, String statusName);




}
