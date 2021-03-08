package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.CommonMethods;
import com.aifurion.oasystem.common.formVaild.BindingResultVOUtil;
import com.aifurion.oasystem.common.formVaild.MapToList;
import com.aifurion.oasystem.common.formVaild.ResultEnum;
import com.aifurion.oasystem.common.formVaild.ResultVO;
import com.aifurion.oasystem.dao.note.AttachmentDao;
import com.aifurion.oasystem.dao.note.CatalogDao;
import com.aifurion.oasystem.dao.note.NoteDao;
import com.aifurion.oasystem.dao.note.NoteUserDao;
import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.user.DeptDao;
import com.aifurion.oasystem.dao.user.PositionDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.note.Attachment;
import com.aifurion.oasystem.entity.note.Catalog;
import com.aifurion.oasystem.entity.note.Note;
import com.aifurion.oasystem.entity.note.Noteuser;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.user.Dept;
import com.aifurion.oasystem.entity.user.Position;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.FileService;
import com.aifurion.oasystem.service.NoteService;
import com.aifurion.oasystem.service.ProcessService;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/20 10:23
 */


@Service
public class NoteServiceImpl implements NoteService {


    @Autowired
    private FileService fileService;
    @Autowired
    private NoteDao noteDao;
    @Autowired
    private NoteService NoteService;
    @Autowired
    private CatalogDao catalogDao;
    @Autowired
    private TypeDao typeDao;
    @Autowired
    private StatusDao statusDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AttachmentDao attachmentDao;
    @Autowired
    private NoteUserDao noteUserDao;
    @Autowired
    private ProcessService processService;
    @Autowired
    private DeptDao deptDao;
    @Autowired
    private PositionDao positionDao;

    @Autowired
    private CommonMethods commonMethods;


    private Attachment attachment;
    private List<Note> noteList;
    private List<Catalog> cataloglist = new ArrayList<Catalog>();
    private List<SystemTypeList> typeLists;
    private List<SystemStatusList> statusLists;


    @Override
    public void nameReceive(Model model, HttpServletRequest req, Long userId, int page, int size) {


        Pageable pa = PageRequest.of(page, size);
        String name = null;
        String qufen = null;
        Page<User> pageuser = null;
        List<User> userlist = null;

        if (!StringUtil.isEmpty(req.getParameter("title"))) {
            name = req.getParameter("title").trim();
        }
        if (!StringUtil.isEmpty(req.getParameter("qufen"))) {
            qufen = req.getParameter("qufen").trim();

            if (StringUtil.isEmpty(name)) {
                // 查询部门下面的员工
                pageuser = userDao.findByFatherId(userId, pa);
            } else {
                // 查询名字模糊查询员工
                pageuser = userDao.findbyFatherId(name, userId, pa);
            }

        } else {
            if (StringUtil.isEmpty(name)) {
                //查看用户并分页
                pageuser = userDao.findAll(pa);
            } else {
                pageuser = userDao.findbyUserNameLike(name, pa);
            }
        }
        userlist = pageuser.getContent();
        // 查询部门表
        Iterable<Dept> deptlist = deptDao.findAll();
        // 查职位表
        Iterable<Position> poslist = positionDao.findAll();
        model.addAttribute("emplist", userlist);
        model.addAttribute("page", pageuser);
        model.addAttribute("deptlist", deptlist);
        model.addAttribute("poslist", poslist);
        model.addAttribute("url", "namereceive");


    }

    @Override
    public void editNote(HttpServletRequest request, HttpSession session, Model model, int page, int size) {

        //验证的重载
        if (!StringUtils.isEmpty(request.getAttribute("errormess"))) {
            request.setAttribute("errormess", request.getAttribute("errormess"));
            request.setAttribute("note", request.getAttribute("note2"));
        } else if (!StringUtils.isEmpty(request.getAttribute("success"))) {
            request.setAttribute("success", request.getAttribute("success"));
            request.setAttribute("note", request.getAttribute("note2"));
        }
        // 目录
        long userid = Long.parseLong(String.valueOf(session.getAttribute("userId")));

        cataloglist = (List<Catalog>) catalogDao.findCataUser(userid);
        //将根目录放在第一
        if (cataloglist.size() == 0) {
            cataloglist.add(catalogDao.findById(33l).get());
        } else {
            cataloglist.set(0, catalogDao.findById(33l).get());
        }

        // 用户 就是联系人
        List<User> users = (List<User>) userDao.findAll();
        String nId = request.getParameter("id");
        if (nId.contains("cata")) {
            //从目录编辑那里进来的
            String newnid = nId.substring(4, nId.length());
            long ca = Long.parseLong(newnid);
            Catalog cate = catalogDao.findById(ca).get();
            request.setAttribute("cata", cate);
            request.setAttribute("note", null);
            request.setAttribute("id", -3);
        } else {
            Long nid = Long.valueOf(nId);
            // 新建
            if (nid == -1) {
                request.setAttribute("note", null);
                // 新建id
                request.setAttribute("id", nid);
            }

            // 修改
            else if (nid > 0) {
                Note note = noteDao.findById(nid).get();
                long ca = note.getCatalogId();
                Catalog cate = catalogDao.findById(ca).get();
                request.setAttribute("cata", cate);
                request.setAttribute("note", note);
                // 修改id
                request.setAttribute("id", nid);
            }
            // Request.setAttribute("id", nid);
        }
        userget(page, size, model);

        request.setAttribute("users", users);
        request.setAttribute("calist", cataloglist);
        commonMethods.setTypeStatus(request, "aoa_note_list", "aoa_note_list");

    }


    public void userget(int page, int size, Model model) {
        Pageable pa = PageRequest.of(page, size);
        //查看用户并分页
        Page<User> pageuser = userDao.findAll(pa);
        List<User> userlist = pageuser.getContent();
        // 查询部门表
        Iterable<Dept> deptlist = deptDao.findAll();
        // 查职位表
        Iterable<Position> poslist = positionDao.findAll();
        model.addAttribute("page", pageuser);
        model.addAttribute("emplist", userlist);
        model.addAttribute("deptlist", deptlist);
        model.addAttribute("poslist", poslist);
        model.addAttribute("url", "namereceive");
    }


    @Override
    public void findCatalog(Model model, HttpServletRequest request, HttpSession session, String cid,
                            int page, String baseKey, String type, String status, String time, String icon) {

        Long userid = Long.parseLong(String.valueOf(session.getAttribute("userId")));
        model.addAttribute("catalog", "&id=" + cid);
        //不为-2就是按照目录查找
        if (!request.getParameter("id").equals("-2")) {
            Long id = Long.valueOf(cid);
            setSomething(baseKey, type, status, time, icon, model, id, null);
            Page<Note> upage = sortpage(page, baseKey, userid, null, id, null, type, status, time);
            request.setAttribute("sort2", "&id=" + cid);
            paging(model, upage);
            model.addAttribute("url", "notecata");
            ////为-2就是按照最近查找
        }
        commonMethods.setTypeStatus(request, "aoa_note_list", "aoa_note_list");


    }

    @Override
    public void showAllNoteType(Model model, HttpServletRequest request, Long tid, Long cid, HttpSession session, int page, String baseKey, String type, String status, String time, String icon) {
        Long userid = Long.valueOf(String.valueOf(session.getAttribute("userId")));

        if (cid == -2) {

            cid = null;
        }
        setSomething(baseKey, type, status, time, icon, model, cid, tid);
        Page<Note> upage = sortpage(page, baseKey, userid, null, cid, tid, type, status, time);//获得数据之后就将cid重新设置
        if (cid == null) {

            cid = -2L;
        }
        request.setAttribute("sort2", "&id=" + cid + "&typeid=" + tid);
        paging(model, upage);
        model.addAttribute("url", "notetype");
        commonMethods.setTypeStatus(request, "aoa_note_list", "aoa_note_list");


    }

    @Override
    public void showAllNotes(Model model, HttpServletRequest request, HttpSession session, int page, String baseKey, String type, String status, String time, String icon) {

        Long userid = Long.parseLong(String.valueOf(session.getAttribute("userId")));

        setSomething(baseKey, type, status, time, icon, model, null, null);
        Page<Note> upage = sortpage(page, baseKey, userid, null, null, null, type, status, time);
        commonMethods.setTypeStatus(request, "aoa_note_list", "aoa_note_list");
        if (baseKey != null) {
            //如果有搜索关键字那么就记住它
            request.setAttribute("sort", "&baseKey=" + baseKey);
        }
        //没有就默认查找所有
        else {

            request.setAttribute("sort", "&userid=" + userid);
        }
        paging(model, upage);
        model.addAttribute("url", "notewrite");


    }

    @Override
    public void downloadNoteFile(HttpServletResponse response, HttpServletRequest request) {


        if (!(StringUtils.isEmpty(request.getParameter("paid")) || request.getParameter("paid") == null
                || request.getParameter("paid").length() == 0)) {
            long paid = Long.parseLong(request.getParameter("paid"));
            attachment = attachmentDao.findByAttachmentId(paid);

        }
        if (!(StringUtils.isEmpty(request.getParameter("nid")) || request.getParameter("nid") == null
                || request.getParameter("nid").length() == 0)) {
            Long nid = Long.valueOf(request.getParameter("nid"));
            Note note = noteDao.findById(nid).get();
            attachment = attachmentDao.findByAttachmentId(note.getAttachId());
        }

        File file = fileService.get(attachment);
        try {
            // 在浏览器里面显示
            response.setContentLength(attachment.getAttachmentSize().intValue());
            response.setContentType(attachment.getAttachmentType());
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(attachment.getAttachmentName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
            ServletOutputStream sos = response.getOutputStream();
            byte[] data = new byte[attachment.getAttachmentSize().intValue()];
            IOUtils.readFully(new FileInputStream(file), data);
            IOUtils.write(data, sos);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void showNoteInfo(String id, HttpServletRequest Request, HttpServletResponse response, HttpSession session) {

        Attachment attachment = null;
        FileInputStream fis = null;
        OutputStream os = null;
        Long nid = Long.valueOf(id);
        Note note = noteDao.findById(nid).get();
        User user = userDao.findById(note.getCreatemanId()).get();
        if (note.getAttachId() != null) {
            attachment = attachmentDao.findByAttachmentId(note.getAttachId());
            Request.setAttribute("att", attachment);
        }
        Request.setAttribute("note", note);
        Request.setAttribute("user", user);


    }

    @Override
    public void addNoteType(HttpServletRequest request, String title, HttpSession session) {


        int flag = 0;
        Long userid = Long.parseLong(String.valueOf(session.getAttribute("userId")));
        User user = userDao.findById(userid).get();
        String catalogName = request.getParameter("name");
        if (catalogName != null) {
            List<String> catanamelist = catalogDao.findCataName(userid);
            for (String caname : catanamelist) {
                if (caname.contains("(") && caname.contains(")"))
                    caname = caname.substring(0, caname.indexOf("("));
                if (caname.equals(catalogName)) {
                    flag++;
                }
            }
            if (flag == 0) {
                catalogDao.save(new Catalog(catalogName, user));
            }
            if (flag > 0) {

                catalogDao.save(new Catalog(catalogName + "(" + flag + ")", user));
            }
        }

    }

    @Override
    public void deleteCatalogById(Long catalogId) {

        catalogDao.delete(catalogId);
    }

    @Override
    public void deleteNotByNoteId(long noteId) {
        noteDao.delete(noteId);
    }

    @Override
    public List<Note> findByCatalogId(Long catalogId, Long realUserId) {


        return noteDao.findByCatalogId(catalogId, realUserId);
    }

    @Override
    public Noteuser findNoteUserById(Long noteId, Long realUerId) {


        return noteDao.finduserid(noteId, realUerId);
    }

    @Override
    public void deleteNote(long realuserId, long noteid) {

        //删除共享笔记就是只删除中间表noteid对应的那个userid
        Note note = noteDao.findById(noteid).get();
        if (note.getTypeId() == 7) {
            noteUserDao.deleteById(noteUserDao.findId(noteid, realuserId));
        }
        //如果笔记的类型不是共享类型的就直接删除
        else {
            noteDao.delete(noteid);
        }


    }

    @Override
    public void saveNote(MultipartFile file, Note note2, BindingResult br, HttpServletRequest request, HttpSession session) {

        Note note = null;
        Long attid = null;
        Set<User> userss = null;
        Long userid = Long.parseLong(session.getAttribute("userId") + "");
        User user = userDao.findById(userid).get();
        Long nid = Long.valueOf(request.getParameter("id"));
        // 接下来就是获取的数据
        String catalogname = request.getParameter("catalogname");
        String catalogName = catalogname.substring(1, catalogname.length());
        long catalogId = catalogDao.findByCatalogName(catalogName);
        String typename = request.getParameter("type");
        long typeId = typeDao.findByTypeModelAndTypeName("aoa_note_list", typename).getTypeId();
        String statusName = request.getParameter("status");
        long statusId = statusDao.findByStatusModelAndStatusName("aoa_note_list", statusName).getStatusId();

        // 这里返回ResultVO对象，如果校验通过，ResultEnum.SUCCESS.getCode()返回的值为200；否则就是没有通过；
        ResultVO res = BindingResultVOUtil.hasErrors(br);
        if (!ResultEnum.SUCCESS.getCode().equals(res.getCode())) {
            List<Object> list = new MapToList<>().mapToList(res.getData());
            request.setAttribute("errormess", list.get(0).toString());
        } else {
            // nid为-1就是新建或者是从某个目录新建
            if (nid == -1 || nid == -3) {
                // 判断文件是否为空
                if (!file.isEmpty()) {
                    attachment = (Attachment) fileService.saveFile(file, user, null, false);
                    attid = attachment.getAttachmentId();
                } else if (file.isEmpty()) {

                    attid = null;
                }

                // 0l表示新建的时候默认为没有收藏
                note = new Note(note2.getTitle(), note2.getContent(), catalogId, typeId, statusId, attid, new Date(),
                        0l);
                // 判断是否共享
                if (request.getParameter("receiver") != null && (request.getParameter("receiver").trim().length() > 0)) {
                    userss = new HashSet<>();
                    String receivers = request.getParameter("receiver");
                    note.setReceiver(receivers);

                    String[] receiver = receivers.split(";");
                    // 先绑定自己再
                    userss.add(user);
                    // 再绑定其他人
                    for (String re : receiver) {
                        User user2 = userDao.findId(re);
                        if (user2 != null) {
                            userss.add(user2);
                        }

                    }

                } else {
                    // 保存为该用户的笔记 绑定用户id
                    userss = new HashSet<>();
                    userss.add(user);
                }
            }
            // nid大于0就是修改某个对象
            if (nid > 0) {
                note = noteDao.findById(nid).get();
                if (note.getAttachId() == null) {
                    if (!file.isEmpty()) {
                        attachment = (Attachment) fileService.saveFile(file, user, null, false);
                        attid = attachment.getAttachmentId();
                        note.setAttachId(attid);
                        noteDao.save(note);
                    }
                }
                if (note.getAttachId() != null)
                    fileService.updateAtt(file, user, null, note.getAttachId());

                // 判断是否共享
                if (request.getParameter("receiver") != null && (request.getParameter("receiver").trim().length() > 0)) {
                    userss = new HashSet<>();
                    String receivers = request.getParameter("receiver");
                    note.setReceiver(receivers);

                    String[] receiver = receivers.split(";");
                    // 先绑定自己再
                    userss.add(user);
                    // 再绑定其他人
                    for (String re : receiver) {
                        User user2 = userDao.findId(re);
                        if (user2 != null) {
                            userss.add(user2);
                        }
                    }

                } else {
                    // 保存为该用户的笔记 绑定用户id
                    userss = new HashSet<>();
                    userss.add(user);
                }
                noteDao.updateCollect(catalogId, typeId, statusId, note2.getTitle(), note2.getContent(), nid);
            }
            request.setAttribute("success", "后台验证成功");
        }
        // 设置创建人
        assert note != null;
        note.setCreatemanId(userid);
        note.setUserss(userss);
        noteDao.save(note);
        request.setAttribute("note2", note2);


    }

    @Override
    public void collect(Model model, HttpServletRequest request, HttpSession session, int page, String baseKey, String type, String status, String time, String icon) {

        Long userid = Long.valueOf(String.valueOf(session.getAttribute("userId")));
        String id = request.getParameter("id");
        String iscollected = request.getParameter("iscollected");
        noteDao.updateCollectByNoteId(Long.parseLong(iscollected), Long.parseLong(id));
        setSomething(baseKey, type, status, time, icon, model, null, null);
        Page<Note> upage = sortpage(page, null, userid, null, null, null, type, status, time);
        model.addAttribute("url", "notewrite");
        paging(model, upage);
        model.addAttribute("sort", "&userid=" + userid);
        commonMethods.setTypeStatus(request, "aoa_note_list", "aoa_note_list");

    }

    @Override
    public void collectFind(Model model, HttpServletRequest request, String iscollected, Long cid, HttpSession session, int page, String baseKey, String type, String status, String time, String icon) {


        if (cid == -2) {

            cid = null;
        }
        long userid = Long.parseLong(String.valueOf(session.getAttribute("userId")));
        long collect = Long.parseLong(iscollected);

        if (collect == 1) {
            setSomething(baseKey, type, status, time, icon, model, cid, null);
            Page<Note> upage = sortpage(page, null, userid, collect, cid, null, type, status, time);
            model.addAttribute("url", "collectfind");
            paging(model, upage);
            //获得数据之后就将cid重新设置
            if (cid == null)
                cid = -2L;
            model.addAttribute("sort", "&iscollect=" + collect + "&cata=" + cid);
            model.addAttribute("sort2", "&iscollect=" + collect + "&cata=" + cid);
            model.addAttribute("collect", 0);
        } else if (collect == 0) {
            setSomething(baseKey, type, status, time, icon, model, cid, null);
            Page<Note> upage = sortpage(page, null, userid, null, cid, null, type, status, time);
            model.addAttribute("url", "notewrite");
            paging(model, upage);
            model.addAttribute("sort", "&userid=" + userid);
            model.addAttribute("sort2", "&userid=" + userid);
            model.addAttribute("collect", 1);
        }

        commonMethods.setTypeStatus(request, "aoa_note_list", "aoa_note_list");

    }

    @Override
    public void noteMain(Model model, HttpServletRequest request, HttpSession session, int page,
                         String baseKey, String type, String status, String time, String icon) {


        long userid = Long.parseLong(String.valueOf(session.getAttribute("userId")));
        cataloglist = (List<Catalog>) catalogDao.findCataUser(userid);
        setSomething(baseKey, type, status, time, icon, model, null, null);
        Page<Note> upage = sortpage(page, baseKey, userid, null, null, null, type, status, time);
        model.addAttribute("sort", "&userid=" + userid);
        paging(model, upage);

        commonMethods.setTypeStatus(request, "aoa_note_list", "aoa_note_list");
        model.addAttribute("url", "notewrite");
        model.addAttribute("calist", cataloglist);
    }


    public void setSomething(String baseKey, String type, String status, String time, String icon, Model model, Long cataid, Long typeid) {
        if (!StringUtils.isEmpty(icon)) {
            model.addAttribute("icon", icon);
            if (!StringUtils.isEmpty(type)) {
                model.addAttribute("type", type);
                setthree("type", type, icon, model, cataid, typeid);
            }
            if (!StringUtils.isEmpty(status)) {
                model.addAttribute("status", status);
                setthree("status", status, icon, model, cataid, typeid);
            }
            if (!StringUtils.isEmpty(time)) {
                model.addAttribute("time", time);
                setthree("time", time, icon, model, cataid, typeid);
            }

        }
        if (StringUtils.isEmpty(icon)) {
            //目录类型查找
            if (!StringUtils.isEmpty(cataid) && !StringUtils.isEmpty(typeid))
                model.addAttribute("sort", "&id=" + cataid + "&typeid=" + typeid);
            //目录单纯查找
            if (!StringUtils.isEmpty(cataid) && StringUtils.isEmpty(typeid))
                model.addAttribute("sort", "&id=" + cataid);
            //单纯类型查找
            if (StringUtils.isEmpty(cataid) && !StringUtils.isEmpty(typeid))
                model.addAttribute("sort", "&typeid=" + typeid);
        }

        if (!StringUtils.isEmpty(baseKey) && StringUtils.isEmpty(cataid)) {
            model.addAttribute("sort", "&baseKey=" + baseKey);
        }
        if (!StringUtils.isEmpty(baseKey) && !StringUtils.isEmpty(cataid)) {
            model.addAttribute("sort", "&baseKey=" + baseKey + "&id=" + cataid);
        }

    }


    private void paging(Model model, Page<Note> upage) {
        model.addAttribute("nlist", upage.getContent());
        model.addAttribute("page", upage);
//		model.addAttribute("url", "notewrite");
    }


    public Page<Note> sortpage(int page, String baseKey, long userid, Long isCollected, Long catalogId, Long typeId, Object type, Object status, Object time) {
        Pageable pa = PageRequest.of(page, 10);
        if (!StringUtils.isEmpty(baseKey) && StringUtils.isEmpty(catalogId)) {
            return noteDao.findBytitleOrderByCreateTimeDesc(baseKey, userid, pa);
        }
        if (!StringUtils.isEmpty(baseKey) && !StringUtils.isEmpty(catalogId)) {

            return noteDao.findBytitleAndCatalogId(baseKey, userid, catalogId, pa);
        }//0为降序 1为升序
        if (!StringUtils.isEmpty(isCollected)) {

            if (!StringUtils.isEmpty(isCollected) && !StringUtils.isEmpty(catalogId)) {

                return noteDao.findByIsCollectedAndCatalogIdOrderByCreateTimeDesc(isCollected, catalogId, userid, pa);
            }
            if (!StringUtils.isEmpty(isCollected) && StringUtils.isEmpty(catalogId)) {
                return noteDao.findByIsCollectedOrderByCreateTimeDesc(isCollected, userid, pa);
            }
        }

        if (!StringUtils.isEmpty(typeId)) {
            if (!StringUtils.isEmpty(typeId) && !StringUtils.isEmpty(catalogId)) {
                //根据目录 然后再根据类型查找
                if (!StringUtils.isEmpty(type)) {
                    if (type.toString().equals("0"))
                        return noteDao.findByTypeIdOrderByTypeIdDesc(typeId, catalogId, userid, pa);
                    if (type.toString().equals("1"))
                        return noteDao.findByTypeIdOrderByTypeIdAsc(typeId, catalogId, userid, pa);
                }
                if (!StringUtils.isEmpty(status)) {
                    if (status.toString().equals("0"))
                        return noteDao.findByTypeIdOrderByStatusIdDesc(typeId, catalogId, userid, pa);
                    if (status.toString().equals("1"))
                        return noteDao.findByTypeIdOrderByStatusIdAsc(typeId, catalogId, userid, pa);
                }
                if (!StringUtils.isEmpty(time)) {

                    if (time.toString().equals("0"))
                        return noteDao.findByTypeIdOrderByCreateTimeDesc(typeId, catalogId, userid, pa);
                    if (time.toString().equals("1"))
                        return noteDao.findByTypeIdOrderByCreateTimeAsc(typeId, catalogId, userid, pa);
                } else return noteDao.findByTypeIdOrderByCreateTimeDesc(typeId, catalogId, userid, pa);
            }
            if (!StringUtils.isEmpty(typeId) && StringUtils.isEmpty(catalogId)) {
                //为空就直接按照类型查找

                if (!StringUtils.isEmpty(type)) {
                    if (type.toString().equals("0")) return noteDao.findByTypeIdOrderByTypeIdDesc(typeId, userid, pa);
                    if (type.toString().equals("1")) return noteDao.findByTypeIdOrderByTypeIdAsc(typeId, userid, pa);
                }
                if (!StringUtils.isEmpty(status)) {
                    if (status.toString().equals("0"))
                        return noteDao.findByTypeIdOrderByStatusIdDesc(typeId, userid, pa);
                    if (status.toString().equals("1"))
                        return noteDao.findByTypeIdOrderByStatusIdAsc(typeId, userid, pa);
                }
                if (!StringUtils.isEmpty(time)) {

                    if (time.toString().equals("0"))
                        return noteDao.findByTypeIdOrderByCreateTimeDesc(typeId, userid, pa);
                    if (time.toString().equals("1"))
                        return noteDao.findByTypeIdOrderByCreateTimeAsc(typeId, userid, pa);
                } else return noteDao.findByTypeIdOrderByCreateTimeDesc(typeId, userid, pa);
            }
        }


        if (!StringUtils.isEmpty(catalogId) && (StringUtils.isEmpty(typeId)) && (StringUtils.isEmpty(isCollected))) {//单纯查找目录
            if (!StringUtils.isEmpty(type)) {
                if (type.toString().equals("0")) return noteDao.findByCatalogIdOrderByTypeIdDesc(catalogId, userid, pa);
                if (type.toString().equals("1")) return noteDao.findByCatalogIdOrderByTypeIdAsc(catalogId, userid, pa);
            }
            if (!StringUtils.isEmpty(status)) {
                if (status.toString().equals("0"))
                    return noteDao.findByCatalogIdOrderByStatusIdDesc(catalogId, userid, pa);
                if (status.toString().equals("1"))
                    return noteDao.findByCatalogIdOrderByStatusIdAsc(catalogId, userid, pa);
            }
            if (!StringUtils.isEmpty(time)) {
                if (time.toString().equals("0"))
                    return noteDao.findByCatalogIdOrderByCreateTimeDesc(catalogId, userid, pa);
                if (time.toString().equals("1"))
                    return noteDao.findByCatalogIdOrderByCreateTimeAsc(catalogId, userid, pa);
            } else return noteDao.findByCatalogIdOrderByCreateTimeDesc(catalogId, userid, pa);
        }


        if (StringUtils.isEmpty(isCollected) && StringUtils.isEmpty(typeId) && StringUtils.isEmpty(catalogId)) {
            if (!StringUtils.isEmpty(type)) {
                if (type.toString().equals("0")) {
                    //降序
                    return noteDao.findByUserssOrderByTypeIdDesc(userid, pa);
                } else {
                    //升序
                    return noteDao.findByUserssOrderByTypeIdAsc(userid, pa);
                }
            }
            if (!StringUtils.isEmpty(status)) {
                if (status.toString().equals("0")) {

                    return noteDao.findByUserssOrderByStatusIdDesc(userid, pa);
                } else {
                    return noteDao.findByUserssOrderByStatusIdAsc(userid, pa);
                }
            }
            if (!StringUtils.isEmpty(time)) {
                if (time.toString().equals("0")) {
                    return noteDao.findByUserssOrderByCreateTimeDesc(userid, pa);
                } else {
                    return noteDao.findByUserssOrderByCreateTimeAsc(userid, pa);
                }
            }
        }
        if (!StringUtils.isEmpty(userid)) {
            return noteDao.findByUserssOrderByCreateTimeDesc(userid, pa);
        } else {
            // 第几页 以及页里面数据的条数
            return noteDao.findByUserss(userid, pa);
        }

    }

    private void setthree(String x, String name, String icon, Model model, Long cataid, Long typeid) {
        //单纯根据目录
        if (!StringUtils.isEmpty(cataid) && StringUtils.isEmpty(typeid))
            model.addAttribute("sort", "&" + x + "=" + name + "&icon=" + icon + "&id=" + cataid);
        //单纯的根据类型
        if (StringUtils.isEmpty(cataid) && !StringUtils.isEmpty(typeid))
            model.addAttribute("sort", "&" + x + "=" + name + "&icon=" + icon + "&typeid=" + typeid);
        //根据目录和类型
        if (!StringUtils.isEmpty(cataid) && !StringUtils.isEmpty(typeid))
            model.addAttribute("sort", "&" + x + "=" + name + "&icon=" + icon + "&id=" + cataid + "&typeid=" + typeid);
        else if (StringUtils.isEmpty(cataid) && StringUtils.isEmpty(typeid))
            model.addAttribute("sort", "&" + x + "=" + name + "&icon=" + icon);
    }


}
