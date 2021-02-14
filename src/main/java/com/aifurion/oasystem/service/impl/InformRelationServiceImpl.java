package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.inform.InformDao;
import com.aifurion.oasystem.dao.inform.InformRelationDao;
import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.notice.NoticeUserRelation;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.InformRelationService;
import org.dom4j.util.UserDataAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 13:35
 */

@Service
@Transactional
public class InformRelationServiceImpl implements InformRelationService {


    @Autowired
    private InformRelationDao informRelationDao;

    @Autowired
    private StatusDao statusDao;


    @Autowired
    private TypeDao typeDao;

    @Autowired
    private InformDao infomDao;

    @Autowired
    private UserDao userDao;


    //保存一个对象

    @Override
    public NoticeUserRelation save(NoticeUserRelation noticeRelation) {

        return informRelationDao.save(noticeRelation);
    }

    //保存多个

    @Override
    public List<NoticeUserRelation> saves(List<NoticeUserRelation> noticeUser) {

        return (List<NoticeUserRelation>) informRelationDao.saveAll(noticeUser);
    }


    //删除一个中间表

    @Override
    public void deleteOne(NoticeUserRelation noticeRelation) {

        informRelationDao.delete(noticeRelation);

    }


    //封装对象，将List<Map<String, Object>>中的值进行封装，例如type_id封装成相对应的名字
    @Override
    public List<Map<String, Object>> setList(List<Map<String, Object>> list) {

        for (Map<String, Object> map : list) {
            map.put("status", statusDao.findById((Long) map.get("status_id")).get().getStatusName());
            map.put("type", typeDao.findById((Long) map.get("type_id")).get().getTypeName());
            map.put("statusColor", statusDao.findById((Long) map.get("status_id")).get().getStatusColor());
            map.put("userName", userDao.findById((Long) map.get("user_id")).get().getUserName());
            map.put("deptName", userDao.findById((Long) map.get("user_id")).get().getDept().getDeptName());
            map.put("contain", this.isForward((Long) map.get("relatin_notice_id"), (Long) map.get("relatin_user_id")));
        }
        return list;


    }

    @Override
    public int isForward(Long noticeId, Long userId) {
        int count = 1;
        if (userDao.findByFatherId(userId).size() > 0) {
            List<User> users = userDao.findByFatherId(userId);
            if (informRelationDao.findByNoticeId(infomDao.findById(noticeId).get()) != null) {
                List<NoticeUserRelation> nul = informRelationDao.findByNoticeId(infomDao.findById(noticeId).get());
                for (NoticeUserRelation noticeUserRelation : nul) {
                    if (users.contains(noticeUserRelation.getUserId())) {
                        count = 2;
                    }
                }
                if (count != 2) {
                    count = 3;
                }
            }

        }
        return count;
    }
}
