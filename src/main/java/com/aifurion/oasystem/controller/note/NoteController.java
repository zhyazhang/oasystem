package com.aifurion.oasystem.controller.note;

import com.aifurion.oasystem.entity.note.Note;
import com.aifurion.oasystem.entity.note.Noteuser;
import com.aifurion.oasystem.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
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
		long realuserId = Long.parseLong(session.getAttribute("userId") + "");
		String nid = request.getParameter("nid");
		long noteid = Long.parseLong(nid);
		Noteuser u = noteService.findNoteUserById(noteid, realuserId);
		if (u != null) {
			noteService.deleteNote(realuserId, noteid);
			return "redirect:/noteview";
		} else {
			System.out.println("权限不匹配，不能删除");
			return "redirect:/notlimit";
		}

	}

















}
