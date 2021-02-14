package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.formVaild.BindingResultVOUtil;
import com.aifurion.oasystem.common.formVaild.MapToList;
import com.aifurion.oasystem.common.formVaild.ResultEnum;
import com.aifurion.oasystem.common.formVaild.ResultVO;
import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/19 10:41
 */

@Service
public class StatusServiceImpl implements StatusService {


    @Autowired
    private StatusDao statusDao;

    @Override
    public Iterable<SystemStatusList> findAll() {

        Iterable<SystemStatusList> all = statusDao.findAll();


        return all;
    }

    @Override
    public List<SystemStatusList> findByStatusNameLikeOrStatusModelLike(String name, String name2) {

        List<SystemStatusList> modelLike = statusDao.findByStatusNameLikeOrStatusModelLike(name, name2);
        return modelLike;
    }

    @Override
    public SystemStatusList findOne(Long id) {

        SystemStatusList statusList = statusDao.findById(id).get();

        return statusList;
    }

    @Override
    public void deleteStatus(Long id) {

        statusDao.deleteById(id);

    }

    @Override
    public void vaildForm(HttpServletRequest req, SystemStatusList menu, BindingResult br) {


        req.setAttribute("menuObj", menu);
		// 这里返回ResultVO对象，如果校验通过，ResultEnum.SUCCESS.getCode()返回的值为200；否则就是没有通过；
		ResultVO res = BindingResultVOUtil.hasErrors(br);
		// 校验失败
		if (!ResultEnum.SUCCESS.getCode().equals(res.getCode())) {
			List<Object> list = new MapToList<>().mapToList(res.getData());
			req.setAttribute("errormess", list.get(0).toString());
		}
		// 校验通过，下面写自己的逻辑业务
		else {
				HttpSession session = req.getSession();
			// 判断是否从编辑界面进来的，前面有"session.setAttribute("getId",getId);",在这里获取，并remove掉；
			if (!StringUtils.isEmpty(session.getAttribute("statusid"))) {
				Long menuId = (Long) session.getAttribute("statusid"); // 获取进入编辑界面的menuID值
				menu.setStatusId(menuId);
				session.removeAttribute("statusid");
			}
			// 执行业务代码
			statusDao.save(menu);
			req.setAttribute("success", "后台验证成功");
		}

    }


    @Override
    public SystemStatusList findByStatusModelAndStatusName(String statusModel, String statusName) {


        return statusDao.findByStatusModelAndStatusName(statusModel, statusName);

    }
}
