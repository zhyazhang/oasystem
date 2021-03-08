package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.file.FileListDao;
import com.aifurion.oasystem.dao.file.FilePathdao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.file.FileList;
import com.aifurion.oasystem.entity.file.FilePath;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.FileManageService;
import com.aifurion.oasystem.service.FileService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 17:14
 */

@Service
public class FileManageServiceImpl implements FileManageService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private FilePathdao filePathdao;

    @Autowired
    private FileService fileServices;

    @Autowired
    private FileListDao fileListDao;


    @Override
    public void fileManage(Long userid, Model model) {
        User user = userDao.findById(userid).get();

        FilePath filepath = filePathdao.findByPathName(user.getUserName());

        if (filepath == null) {
            FilePath filepath1 = new FilePath();
            filepath1.setParentId(1L);
            filepath1.setPathName(user.getUserName());
            filepath1.setPathUserId(user.getUserId());
            filepath = filePathdao.save(filepath1);
        }

        model.addAttribute("nowpath", filepath);
        model.addAttribute("paths", fileServices.findPathByParent(filepath.getId()));
        model.addAttribute("files", fileServices.findFileByPath(filepath));
        model.addAttribute("userrootpath", filepath);
        model.addAttribute("mcpaths", fileServices.findPathByParent(filepath.getId()));


    }

    @Override
    public void uploadFile(MultipartFile file, Long pathid, Long userid) {

        User user = userDao.findById(userid).get();
        FilePath nowpath = filePathdao.findById(pathid).get();
        // true 表示从文件使用上传
        FileList uploadfile = (FileList) fileServices.saveFile(file, user, nowpath, true);

    }

    @Override
    public void intoDirectory(Long userid, Long pathid, Model model) {


        User user = userDao.findById(userid).get();
        FilePath userrootpath = filePathdao.findByPathName(user.getUserName());

        // 查询当前目录
        FilePath filepath = filePathdao.findById(pathid).get();

        // 查询当前目录的所有父级目录
        List<FilePath> allparentpaths = new ArrayList<>();
        fileServices.findAllParent(filepath, allparentpaths);
        Collections.reverse(allparentpaths);

        model.addAttribute("allparentpaths", allparentpaths);
        model.addAttribute("nowpath", filepath);
        model.addAttribute("paths", fileServices.findPathByParent(filepath.getId()));
        model.addAttribute("files", fileServices.findFileByPath(filepath));
        //复制移动显示 目录
        model.addAttribute("userrootpath", userrootpath);
        model.addAttribute("mcpaths", fileServices.findPathByParent(userrootpath.getId()));


    }

    @Override
    public void downFile(HttpServletResponse response, Long fileid) {

        try {
            FileList filelist = fileListDao.findById(fileid).get();
            File file = fileServices.getFile(filelist.getFilePath());
            response.setContentLength(filelist.getSize().intValue());
            response.setContentType(filelist.getContentType());
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(filelist.getFileName().getBytes("UTF-8"), "ISO8859-1"));
            writeFile(response, file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void shareFile(Long pathid, List<Long> filesId, Model model) {

        if (!filesId.isEmpty()) {
            fileServices.doShare(filesId);
        }
        model.addAttribute("pathid", pathid);
        model.addAttribute("message", "分享成功");

    }

    @Override
    public void deleteFile(Long userid, Long pathid, List<Long> pathsId, List<Long> filesId, Model model) {

        if (!filesId.isEmpty()) {
            // 删除文件
            //fs.deleteFile(checkfileids);
            //文件放入回收战
            fileServices.trashFile(filesId, 1L, userid);
        }
        if (!pathsId.isEmpty()) {
            // 删除文件夹
            //fs.deletePath(checkpathids);
            fileServices.trashPath(pathsId, 1L, true);
            //fs.trashPath(checkpathids);
        }

        model.addAttribute("pathid", pathid);

    }

    @Override
    public void reFileName(String name, Long renameFP, Long pathid, boolean isFile, Model model) {

        fileServices.reName(name, renameFP, pathid, isFile);

        model.addAttribute("pathid", pathid);


    }

    @Override
    public void moveOrCopyFile(Long userid, boolean morc, Long mctoid, Long pathid, List<Long> mcfileids, List<Long> mcpathids, Model model) {

        if (morc) {
            fileServices.moveAndCopy(mcfileids, mcpathids, mctoid, true, userid);
        } else {
            fileServices.moveAndCopy(mcfileids, mcpathids, mctoid, false, userid);
        }

        model.addAttribute("pathid", pathid);


    }

    @Override
    public void createPath(Long userid, Long pathid, String pathname, Model model) {
        FilePath filepath = filePathdao.findById(pathid).get();
        String newname = fileServices.onlyName(pathname, filepath, null, 1, false);

        FilePath newfilepath = new FilePath(pathid, newname);
        newfilepath.setPathUserId(userid);
        filePathdao.save(newfilepath);
        model.addAttribute("pathid", pathid);


    }

    @Override
    public void imageShow(HttpServletResponse response, Long fileid) {

        FileList filelist = fileListDao.findById(fileid).get();
        File file = fileServices.getFile(filelist.getFilePath());
        writeFile(response, file);

    }

    @Override
    public void fileTypeLoad(Long userid, String type, Model model) {

        User user = userDao.findById(userid).get();
        String contenttype;
        List<FileList> fileLists = null;
        List<FilePath> filePaths = null;

        switch (type) {
            case "document":
                fileLists = fileListDao.finddocument(user);
                model.addAttribute("files", fileLists);
                model.addAttribute("isload", 1);
                break;

            case "picture":
                contenttype = "image/%";
                fileLists = fileListDao.findByUserAndContentTypeLikeAndFileIstrash(user, contenttype, 0L);
                model.addAttribute("files", fileLists);
                model.addAttribute("isload", 1);
                break;

            case "music":
                contenttype = "audio/%";
                fileLists = fileListDao.findByUserAndContentTypeLikeAndFileIstrash(user, contenttype, 0L);
                model.addAttribute("files", fileLists);
                model.addAttribute("isload", 1);
                break;

            case "video":
                contenttype = "video/%";
                fileLists = fileListDao.findByUserAndContentTypeLikeAndFileIstrash(user, contenttype, 0L);
                model.addAttribute("files", fileLists);
                model.addAttribute("isload", 1);
                break;
            case "yasuo":
                contenttype = "application/x%";
                fileLists = fileListDao.findByUserAndContentTypeLikeAndFileIstrash(user, contenttype, 0L);
                model.addAttribute("files", fileLists);
                model.addAttribute("isload", 1);
                break;

            case "trash":
                filePaths = filePathdao.findByPathUserIdAndPathIstrash(userid, 1L);
                fileLists = fileListDao.findByUserAndFileIstrash(user, 1L);

                model.addAttribute("paths", filePaths);
                model.addAttribute("files", fileLists);
                model.addAttribute("istrash", 1);
                model.addAttribute("isload", 1);
                break;

            case "share":
                fileLists = fileListDao.findByFileIsshareAndFileIstrash(1L, 0L);
                model.addAttribute("files", fileLists);
                model.addAttribute("isshare", 1);
                model.addAttribute("isload", 1);
                model.addAttribute("userid", userid);
                break;


            default:
                break;
        }

        model.addAttribute("type", type);


    }

    @Override
    public void findFile(Long userid, String fileName, String type, Model model) {


        String findlike = "%" + fileName + "%";
        User user = userDao.findById(userid).get();
        //FilePath fpath = filePathdao.findByParentIdAndPathUserId(1L, userid);
        String contenttype;
        List<FileList> fileLists = null;
        List<FilePath> filePaths = null;
        switch (type) {

            case "document":
                fileLists = fileListDao.finddocumentlike(user, findlike);
                model.addAttribute("files", fileLists);
                model.addAttribute("isload", 1);
                break;

            case "picture":
                contenttype = "image/%";
                fileLists = fileListDao.findByUserAndFileIstrashAndContentTypeLikeAndFileNameLike(user, 0L, contenttype, findlike);
                model.addAttribute("files", fileLists);
                model.addAttribute("isload", 1);
                break;

            case "music":
                contenttype = "audio/%";
                fileLists = fileListDao.findByUserAndFileIstrashAndContentTypeLikeAndFileNameLike(user, 0L, contenttype, findlike);
                model.addAttribute("files", fileLists);
                model.addAttribute("isload", 1);
                break;

            case "video":
                contenttype = "video/%";
                fileLists = fileListDao.findByUserAndFileIstrashAndContentTypeLikeAndFileNameLike(user, 0L, contenttype, findlike);
                model.addAttribute("files", fileLists);
                model.addAttribute("isload", 1);
                break;

            case "yasuo":
                contenttype = "application/x%";
                fileLists = fileListDao.findByUserAndFileIstrashAndContentTypeLikeAndFileNameLike(user, 0L, contenttype, findlike);
                model.addAttribute("files", fileLists);
                model.addAttribute("isload", 1);
                break;

            case "trash":
                filePaths = filePathdao.findByPathUserIdAndPathIstrashAndPathNameLikeAndParentIdNot(userid, 1L, findlike, 1L);
                fileLists = fileListDao.findByUserAndFileIstrashAndContentTypeLikeAndFileNameLike(user, 1L, "%%", findlike);
                model.addAttribute("istrash", 1);
                model.addAttribute("isload", 1);
                model.addAttribute("paths", filePaths);
                model.addAttribute("files", fileLists);
                break;

            case "share":
                fileLists = fileListDao.findByFileIsshareAndFileNameLike(1L, findlike);
                model.addAttribute("files", fileLists);
                model.addAttribute("isshare", 1);
                model.addAttribute("isload", 1);
                break;

            default:
                filePaths = filePathdao.findByPathUserIdAndPathIstrashAndPathNameLikeAndParentIdNot(userid, 0L, findlike, 1L);
                fileLists = fileListDao.findByUserAndFileIstrashAndFileNameLike(user, 0L, findlike);
                model.addAttribute("files", fileLists);
                model.addAttribute("paths", filePaths);
                model.addAttribute("isload", 1);
                break;
        }

        model.addAttribute("type", type);


    }


    public void writeFile(HttpServletResponse response, File file) {
        ServletOutputStream servletOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            servletOutputStream = response.getOutputStream();
            // 读取文件问字节码
            byte[] data = new byte[(int) file.length()];
            IOUtils.readFully(fileInputStream, data);
            // 将文件流输出到浏览器
            IOUtils.write(data, servletOutputStream);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                servletOutputStream.close();
                fileInputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
