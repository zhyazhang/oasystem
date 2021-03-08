package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.formVaild.BindingResultVOUtil;
import com.aifurion.oasystem.common.formVaild.ResultEnum;
import com.aifurion.oasystem.common.formVaild.ResultVO;
import com.aifurion.oasystem.dao.discuss.*;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.discuss.Discuss;
import com.aifurion.oasystem.entity.discuss.VoteList;
import com.aifurion.oasystem.entity.discuss.VoteTitles;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.DiscussManageService;
import com.aifurion.oasystem.service.DiscussService;
import com.aifurion.oasystem.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 13:16
 */

@Service
public class DiscussManageServiceImpl implements DiscussManageService {

    @Autowired
    DiscussDao discussDao;
    @Autowired
    DiscussService discussService;
    @Autowired
    UserDao userDao;
    @Autowired
    TypeDao typeDao;
    @Autowired
    ReplyDao replyDao;
    @Autowired
    CommentDao commentDao;
    @Autowired
    VoteService voteService;
    @Autowired
    VoteTitleListDao voteTitlesDao;
    @Autowired
    VoteTitlesUserDao voteUserDao;


    @Override
    public void getChatList(int page, Model model, HttpSession session) {

        Page<Discuss> page2 = discussService.paging(page, null, null, null, null, null);
        setPagintMess(model, page2, "/seetable", null, "讨论列表");
        session.removeAttribute("returnUrl");
        session.setAttribute("returnUrl", "chatlist");

    }

    @Override
    public void adminManage(int page, HttpSession session, Long userId, Model model) {

        Page<Discuss> page2 = discussService.paging(page, null, 1L, null, null, null);
        setPagintMess(model, page2, "/chattable", "manage", "超级管理员");
        session.removeAttribute("returnUrl");
        session.setAttribute("returnUrl", "adminmanage");


    }

    @Override
    public void chatManage(int page, Long userId, Model model, HttpSession session) {

        Page<Discuss> page2 = discussService.pagingMe(page, null, userId, null, null, null);
        setPagintMess(model, page2, "/metable", "manage", "我的管理");
        model.addAttribute("me", "me");
        session.removeAttribute("returnUrl");
        session.setAttribute("returnUrl", "chatmanage");
    }

    @Override
    public boolean deleteDiscuss(String name, Long userId, Long discussId) {


        Discuss discuss = discussDao.findById(discussId).get();
        User user = userDao.findById(userId).get();
        if (!user.getSuperman()) {
            if (!Objects.equals(discuss.getUser().getUserId(), user.getUserId())) {
                return false;
            }

        }
        discussService.deleteDiscuss(discussId);
        return true;

    }

    @Override
    public void chatTable(int page, String baseKey, String type, String time, String visitnum, String icon, Long userId, Model model) {


        setSomething(baseKey, type, time, visitnum, icon, model);
        Page<Discuss> page2 = discussService.paging(page, baseKey, 1L, type, time, visitnum);
        setPagintMess(model, page2, "/chattable", "manage", "超级管理员");


    }


    @Override
    public void meTable(int page, String baseKey, String type, String time, String visitnum, String icon, Long userId, Model model) {


        setSomething(baseKey, type, time, visitnum, icon, model);
        Page<Discuss> page2 = discussService.pagingMe(page, baseKey, userId, type, time, visitnum);
        setPagintMess(model, page2, "/metable", "manage", "我的管理");

    }


    @Override
    public void seeTable(int page, String baseKey, String type, String time, String visitnum, String icon, Long userId, Model model) {

        setSomething(baseKey, type, time, visitnum, icon, model);
        //传过去的userid为null；
        Page<Discuss> page2 = discussService.paging(page, baseKey, null, type, time, visitnum);
        setPagintMess(model, page2, "/seetable", null, "讨论列表");
    }

    @Override
    public void seeDiscuss(Long id, Integer pageNumber, HttpSession session) {

        discussService.addOneDiscuss(id);
        session.removeAttribute("id");
        session.setAttribute("id", id);
        session.setAttribute("pageNumber", pageNumber);

    }


    @Override
    public void replyManage(Model model, HttpSession session, int page, int size, Long userId) {


        Long id = Long.parseLong(String.valueOf(session.getAttribute("id")));
        User user = userDao.findById(userId).get();
        Discuss discuss = discussDao.findById(id).get();
        //用来处理vote相关的数据
        voteService.voteServiceHandle(model, user, discuss);
        if (user.getSuperman()) {
            model.addAttribute("manage", "具有管理权限");
        } else {
            if (Objects.equals(user.getUserId(), discuss.getUser().getUserId())) {
                model.addAttribute("manage", "具有管理权限");
            }
        }
        discussService.setDiscussMess(model, id, userId, page, size);

    }


    @Override
    public void writeDiscuss(HttpServletRequest req, Long userId, Model model) {

        HttpSession session = req.getSession();
        session.removeAttribute("id");
        if (!StringUtils.isEmpty(req.getParameter("id"))) {
            //修改界面的显示
            Long disId = Long.parseLong(req.getParameter("id"));
            Discuss discuss = discussDao.findById(disId).get();
            //回填投票的信息
            if (!Objects.isNull(discuss.getVoteList())) {
                model.addAttribute("voteList", discuss.getVoteList());
                List<VoteTitles> voteTitles = voteTitlesDao.findByVoteList(discuss.getVoteList());
                model.addAttribute("voteTitles", voteTitles);
            }
            //回填投票标题的信息
            session.setAttribute("id", disId);
            model.addAttribute("discuss", discuss);
            model.addAttribute("typeName", typeDao.findById(discuss.getTypeId()).get().getTypeName());
        }
        if (!StringUtils.isEmpty(req.getAttribute("success"))) {
            model.addAttribute("success", "成功了");
        }
        User user = userDao.findById(userId).get();
        List<SystemTypeList> typeList = typeDao.findByTypeModel("chat");
        model.addAttribute("typeList", typeList);
        model.addAttribute("user", user);


    }


    @Override
    public void addDiscuss(HttpServletRequest req, Discuss menu, VoteList voteList) {


        HttpSession session = req.getSession();
        Long userId = Long.parseLong(String.valueOf(session.getAttribute("userId")));
        User user = userDao.findById(userId).get();
        // 校验失败

        //修改处理
        if (!StringUtils.isEmpty(session.getAttribute("id"))) {
            Long disId = Long.parseLong(session.getAttribute("id") + "");
            Discuss discuss = discussDao.findById(disId).get();
            //在此处判断一下是哪种类型，投票又不一样；
            if (discuss.getTypeId() == 21) {
                VoteList vote = discuss.getVoteList();
                vote.setEndTime(voteList.getEndTime());
                voteService.savaVoteList(vote);
            }
            discuss.setModifyTime(new Date());
            discuss.setTitle(menu.getTitle());
            discuss.setContent(menu.getContent());
            discussService.save(discuss);
            req.setAttribute("success", "成功了");


        } else {
            //新增处理
            Long typeId = Long.parseLong(req.getParameter("typeId"));
            if (menu.getTypeId() == 21) {
                String[] title2 = req.getParameterValues("votetitle");
                String[] colors = req.getParameterValues("votecolor");


                Set<VoteTitles> voteTitles = new HashSet<>();
                //处理投票标题
                for (int i = 0; i < colors.length; i++) {
                    VoteTitles voteTitle = new VoteTitles();
                    voteTitle.setColor(colors[i]);
                    voteTitle.setTitle(title2[i]);
                    voteTitle.setVoteList(voteList);
                    voteTitles.add(voteTitle);
                }
                voteList.setVoteTitles(voteTitles);        //将所有投票表格保存到投票对象中
//					voteService.savaVoteList(voteList);		//将投票信息保存到投票表中
                menu.setVoteList(voteList);                //将投票对象保存到讨论表中；

            }
            menu.setVisitNum(0);
            menu.setUser(user);
            menu.setCreateTime(new Date());
            discussService.save(menu);
            req.setAttribute("success", "成功了");


        }


    }

    private void setPagintMess(Model model, Page<Discuss> page2, String url, String manage, String name) {
        model.addAttribute("list", discussService.packaging(page2.getContent()));
        model.addAttribute("page", page2);
        model.addAttribute("url", url);
        model.addAttribute("name", name);
        if (!StringUtils.isEmpty(manage)) {
            model.addAttribute("manage", "manage");
        }
    }

    private void setSomething(String baseKey, String type, String time, String visitnum, String icon,
                              Model model) {
        if (!StringUtils.isEmpty(icon)) {
            model.addAttribute("icon", icon);
            if (!StringUtils.isEmpty(type)) {
                model.addAttribute("type", type);
                if ("1".equals(type)) {
                    model.addAttribute("sort", "&type=1&icon=" + icon);
                } else {
                    model.addAttribute("sort", "&type=0&icon=" + icon);
                }
            }
            if (!StringUtils.isEmpty(visitnum)) {
                model.addAttribute("visitnum", visitnum);
                if ("1".equals(visitnum)) {
                    model.addAttribute("sort", "&visitnum=1&icon=" + icon);
                } else {
                    model.addAttribute("sort", "&visitnum=0&icon=" + icon);
                }
            }
            if (!StringUtils.isEmpty(time)) {
                model.addAttribute("time", time);
                if ("1".equals(time)) {
                    model.addAttribute("sort", "&time=1&icon=" + icon);
                } else {
                    model.addAttribute("sort", "&time=0&icon=" + icon);
                }
            }
        }
        if (!StringUtils.isEmpty(baseKey)) {
            model.addAttribute("baseKey", baseKey);
        }
    }


}
