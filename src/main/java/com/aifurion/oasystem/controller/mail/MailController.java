package com.aifurion.oasystem.controller.mail;

import com.aifurion.oasystem.entity.mail.Inmaillist;
import com.aifurion.oasystem.entity.mail.Mailnumber;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.MailManageService;
import com.aifurion.oasystem.service.MailService;
import com.github.pagehelper.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 17:11
 */

@Controller
public class MailController {


    @Autowired
    private MailManageService mailManageService;

    @Autowired
	private MailService mailService;



    	/**
	 * 邮件管理主页
	 * @return
	 */
	@RequestMapping("/mail")
	public String index(@SessionAttribute("userId") Long userId, Model model,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "10") int size) {

		mailManageService.index(userId, model, page, size);

        return "mail/mail";

    }


    /**
	 * 删除邮件
	 */
	@RequestMapping("/alldelete")
	public String delete(HttpServletRequest req, @SessionAttribute("userId") Long userId, Model model,
						 @RequestParam(value = "page", defaultValue = "0") int page,
						 @RequestParam(value = "size", defaultValue = "10") int size){


		return mailManageService.delete(req, userId, model, page, size);

	}


	/**
	 * 批量查看
	 */
	@RequestMapping("/watch")
	public String watch(@SessionAttribute("userId") Long userId, Model model,HttpServletRequest req,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){

		mailManageService.watch(userId, model, req, page, size);
		return "mail/mailbody";
	}



		/**
	 * 批量标星
	 */
	@RequestMapping("/star")
	public String star(@SessionAttribute("userId") Long userId, Model model,HttpServletRequest req,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){


		mailManageService.star(userId, model, req, page, size);
		return "mail/mailbody";
	}


	/**
	 *邮箱条件查找
	 */
	@RequestMapping("/mailtitle")
	public String serch(@SessionAttribute("userId") Long userId, Model model,HttpServletRequest req,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){


		mailManageService.searchByMail(userId, model, req, page, size);
		return "mail/mailbody";
	}




	/**
	 * 账号管理
	 */
	@RequestMapping("/accountmanage")
	public String account(@SessionAttribute("userId") Long userId, Model model,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){

		mailManageService.account(userId, model, page, size);
		return "mail/mailmanage";
	}



	/**
	 * 账号各种排序
	 * 和查询
	 */
	@RequestMapping("/mailpaixu")
	public String sortAccount(HttpServletRequest request, @SessionAttribute("userId") Long userId, Model model,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "10") int size){

		mailManageService.sortAccount(request, userId, model, page, size);
		return "mail/mailtable";
	}


	/**
	 * 新增账号
	 * 修改账号
	 */
	@RequestMapping("/addaccount")
	public String add(@SessionAttribute("userId") Long userId, Model model,HttpServletRequest req){

		mailManageService.add(userId, model, req);
		return "mail/addaccounts";
	}


	/**
	 * 存邮箱账号
	 */
	@RequestMapping("/saveaccount")
	public String save(HttpServletRequest request, @Valid Mailnumber mail, BindingResult br, @SessionAttribute("userId") Long userId){

		mailManageService.save(request, mail, br, userId);
		return "forward:/addaccount";
	}


	/**
	 * 删除账号
	 */
	@RequestMapping("/dele")
	public String deleteAccount(HttpServletRequest request,@SessionAttribute("userId") Long userId){
		//得到账号id
		Long accountid=Long.parseLong(request.getParameter("id"));
		Mailnumber mail=mailService.findOne(accountid);
		if(mail.getMailUserId().getUserId().equals(userId)){
			mailService.deleteAccount(accountid);
		}else{
			return "redirect:/notlimit";
		}
		return "redirect:/accountmanage";
	}


	/**
	 * 写信
	 */
	@RequestMapping("/wmail")
	public  String writeMail(Model model, @SessionAttribute("userId") Long userId,HttpServletRequest request,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		mailManageService.writeMail(model, userId, request, page, size);

		return "mail/wirtemail";
	}

	/**
	 * 发送邮件
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@RequestMapping("/pushmail")
	public String pushMail(@RequestParam("file") MultipartFile file,
						   HttpServletRequest request, @Valid Inmaillist mail,
						   BindingResult br, @SessionAttribute("userId") Long userId) throws IllegalStateException, IOException{

		mailManageService.pushMail(file, request, mail, br, userId);

		return "redirect:/mail";
	}



	/**
	 * 用户姓名查找
	 */
	@RequestMapping("/names")
	public String search(Model model,HttpServletRequest req, @SessionAttribute("userId") Long userId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){

		mailManageService.searchByName(model, req, userId, page, size);

		return "common/recivers";

	}




	/**
	 * 最近邮件
	 */
	@RequestMapping("/amail")
	public  String recentlyMail(HttpServletRequest req,@SessionAttribute("userId") Long userId,Model model,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		mailManageService.recentlyMail(req, userId, model, page, size);
		return "mail/allmail";
	}



	/**
	 * 查看邮件
	 */
	@RequestMapping("/smail")
	public String seeMail(HttpServletRequest req,@SessionAttribute("userId") Long userId,Model model) {

		mailManageService.seeMail(req, userId, model);

		return "mail/seemail";
	}


	/**
	 *
	 */
	@RequestMapping("/refresh")
	public String refresh(HttpServletRequest req,@SessionAttribute("userId") Long userId,Model model,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){


		return mailManageService.refresh(req, userId, model, page, size);

	}













}
