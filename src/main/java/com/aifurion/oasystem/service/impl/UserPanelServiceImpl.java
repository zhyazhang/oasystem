package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.formVaild.BindingResultVOUtil;
import com.aifurion.oasystem.common.formVaild.MapToList;
import com.aifurion.oasystem.common.formVaild.ResultEnum;
import com.aifurion.oasystem.common.formVaild.ResultVO;
import com.aifurion.oasystem.dao.inform.InformRelationDao;
import com.aifurion.oasystem.dao.mail.MailReceiverDao;
import com.aifurion.oasystem.dao.process.NotepaperDao;
import com.aifurion.oasystem.dao.user.DeptDao;
import com.aifurion.oasystem.dao.user.PositionDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.mail.MailReceiver;
import com.aifurion.oasystem.entity.notice.NoticeUserRelation;
import com.aifurion.oasystem.entity.process.Notepaper;
import com.aifurion.oasystem.entity.user.Position;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.NotepaperService;
import com.aifurion.oasystem.service.UserPanelService;
import com.github.pagehelper.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/3/8 18:38
 */

@Service
public class UserPanelServiceImpl implements UserPanelService {


    @Autowired
    private UserDao userDao;
    @Autowired
    private DeptDao deptDao;
    @Autowired
    private PositionDao positionDao;
    @Autowired
    private InformRelationDao informRelationDao;
    @Autowired
    private MailReceiverDao mailReceiverDao;
    @Autowired
    private NotepaperDao notepaperDao;

    @Autowired
    private NotepaperService notepaperService;

    @Value("${img.rootpath}")
    private String rootpath;


    @Override
    public void saveUser(MultipartFile filePath, HttpServletRequest request, User user, BindingResult br, Long userId) {

        String imgpath = notepaperService.upload(filePath);
        User users = userDao.findById(userId).get();

        //重新set用户
        users.setRealName(user.getRealName());
        users.setUserTel(user.getUserTel());
        users.setEamil(user.getEamil());
        users.setAddress(user.getAddress());
        users.setUserEdu(user.getUserEdu());
        users.setSchool(user.getSchool());
        users.setIdCard(user.getIdCard());
        users.setBank(user.getBank());
        users.setSex(user.getSex());
        users.setThemeSkin(user.getThemeSkin());
        users.setBirth(user.getBirth());
        if (!StringUtil.isEmpty(user.getUserSign())) {
            users.setUserSign(user.getUserSign());
        }
        if (!StringUtil.isEmpty(user.getPassword())) {
            users.setPassword(user.getPassword());
        }
        if (!StringUtil.isEmpty(imgpath)) {
            users.setImgPath(imgpath);
        }

        request.setAttribute("users", users);
        ResultVO res = BindingResultVOUtil.hasErrors(br);
        if (!ResultEnum.SUCCESS.getCode().equals(res.getCode())) {
            List<Object> list = new MapToList<>().mapToList(res.getData());
            request.setAttribute("errormess", list.get(0).toString());
        } else {
            userDao.save(users);
            request.setAttribute("success", "执行成功！");
        }


    }

    @Override
    public boolean deletePaper(Long paperId, Long userId) {


        User user = userDao.findById(userId).get();

        Notepaper note = notepaperDao.findById(paperId).get();

        return user.getUserId().equals(note.getUserId().getUserId());


    }

    @Override
    public void savePanel(Notepaper notepaper, Long userId, String content) {

        User user = userDao.findById(userId).get();
        notepaper.setCreateTime(new Date());

        notepaper.setUserId(user);
        if (notepaper.getTitle() == null || notepaper.getTitle().equals("")) {

            notepaper.setTitle("无标题");
        }
        if (notepaper.getConcent() == null || notepaper.getConcent().equals("")) {

            notepaper.setConcent(content);
        }
        notepaperDao.save(notepaper);

    }

    @Override
    public void getPanel(Long userId, Model model, int page, int size) {

        Pageable pa = PageRequest.of(page, size);
        User user = userDao.findById(userId).get();
        //找便签
        Page<Notepaper> list = notepaperDao.findByUserIdOrderByCreateTimeDesc(user, pa);
        List<Notepaper> notepaperlist = list.getContent();
        model.addAttribute("notepaperlist", notepaperlist);
        model.addAttribute("page", list);
        model.addAttribute("url", "panel");

    }

    @Override
    public void userPanel(Long userId, Model model, HttpServletRequest req, int page, int size) {

        Pageable pa = PageRequest.of(page, size);
        User user = null;
        if (!StringUtil.isEmpty((String) req.getAttribute("errormess"))) {
            user = (User) req.getAttribute("users");
            req.setAttribute("errormess", req.getAttribute("errormess"));
        } else if (StringUtil.isEmpty((String) req.getAttribute("errormess"))) {
            //找到这个用户
            user = userDao.findById(userId).get();
        } else {
            user = (User) req.getAttribute("users");
            req.setAttribute("success", "fds");
        }

        //找到部门名称
        String deptname = deptDao.findname(user.getDept().getDeptId());

        //找到职位名称
        Position position = positionDao.findById(user.getPosition().getId()).get();



        //找未读通知消息
        List<NoticeUserRelation> noticelist = informRelationDao.findByReadAndUserId(false, user);

        //找未读邮件
        List<MailReceiver> maillist = mailReceiverDao.findByReadAndDelAndReciverId(false, false, user);

        //找便签
        Page<Notepaper> list = notepaperDao.findByUserIdOrderByCreateTimeDesc(user, pa);

        List<Notepaper> notepaperlist = list.getContent();

        model.addAttribute("user", user);
        model.addAttribute("deptname", deptname);
        model.addAttribute("positionname", position.getName());
        model.addAttribute("noticelist", noticelist.size());
        model.addAttribute("maillist", maillist.size());
        model.addAttribute("notepaperlist", notepaperlist);
        model.addAttribute("page", list);
        model.addAttribute("url", "panel");


    }
}
