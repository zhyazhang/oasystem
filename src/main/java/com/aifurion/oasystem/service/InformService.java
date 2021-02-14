package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.notice.NoticesList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 12:01
 */
public interface InformService {

    NoticesList save(NoticesList noticelist);

    void deleteOne(Long noticeId);


    List<Map<String, Object>> packaging(List<NoticesList> noticelist);

    Page<NoticesList> pageThis(int page, Long userId);

    Sort getSort();

    Page<NoticesList> pageThis(int page, Long userId, String baseKey, Object type, Object status, Object time);


}
