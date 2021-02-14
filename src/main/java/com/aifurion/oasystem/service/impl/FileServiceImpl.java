package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.file.FileListDao;
import com.aifurion.oasystem.dao.file.FilePathdao;
import com.aifurion.oasystem.dao.note.AttachmentDao;
import com.aifurion.oasystem.entity.file.FileList;
import com.aifurion.oasystem.entity.file.FilePath;
import com.aifurion.oasystem.entity.note.Attachment;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.AttachService;
import com.aifurion.oasystem.service.FileService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 17:25
 */

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileListDao fileListDao;
    @Autowired
    private FilePathdao filePathdao;
    @Autowired
    private AttachmentDao attachmentDao;
    @Autowired
    private com.aifurion.oasystem.service.AttachService AttachService;


    @Value("${file.root.path}")
    private String rootPath;


    @Override
    public void trashFile(List<Long> fileids, Long setistrashhowmany, Long userid) {


        for (Long fileid : fileids) {
            FileList fileList = fileListDao.findById(fileid).get();
            fileList.setFileIstrash(setistrashhowmany);
            if(userid != null){
                fileList.setFpath(null);
            }

            fileListDao.save(fileList);
        }


    }

    @Override
    public void UserpanelController() {

        try {
            rootPath = ResourceUtils.getURL("classpath:").getPath().replace("target/classes/", "static/file");
            //System.out.println(rootPath);
        } catch (IOException e) {
            System.out.println("获取项目路径异常");
        }
    }

    @Override
    public List<FilePath> findPathByParent(Long parentId) {
        return filePathdao.findByParentIdAndPathIstrash(parentId, 0L);
    }

    @Override
    public List<FileList> findFileByPath(FilePath filePath) {
        return fileListDao.findByFpathAndFileIstrash(filePath, 0L);
    }

    @Override
    public void findAllParent(FilePath filePath, List<FilePath> parentpaths) {

        if (filePath.getParentId() != 1L) {
            FilePath filepath1 = filePathdao.findById(filePath.getParentId()).get();
            parentpaths.add(filepath1);
            findAllParent(filepath1, parentpaths);
        }

    }

    @Override
    public Object saveFile(MultipartFile file, User user, FilePath nowpath, boolean isfile) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM");
        File root = new File(this.rootPath, simpleDateFormat.format(new Date()));

        File savepath = new File(root, user.getUserName());
        //System.out.println(savePath.getPath());

        if (!savepath.exists()) {
            savepath.mkdirs();
        }

        String shuffix = FilenameUtils.getExtension(file.getOriginalFilename());

        String newFileName = UUID.randomUUID().toString().toLowerCase() + "." + shuffix;
        File targetFile = new File(savepath, newFileName);
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isfile) {
            FileList filelist = new FileList();
            String filename = file.getOriginalFilename();
            filename = onlyName(filename, nowpath, shuffix, 1, true);
            filelist.setFileName(filename);
            filelist.setFilePath(targetFile.getAbsolutePath().replace("\\", "/").replace(this.rootPath, ""));
            filelist.setFileShuffix(shuffix);
            filelist.setSize(file.getSize());
            filelist.setUploadTime(new Date());
            filelist.setFpath(nowpath);
            filelist.setContentType(file.getContentType());
            filelist.setUser(user);
            fileListDao.save(filelist);
            return filelist;
        } else {
            Attachment attachment = new Attachment();
            attachment.setAttachmentName(file.getOriginalFilename());
            attachment.setAttachmentPath(targetFile.getAbsolutePath().replace("\\", "/").replace(this.rootPath, ""));
            attachment.setAttachmentShuffix(shuffix);
            attachment.setAttachmentSize(file.getSize());
            attachment.setAttachmentType(file.getContentType());
            attachment.setUploadTime(new Date());
            attachment.setUserId(user.getUserId() + "");
            attachment.setModel("note");
            attachmentDao.save(attachment);
            return attachment;
        }
    }

    @Override
    public Integer updateAtt(MultipartFile file, User user, FilePath nowpath, long attid) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM");
		File root = new File(this.rootPath,simpleDateFormat.format(new Date()));

		File savepath = new File(root,user.getUserName());
		//System.out.println(savePath.getPath());

		if (!savepath.exists()) {
			savepath.mkdirs();
		}
		if(!file.isEmpty()){
		String shuffix = FilenameUtils.getExtension(file.getOriginalFilename());

		String newFileName = UUID.randomUUID().toString().toLowerCase()+"."+shuffix;
		File targetFile = new File(savepath,newFileName);
            try {
                file.transferTo(targetFile);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return AttachService.updateAttachment(file.getOriginalFilename(),
				targetFile.getAbsolutePath().replace("\\", "/").replace(this.rootPath, ""), shuffix, file.getSize(),
			    file.getContentType(), new Date(), attid);
		}
		return 0;
    }

    @Transactional
    @Override
    public void doShare(List<Long> fileids) {

        for (Long fileid : fileids) {

			FileList filelist = fileListDao.findById(fileid).get();

			filelist.setFileIsshare(1L);
			fileListDao.save(filelist);
		}

    }


    @Transactional
    @Override
    public void deleteFile(List<Long> fileids) {

        for (Long fileid : fileids) {
			FileList filelist = fileListDao.findById(fileid).get();

			File file = new File(this.rootPath,filelist.getFilePath());
			//System.out.println(fileid+":"+file.exists());
			if(file.exists()&&file.isFile()){
				System.out.println("现在删除"+filelist.getFileName()+"数据库存档>>>>>>>>>");
				fileListDao.deleteById(fileid);
				System.out.println("现在删除"+filelist.getFileName()+"本地文件    >>>>>>>>>");
				file.delete();
			}
		}

    }


    @Transactional
    @Override
    public void deletePath(List<Long> pathids) {

        for (Long pathid : pathids) {
			FilePath filepath = filePathdao.findById(pathid).get();
//			System.out.println("第一个文件夹："+filepath);

			//首先删除此文件夹下的文件
			List<FileList> files = fileListDao.findByFpath(filepath);
			if(!files.isEmpty()){
				List<Long> fileids= new ArrayList<>();
				for (FileList filelist : files) {
					fileids.add(filelist.getFileId());
				}
				deleteFile(fileids);
			}

			//然后删除此文件夹下的文件夹
			List<FilePath> filepaths = filePathdao.findByParentId(pathid);
			if(!filepaths.isEmpty()){
				List<Long> pathids2 = new ArrayList<>();
				for (FilePath filePath : filepaths) {
					pathids2.add(filePath.getId());
				}
				deletePath(pathids2);
			}

			filePathdao.delete(filepath);
		}

    }

    @Override
    public void trashPath(List<Long> pathids, Long setistrashhaomany, boolean isfirst) {


        for (Long pathid : pathids) {
			FilePath filepath = filePathdao.findById(pathid).get();
			//System.out.println("第一个文件夹："+filepath);

			//首先将此文件夹下的文件放入回收战
			List<FileList> files = fileListDao.findByFpath(filepath);
			if(!files.isEmpty()){
			//	System.out.println("找到第一个文件夹下的文件不为空！~~~");
				//System.out.println(files);
				List<Long> fileids= new ArrayList<>();
				for (FileList filelist : files) {
					fileids.add(filelist.getFileId());
				}
				trashFile(fileids,2L,null);
			}

			List<FilePath> filepaths = filePathdao.findByParentId(pathid);
			if(!filepaths.isEmpty()){
				List<Long> pathids2 = new ArrayList<>();
				for (FilePath filePath : filepaths) {
					pathids2.add(filePath.getId());
				}

				trashPath(pathids2,2L,false);
			}

			if (isfirst) {
				filepath.setParentId(0L);
			}
			filepath.setPathIstrash(setistrashhaomany);
			filePathdao.save(filepath);
		}


    }

    @Override
    public void fileReturnBack(List<Long> checkfileids, Long userid) {

        FilePath fpath = filePathdao.findByParentIdAndPathUserId(1L, userid);
		for (Long checkfileid : checkfileids) {
			FileList fileList = fileListDao.findById(checkfileid).get();

			if (userid != null) {
				String name = onlyName(fileList.getFileName(), fpath, fileList.getFileShuffix(), 1, true);
				fileList.setFpath(fpath);
				fileList.setFileName(name);
			}
			fileList.setFileIstrash(0L);
			fileListDao.save(fileList);
		}


    }

    @Override
    public void pathReturnBack(List<Long> pathids, Long userid) {


        for (Long pathid : pathids) {
			FilePath filepath = filePathdao.findById(pathid).get();
			System.out.println("第一个文件夹："+filepath);

			//首先将此文件夹下的文件还原
			List<FileList> files = fileListDao.findByFpath(filepath);
			if(!files.isEmpty()){
				System.out.println("找到第一个文件夹下的文件不为空！~~~");
				System.out.println(files);
				List<Long> fileids= new ArrayList<>();
				for (FileList filelist : files) {
					fileids.add(filelist.getFileId());
				}
				fileReturnBack(fileids,null);
			}
			System.out.println("此文件夹内的文件还原成功");
			System.out.println("然后将此文件夹下的文件夹还原");
			//然后将此文件夹下的文件夹还原
			List<FilePath> filepaths = filePathdao.findByParentId(pathid);
			if(!filepaths.isEmpty()){
				System.out.println("此文件夹下还有文件夹");
				List<Long> pathids2 = new ArrayList<>();
				for (FilePath filePath : filepaths) {
					pathids2.add(filePath.getId());
				}
				System.out.println("pathids2"+pathids2);
				System.out.println("接下来尽心递归调用");
				pathReturnBack(pathids2,null);
			}
			System.out.println("此文件夹下再无文件夹");
			if(userid!=null){
				System.out.println("userid不为空表示是第一次进入的文件夹 现在还原");
				FilePath fpath = filePathdao.findByParentIdAndPathUserId(1L, userid);
				String name = onlyName(filepath.getPathName(), fpath, null, 1, false);
				filepath.setPathName(name);
				filepath.setParentId(fpath.getId());
			}
			System.out.println("还原成功");

			filepath.setPathIstrash(0L);
			filePathdao.save(filepath);
		}

    }


    @Transactional
    @Override
    public void moveAndCopy(List<Long> mcfileids, List<Long> mcpathids, Long topathid, boolean fromwhere, Long userid) {

        FilePath topath = filePathdao.findById(topathid).get();
		if(fromwhere){
			System.out.println("这里是移动！！~~");
			if(!mcfileids.isEmpty()){
				System.out.println("fileid is not null");
				for (Long mcfileid : mcfileids) {
					FileList filelist = fileListDao.findById(mcfileid).get();
					String filename = onlyName(filelist.getFileName(),topath,filelist.getFileShuffix(),1,true);
					filelist.setFpath(topath);
					filelist.setFileName(filename);
					fileListDao.save(filelist);
				}
			}
			if(!mcpathids.isEmpty()){
				System.out.println("pathid is not null");
				for (Long mcpathid : mcpathids) {
					FilePath filepath = filePathdao.findById(mcpathid).get();
					String name = onlyName(filepath.getPathName(), topath, null, 1, false);
					filepath.setParentId(topathid);
					filepath.setPathName(name);
					filePathdao.save(filepath);
				}
			}
		}else{
			System.out.println("这里是复制！！~~");
			if(!mcfileids.isEmpty()){
				System.out.println("fileid is not null");
				for (Long mcfileid : mcfileids) {
					FileList filelist = fileListDao.findById(mcfileid).get();
					copyFile(filelist,topath,true);
				}
			}
			if(!mcpathids.isEmpty()){
				System.out.println("pathid is not null");
				for (Long mcpathid : mcpathids) {
					copyPath(mcpathid, topathid, true, userid);
				}
			}
		}



    }

    @Override
    public void copyPath(Long mcpathid, Long topathid, boolean isfirst, Long userid) {

        FilePath filepath = filePathdao.findById(mcpathid).get();

		//第一个文件夹的复制
		FilePath copypath = new FilePath();
		copypath.setParentId(topathid);
		String copypathname = filepath.getPathName();
		if(isfirst){
			copypathname = "拷贝 "+filepath.getPathName().replace("拷贝 ", "");
		}
		copypath.setPathName(copypathname);
		copypath.setPathUserId(userid);
		copypath = filePathdao.save(copypath);

		//这一个文件夹下的文件的复制
		List<FileList> filelists = fileListDao.findByFpathAndFileIstrash(filepath, 0L);
		for (FileList fileList : filelists) {
			copyFile(fileList,copypath,false);
		}

		List<FilePath> filepathsons = filePathdao.findByParentIdAndPathIstrash(filepath.getId(), 0L);

		if(!filepathsons.isEmpty()){
			for (FilePath filepathson : filepathsons) {
				copyPath(filepathson.getId(),copypath.getId(),false,userid);
			}
		}


    }

    @Override
    public void copyFile(FileList filelist, FilePath topath, boolean isfilein) {

        File s = getFile(filelist.getFilePath());
		User user = filelist.getUser();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM");
		File root = new File(this.rootPath,simpleDateFormat.format(new Date()));
		File savepath = new File(root,user.getUserName());

		if (!savepath.exists()) {
			savepath.mkdirs();
		}

		String shuffix = filelist.getFileShuffix();

		String newFileName = UUID.randomUUID().toString().toLowerCase()+"."+shuffix;
		File t = new File(savepath,newFileName);

		copyFileIO(s,t);

		FileList filelist1 = new FileList();
		String filename="";
		if(isfilein){
			filename = "拷贝 "+filelist.getFileName().replace("拷贝 ", "");
		}else{
			filename = filelist.getFileName();
		}
		filename = onlyName(filename,topath,shuffix,1,true);
		filelist1.setFileName(filename);
		filelist1.setFilePath(t.getAbsolutePath().replace("\\", "/").replace(this.rootPath, ""));
		filelist1.setFileShuffix(shuffix);
		filelist1.setSize(filelist.getSize());
		filelist1.setUploadTime(new Date());
		filelist1.setFpath(topath);
		filelist1.setContentType(filelist.getContentType());
		filelist1.setUser(user);
		fileListDao.save(filelist1);



    }

    @Override
    public void copyFileIO(File s, File t) {
        InputStream fis = null;
		OutputStream fos = null;

		try {
			fis = new BufferedInputStream(new FileInputStream(s));
			fos = new BufferedOutputStream(new FileOutputStream(t));
			byte[] buf = new byte[2048];
			int i ;
			while((i = fis.read(buf)) != -1){
				fos.write(buf, 0, i);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

    }

    @Override
    public List<FilePath> mcPathLoad(Long mctoid, List<Long> mcpathids) {

        List<FilePath> showsonpath = new ArrayList<>();
		List<FilePath> sonpaths = filePathdao.findByParentIdAndPathIstrash(mctoid, 0L);

		for (FilePath sonpath : sonpaths) {
			boolean nosame = true;
			for (Long mcpathid : mcpathids) {
				if(sonpath.getId().equals(mcpathid)){
					nosame = false;
					break;
				}
			}
			if(nosame){
				showsonpath.add(sonpath);
			}
		}
		return showsonpath;

    }

    @Override
    public void reName(String name, Long renamefp, Long nowpathid, boolean isfile) {

        if(isfile){
			//文件名修改
			FileList fl = fileListDao.findById(renamefp).get();
			String newname = onlyName(name, fl.getFpath(), fl.getFileShuffix(), 1, isfile);
			fl.setFileName(newname);
			fileListDao.save(fl);
		}else{
			//文件夹名修改
			FilePath fp = filePathdao.findById(renamefp).get();
			FilePath filepath = filePathdao.findById(nowpathid).get();
			String newname = onlyName(name, filepath, null, 1, false);
			fp.setPathName(newname);
			filePathdao.save(fp);
		}

    }

    @Override
    public String onlyName(String name, FilePath filepath, String shuffix, int num, boolean isfile) {
        Object f = null;
		if (isfile) {
			f = fileListDao.findByFileNameAndFpath(name, filepath);
		}else{
			f = filePathdao.findByPathNameAndParentId(name, filepath.getId());
		}
		if(f != null){
			int num2 = num -1;
			if(shuffix == null){
				name = name.replace("("+num2+")", "")+"("+num+")";
			}else{
				name = name.replace("."+shuffix,"").replace("("+num2+")", "")+"("+num+")"+"."+shuffix;
			}
			num += 1;
			return onlyName(name,filepath,shuffix,num,isfile);
		}
		return name;
    }

    @Override
    public File getFile(String filepath) {
        return new File(this.rootPath,filepath);
    }

    @Override
    public String savePath(FilePath nowpath, List<FilePath> parentpaths) {
        findAllParent(nowpath,parentpaths);
		Collections.reverse(parentpaths);
		String savepath = "";
		for (FilePath filePath : parentpaths) {
			savepath += filePath.getPathName()+"/";
		}
		savepath = savepath.substring(0, savepath.length()-1);
		return savepath;
    }

    @Override
    public File get(Attachment att) {
        return new File(this.rootPath+att.getAttachmentPath());
    }

    @Override
    public Attachment get(String filePath) {
        return attachmentDao.findByAttachmentPath(filePath);
    }
}
