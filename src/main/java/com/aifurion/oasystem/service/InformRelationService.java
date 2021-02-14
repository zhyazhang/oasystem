package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.notice.NoticeUserRelation;

import java.util.List;
import java.util.Map;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 13:34
 */
public interface InformRelationService {

    NoticeUserRelation save(NoticeUserRelation noticeRelation);

    List<NoticeUserRelation> saves(List<NoticeUserRelation> noticeUser);


    void deleteOne(NoticeUserRelation noticeRelation);


    List<Map<String, Object>> setList(List<Map<String, Object>> list);


    int isForward(Long noticeId, Long userId);

}
