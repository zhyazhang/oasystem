package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.process.AubUser;
import com.aifurion.oasystem.entity.process.ProcessList;
import com.aifurion.oasystem.entity.process.Regular;
import com.aifurion.oasystem.entity.process.Reviewed;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.data.domain.Page;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:21
 */
public interface ProcessService {

    void writefile(HttpServletResponse response, File file);

    void user(int page, int size, Model model);

    Page<AubUser> index(User user, int page, int size, String val, org.springframework.ui.Model model);

    List<Map<String, Object>> index2(Page<AubUser> page, User user);

    List<Map<String, Object>> index4(ProcessList process);

    Map<String, Object> index3(String name, User user, String typename, ProcessList process);

    void index6(org.springframework.ui.Model model, Long id, int page, int size);

    void index5(ProcessList pro, String val, User lu, MultipartFile filePath, String name);

    void index8(ProcessList pro, String val, User lu, String name);

    void save(Long proid, User u, Reviewed reviewed, ProcessList pro, User u2);

    void index7(User reuser, ProcessList pro);





}
