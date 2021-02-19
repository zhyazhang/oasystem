package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.formVaild.BindingResultVOUtil;
import com.aifurion.oasystem.common.formVaild.MapToList;
import com.aifurion.oasystem.common.formVaild.ResultEnum;
import com.aifurion.oasystem.common.formVaild.ResultVO;
import com.aifurion.oasystem.dao.inform.InformDao;
import com.aifurion.oasystem.dao.inform.InformRelationDao;
import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.user.DeptDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.notice.NoticeUserRelation;
import com.aifurion.oasystem.entity.notice.NoticesList;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.mapper.NoticeMapper;
import com.aifurion.oasystem.service.InformManageService;
import com.aifurion.oasystem.service.InformRelationService;
import com.aifurion.oasystem.service.InformService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 15:50
 */

@Service
public class InformManageServiceImpl implements InformManageService {

    @Autowired
	private StatusDao statusDao;

	@Autowired
	private TypeDao typeDao;

	@Autowired
	private InformDao informDao;

	@Autowired
	private InformService informService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private DeptDao deptDao;

	@Autowired
	private InformRelationDao informrelationDao;

	@Autowired
	private InformRelationService informRelationService;

	@Autowired
	private NoticeMapper noticeMapper;


    @Override
    public void inform(int page, Long userId, Model model) {

        Page<NoticesList> page2 = informService.pageThis(page,userId);
		List<NoticesList> noticeList=page2.getContent();
		List<Map<String, Object>> list=informService.packaging(noticeList);
		model.addAttribute("list", list);
		model.addAttribute("page", page2);
		//设置变量，需要load的url；
        model.addAttribute("url", "infrommanagepaging");

    }

    @Override
    public void forwardOther(Long userId, Long noticeId) {


        List<User> users=userDao.findByFatherId(userId);
		NoticesList nl=informDao.findById(noticeId).get();
		List<NoticeUserRelation> nurs=new ArrayList<>();
		for (User user : users) {
			nurs.add(new NoticeUserRelation(nl, user, false));
		}
        informRelationService.saves(nurs);
    }

    @Override
    public String infromDelete(HttpSession session, HttpServletRequest req) {


        Long noticeId = Long.parseLong(req.getParameter("id"));
		Long userId = Long.parseLong(session.getAttribute("userId") + "");
		NoticesList notice = informDao.findById(noticeId).get();
		if (!Objects.equals(userId, notice.getUserId())) {
			return "redirect:/notlimit";
		}
        informService.deleteOne(noticeId);
        return "redirect:/infrommanage";


    }


    @Override
    public String informListDelete(HttpServletRequest req, HttpSession session) {


        Long userId = Long.parseLong(session.getAttribute("userId") + "");
		Long noticeId = Long.parseLong(req.getParameter("id"));
		NoticeUserRelation relation = informrelationDao.findByUserIdAndNoticeId(userDao.findById(userId).get(),
				informDao.findById(noticeId).get());
		if (Objects.isNull(relation)) {
			return "redirect:/notlimit";
		}
		informRelationService.deleteOne(relation);
		return "forward:/infromlist";
    }

    @Override
    public void infromList(HttpSession session, HttpServletRequest req, Model model, int page) {

        Long userId = Long.parseLong(session.getAttribute("userId") + "");
		PageHelper.startPage(page, 10);
		List<Map<String, Object>> list = noticeMapper.findMyNotice(userId);
		PageInfo<Map<String, Object>> pageinfo=new PageInfo<Map<String, Object>>(list);
		List<Map<String, Object>> list2=informRelationService.setList(list);
		model.addAttribute("url", "informlistpaging");
		model.addAttribute("list", list2);
		model.addAttribute("page", pageinfo);

    }

    @Override
    public void infromEdit(HttpServletRequest req, HttpSession session, Model model) {

        session.removeAttribute("noticeId");
		List<SystemTypeList> typeList = typeDao.findByTypeModel("inform");
		List<SystemStatusList> statusList = statusDao.findByStatusModel("inform");
		if (!StringUtils.isEmpty(req.getAttribute("errormess"))) {
			req.setAttribute("errormess", req.getAttribute("errormess"));
		}
		if (!StringUtils.isEmpty(req.getAttribute("success"))) {
			req.setAttribute("success", "数据保存成功");
		}
		req.setAttribute("typeList", typeList);
		req.setAttribute("statusList", statusList);
		if (!StringUtils.isEmpty(req.getParameter("id"))) {
			Long noticeId = Long.parseLong(req.getParameter("id"));
			NoticesList noticeList = informDao.findById(noticeId).get();
			model.addAttribute("noticeList", noticeList);
			model.addAttribute("typeName", typeDao.findById(noticeList.getTypeId()).get().getTypeName());
			model.addAttribute("statusName", statusDao.findById(noticeList.getStatusId()).get().getStatusName());
			session.setAttribute("noticeId", noticeId);
		}

    }

    @Override
    public void informShow(HttpServletRequest req, Model model) {

        Long noticeId = Long.parseLong(req.getParameter("id"));
		if (!StringUtils.isEmpty(req.getParameter("read"))) {
			if (("0").equals(req.getParameter("read"))) {
				Long relationId = Long.parseLong(req.getParameter("relationid"));
				NoticeUserRelation relation = informrelationDao.findById(relationId).get();
				relation.setRead(true);
				informRelationService.save(relation);
			}
		}
		NoticesList notice = informDao.findById(noticeId).get();
		User user = userDao.findById(notice.getUserId()).get();
		model.addAttribute("notice", notice);
        model.addAttribute("userName", user.getUserName());

    }

    @Override
    public void verifyMessage(HttpServletRequest req, @Valid NoticesList menu, BindingResult br) {
        HttpSession session = req.getSession();
		Long menuId = null;
		req.setAttribute("menuObj", menu);
		Long userId = Long.parseLong(session.getAttribute("userId") + "");
		menu.setUserId(userId);

		// 这里返回ResultVO对象，如果校验通过，ResultEnum.SUCCESS.getCode()返回的值为200；否则就是没有通过；
		ResultVO res = BindingResultVOUtil.hasErrors(br);
		// 校验失败
		if (!ResultEnum.SUCCESS.getCode().equals(res.getCode())) {
			List<Object> list = new MapToList<>().mapToList(res.getData());
			req.setAttribute("errormess", list.get(0).toString());

		}
		// 校验通过，下面写自己的逻辑业务
		else {
			// 判断是否从编辑界面进来的，前面有"session.setAttribute("getId",getId);",在这里获取，并remove掉；
			if (!StringUtils.isEmpty(session.getAttribute("noticeId"))) {
				menuId = (Long) session.getAttribute("noticeId"); // 获取进入编辑界面的menuID值
				NoticesList inform = informDao.findById(menuId).get();
				menu.setNoticeTime(inform.getNoticeTime());
				menu.setNoticeId(menuId);
				session.removeAttribute("noticeId");
				informService.save(menu);
			} else {
				menu.setNoticeTime(new Date());
				menu.setUserId(userId);
				NoticesList noticeList = informService.save(menu);
				List<User> userList = userDao.findByFatherId(userId);
				for (User user : userList) {
					informRelationService.save(new NoticeUserRelation(noticeList, user, false));
				}
			}
			// 执行业务代码
			req.setAttribute("success", "后台验证成功");
		}


    }
}
