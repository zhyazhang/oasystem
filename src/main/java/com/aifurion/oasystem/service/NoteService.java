package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.note.Note;
import com.aifurion.oasystem.entity.note.Noteuser;
import org.apache.ibatis.annotations.Param;
import org.aspectj.weaver.ast.Not;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

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

    List<Note> findByCatalogId(Long catalogId, Long realUserId);

    void deleteNotByNoteId(long noteId);

    void deleteCatalogById(Long catalogId);

    void addNoteType(HttpServletRequest request, String title, HttpSession session);

    void showNoteInfo(String id, HttpServletRequest Request, HttpServletResponse response, HttpSession session);


    void downloadNoteFile(HttpServletResponse response, HttpServletRequest request);

    void showAllNotes(Model model, HttpServletRequest request, HttpSession session, int page,
                      String baseKey, String type, String status, String time, String icon);


    void showAllNoteType(Model model, HttpServletRequest request, Long tid, Long cid,
                           HttpSession session,  int page, String baseKey, String type,
                           String status, String time, String icon);

    void findCatalog(Model model, HttpServletRequest request, HttpSession session, String cid,
                     int page, String baseKey, String type, String status, String time, String icon);


    void editNote(HttpServletRequest Request, HttpSession session, Model model, int page, int size);


    void nameReceive(Model model, HttpServletRequest req, Long userId, int page, int size);



}
