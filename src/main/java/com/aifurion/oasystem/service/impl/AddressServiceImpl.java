package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.formVaild.BindingResultVOUtil;
import com.aifurion.oasystem.common.formVaild.ResultEnum;
import com.aifurion.oasystem.common.formVaild.ResultVO;
import com.aifurion.oasystem.dao.address.AddressDao;
import com.aifurion.oasystem.dao.address.AddressUserDao;
import com.aifurion.oasystem.dao.note.AttachmentDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.note.Attachment;
import com.aifurion.oasystem.entity.note.Director;
import com.aifurion.oasystem.entity.note.DirectorUser;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.AddressService;
import com.aifurion.oasystem.service.MailService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/17 11:06
 */

@Transactional
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AddressUserDao addressUserDao;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private AttachmentDao attachmentDao;

    @Autowired
    private MailService mailService;


    @Override
    public void addressManage(Long userId, Model model, int page, int size) {

        User user = userDao.findById(userId).get();
        Set<String> catalogs = addressUserDao.findByUser(user);
        Pageable pa = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dept"));
        Page<User> userspage = userDao.findAll(pa);
        List<User> users = userspage.getContent();
        List<DirectorUser> nothandles = addressUserDao.findByUserAndShareuserNotNullAndHandle(user, false);
        model.addAttribute("count", nothandles.size());
        model.addAttribute("catalogs", catalogs);
        model.addAttribute("users", users);
        model.addAttribute("page", userspage);
        model.addAttribute("url", "inaddresspaging");

    }

    @Override
    public void inAddressShow(Model model, Long userId) {

        User user = userDao.findById(userId).get();
        model.addAttribute("user", user);
    }

    @Override
    public boolean outAddressShow(Model model, Long director, Long userId) {

        Director d = addressDao.findById(director).get();
        User user = userDao.findById(userId).get();
        DirectorUser du = addressUserDao.findByDirectorAndUser(d, user);
        if (Objects.isNull(d) || Objects.isNull(du)) {

            return false;
        }
        if (d.getAttachment() != null) {
            model.addAttribute("imgpath", attachmentDao.findById(d.getAttachment()).get().getAttachmentPath());
        } else {
            model.addAttribute("imgpath", "/timg.jpg");
        }
        model.addAttribute("d", d);
        return true;


    }

    @Override
    public void changeType(Long did, Long userId, String catalog) {

        User user = userDao.findById(userId).get();
        Director d = addressDao.findById(did).get();
        DirectorUser du = addressUserDao.findByDirectorAndUser(d, user);
        du.setCatalogName(catalog);
        addressUserDao.save(du);

    }

    @Override
    public void changeTypeName(String newName, String oldName, Long userId) {

        User user = userDao.findById(userId).get();
        List<DirectorUser> dus = addressUserDao.findByCatalogNameAndUser(oldName, user);
        for (DirectorUser directorUser : dus) {
            directorUser.setCatalogName(newName);
        }
        addressUserDao.saveAll(dus);


    }

    @Override
    public boolean addAddress(HttpServletRequest req, Long did, HttpSession session, Long userId, Model model) {

        User user = userDao.findById(userId).get();
        Set<String> calogs = addressUserDao.findByUser(user);
        model.addAttribute("calogs", calogs);
        if (!StringUtils.isEmpty(did)) {
            Director director = addressDao.findById(did).get();
            if (Objects.isNull(director) || !Objects.equals(director.getMyuser().getUserId(), userId)) {
                return false;
            }
            DirectorUser du = addressUserDao.findByDirectorAndUser(director, user);
            model.addAttribute("director", director);
            model.addAttribute("du", du);
            session.setAttribute("did", did);
        }

        return true;
    }


    @Override
    public void saveAddress(Director director, DirectorUser directorUser, BindingResult br, MultipartFile file, HttpSession session, Model model, Long userId, HttpServletRequest req) {

        User user = userDao.findById(userId).get();
        ResultVO res = BindingResultVOUtil.hasErrors(br);
        if (ResultEnum.SUCCESS.getCode().equals(res.getCode())) {
            String pinyin = null;
            try {
                pinyin = PinyinHelper.convertToPinyinString(director.getUserName(), "", PinyinFormat.WITHOUT_TONE);
            } catch (PinyinException e) {
                e.printStackTrace();
            }
            director.setPinyin(pinyin);
            director.setMyuser(user);
            if (!StringUtils.isEmpty(session.getAttribute("did"))) {
                /*修改*/

                Long did = Long.parseLong(String.valueOf(session.getAttribute("did")));
                Director di = addressDao.findById(did).get();
                director.setDirectorId(di.getDirectorId());
                director.setAttachment(di.getAttachment());
                DirectorUser dc = addressUserDao.findByDirectorAndUser(director, user);
                directorUser.setDirectorUserId(dc.getDirectorUserId());
                session.removeAttribute("did");
            }
            //试一下
            if (file.getSize() > 0) {
                Attachment attaid = mailService.upload(file, user);
                attaid.setModel("aoa_bursement");
                Attachment att = attachmentDao.save(attaid);
                /*Attachment att= (Attachment) fileServices.savefile(file, user, null, false);*/
                director.setAttachment(att.getAttachmentId());
            }

            directorUser.setHandle(true);
            directorUser.setDirector(director);
            directorUser.setUser(user);
            addressDao.save(director);
            addressUserDao.save(directorUser);
        }

    }

    @Override
    public boolean deleteDirector(Long userId, Model model, Long did) {

        DirectorUser du = addressUserDao.findById(did).get();
        Director director = du.getDirector();
        List<DirectorUser> dires = addressUserDao.findByDirector(director);
        User user = userDao.findById(userId).get();
        if (!Objects.equals(du.getUser().getUserId(), userId)) {
            return false;
        }
        List<DirectorUser> dus = addressUserDao.findByCatalogNameAndUser(du.getCatalogName(), user);
        if (dus.size() > 1) {
            addressUserDao.delete(du);
            if (dires.size() == 1) {
                addressDao.delete(du.getDirector());

            }
        } else {
            /*当size=1时，说明这是最后一位拥有*/
            du.setDirector(null);
            addressUserDao.save(du);
            if (dires.size() == 1) {
                addressDao.delete(director);
            }

        }

        return true;
    }

    @Override
    public void deleteTypeName(String typename, Long userId) {

        User user = userDao.findById(userId).get();
        List<DirectorUser> dus = addressUserDao.findByCatalogNameAndUser(typename, user);
        for (DirectorUser directorUser : dus) {
            directorUser.setCatalogName(null);
        }
        addressUserDao.saveAll(dus);

    }

    @Override
    public void addTypeName(String typename, String oldtypename, Long userId, Model model) {

        User user = userDao.findById(userId).get();
        if (oldtypename != null) {
            List<DirectorUser> dus = addressUserDao.findByCatalogNameAndUser(oldtypename, user);
            for (DirectorUser directorUser : dus) {
                directorUser.setCatalogName(typename);
            }
            addressUserDao.saveAll(dus);
        } else {
            DirectorUser dc = new DirectorUser(user, typename);
            addressUserDao.save(dc);
        }
        Set<String> catalogs = addressUserDao.findByUser(user);
        model.addAttribute("catalogs", catalogs);

    }

    @Override
    public void shareMessage(int page, int size, String baseKey, String catalog, Long duid, Model model, Long userId) {


        User user = userDao.findById(userId).get();
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "handle"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "sharetime"));
        Sort sort = Sort.by(orders);
        Pageable pa = PageRequest.of(page, size, sort);
        Page<DirectorUser> duspage = addressUserDao.findByShareuser(user, pa);
        if (!StringUtils.isEmpty(duid)) {
            DirectorUser du = addressUserDao.findById(duid).get();
            du.setCatalogName(catalog);
            du.setHandle(true);
            addressUserDao.save(du);
        }
        if (!StringUtils.isEmpty(baseKey)) {
            duspage = addressUserDao.findBaseKey("%" + baseKey + "%", user, pa);
            model.addAttribute("sort", "&baseKey=" + baseKey);
        } else {
            duspage = addressUserDao.findByUserAndShareuserNotNull(user, pa);
        }
        Set<String> catalogs = addressUserDao.findByUser(user);
        model.addAttribute("catalogs", catalogs);
        List<DirectorUser> dus = duspage.getContent();
        model.addAttribute("page", duspage);
        model.addAttribute("dus", dus);
        model.addAttribute("url", "sharemess");

    }

    @Override
    public void myShareMessage(int page, int size, String baseKey, Model model, Long userId) {

        User user = userDao.findById(userId).get();
        Pageable pa = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sharetime"));
        Page<DirectorUser> duspage = addressUserDao.findByShareuser(user, pa);
        if (!StringUtils.isEmpty(baseKey)) {
            duspage = addressUserDao.findBaseKey("%" + baseKey + "%", user, pa);
            model.addAttribute("sort", "&baseKey=" + baseKey);
        } else {
            duspage = addressUserDao.findByShareuser(user, pa);
        }

        List<DirectorUser> dus = duspage.getContent();
        model.addAttribute("page", duspage);
        model.addAttribute("dus", dus);
        model.addAttribute("url", "mesharemess");


    }

    @Override
    public List<User> shareOther(Long userId, Long[] directors, Model model, Long sharedirector) {

        User user = userDao.findById(userId).get();
        Director director = addressDao.findById(sharedirector).get();
        List<User> users = new ArrayList<>();
        List<DirectorUser> dus = new ArrayList<>();
        for (Long aLong : directors) {
            User shareuser = userDao.findById(aLong).get();
            DirectorUser du = addressUserDao.findByDirectorAndUser(director, shareuser);
            if (Objects.isNull(du)) {
                DirectorUser dir = new DirectorUser();
                dir.setUser(shareuser);
                dir.setShareuser(user);
                dir.setDirector(director);
                dus.add(dir);
            } else {
                users.add(shareuser);
            }
        }

        addressUserDao.saveAll(dus);
        return users;


    }

    @Override
    public void modalShare(int page, Model model, int size) {

        Pageable pa = PageRequest.of(0, 10);
        Page<User> userspage = userDao.findAll(pa);
        List<User> users = userspage.getContent();
        model.addAttribute("modalurl", "modalpaging");
        model.addAttribute("modalpage", userspage);
        model.addAttribute("users", users);

    }

    @Override
    public void modalpaging(int page, Model model, int size, String baseKey) {

        Pageable pa = PageRequest.of(page, size);
        Page<User> userspage = null;
        List<User> users = null;
        if (!StringUtils.isEmpty(baseKey)) {
            userspage = userDao.findUsers("%" + baseKey + "%", baseKey + "%", pa);
            model.addAttribute("baseKey", baseKey);
            model.addAttribute("sort", "&baseKey=" + baseKey);
        } else {
            userspage = userDao.findAll(pa);
        }
        users = userspage.getContent();
        model.addAttribute("modalurl", "modalpaging");
        model.addAttribute("modalpage", userspage);
        model.addAttribute("users", users);

    }

    @Override
    public void outaddresspaging(int page, Model model, String baseKey, String outtype, String alph, Long userId) {


        PageHelper.startPage(page, 10);
        List<Map<String, Object>> directors = addressDao.getOutDirector(userId, alph, outtype, baseKey);
        List<Map<String, Object>> adds = encapsulation(directors);

        PageInfo<Map<String, Object>> pageinfo = new PageInfo<>(directors);
        if (!StringUtils.isEmpty(outtype)) {
            model.addAttribute("outtype", outtype);
        }
        Pageable pa = PageRequest.of(0, 10);

        Page<User> userspage = userDao.findAll(pa);
        List<User> users = userspage.getContent();
        model.addAttribute("modalurl", "modalpaging");
        model.addAttribute("modalpage", userspage);
        model.addAttribute("users", users);
        model.addAttribute("userId", userId);
        model.addAttribute("baseKey", baseKey);
        model.addAttribute("directors", adds);
        model.addAttribute("page", pageinfo);
        model.addAttribute("url", "outaddresspaging");


    }

    @Override
    public void inaddresspaging(int page, Model model, int size, String baseKey, String alph) {

        Page<User> userspage = null;
        Pageable pa = PageRequest.of(page, size);
        if (StringUtils.isEmpty(baseKey)) {
            if ("ALL".equals(alph)) {
                userspage = userDao.findAll(pa);
            } else {
                userspage = userDao.findByPinyinLike(alph + "%", pa);
            }
        } else {
            if ("ALL".equals(alph)) {
                userspage = userDao.findUsers("%" + baseKey + "%", baseKey + "%", pa);
            } else {
                userspage = userDao.findSelectUsers("%" + baseKey + "%", alph + "%", pa);
            }
        }
        if (!StringUtils.isEmpty(baseKey)) {
            model.addAttribute("baseKey", baseKey);
            model.addAttribute("sort", "&alph=" + alph + "&baseKey=" + baseKey);
        } else {
            model.addAttribute("sort", "&alph=" + alph);
        }
        List<User> users = userspage.getContent();
        model.addAttribute("users", users);
        model.addAttribute("page", userspage);
        model.addAttribute("url", "inaddresspaging");

    }


    public List<Map<String, Object>> encapsulation(List<Map<String, Object>> addressList) {

        List<Map<String, Object>> adds = new ArrayList<>();
        for (Map<String, Object> stringObjectMap : addressList) {
            Map<String, Object> result = new HashMap<>();
            result.put("director_users_id", stringObjectMap.get("director_users_id"));
            result.put("director_id", stringObjectMap.get("director_id"));
            result.put("user_id", stringObjectMap.get("user_id"));
            result.put("catelog_name", stringObjectMap.get("catelog_name"));
            result.put("companyname", stringObjectMap.get("companyname"));
            result.put("user_name", stringObjectMap.get("user_name"));
            result.put("sex", stringObjectMap.get("sex"));
            result.put("phone_number", stringObjectMap.get("phone_number"));
            result.put("email", stringObjectMap.get("email"));
//			!Objects.isNull(atDao.findOne(d.getAttachment()))
            if (stringObjectMap.get("image_path") != null) {
                result.put("image_path", attachmentDao.
                        findById(Long.parseLong(String.valueOf(stringObjectMap
                                .get("image_path") + "")))
                        .get().getAttachmentPath());
            } else {
                result.put("image_path", "timg.jpg");
            }
            adds.add(result);
        }
        return adds;

    }


}
