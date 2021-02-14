package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.discuss.Comment;
import com.aifurion.oasystem.entity.discuss.Discuss;
import com.aifurion.oasystem.entity.discuss.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 13:21
 */
public interface DiscussService {


    Discuss save(Discuss d);


    void deleteDiscuss(Long discussId);


    Discuss addOneDiscuss(Long id);


    Page<Discuss> paging(int page, String baseKey, Long userId, String type, String time, String visitnum);


    Page<Discuss> pagingMe(int page, String baseKey, Long userId, String type, String time, String visitnum);


    Pageable setPageable(int page, String type, String time, String visitnum, List<Sort.Order> orders);


    void setDiscussMess(Model model, Long num, Long userId, int page, int size);


    void discussHandle(Model model, Long num, Long userId, int page, int size, Long selectType, Long selectSort);


    Integer getComments(Discuss discuss);


    List<Map<String, Object>> replyPackaging(List<Reply> replyList, Long userId);


    List<Map<String, Object>> commentPackaging(List<Comment> commentList);


    List<Map<String, Object>> packaging(List<Discuss> list);



}
