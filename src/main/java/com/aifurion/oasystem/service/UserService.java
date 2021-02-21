package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.user.Position;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/23 9:54
 */
public interface UserService {

    void getUserEdit(Long userid, Model model);

    void userPaging(Model model, int page, int size, String usersearch);

    void userManage(Model model, int page, int size);


    void addUser(User user, Long deptid, Long positionid, Long roleid, boolean isbackpassword, Model model);

    void deleteUser(Long id,Model model);

    boolean useronlyname(String username);

    List<Position> selectdept( Long deptid);

    Page<User> findMyEmployUser(int page, String baseKey, long parentid);

    User findOne(Long id);

    Page<User> findByFatherId(Long id, Pageable pageable);


    User findUserByName(String name);

    Long findPkId(Long taskId, Long userId);





}
