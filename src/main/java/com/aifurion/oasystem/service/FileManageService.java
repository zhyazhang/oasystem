package com.aifurion.oasystem.service;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 17:12
 */
public interface FileManageService {


    void fileManage(Long userid, Model model);

    void uploadFile(MultipartFile file, Long pathid, Long userid);

    void intoDirectory(Long userid, Long pathid, Model model);

    void downFile(HttpServletResponse response, Long fileid);

    void shareFile(Long pathid, List<Long> filesId, Model model);

    void deleteFile(Long userid,
                    Long pathid,
                    List<Long> pathsId,
                    List<Long> filesId, Model model);

    void reFileName(String name,
                    Long renameFP,
                    Long pathid,
                    boolean isFile,
                    Model model);

    void moveOrCopyFile(Long userid,
                        boolean morc,
                        Long mctoid,
                        Long pathid,
                        List<Long> mcfileids,
                        List<Long> mcpathids,
                        Model model);

    void createPath(Long userid,
                    Long pathid,
                    String pathname,
                    Model model);

    void imageShow(HttpServletResponse response, Long fileid);


    void fileTypeLoad(Long userid, String type, Model model);


    void findFile(Long userid,
			String fileName,
			String type,
			Model model);
}
