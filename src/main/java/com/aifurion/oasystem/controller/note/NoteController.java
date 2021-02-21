package com.aifurion.oasystem.controller.note;

import com.aifurion.oasystem.entity.note.Note;
import com.aifurion.oasystem.entity.note.Noteuser;
import com.aifurion.oasystem.service.NoteService;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/20 10:22
 */

@Controller
public class NoteController {


    @Autowired
    private NoteService noteService;

    	// 笔记主界面
	@GetMapping("/noteview")
	public String noteMain(Model model, HttpServletRequest request, HttpSession session,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "baseKey", required = false) String baseKey,
                       @RequestParam(value = "type", required = false) String type,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "time", required = false) String time,
                       @RequestParam(value = "icon", required = false) String icon) {

		noteService.noteMain(model, request, session, page, baseKey, type, status, time, icon);

		return "note/noteview";
	}


	@RequestMapping("/collectfind")
	public String collectFind(Model model, HttpServletRequest request, @RequestParam("iscollect") String iscollected,@RequestParam("cata") Long cid,
			HttpSession session,@RequestParam(value="page",defaultValue="0")int page,@RequestParam(value="baseKey",required=false)String baseKey,
						  @RequestParam(value = "type", required = false) String type,
						  @RequestParam(value = "status", required = false) String status,
						  @RequestParam(value = "time", required = false) String time,
						  @RequestParam(value = "icon", required = false) String icon) {

		noteService.collectFind(model, request, iscollected, cid, session, page, baseKey, type, status, time, icon);
		return "note/notewrite";

	}


		// 收藏
	@RequestMapping("/collect")
	public String collect(Model model,HttpServletRequest request, HttpSession session,@RequestParam(value="page",defaultValue="0")int page,
					   @RequestParam(value="baseKey",required=false)String baseKey,
					   @RequestParam(value = "type", required = false) String type,
					   @RequestParam(value = "status", required = false) String status,
					   @RequestParam(value = "time", required = false) String time,
					   @RequestParam(value = "icon", required = false) String icon) {


		noteService.collect(model, request, session, page, baseKey, type, status, time, icon);
		return "note/notewrite";
	}


	// 保存的post方法
	@PostMapping(value = "notesave")
	public String saveNote(@RequestParam("file") MultipartFile file, @Valid Note note, BindingResult br,
						   HttpServletRequest request, HttpSession session) throws IllegalStateException, IOException {

		noteService.saveNote(file, note, br, request, session);

		return "forward:/noteedit";
	}


		// 笔记批量删除
	@RequestMapping("/notesomedelete")
	public String deleteBatchNote(HttpServletRequest request, HttpSession session) {
		long realuserId = Long.parseLong(session.getAttribute("userId") + "");
		String sum = request.getParameter("sum");
		String[] strings = sum.split(";");
		for (String s : strings) {
			long noteids = Long.parseLong(s);
			noteService.deleteNote(realuserId, noteids);
		}
		return "redirect:/noteview";
	}

		// 笔记删除
	@RequestMapping("/notedelete")
	public String deleteNote(Model model, HttpServletRequest request, HttpSession session) {
		long realuserId = Long.parseLong(String.valueOf(session.getAttribute("userId")));
		String nid = request.getParameter("nid");
		long noteid = Long.parseLong(nid);
		Noteuser u = noteService.findNoteUserById(noteid, realuserId);
		if (u != null) {
			noteService.deleteNote(realuserId, noteid);
			return "redirect:/noteview";
		} else {

			return "redirect:/notlimit";
		}

	}


		// 目录删除
	@RequestMapping("/catadelete")
	public String testrwd(Model model, HttpServletRequest request, HttpSession session) {
		long realuserId = Long.parseLong(String.valueOf(session.getAttribute("userId")));

		String cid = request.getParameter("cid");
		long catalogid = Long.parseLong(cid);

		List<Note> noteList = noteService.findByCatalogId(catalogid, realuserId);
		// 没有做级联删除 先删除目录下的笔记 再删除目录
		for (Note note : noteList) {
			noteService.deleteNotByNoteId(note.getNoteId());
		}
		noteService.delteCatalogById(catalogid);

		return "redirect:/noteview";
	}


	// post请求 添加类型
	@PostMapping("noteview")
	public String addNoteType(HttpServletRequest request, @Param("title") String title, HttpSession session) {

		noteService.addNoteType(request, title, session);

		return "redirect:/noteview";
	}




	// 显示具体信息
	@RequestMapping("/noteinfo")
	public String showNoteInfo(@Param("id") String id, HttpServletRequest Request, HttpServletResponse response,
			HttpSession session) throws IOException {


		noteService.showNoteInfo(id, Request, response, session);
		return "note/noteinfo";
	}




	// 下载文件
	@RequestMapping("/down")
	public void downloadNoteFile(HttpServletResponse response, HttpServletRequest request) {

		noteService.downloadNoteFile(response, request);

	}



	// 显示表格所有
	@GetMapping("/notewrite")
	public String showAllNotes(Model model, HttpServletRequest request,  HttpSession session,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "baseKey", required = false) String baseKey,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "time", required = false) String time,
			@RequestParam(value = "icon", required = false) String icon) {


		noteService.showAllNotes(model, request, session, page, baseKey, type, status, time, icon);
		return "note/notewrite";
	}



	// 查找类型
		@RequestMapping("/notetype")
		public String showAllNoteType(Model model, HttpServletRequest request, @RequestParam("typeid") Long tid, @RequestParam("id") Long cid,
									  HttpSession session,@RequestParam(value="page",defaultValue="0")int page,
									  @RequestParam(value = "baseKey", required = false) String baseKey,@RequestParam(value = "type", required = false) String type,
									  @RequestParam(value = "status", required = false) String status,
									  @RequestParam(value = "time", required = false) String time,
									  @RequestParam(value = "icon", required = false) String icon) {


			noteService.showAllNoteType(model, request, tid, cid, session, page, baseKey, type, status, time, icon);
			return "note/notewrite";
		}



			//查找目录
	@RequestMapping("/notecata")
	public String findCatalog(Model model, HttpServletRequest request,  HttpSession session,
							  @RequestParam("id")String cid,
							  @RequestParam(value = "page", defaultValue = "0") int page,
							  @RequestParam(value = "baseKey", required = false) String baseKey,
							  @RequestParam(value = "type", required = false) String type,
							  @RequestParam(value = "status", required = false) String status,
							  @RequestParam(value = "time", required = false) String time,
							  @RequestParam(value = "icon", required = false) String icon){


		noteService.findCatalog(model, request, session, cid, page, baseKey, type, status, time, icon);
		return "note/notewrite";
	}




	// 编辑
	@RequestMapping(value = "/noteedit")
	public String editNote(HttpServletRequest request, HttpSession session,Model model,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {


		noteService.editNote(request, session, model, page, size);
		return "note/noteedit";
	}


	@RequestMapping("/namereceive")
	public String nameReceive(Model model,HttpServletRequest req, @SessionAttribute("userId") Long userId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size){

		noteService.nameReceive(model, req, userId, page, size);

		return "common/noterecivers";

	}























}
