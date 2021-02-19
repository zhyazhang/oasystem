package com.aifurion.oasystem.controller.inform;

import com.aifurion.oasystem.entity.notice.NoticesList;
import com.aifurion.oasystem.service.InformManageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 15:49
 */

@Controller
public class InformManageController {

    @Autowired
    private InformManageService informManageService;


    	/**
	 * 通知管理面板
	 *
	 * @return
	 */
	@RequestMapping("/infrommanage")
	public String inform(@RequestParam(value = "page", defaultValue = "0") int page, @SessionAttribute("userId") Long userId, Model model) {
        informManageService.inform(page, userId, model);
		return "inform/informmanage";
	}



	@RequestMapping("/forwardother")
	public String forwardOther(@SessionAttribute("userId")Long userId,@RequestParam(value="noticeId")Long noticeId){
        informManageService.forwardOther(userId, noticeId);
		return "redirect:/infromlist";
	}

	/**
	 * 通知管理删除
	 */

	@RequestMapping("/infromdelete")
	public String infromDelete(HttpSession session, HttpServletRequest req) {


        return informManageService.infromDelete(session, req);

	}



		/**
	 * 通知列表删除
	 */
	@RequestMapping("/informlistdelete")
	public String informListDelete(HttpServletRequest req, HttpSession session) {

       return informManageService.informListDelete(req, session);
	}



	/**
	 * 通知列表
	 *
	 * @return
	 */
	@RequestMapping("/infromlist")
	public String infromList(HttpSession session, HttpServletRequest req, Model model,
			@RequestParam(value="pageNum",defaultValue="1") int page) {

        informManageService.infromList(session, req, model, page);

		return "inform/informlist";
	}



	/**
	 * 编辑通知界面
	 */
	@RequestMapping("/informedit")
	public String infromEdit(HttpServletRequest req, HttpSession session, Model model) {

        informManageService.infromEdit(req, session, model);

		return "inform/informedit";
	}



	/**
	 * 详细通知显示
	 */
	@RequestMapping("/informshow")
	public String informShow(HttpServletRequest req, Model model) {
        informManageService.informShow(req, model);
		return "inform/informshow";
	}

	/**
	 * 系统管理表单验证
	 *
	 * @param req
	 * @param menu
	 * @param br
	 *            后台校验表单数据，不通过则回填数据，显示错误信息；通过则直接执行业务，例如新增、编辑等；
	 * @return
	 */
	@RequestMapping("/informcheck")
	public String verifyMessage(HttpServletRequest req, @Valid NoticesList menu, BindingResult br) {

        informManageService.verifyMessage(req, menu, br);
		return "forward:/informedit";
	}



}
