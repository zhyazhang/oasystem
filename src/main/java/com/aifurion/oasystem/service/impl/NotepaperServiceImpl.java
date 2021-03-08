package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.process.NotepaperDao;
import com.aifurion.oasystem.service.NotepaperService;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/3/8 19:03
 */

@Service
public class NotepaperServiceImpl implements NotepaperService {


    @Value("${img.rootpath}")
    private String rootpath;


    @Autowired
    private NotepaperDao notepaperDao;

    /**
	 * 上传头像
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public String upload(MultipartFile file){

		File dir=new File(rootpath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String fileName=file.getOriginalFilename();
		if(!StringUtil.isEmpty(fileName)){

			String suffix= FilenameUtils.getExtension(fileName);
			String newFileName = UUID.randomUUID().toString().toLowerCase() + "." + suffix;
			File targetFile = new File(dir,newFileName);
            try {
                file.transferTo(targetFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return targetFile.getPath().replace("\\", "/").replace(rootpath, "");
		}else{
			return null;
		}

	}



    @Override
    public void deleteNotepaper(Long id) {

        notepaperDao.deleteById(id);


    }
}
