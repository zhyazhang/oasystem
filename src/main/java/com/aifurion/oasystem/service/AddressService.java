package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.note.Director;
import com.aifurion.oasystem.entity.note.DirectorUser;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/17 11:06
 */
public interface AddressService {


    void addressManage(Long userId, Model model, int page, int size);

    void inAddressShow(Model model, Long userId);
    boolean outAddressShow(Model model,Long director,Long userId);

    void changeType(Long did, Long userId, String catalog);

    void changeTypeName(String newName, String oldName, Long userId);

    boolean addAddress(HttpServletRequest req, Long did, HttpSession session, Long userId, Model model);

    void saveAddress(Director director, DirectorUser directorUser,
                     BindingResult br, MultipartFile file, HttpSession session,
                     Model model, Long userId, HttpServletRequest req);

    boolean deleteDirector(Long userId, Model model, Long did);


    void deleteTypeName(String typename, Long userId);

    void addTypeName(String typename, String oldtypename, Long userId,Model model);


    void shareMessage(int page, int size, String baseKey, String catalog, Long duid, Model model, Long userId);

    void myShareMessage(int page, int size, String baseKey, Model model, Long userId);

    List<User> shareOther(Long userId, Long[] directors, Model model, Long sharedirector);

    void modalShare( int page, Model model, int size);

    void modalpaging(int page,Model model, int size, String baseKey);

    void outaddresspaging(int page,Model model, String baseKey, String outtype, String alph, Long userId);


    void inaddresspaging(int page,Model model, int size, String baseKey, String alph);


}
