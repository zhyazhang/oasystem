package com.aifurion.oasystem.service;

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 11:02
 */

public interface IndexService {

    boolean initIndex(HttpServletRequest request, Model model);


    void initContent(HttpServletRequest request, Model model);




}
