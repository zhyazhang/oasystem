package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.enums.VoteEnum;
import com.aifurion.oasystem.dao.discuss.*;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.discuss.*;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.DiscussService;
import com.aifurion.oasystem.service.ReplyService;
import com.aifurion.oasystem.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 15:45
 */


@Service
public class ReplyServiceImpl implements ReplyService {

    @Autowired
    private ReplyDao replyDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private DiscussDao discussDao;
    @Autowired
    private DiscussService disService;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private VoteTitleListDao voteTitleListDao;
    @Autowired
    private VoteService voteService;
    @Autowired
    private VoteTitlesUserDao voteUserDao;


    @Override
    public void reply(HttpServletRequest req, int page, int size, Long userId, Model model) {

        Long num = null;
        Long discussId = Long.parseLong(req.getParameter("replyId"));
        String module = req.getParameter("module");    //用来判断是保存在哪个表

        User user = userDao.findById(userId).get();

        Discuss dis = null;
        if ("discuss".equals(module)) {
            dis = discussDao.findById(discussId).get();
            num = dis.getDiscussId();
        } else {
            Reply replyyy = replyDao.findById(discussId).get();
            dis = replyyy.getDiscuss();
            num = dis.getDiscussId();
        }
        if (!StringUtils.isEmpty(req.getParameter("comment"))) {
            String comment = req.getParameter("comment");


            if ("discuss".equals(module)) {
                //说明是回复-楼主

                Discuss discuss = discussDao.findById(discussId).get();
                Reply reply = new Reply(new Date(), comment, user, discuss);
                num = discuss.getDiscussId();
                replyDao.save(reply);
            } else { //说明是回复-评论

                Reply reply = replyDao.findById(discussId).get();
                Comment comment2 = new Comment(new Date(), comment, user, reply);
                commentDao.save(comment2);
                num = reply.getDiscuss().getDiscussId();
            }
            Discuss discuss = discussDao.findById(num).get();
            if (user.getSuperman()) {
                model.addAttribute("manage", "具有管理权限");
            } else {
                if (Objects.equals(user.getUserId(), discuss.getUser().getUserId())) {
                    model.addAttribute("manage", "具有管理权限");
                }
            }
        }
        disService.setDiscussMess(model, num, userId, page, size);

    }

    @Override
    public void likeThis(HttpServletRequest req, Long userId) {

        likeThisFun(req, userId);


    }

    @Override
    public void likeThisFun(HttpServletRequest req, Long userId) {

        Long replyId = Long.parseLong(req.getParameter("replyId"));
        String module = req.getParameter("module");
        int number = 0;
        int likenum = 0;
        User user = userDao.findById(userId).get();
        if ("discuss".equals(module)) {
            Discuss discuss = discussDao.findById(replyId).get();
            Set<User> users = discuss.getUsers();
            likenum = discuss.getUsers().size();
            if (!discuss.getUsers().contains(user)) {

                users.add(user);
                number = 1;
            } else {

                users.remove(user);
                number = -1;
            }
            discuss.setUsers(users);
            disService.save(discuss);
        } else if ("reply".equals(module)) {
            Reply reply = replyDao.findById(replyId).get();
            Set<User> users = reply.getUsers();
            likenum = reply.getUsers().size();
            if (!reply.getUsers().contains(user)) {

                users.add(user);
                number = 1;
            } else {

                users.remove(user);
                number = -1;
            }
            reply.setUsers(users);
            replyDao.save(reply);
        }


    }

    @Override
    public void replyPaging(HttpServletRequest req, Long selecttype, Long selectsort, int page, int size, Long userId, Model model) {

        Long num = Long.parseLong(req.getParameter("num"));
        disService.discussHandle(model, num, userId, page, size, selecttype, selectsort);

    }

    @Override
    public boolean replyDelete(HttpServletRequest req, int page, int size, Long userId, Model model) {
        User user = userDao.findById(userId).get();

        Long num = Long.parseLong(req.getParameter("num"));
        Discuss discuss = discussDao.findById(num).get();
        Long discussId = Long.parseLong(req.getParameter("replyId"));
        String module = req.getParameter("module");    //用来判断是保存在哪个表
        if (user.getSuperman()) {
        } else {
            if (Objects.equals(user.getUserId(), discuss.getUser().getUserId())) {
            } else {
                return false;
            }
        }
        if ("reply".equals(module)) {
            Reply reply = replyDao.findById(discussId).get();

            replyDao.delete(reply);
        } else if ("comment".equals(module)) {
            commentDao.deleteById(discussId);
        }
        disService.setDiscussMess(model, num, userId, page, size);
        model.addAttribute("manage", "manage");
        return true;

    }

    @Override
    public VoteEnum voteHandle(HttpServletRequest req, Long userId, Model model) {
        Long discussId = Long.parseLong(req.getParameter("discussId"));
        Long titleId = Long.parseLong(req.getParameter("titleId"));
        Integer selectOne = Integer.parseInt(req.getParameter("selectType"));
        Discuss discuss = discussDao.findById(discussId).get();
        User user = userDao.findById(userId).get();
        VoteTitles voteTitle = voteTitleListDao.findById(titleId).get();
        VoteTitleUser voteTitleUser = new VoteTitleUser(discuss.getVoteList().getVoteId(), voteTitle, user);
        VoteList vote = discuss.getVoteList();
        Date date = new Date();
        if (date.getTime() < vote.getStartTime().getTime()) {
            return VoteEnum.NOTSTART;
        } else if (date.getTime() > vote.getEndTime().getTime()) {
            return VoteEnum.OVER;
        } else {
            model.addAttribute("dateType", 3);
        }
        if (Objects.isNull(voteUserDao.findByVoteTitlesAndUser(voteTitle, user))) {
            voteService.savaVoteTitleUser(voteTitleUser);
        } else {
            return VoteEnum.HAVEVOTE;
        }
        voteService.voteServiceHandle(model, user, discuss);
        model.addAttribute("discuss", discuss);
        return VoteEnum.NORMAL;


    }

    @Override
    public void likeUserLoad(HttpServletRequest req, Model model, Long userId,String module) {


        Long replyId = Long.parseLong(req.getParameter("replyId"));

        Integer size = Integer.parseInt(req.getParameter("size"));
        User user = userDao.findById(userId).get();
        if ("discuss".equals(module)) {
            //处理讨论表的点赞，刷新
            likeThisFun(req, userId);
            disService.setDiscussMess(model, replyId, userId, 0, size);
        } else{
            //处理回复表的点赞，刷新
            String rightNum = req.getParameter("rightNum");
            likeThisFun(req, userId);
            Reply reply = replyDao.findById(replyId).get();
            int likeNum = reply.getUsers().size();
            Set<User> users = reply.getUsers();
            model.addAttribute("rightNum", rightNum);
            model.addAttribute("comments", commentDao.findByReply(reply).size());
            model.addAttribute("reply", reply);
            model.addAttribute("contain", users.contains(user));
            model.addAttribute("likeNum", likeNum);
            model.addAttribute("users", users);
        }

    }
}
