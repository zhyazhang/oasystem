package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.note.Note;
import com.aifurion.oasystem.entity.note.Noteuser;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/20 10:22
 */


public interface NoteService {

    void noteMain(Model model, HttpServletRequest request, HttpSession session,
                    int page, String baseKey, String type, String status, String time, String icon);

    void collectFind(Model model, HttpServletRequest request, String iscollected, Long cid,
                     HttpSession session, int page, String baseKey,
                     String type, String status, String time, String icon);

    void collect(Model model, HttpServletRequest request, HttpSession session, int page,
                String baseKey, String type, String status, String time, String icon);


    void saveNote(MultipartFile file, Note note, BindingResult br, HttpServletRequest request, HttpSession session);

    void deleteNote(long realuserId, long noteid);

    Noteuser findNoteUserById(Long noteId, Long realUerId);


}
