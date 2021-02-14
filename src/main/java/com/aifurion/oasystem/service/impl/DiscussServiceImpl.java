package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.discuss.CommentDao;
import com.aifurion.oasystem.dao.discuss.DiscussDao;
import com.aifurion.oasystem.dao.discuss.ReplyDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.discuss.Comment;
import com.aifurion.oasystem.entity.discuss.Discuss;
import com.aifurion.oasystem.entity.discuss.Reply;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.DiscussService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 13:22
 */

@Service
public class DiscussServiceImpl implements DiscussService {


    @Autowired
	private DiscussDao discussDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private TypeDao typeDao;

	@Autowired
	private CommentDao commentDao;

    @Autowired
    private ReplyDao replyDao;


    @Override
    public Discuss save(Discuss d) {
        return discussDao.save(d);
    }

    @Override
    public void deleteDiscuss(Long discussId) {
        discussDao.deleteById(discussId);

    }

    @Override
    public Discuss addOneDiscuss(Long id) {
        Discuss discuss=discussDao.findById(id).get();
		discuss.setVisitNum(discuss.getVisitNum()+1);
		return this.save(discuss);
    }

    @Override
    public Page<Discuss> paging(int page, String baseKey, Long userId, String type, String time, String visitnum) {
       List<Sort.Order> orders = new ArrayList<>();
		Pageable pa = setPageable(page, type, time, visitnum, orders);
		if(StringUtils.isEmpty(userId)){
			if (!StringUtils.isEmpty(baseKey)) {

				return discussDao.findByTitleLike("%"+baseKey+"%",pa);
			}

			return discussDao.findAll(pa);
		}else{
			User user=userDao.findById(userId).get();
			if(user.getSuperman()){
				if (!StringUtils.isEmpty(baseKey)) {

					return discussDao.findByTitleLike("%"+baseKey+"%",pa);
				}

				return discussDao.findAll(pa);
			}else{
				if (!StringUtils.isEmpty(baseKey)) {

					return discussDao.findByUserAndTitleLike(user,"%"+baseKey+"%",pa);
				}

				return discussDao.findByUser(user, pa);
			}
		}
    }

    @Override
    public Page<Discuss> pagingMe(int page, String baseKey, Long userId, String type, String time, String visitnum) {
       List<Sort.Order> orders = new ArrayList<>();
		Pageable pa = setPageable(page, type, time, visitnum, orders);
		User user=userDao.findById(userId).get();
		if (!StringUtils.isEmpty(baseKey)) {

			return discussDao.findByUserAndTitleLike(user,"%"+baseKey+"%",pa);
		}

		return discussDao.findByUser(user, pa);
    }

    @Override
    public Pageable setPageable(int page, String type, String time, String visitnum, List<Sort.Order> orders) {
        int size=10;
		if (!StringUtils.isEmpty(type)) {
			if ("1".equals(type)) {
				orders.add(new Sort.Order(Sort.Direction.ASC, "typeId"));
			} else {
				orders.add(new Sort.Order(Sort.Direction.DESC, "typeId"));
			}
		} else if (!StringUtils.isEmpty(time)) {
			if ("1".equals(time)) {
				orders.add(new Sort.Order(Sort.Direction.DESC, "modifyTime"));
			} else {
				orders.add(new Sort.Order(Sort.Direction.ASC, "modifyTime"));
			}
		} else if (!StringUtils.isEmpty(visitnum)) {
			if ("1".equals(visitnum)) {
				orders.add(new Sort.Order(Sort.Direction.DESC, "visitNum"));
			} else {
				orders.add(new Sort.Order(Sort.Direction.ASC, "visitNum"));
			}
		}else {
			orders.add(new Sort.Order(Sort.Direction.ASC, "typeId"));
			orders.add(new Sort.Order(Sort.Direction.DESC, "modifyTime"));
		}
		Sort sort = Sort.by(orders);
		Pageable pa = PageRequest.of(page, size, sort);
		return pa;
    }

    @Override
    public void setDiscussMess(Model model, Long num, Long userId, int page, int size) {

        discussHandle(model, num, userId, page, size,null,null);

    }

    @Override
    public void discussHandle(Model model, Long num, Long userId, int page, int size, Long selectType, Long selectSort) {

        Pageable pa;
		Page<Reply> replyPage = null;
		if(!StringUtils.isEmpty(selectSort)&& selectSort==1){
			pa=PageRequest.of(page, size,Sort.by(Sort.Direction.DESC,"replayTime"));
		}else{
			pa=PageRequest.of(page, size,Sort.by(Sort.Direction.ASC,"replayTime"));
		}
		Discuss discuss=discussDao.findById(num).get();
		User user=userDao.findById(userId).get();
		Boolean discussContain=discuss.getUsers().contains(user);
		int discussLikeNum=discuss.getUsers().size();
		Set<User> setUsers=discuss.getUsers();
		model.addAttribute("discussContain", discussContain);
		model.addAttribute("discussLikeNum", discussLikeNum);
		model.addAttribute("setUsers", setUsers);
		//这句是关键代码，从数据库拿到所有数据，也进行排序，只要在这进行判断
		if(!StringUtils.isEmpty(selectType)){
			User user2=userDao.findById(selectType).get();
			replyPage=replyDao.findByDiscussAndUser(discuss, user2, pa);
		}else{
			replyPage=replyDao.findByDiscuss(discuss,pa);				//根据讨论id找到所有的回复表
		}
		List<Reply> replyCols=replyDao.findByDiscuss(discuss);
		List<Reply> replyList=replyPage.getContent();
		List<Map<String, Object>> replys=this.replyPackaging(replyList,userId);		//对回复表字段进行封装，主要是为了获取到评论数
		if(replyCols.size()>0){
			Long[] replyLong=new Long[replyCols.size()];							//用数组来结束所有回复表的id
			for (int i = 0; i < replyCols.size(); i++) {
				replyLong[i]=replyCols.get(i).getReplyId();
			}
			List<Comment> commentList=commentDao.findComments(replyLong);			//in 查找所有回复id的所有评论
			List<Map<String, Object>> commentMap=this.commentPackaging(commentList);	//对评论字段进行封装
			model.addAttribute("commentList", commentMap);
			int chatNum=commentList.size()+replyCols.size();
			model.addAttribute("chatNum", chatNum);
		}		model.addAttribute("replyList", replys);
		model.addAttribute("discuss", discuss);
		model.addAttribute("page", replyPage);
		model.addAttribute("user", discuss.getUser());

    }

    @Override
    public Integer getComments(Discuss discuss) {
       int chatNum=0;
		List<Reply> replyCols=replyDao.findByDiscuss(discuss);
		if(replyCols.size()>0){
			Long[] replyLong=new Long[replyCols.size()];							//用数组来结束所有回复表的id
			for (int i = 0; i < replyCols.size(); i++) {
				replyLong[i]=replyCols.get(i).getReplyId();
			}
			List<Comment> commentList=commentDao.findComments(replyLong);			//in 查找所有回复id的所有评论
			chatNum=commentList.size()+replyCols.size();
		}
		return chatNum;
    }

    @Override
    public List<Map<String, Object>> replyPackaging(List<Reply> replyList, Long userId) {
        User user=userDao.findById(userId).get();
		List<Map<String, Object>> replyMap=new ArrayList<>();
		for (int i = 0; i < replyList.size(); i++) {
			Map<String, Object> result=new HashMap<>();
			if(replyList.get(i)==null||replyList.get(i).getUsers()==null){
				result.put("contain", false);
				result.put("likenum", 0);
			}else{
				result.put("contain", replyList.get(i).getUsers().contains(user));
				result.put("likenum", replyList.get(i).getUsers().size());
			}
			result.put("count",commentDao.findByReply(replyList.get(i)).size());
			result.put("replyLikeUsers", replyList.get(i).getUsers());

			result.put("replyId",replyList.get(i).getReplyId());
			result.put("replayTime",replyList.get(i).getReplayTime());
			result.put("content",replyList.get(i).getContent());
			result.put("user",replyList.get(i).getUser());
			result.put("discuss",replyList.get(i).getDiscuss());
			replyMap.add(result);
		}
		return replyMap;
    }

    @Override
    public List<Map<String, Object>> commentPackaging(List<Comment> commentList) {
       List<Map<String, Object>> commentMap=new ArrayList<>();
        for (Comment comment : commentList) {
            Map<String, Object> map = new HashMap<>();
            map.put("commentId", comment.getCommentId());
            map.put("comment", comment.getComment());
            map.put("time", comment.getTime());
            map.put("user", comment.getUser());
            map.put("reply", comment.getReply().getReplyId());
            commentMap.add(map);
        }
		return commentMap;
    }

    @Override
    public List<Map<String, Object>> packaging(List<Discuss> list) {
       List<Map<String, Object>> listMap = new ArrayList<>();
        for (Discuss discuss : list) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", discuss.getDiscussId());
            result.put("typeName", typeDao.findById(discuss.getTypeId()).get().getTypeName());
            result.put("userName", discuss.getUser().getUserName());
            if (discuss.getUsers() == null) {
                result.put("likeNum", 0);
            } else {
                result.put("likeNum", discuss.getUsers().size());
            }
            result.put("commentsNum", getComments(discuss));
            result.put("title", discuss.getTitle());
            result.put("createTime", discuss.getCreateTime());
            result.put("visitNum", discuss.getVisitNum());
            result.put("typecolor", typeDao.findById(discuss.getTypeId()).get().getTypeColor());
            listMap.add(result);
        }
		return listMap;
    }
}
