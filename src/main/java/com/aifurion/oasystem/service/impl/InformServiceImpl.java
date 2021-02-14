package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.inform.InformDao;
import com.aifurion.oasystem.dao.inform.InformRelationDao;
import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.notice.NoticeUserRelation;
import com.aifurion.oasystem.entity.notice.NoticesList;
import com.aifurion.oasystem.service.InformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 12:05
 */

@Service
@Transactional
public class InformServiceImpl implements InformService {

    @Autowired
    private InformDao informDao;
    @Autowired
    private InformRelationDao informRelationDao;
    @Autowired
    private TypeDao typeDao;
    @Autowired
    private StatusDao statusDao;
    @Autowired
    private UserDao userDao;



    //保存通知
    @Override
    public NoticesList save(NoticesList noticelist) {
        return informDao.save(noticelist);
    }

    //删除通知
    @Override
    public void deleteOne(Long noticeId) {

        NoticesList noticesList = informDao.findById(noticeId).get();
        List<NoticeUserRelation> relationList = informRelationDao.findByNoticeId(noticesList);

        informRelationDao.deleteAll(relationList);

        informDao.deleteById(noticeId);
        System.out.println("删除成功");


    }

    @Override
    public List<Map<String, Object>> packaging(List<NoticesList> noticelist) {


        List<Map<String, Object>> list = new ArrayList<>();
		for (int i = 0; i < noticelist.size(); i++) {
			Map<String, Object> result = new HashMap<>();
			result.put("noticeId", noticelist.get(i).getNoticeId());
			result.put("typename", typeDao.findname(noticelist.get(i).getTypeId()));
			result.put("statusname", statusDao.findname(noticelist.get(i).getStatusId()));
			result.put("statuscolor", statusDao.findcolor(noticelist.get(i).getStatusId()));
			result.put("title", noticelist.get(i).getTitle());
			result.put("noticeTime", noticelist.get(i).getNoticeTime());
			result.put("top", noticelist.get(i).getTop());
			result.put("url", noticelist.get(i).getUrl());
			result.put("username", userDao.findById(noticelist.get(i).getUserId()).get().getUserName());
			result.put("deptname", userDao.findById(noticelist.get(i).getUserId()).get().getDept().getDeptName());
			list.add(result);
		}
		return list;
    }

    @Override
    public Page<NoticesList> pageThis(int page, Long userId) {


        int size=10;
		Sort sort = getSort();
		Pageable pa = PageRequest.of(page, size, sort);
		return informDao.findByUserId(userId, pa);

    }

    @Override
    public Sort getSort() {

        List<Sort.Order> orders = new ArrayList<>();
		orders.addAll(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "top"), new Sort.Order(Sort.Direction.DESC, "modifyTime")));

		Sort sort = Sort.by(orders);

		return sort;

    }

    @Override
    public Page<NoticesList> pageThis(int page, Long userId, String baseKey, Object type, Object status, Object time) {
        int size=10;
		List<Sort.Order> orders = new ArrayList<>();
		Pageable pa=null;
		//根据类型排序
		if(!StringUtils.isEmpty(type)){
			if("1".equals(type)){
				orders.add(new Sort.Order(Sort.Direction.DESC, "typeId"));
			}
			else{
				orders.add(new Sort.Order(Sort.Direction.ASC, "typeId"));
			}
		}
		//根据状态排序
		else if(!StringUtils.isEmpty(status)){
			if("1".equals(status)){
				orders.add(new Sort.Order(Sort.Direction.DESC, "statusId"));
			}
			else{
				orders.add(new Sort.Order(Sort.Direction.ASC, "statusId"));
			}
		}
		//根据时间排序
		else if(!StringUtils.isEmpty(time)){
			if("1".equals(time)){
				orders.add(new Sort.Order(Sort.Direction.DESC, "modifyTime"));
			}
			else{
				orders.add(new Sort.Order(Sort.Direction.ASC, "modifyTime"));
			}
		}
		else if (!StringUtils.isEmpty(baseKey)) {
			String key="%"+baseKey+"%";
			Sort sort = getSort();
			pa=PageRequest.of(page, size, sort);

			return informDao.findByBaseKey(userId, key,pa);
		}
		System.out.println("orders:"+orders);
		if(orders.size()>0){
			Sort sort = Sort.by(orders);
			 pa= PageRequest.of(page, size, sort);
		}else{
			pa=PageRequest.of(page, size);
		}
		return informDao.findByUserId(userId, pa);
	}
}
