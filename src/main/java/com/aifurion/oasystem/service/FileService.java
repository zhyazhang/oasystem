package com.aifurion.oasystem.service;

import com.aifurion.oasystem.dao.file.FileListDao;
import com.aifurion.oasystem.entity.file.FileList;
import com.aifurion.oasystem.entity.file.FilePath;
import com.aifurion.oasystem.entity.note.Attachment;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 17:17
 */
public interface FileService {



        /**
     * 根据文件id 将文件放入回收站
     * @param fileids
     */


    void trashFile(List<Long> fileids, Long setistrashhowmany, Long userid);


    void UserpanelController();

    /**
     * 根据父	ID 查询 显示的 路径
     *
     * @param parentId
     * @return
     */
    List<FilePath> findPathByParent(Long parentId);

    /**
     * 根据路径查询 文件集合
     *
     * @param filePath
     * @return
     */
    List<FileList> findFileByPath(FilePath filePath);

    /**
     * 根据文件路径 递归查询父级目录
     *
     * @param filePath
     * @param parentpaths
     */
    void findAllParent(FilePath filePath, List<FilePath> parentpaths);

    /**
     * 保存文件以及附件
     *
     * @param file
     * @param user
     * @param nowpath
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    Object saveFile(MultipartFile file, User user, FilePath nowpath, boolean isfile);

    //修改附件
    Integer updateAtt(MultipartFile file, User user, FilePath nowpath, long attid);


    void doShare(List<Long> fileids);


    /**
     * 根据文件id 批量 删除文件  同时删除 数据库以及本地文件
     *
     * @param fileids
     */

    void deleteFile(List<Long> fileids);

    /**
     * 根据文件夹id 批量删除 文件夹    并删除此路径下的所有文件以及文件夹
     *
     * @param pathids
     */

    void deletePath(List<Long> pathids);

    /**
     * 根据文件夹id 批量放入回收战
     *
     * @param pathids
     */
    void trashPath(List<Long> pathids, Long setistrashhaomany, boolean isfirst);

    /**
     * 文件还原
     *
     * @param checkfileids
     */

    void fileReturnBack(List<Long> checkfileids, Long userid);

    /**
     * 文件夹还原
     */
    void pathReturnBack(List<Long> pathids, Long userid);

    /**
     * 复制和移动
     *
     * @param fromwhere 1为移动  2 为复制
     */

    void moveAndCopy(List<Long> mcfileids, List<Long> mcpathids, Long topathid, boolean fromwhere, Long userid);

    void copyPath(Long mcpathid, Long topathid, boolean isfirst, Long userid);

    /**
     * 文件复制
     *
     * @param filelist
     */
    void copyFile(FileList filelist, FilePath topath, boolean isfilein);

    /**
     * 本地文件复制
     *
     * @param s
     * @param t
     */
    void copyFileIO(File s, File t);

    /**
     * 移动复制文件树 点击加载
     *
     * @param mctoid
     * @param mcpathids
     * @return
     */
    List<FilePath> mcPathLoad(Long mctoid, List<Long> mcpathids);

    /**
     * 重命名业务方法
     *
     * @param name
     * @param renamefp
     * @param nowpathid
     * @param isfile
     */
    void reName(String name, Long renamefp, Long nowpathid, boolean isfile);


    /**
     * 文件以及路径得同名处理
     *
     * @param name
     * @param filepath
     * @param shuffix
     * @param num
     * @return
     */
    String onlyName(String name, FilePath filepath, String shuffix, int num, boolean isfile);

    /**
     * 得到文件
     *
     * @param filepath
     * @return
     */
    File getFile(String filepath);

    /**
     * 找到父级目录 并 拼接成路径
     *
     * @param nowpath
     * @param parentpaths
     * @return
     */
    String savePath(FilePath nowpath, List<FilePath> parentpaths);


    /**
     * 获取附件
     *
     * @param att
     * @return
     */
    File get(Attachment att);

    Attachment get(String filePath);


}
