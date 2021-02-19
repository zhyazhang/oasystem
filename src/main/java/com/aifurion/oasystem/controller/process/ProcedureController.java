package com.aifurion.oasystem.controller.process;

import com.aifurion.oasystem.entity.process.*;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.ProcedureService;
import com.aifurion.oasystem.service.ProcessService;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:02
 */

@Controller
public class ProcedureController {


	@Autowired
	private ProcedureService procedureService;

	@Autowired
	private ProcessService processService;


    	//新增页面
	@GetMapping("/xinxeng")
	public String add(){
		return "process/procedure";
	}


	@RequestMapping("/burse")
	public String bursement(Model model, @SessionAttribute("userId") Long userId, HttpServletRequest request,
							@RequestParam(value = "page", defaultValue = "0") int page,
							@RequestParam(value = "size", defaultValue = "10") int size){

		procedureService.bursement(model, userId, request, page, size);

		return "process/bursement";
	}




	@RequestMapping("/apply")
	public String apply(@RequestParam("filePath") MultipartFile filePath, HttpServletRequest req, @Valid Bursement bu, BindingResult br,
						@SessionAttribute("userId") Long userId) throws IllegalStateException, IOException {


		return procedureService.apply(filePath, req, bu, br, userId);

	}


	/**
	 * 查找出自己的申请
	 * @return
	 */
	@RequestMapping("/flowmanage")
	public String flowManage(@SessionAttribute("userId") Long userId,Model model,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){

		procedureService.flowManage(userId, model, page, size);
		return "process/flowmanage";
	}



	/**
	 * 申请人查看流程条件查询
	 */
	@RequestMapping("/shenser")
	public String ser(@SessionAttribute("userId") Long userId,Model model,HttpServletRequest req,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){

		procedureService.ser(userId, model, req, page, size);

		return "process/managetable";
	}

	/**
	 * 流程审核
	 * @return
	 */
	@RequestMapping("/audit")
	public String auding(@SessionAttribute("userId") Long userId,Model model,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){

		procedureService.auding(userId, model, page, size);
		return "process/auditing";
	}



	/**
	 * 流程审核条件查询
	 * @return
	 */
	@RequestMapping("/serch")
	public String serch(@SessionAttribute("userId") Long userId,Model model,HttpServletRequest req,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){

		procedureService.serch(userId, model, req, page, size);

		return "process/audtable";
	}



		/**
	 * 查看详细
	 * @return
	 */
	@RequestMapping("/particular")
	public String particular(@SessionAttribute("userId") Long userId,Model model,HttpServletRequest req){


		return procedureService.particular(userId, model, req);
	}

	/**
	 * 进入审核页面
	 * @return
	 */
	@RequestMapping("/auditing")
	public String auditing(@SessionAttribute("userId") Long userId,Model model,HttpServletRequest req,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){

		procedureService.auditing(userId, model, req, page, size);

		return "process/audetail";

	}



	/**
	 * 审核确定的页面
	 * @return
	 */
	@RequestMapping("/susave")
	public String save(@SessionAttribute("userId") Long userId, Model model, HttpServletRequest req, Reviewed reviewed){


		return procedureService.save(userId, model, req, reviewed);

	}


		//出差费用
	@RequestMapping("/evemoney")
	public String evemoney(Model model, @SessionAttribute("userId") Long userId,HttpServletRequest req,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){
		procedureService.evemoney(model, userId, req, page, size);

		return "process/evectionmoney";
	}


	/**
	 * 出差费用表单接收
	 */
	@RequestMapping("/moneyeve")
	public String moneyeve(@RequestParam("filePath")MultipartFile filePath, HttpServletRequest req, @Valid EvectionMoney eve, BindingResult br,
						   @SessionAttribute("userId") Long userId, Model model) throws IllegalStateException, IOException{


		return procedureService.moneyeve(filePath, req, eve, br, userId, model);
	}

		//出差申请
	@RequestMapping("/evection")
	public String evection(Model model, @SessionAttribute("userId") Long userId,HttpServletRequest request,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){
		procedureService.evection(model, userId, request, page, size);
		return "process/evection";
	}

	@RequestMapping("/evec")
	public String evec(@RequestParam("filePath")MultipartFile filePath, HttpServletRequest req, @Valid Evection eve, BindingResult br,
					   @SessionAttribute("userId") Long userId) throws IllegalStateException, IOException{

		return procedureService.evec(filePath, req, eve, br, userId);
	}

	//加班申请
		@RequestMapping("/overtime")
		public String overtime(Model model, @SessionAttribute("userId") Long userId,HttpServletRequest request,
				@RequestParam(value = "page", defaultValue = "0") int page,
				@RequestParam(value = "size", defaultValue = "10") int size){

			procedureService.overtime(model, userId, request, page, size);
			return "process/overtime";
		}


		/**
	 * 加班申请接收
	 */
		@RequestMapping("/over")
		public String over(HttpServletRequest req,@Valid Overtime eve,BindingResult br,
				@SessionAttribute("userId") Long userId) throws IllegalStateException, IOException{


			return procedureService.over(req, eve, br, userId);

		}




			//请假申请
	@RequestMapping("/holiday")
	public String holiday(Model model, @SessionAttribute("userId") Long userId,HttpServletRequest request,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){
		//查找类型
		procedureService.holiday(model, userId, request, page, size);

		return "process/holiday";
	}


		/**
	 * 请假申请接收
	 */


	@RequestMapping("/holi")
	public String holi(@RequestParam("filePath")MultipartFile filePath,HttpServletRequest req,@Valid Holiday eve,BindingResult br,
			@SessionAttribute("userId") Long userId,Model model) throws IllegalStateException, IOException{

		return procedureService.holi(filePath, req, eve, br, userId, model);

	}

		//转正申请
	@RequestMapping("/regular")
	public String regular(Model model, @SessionAttribute("userId") Long userId,HttpServletRequest request,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){
		processService.index6(model, userId, page, size);
		return "process/regular";
	}


		/**
	 * 转正申请接收
	 */
		@RequestMapping("/regu")
		public String regu(HttpServletRequest req,@Valid Regular eve,BindingResult br,
				@SessionAttribute("userId") Long userId,Model model) throws IllegalStateException, IOException{

			return procedureService.regu(req, eve, br, userId, model);

		}


			//离职申请
	@RequestMapping("/resign")
	public String resign(Model model, @SessionAttribute("userId") Long userId,HttpServletRequest request,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){
		processService.index6(model, userId, page, size);
		return "process/resign";
	}


	/**
	 * 离职申请接收
	 */
		@RequestMapping("/res")
		public String res(HttpServletRequest req,@Valid Resign eve,BindingResult br,
				@SessionAttribute("userId") Long userId,Model model) throws IllegalStateException, IOException{

			return procedureService.res(req, eve, br, userId, model);
		}



		/**
		 * 删除
		 */
		@RequestMapping("/sdelete")
		public String dele(HttpServletRequest req,@SessionAttribute("userId") Long userId,Model model){

			return procedureService.dele(req, userId, model);

		}


				/**
		 * 下载文件
		 * @param response
		 * @param fileid
		 */
		@RequestMapping("/file")
		public void downFile(HttpServletResponse response, @RequestParam("fileid") Long fileid) {

			procedureService.downFile(response, fileid);
		}


				/**
		 * 图片预览
		 */
		@RequestMapping("/show/**")
		public void image(Model model, HttpServletResponse response, @SessionAttribute("userId") Long userId, HttpServletRequest request)
				throws IOException {

			procedureService.image(model, response, userId, request);

		}


























}
