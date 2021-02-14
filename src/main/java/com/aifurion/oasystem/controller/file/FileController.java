package com.aifurion.oasystem.controller.file;

import com.aifurion.oasystem.service.FileManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 17:10
 */
@Controller
public class FileController {

    @Autowired
    private FileManageService fileManageService;

    @GetMapping("/filemanage")
    public String userFilemManage(@SessionAttribute("userId")Long userid, Model model) {

        fileManageService.fileManage(userid, model);
        return "file/filemanage";
    }


    @PostMapping("/fileupload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("pathid") Long pathid,
                             HttpSession session, Model model) {
        Long userid = Long.parseLong(String.valueOf(session.getAttribute("userId")));
        fileManageService.uploadFile(file, pathid, userid);
        return "forward:/intodir";
    }

    @RequestMapping("/intodir")
    public String intoDirectory(@SessionAttribute("userId") Long userid, @RequestParam("pathid") Long pathid, Model model) {

        fileManageService.intoDirectory(userid, pathid, model);
        return "file/filemanage";
    }


    @GetMapping("/downfile")
    public void downFile(HttpServletResponse response, @RequestParam("fileid") Long fileid) {

        fileManageService.downFile(response, fileid);

    }


    @GetMapping("/doshare")
    public String shareFile(@RequestParam("pathid") Long pathid,
			@RequestParam("checkfileids") List<Long> filesId,
			Model model) {
        fileManageService.shareFile(pathid, filesId, model);

        return "forward:/intodir";

    }


    @GetMapping("/deletefile")
    public String deleteFile(@SessionAttribute("userId") Long userid,
			@RequestParam("pathid") Long pathid,
			@RequestParam("checkpathids") List<Long> pathsId,
			@RequestParam("checkfileids") List<Long> filesId, Model model) {

        fileManageService.deleteFile(userid, pathid, pathsId, filesId, model);

        return "forward:/intodir";

    }


    @PostMapping("/rename")
    public String rename(@RequestParam("name") String name,
                         @RequestParam("renamefp") Long renamefp,
                         @RequestParam("pathid") Long pathid,
                         @RequestParam("isfile") boolean isfile,
                         Model model) {

        fileManageService.reFileName(name, renamefp, pathid, isfile, model);

        return "forward:/intodir";
    }


    @GetMapping("/moveorcopy")
    public String moveOrCopyFile(@SessionAttribute("userId") Long userid,
			@RequestParam("morc") boolean morc,
			@RequestParam("mctoid") Long mctoid,
			@RequestParam("pathid") Long pathid,
			@RequestParam("mcfileids")List<Long> mcfileids,
			@RequestParam("mcpathids")List<Long> mcpathids,
			Model model) {

        fileManageService.moveOrCopyFile(userid, morc, mctoid, pathid, mcfileids, mcpathids, model);

        return "forward:/intodir";

    }


    @PostMapping("/createpath")
    public String createPath(@SessionAttribute("userId") Long userid,
							 @RequestParam("pathid") Long pathid,
							 @RequestParam("pathname") String pathname,
							 Model model) {

        fileManageService.createPath(userid, pathid, pathname, model);

        return "forward:/intodir";

    }



    @GetMapping("/imgshow")
    public String imageShow(HttpServletResponse response, @RequestParam("fileid") Long fileid) {
        fileManageService.imageShow(response, fileid);

        return "forward:/intodir";
    }






}
