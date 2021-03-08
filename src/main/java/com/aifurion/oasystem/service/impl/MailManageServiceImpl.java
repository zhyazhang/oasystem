package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.formVaild.BindingResultVOUtil;
import com.aifurion.oasystem.common.formVaild.MapToList;
import com.aifurion.oasystem.common.formVaild.ResultEnum;
import com.aifurion.oasystem.common.formVaild.ResultVO;
import com.aifurion.oasystem.dao.mail.InMailDao;
import com.aifurion.oasystem.dao.mail.MailNumberDao;
import com.aifurion.oasystem.dao.mail.MailReceiverDao;
import com.aifurion.oasystem.dao.note.AttachmentDao;
import com.aifurion.oasystem.dao.role.RoleDao;
import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.user.DeptDao;
import com.aifurion.oasystem.dao.user.PositionDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.mail.Inmaillist;
import com.aifurion.oasystem.entity.mail.MailReceiver;
import com.aifurion.oasystem.entity.mail.Mailnumber;
import com.aifurion.oasystem.entity.mail.Pagemail;
import com.aifurion.oasystem.entity.note.Attachment;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.user.Dept;
import com.aifurion.oasystem.entity.user.Position;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.MailManageService;
import com.aifurion.oasystem.service.MailService;
import com.aifurion.oasystem.service.ProcessService;
import com.github.pagehelper.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 17:21
 */

@Service
public class MailManageServiceImpl implements MailManageService {


    @Autowired
    private MailNumberDao mailNumberDao;

    @Autowired
    private StatusDao statusDao;
    @Autowired
    private TypeDao typeDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private DeptDao deptDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PositionDao positionDao;
    @Autowired
    private InMailDao inMailDao;
    @Autowired
    private MailReceiverDao mailReceiverDao;
    @Autowired
    private AttachmentDao attachmentDao;
    @Autowired
    private MailService mailService;
    @Autowired
    private ProcessService processService;


    @Override
    public void index(Long userId, Model model, int page, int size) {


        //查找用户
        User user = userDao.findById(userId).get();
        //查找未读邮件
        List<MailReceiver> noreadlist = mailReceiverDao.findByReadAndDelAndReciverId(false, false, user);
        //查找创建了但是却没有发送的邮件
        List<Inmaillist> nopushlist = inMailDao.findByPushAndDelAndMailUserid(false, false, user);
        //查找发件条数
        List<Inmaillist> pushlist = inMailDao.findByPushAndDelAndMailUserid(true, false, user);
        //查找收件箱删除的邮件条数
        List<MailReceiver> rubbish = mailReceiverDao.findByDelAndReciverId(true, user);
        //分页及查找
        Page<Pagemail> pagelist = mailService.recive(page, size, user, null, "收件箱");
        List<Map<String, Object>> maillist = mailService.mail(pagelist);

        model.addAttribute("page", pagelist);
        model.addAttribute("maillist", maillist);
        model.addAttribute("url", "mailtitle");
        model.addAttribute("noread", noreadlist.size());
        model.addAttribute("nopush", nopushlist.size());
        model.addAttribute("push", pushlist.size());
        model.addAttribute("rubbish", rubbish.size());
        model.addAttribute("mess", "收件箱");
        model.addAttribute("sort", "&title=收件箱");

    }


    @Override
    public String delete(HttpServletRequest req, Long userId, Model model, int page, int size) {

        //查找用户
        User user = userDao.findById(userId).get();
        String title = req.getParameter("title");
        Page<Pagemail> pagelist = null;
        Page<Inmaillist> pagemail = null;
        List<Map<String, Object>> maillist = null;
        //得到删除id
        String ids = req.getParameter("ids");
        if (("收件箱").equals(title)) {

            StringTokenizer st = new StringTokenizer(ids, ",");
            while (st.hasMoreElements()) {
                //找到该用户联系邮件的中间记录
                MailReceiver mailr = mailReceiverDao.findbyReciverIdAndmailId(user, Long.parseLong(st.nextToken()));
                if (!Objects.isNull(mailr)) {
                    //把删除的字段改为1
                    mailr.setDel(true);
                    mailReceiverDao.save(mailr);
                } else {
                    return "redirect:/notlimit";
                }
            }
            //分页及查找
            pagelist = mailService.recive(page, size, user, null, title);
            maillist = mailService.mail(pagelist);
        } else if (("发件箱").equals(title)) {
            StringTokenizer st = new StringTokenizer(ids, ",");
            while (st.hasMoreElements()) {
                //找到该邮件
                Inmaillist inmail = inMailDao.findByMailUseridAndMailId(user, Long.parseLong(st.nextToken()));
                if (!Objects.isNull(inmail)) {
                    //把删除的字段改为1
                    inmail.setDel(true);
                    inMailDao.save(inmail);
                } else {
                    return "redirect:/notlimit";
                }
            }
            pagemail = mailService.inmail(page, size, user, null, title);
            maillist = mailService.maillist(pagemail);
        } else if (("草稿箱").equals(title)) {
            StringTokenizer st = new StringTokenizer(ids, ",");
            while (st.hasMoreElements()) {
                //找到该邮件
                Inmaillist inmail = inMailDao.findByMailUseridAndMailId(user, Long.parseLong(st.nextToken()));
                if (!Objects.isNull(inmail)) {
                    inMailDao.delete(inmail);
                } else {
                    return "redirect:/notlimit";
                }
            }
            pagemail = mailService.inmail(page, size, user, null, title);
            maillist = mailService.maillist(pagemail);
        } else {
            //垃圾箱
            StringTokenizer st = new StringTokenizer(ids, ",");
            while (st.hasMoreElements()) {
                Long mailid = Long.parseLong(st.nextToken());
                //查看中间表关于这条邮件的del字段
                List<Boolean> dellist = mailReceiverDao.findbyMailId(mailid);

                //判断中间表中关于这条邮件是否还有del字段为false的
                if (dellist.contains(false)) {
                    MailReceiver mailr = mailReceiverDao.findbyReciverIdAndmailId(user, mailid);
                    if (!Objects.isNull(mailr)) {
                        mailReceiverDao.delete(mailr);
                    } else {
                        return "redirect:/notlimit";
                    }
                } else {
                    Inmaillist imail = inMailDao.findById(mailid).get();
                    //判断这条邮件的del字段是为true
                    if (imail.getDel().equals(true)) {
                        List<MailReceiver> mreciver = mailReceiverDao.findByMailId(mailid);
                        //循环删除关于这条邮件的所有中间表信息
                        for (MailReceiver mailreciver : mreciver) {
                            mailReceiverDao.delete(mailreciver);
                        }
                        inMailDao.delete(imail);
                    } else {
                        //这条邮件的del字段为false，则删除中间表信息
                        MailReceiver mailr = mailReceiverDao.findbyReciverIdAndmailId(user, mailid);
                        if (!Objects.isNull(mailr)) {
                            mailReceiverDao.delete(mailr);
                        } else {
                            return "redirect:/notlimit";
                        }
                    }
                }
            }
            pagelist = mailService.recive(page, size, user, null, title);
            maillist = mailService.mail(pagelist);
        }

        if (!Objects.isNull(pagelist)) {
            model.addAttribute("page", pagelist);
        } else {
            model.addAttribute("page", pagemail);
        }
        model.addAttribute("maillist", maillist);
        model.addAttribute("url", "mailtitle");
        model.addAttribute("mess", title);
        return "mail/mailbody";
    }


    @Override
    public void watch(Long userId, Model model, HttpServletRequest req, int page, int size) {

        User user = userDao.findById(userId).get();
        String title = req.getParameter("title");
        String ids = req.getParameter("ids");
        Page<Pagemail> pagelist = null;
        List<Map<String, Object>> maillist = null;

        if (("收件箱").equals(title)) {
            StringTokenizer st = new StringTokenizer(ids, ",");
            while (st.hasMoreElements()) {
                //找到该用户联系邮件的中间记录
                MailReceiver mailr = mailReceiverDao.findbyReciverIdAndmailId(user, Long.parseLong(st.nextToken()));
                mailr.setRead(mailr.getRead().equals(false));

                mailReceiverDao.save(mailr);
            }
            //分页及查找
            pagelist = mailService.recive(page, size, user, null, title);

        } else {
            //垃圾箱
            StringTokenizer st = new StringTokenizer(ids, ",");
            while (st.hasMoreElements()) {
                //找到该用户联系邮件的中间记录
                MailReceiver mailr = mailReceiverDao.findbyReciverIdAndmailId(user, Long.parseLong(st.nextToken()));
                if (mailr.getRead().equals(false)) {
                    mailr.setRead(true);
                } else {
                    mailr.setRead(false);
                }
                mailReceiverDao.save(mailr);
            }
            //分页及查找
            pagelist = mailService.recive(page, size, user, null, title);
        }
        maillist = mailService.mail(pagelist);

        model.addAttribute("page", pagelist);
        model.addAttribute("maillist", maillist);
        model.addAttribute("url", "mailtitle");
        model.addAttribute("mess", title);
    }


    @Override
    public void star(Long userId, Model model, HttpServletRequest req, int page, int size) {

        User user = userDao.findById(userId).get();
        String title = req.getParameter("title");
        String ids = req.getParameter("ids");
        Page<Pagemail> pagelist = null;
        Page<Inmaillist> pagemail = null;
        List<Map<String, Object>> maillist = null;

        if (("收件箱").equals(title)) {
            StringTokenizer st = new StringTokenizer(ids, ",");
            while (st.hasMoreElements()) {

                //找到该用户联系邮件的中间记录
                MailReceiver mailr = mailReceiverDao.findbyReciverIdAndmailId(user, Long.parseLong(st.nextToken()));
                mailr.setStar(mailr.getStar().equals(false));
                mailReceiverDao.save(mailr);
            }
            //分页及查找
            pagelist = mailService.recive(page, size, user, null, title);
            maillist = mailService.mail(pagelist);
        } else if (("发件箱").equals(title)) {
            StringTokenizer st = new StringTokenizer(ids, ",");
            while (st.hasMoreElements()) {
                //找到该邮件
                Inmaillist inmail = inMailDao.findByMailUseridAndMailId(user, Long.parseLong(st.nextToken()));
                inmail.setStar(inmail.getStar().equals(false));
                inMailDao.save(inmail);
            }
            pagemail = mailService.inmail(page, size, user, null, title);
            maillist = mailService.maillist(pagemail);
        } else if (("草稿箱").equals(title)) {
            StringTokenizer st = new StringTokenizer(ids, ",");
            while (st.hasMoreElements()) {
                //找到该邮件
                Inmaillist inmail = inMailDao.findByMailUseridAndMailId(user, Long.parseLong(st.nextToken()));
                inmail.setStar(inmail.getStar().equals(false));
                inMailDao.save(inmail);
            }
            pagemail = mailService.inmail(page, size, user, null, title);
            maillist = mailService.maillist(pagemail);
        } else {
            //垃圾箱
            StringTokenizer st = new StringTokenizer(ids, ",");
            while (st.hasMoreElements()) {
                //找到该用户联系邮件的中间记录
                MailReceiver mailr = mailReceiverDao.findbyReciverIdAndmailId(user, Long.parseLong(st.nextToken()));
                if (mailr.getStar().equals(false)) {
                    mailr.setStar(true);
                } else {
                    mailr.setStar(false);
                }
                mailReceiverDao.save(mailr);
            }
            //分页及查找
            pagelist = mailService.recive(page, size, user, null, title);
            maillist = mailService.mail(pagelist);
        }

        if (!Objects.isNull(pagelist)) {
            model.addAttribute("page", pagelist);
        } else {
            model.addAttribute("page", pagemail);
        }
        model.addAttribute("maillist", maillist);
        model.addAttribute("url", "mailtitle");
        model.addAttribute("mess", title);


    }


    @Override
    public void searchByMail(Long userId, Model model, HttpServletRequest req, int page, int size) {

        User user = userDao.findById(userId).get();
        String title = req.getParameter("title");
        String val = null;
        Page<Pagemail> pagelist = null;
        Page<Inmaillist> pagemail = null;
        List<Map<String, Object>> maillist = null;

        if (!StringUtil.isEmpty(req.getParameter("val"))) {
            val = req.getParameter("val");
        }
        if (("收件箱").equals(title)) {
            pagelist = mailService.recive(page, size, user, val, title);
            maillist = mailService.mail(pagelist);

        } else if (("发件箱").equals(title)) {

            pagemail = mailService.inmail(page, size, user, val, title);
            maillist = mailService.maillist(pagemail);
        } else if (("草稿箱").equals(title)) {

            pagemail = mailService.inmail(page, size, user, val, title);
            maillist = mailService.maillist(pagemail);
        } else {
            //垃圾箱
            pagelist = mailService.recive(page, size, user, val, title);
            maillist = mailService.mail(pagelist);
        }

        if (!Objects.isNull(pagelist)) {
            model.addAttribute("page", pagelist);
        } else {
            model.addAttribute("page", pagemail);
        }
        if (val != null) {
            model.addAttribute("sort", "&title=" + title + "&val=" + val);
        } else {
            model.addAttribute("sort", "&title=" + title);
        }
        model.addAttribute("maillist", maillist);
        model.addAttribute("url", "mailtitle");
        model.addAttribute("mess", title);
    }


    @Override
    public void account(Long userId, Model model, int page, int size) {


        // 通过邮箱建立用户id找用户对象
        User tu = userDao.findById(userId).get();

        Page<Mailnumber> pagelist = mailService.index(page, size, tu, null, model);
        List<Map<String, Object>> list = mailService.up(pagelist);

        model.addAttribute("account", list);
        model.addAttribute("page", pagelist);
        model.addAttribute("url", "mailpaixu");

    }


    @Override
    public void sortAccount(HttpServletRequest request, Long userId, Model model, int page, int size) {

        // 通过发布人id找用户
        User tu = userDao.findById(userId).get();
        //得到传过来的值
        String val = null;
        if (!StringUtil.isEmpty(request.getParameter("val"))) {
            val = request.getParameter("val");
        }
        Page<Mailnumber> pagelist = mailService.index(page, size, tu, val, model);
        List<Map<String, Object>> list = mailService.up(pagelist);
        model.addAttribute("account", list);
        model.addAttribute("page", pagelist);
        model.addAttribute("url", "mailpaixu");
    }

    @Override
    public void add(Long userId, Model model, HttpServletRequest req) {

        // 通过用户id找用户
        User tu = userDao.findById(userId).get();

        Mailnumber mailn = null;
        if (StringUtil.isEmpty(req.getParameter("id"))) {
            List<SystemTypeList> typelist = typeDao.findByTypeModel("aoa_mailnumber");
            List<SystemStatusList> statuslist = statusDao.findByStatusModel("aoa_mailnumber");
            model.addAttribute("typelist", typelist);
            model.addAttribute("statuslist", statuslist);

            if (!StringUtil.isEmpty((String) req.getAttribute("errormess"))) {
                mailn = (Mailnumber) req.getAttribute("mail");
                req.setAttribute("errormess", req.getAttribute("errormess"));
                model.addAttribute("mails", mailn);
                model.addAttribute("type", typeDao.findname(mailn.getMailType()));
                model.addAttribute("status", typeDao.findname(mailn.getStatus()));

            } else if (!StringUtil.isEmpty((String) req.getAttribute("success"))) {
                mailn = (Mailnumber) req.getAttribute("mail");
                req.setAttribute("success", "fds");
                model.addAttribute("mails", mailn);
                model.addAttribute("type", typeDao.findname(mailn.getMailType()));
                model.addAttribute("status", statusDao.findname(mailn.getStatus()));
            }
        } else {

            Long id = Long.parseLong(req.getParameter("id"));
            Mailnumber mailnum = mailNumberDao.findById(id).get();
            model.addAttribute("type", typeDao.findname(mailnum.getMailType()));
            model.addAttribute("status", statusDao.findname(mailnum.getStatus()));
            model.addAttribute("mails", mailnum);

        }
        model.addAttribute("username", tu.getUserName());
    }


    @Override
    public void save(HttpServletRequest request, Mailnumber mail, BindingResult br, Long userId) {


        User tu = userDao.findById(userId).get();
        request.setAttribute("mail", mail);
        ResultVO res = BindingResultVOUtil.hasErrors(br);
        if (!ResultEnum.SUCCESS.getCode().equals(res.getCode())) {
            List<Object> list = new MapToList<>().mapToList(res.getData());
            request.setAttribute("errormess", list.get(0).toString());

        } else {
            if (Objects.isNull(mail.getMailNumberId())) {
                mail.setMailUserId(tu);
                mail.setMailCreateTime(new Date());
                mailNumberDao.save(mail);
            } else {
                Mailnumber mails = mailNumberDao.findById(mail.getMailNumberId()).get();
                mails.setMailType(mail.getMailType());
                mails.setStatus(mail.getStatus());
                mails.setMailDes(mail.getMailDes());
                mails.setMailAccount(mail.getMailAccount());
                mails.setPassword(mail.getPassword());
                mails.setMailUserName(mail.getMailUserName());
                mailNumberDao.save(mails);
            }
            request.setAttribute("success", "执行成功！");

        }
    }

    @Override
    public void writeMail(Model model, Long userId, HttpServletRequest request, int page, int size) {

        User mu = userDao.findById(userId).get();
        //得到编辑过来的id
        String id = null;
        if (!StringUtil.isEmpty(request.getParameter("id"))) {
            id = request.getParameter("id");
        }
        //回复那边过来的
        String huifu = null;

        if (!StringUtil.isEmpty(id)) {
            Long lid = Long.parseLong(id);
            //找到这条邮件
            Inmaillist mail = inMailDao.findById(lid).get();
            if (!StringUtil.isEmpty(request.getParameter("huifu"))) {
                huifu = request.getParameter("huifu");
                model.addAttribute("title", huifu + mail.getMailTitle());
                model.addAttribute("content", mail.getContent());

            } else {
                model.addAttribute("title", mail.getMailTitle());
                model.addAttribute("content", mail.getContent());
            }
            model.addAttribute("status", statusDao.findById(mail.getMailStatusid()).get());
            model.addAttribute("type", typeDao.findById(mail.getMailType()).get());
            model.addAttribute("id", "回复");

        } else {

            List<SystemTypeList> typelist = typeDao.findByTypeModel("aoa_in_mail_list");
            List<SystemStatusList> statuslist = statusDao.findByStatusModel("aoa_in_mail_list");
            model.addAttribute("typelist", typelist);
            model.addAttribute("statuslist", statuslist);
            model.addAttribute("id", "新发");

        }
        //查看该用户所创建的有效邮箱账号
        List<Mailnumber> mailnum = mailNumberDao.findByStatusAndMailUserId(1L, mu);
        processService.user(page, size, model);
        model.addAttribute("mailnum", mailnum);

    }


    @Override
    public void pushMail(MultipartFile file, HttpServletRequest request, Inmaillist mail, BindingResult br, Long userId) {


        User tu = userDao.findById(userId).get();

        String name = null;
        Attachment attaid = null;
        Mailnumber number = null;
        StringTokenizer st = null;
        ResultVO res = BindingResultVOUtil.hasErrors(br);
        if (!ResultEnum.SUCCESS.getCode().equals(res.getCode())) {
            List<Object> list = new MapToList<>().mapToList(res.getData());
            request.setAttribute("errormess", list.get(0).toString());
        } else {
            if (!StringUtil.isEmpty(request.getParameter("fasong"))) {
                name = request.getParameter("fasong");
            }


            if (!StringUtil.isEmpty(name)) {
                if (!StringUtil.isEmpty(file.getOriginalFilename())) {
                    attaid = mailService.upload(file, tu);
                    attaid.setModel("mail");
                    attachmentDao.save(attaid);
                }
                //发送成功
                mail.setPush(true);

            } else {
                //存草稿
                mail.setInReceiver(null);
            }
            mail.setMailFileid(attaid);
            mail.setMailCreateTime(new Date());
            mail.setMailUserid(tu);
            if (!mail.getInmail().equals(0)) {
                number = mailService.findOne(mail.getInmail());
                mail.setMailNumberid(number);
            }
            //存邮件
            Inmaillist imail = inMailDao.save(mail);

            if (!StringUtil.isEmpty(name)) {
                if (mailService.isContainChinese(mail.getInReceiver())) {
                    // 分割任务接收人
                    StringTokenizer st2 = new StringTokenizer(mail.getInReceiver(), ";");
                    while (st2.hasMoreElements()) {
                        User reciver = userDao.findId(st2.nextToken());
                        MailReceiver mreciver = new MailReceiver();
                        mreciver.setMailId(imail);
                        mreciver.setReciverId(reciver);
                        mailReceiverDao.save(mreciver);
                    }
                } else {
                    if (mail.getInReceiver().contains(";")) {
                        st = new StringTokenizer(mail.getInReceiver(), ";");
                    } else {
                        st = new StringTokenizer(mail.getInReceiver(), "；");
                    }

                    while (st.hasMoreElements()) {
                        if (!StringUtil.isEmpty(file.getOriginalFilename())) {
                            mailService.pushmail(number.getMailAccount(), number.getPassword(), st.nextToken(), number.getMailUserName(), mail.getMailTitle(),
                                    mail.getContent(), attaid.getAttachmentPath(), attaid.getAttachmentName());

                        } else {
                            mailService.pushmail(number.getMailAccount(), number.getPassword(), st.nextToken(), number.getMailUserName(), mail.getMailTitle(),
                                    mail.getContent(), null, null);
                        }
                    }
                }

            }
        }
    }


    @Override
    public void searchByName(Model model, HttpServletRequest req, Long userId, int page, int size) {

        Pageable pa = PageRequest.of(page, size);
        String name = null;
        String qufen = null;
        Page<User> pageuser = null;
        List<User> userlist = null;

        if (!StringUtil.isEmpty(req.getParameter("title"))) {
            name = req.getParameter("title").trim();
        }
        if (!StringUtil.isEmpty(req.getParameter("qufen"))) {
            qufen = req.getParameter("qufen").trim();

            if (StringUtil.isEmpty(name)) {
                // 查询部门下面的员工
                pageuser = userDao.findByFatherId(userId, pa);
            } else {
                // 查询名字模糊查询员工
                pageuser = userDao.findbyFatherId(name, userId, pa);
            }

        } else {
            if (StringUtil.isEmpty(name)) {
                //查看用户并分页
                pageuser = userDao.findAll(pa);
            } else {
                pageuser = userDao.findbyUserNameLike(name, pa);
            }
        }
        userlist = pageuser.getContent();
        // 查询部门表
        Iterable<Dept> deptlist = deptDao.findAll();
        // 查职位表
        Iterable<Position> poslist = positionDao.findAll();
        model.addAttribute("emplist", userlist);
        model.addAttribute("page", pageuser);
        model.addAttribute("deptlist", deptlist);
        model.addAttribute("poslist", poslist);
        model.addAttribute("url", "names");

    }


    @Override
    public void recentlyMail(HttpServletRequest req, Long userId, Model model, int page, int size) {

        Pageable pa = PageRequest.of(page, size);
        User mu = userDao.findById(userId).get();
        String mess = req.getParameter("title");

        Page<Pagemail> pagelist = null;
        Page<Inmaillist> pagemail = null;
        List<Map<String, Object>> maillist = null;
        if (("收件箱").equals(mess)) {
            //分页及查找
            pagelist = mailService.recive(page, size, mu, null, mess);
            maillist = mailService.mail(pagelist);
        } else if (("发件箱").equals(mess)) {
            pagemail = mailService.inmail(page, size, mu, null, mess);
            maillist = mailService.maillist(pagemail);
        } else if (("草稿箱").equals(mess)) {
            pagemail = mailService.inmail(page, size, mu, null, mess);
            maillist = mailService.maillist(pagemail);
        } else {
            //垃圾箱
            //分页及查找
            pagelist = mailService.recive(page, size, mu, null, mess);
            maillist = mailService.mail(pagelist);

        }

        if (!Objects.isNull(pagelist)) {
            model.addAttribute("page", pagelist);
        } else {
            model.addAttribute("page", pagemail);

        }
        model.addAttribute("sort", "&title=" + mess);
        model.addAttribute("maillist", maillist);
        model.addAttribute("url", "mailtitle");
        model.addAttribute("mess", mess);
    }

    @Override
    public void seeMail(HttpServletRequest req, Long userId, Model model) {


        User mu = userDao.findById(userId).get();
        //邮件id
        Long id = Long.parseLong(req.getParameter("id"));
        //title
        String title = req.getParameter("title");
        //找到中间表信息
        if (("收件箱").equals(title) || ("垃圾箱").equals(title)) {
            MailReceiver mailr = mailReceiverDao.findbyReciverIdAndmailId(mu, id);
            mailr.setRead(true);
            mailReceiverDao.save(mailr);
        }

        //找到该邮件信息
        Inmaillist mail = inMailDao.findById(id).get();
        String filetype = null;
        if (!Objects.isNull(mail.getMailFileid())) {
            String filepath = mail.getMailFileid().getAttachmentPath();
            if (mail.getMailFileid().getAttachmentType().startsWith("image")) {
                filetype = "img";
            } else {
                filetype = "appli";

            }
            model.addAttribute("filepath", filepath);
            model.addAttribute("filetype", filetype);
        }

        User pushuser = userDao.findById(mail.getMailUserid().getUserId()).get();
        model.addAttribute("pushname", pushuser.getUserName());
        model.addAttribute("mail", mail);
        model.addAttribute("mess", title);
        model.addAttribute("file", mail.getMailFileid());

    }

    @Override
    public String refresh(HttpServletRequest req, Long userId, Model model, int page, int size) {


        //查找用户
        User user = userDao.findById(userId).get();
        String title = req.getParameter("title");
        Page<Pagemail> pagelist = null;
        List<Map<String, Object>> maillist = null;
        //得到恢复删除id
        String ids = req.getParameter("ids");

        StringTokenizer st = new StringTokenizer(ids, ",");
        while (st.hasMoreElements()) {
            //找到该用户联系邮件的中间记录
            MailReceiver mailr = mailReceiverDao.findbyReciverIdAndmailId(user, Long.parseLong(st.nextToken()));
            if (!Objects.isNull(mailr)) {
                mailr.setDel(false);
                mailReceiverDao.save(mailr);
            } else {
                return "redirect:/notlimit";
            }
        }
        //分页及查找
        pagelist = mailService.recive(page, size, user, null, title);
        maillist = mailService.mail(pagelist);

        model.addAttribute("page", pagelist);
        model.addAttribute("maillist", maillist);
        model.addAttribute("url", "mailtitle");
        model.addAttribute("mess", title);


        return "mail/mailbody";

    }
}
