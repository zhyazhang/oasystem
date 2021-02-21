package com.aifurion.oasystem.service;

import com.aifurion.oasystem.common.formVaild.ResultVO;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.task.Tasklist;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 20:51
 */
public interface TypeService {

    void checkForm(HttpServletRequest request, SystemTypeList menu, ResultVO resultVO);

    Iterable<SystemTypeList> findAll();

    List<SystemTypeList> findByTypeNameLikeOrTypeModelLike(String name, String string);

    SystemTypeList findById(Long id);

    void deleteById(Long id);

    SystemTypeList findOne(Long id);







}
