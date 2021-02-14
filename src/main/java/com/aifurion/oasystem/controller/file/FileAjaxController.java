package com.aifurion.oasystem.controller.file;

import com.aifurion.oasystem.entity.file.FilePath;
import com.aifurion.oasystem.entity.user.Position;
import com.aifurion.oasystem.service.FileManageService;
import com.aifurion.oasystem.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.soap.Addressing;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 19:36
 */

@Controller
public class FileAjaxController {

    @Autowired
    private FileManageService fileManageService;

    @Autowired
    private FileService fileService;

    @PostMapping("/filetypeload")
    public String fileTypeLoad(@SessionAttribute("userId")Long userid,
			@RequestParam("type") String type,
			Model model) {

        fileManageService.fileTypeLoad(userid, type, model);

        return "file/filetypeload";

    }


    @PostMapping("/mcloadpath")
    public String mcLoadPath(@RequestParam("mctoid") Long mctoid, @RequestParam("mcpathids") List<Long> mcpathids, Model model) {

        List<FilePath> showsonpaths = fileService.mcPathLoad(mctoid, mcpathids);
		model.addAttribute("mcpaths",showsonpaths);
        return "file/mcpathload";
    }


    @PostMapping("/findfileandpath")
    public String findFile(@SessionAttribute("userId") Long userid,
			@RequestParam(value = "findfileandpath",required=false) String fileName,
			@RequestParam(value = "type",defaultValue="all") String type,
			Model model) {

        fileManageService.findFile(userid, fileName, type, model);
        return "file/filetypeload";

    }

    @PostMapping("/fileloadshare")
    public String fileShare(@RequestParam("type") String type,
			@RequestParam(value="checkfileids[]",required=false) List<Long> checkfileids,
			Model model) {

        if (checkfileids!=null) {
			fileService.doShare(checkfileids);
		}
		model.addAttribute("message","分享成功");
		model.addAttribute("type", type);
        return "forward:/filetypeload";

    }

    @PostMapping("/fileloaddeletefile")
    public String deleteFile(@RequestParam("type") String type,
			@RequestParam(value="checkpathids[]",required=false) List<Long> checkpathids,
			@RequestParam(value="checkfileids[]",required=false) List<Long> checkfileids,
			Model model) {

        if (checkfileids!=null) {
			// 删除文件
			fileService.deleteFile(checkfileids);
		}
		if (checkpathids!=null) {
			// 删除文件夹
			fileService.deletePath(checkpathids);
		}
		model.addAttribute("type", type);
        return "forward:/filetypeload";

    }



    @PostMapping("/fileloadtrashfile")
    public String moveFileToTrash(@SessionAttribute("userId") Long userid,
			@RequestParam("type") String type,
			@RequestParam(value="checkpathids[]",required=false) List<Long> checkpathids,
			@RequestParam(value="checkfileids[]",required=false) List<Long> checkfileids,
			Model model) {

        if (checkfileids!=null) {
			// 文件放入回收站
			fileService.trashFile(checkfileids, 1L,userid);
		}
		if (checkpathids!=null) {
			// 删除文件夹
			fileService.trashPath(checkpathids,1L,true);
			//fs.trashPath(checkpathids);
		}

		model.addAttribute("type", type);

        return "forward:/filetypeload";

    }


    @PostMapping("/fileloadrename")
	public String fileloadrename(@RequestParam("type") String type,
			@RequestParam("renamefp") Long renamefp,
			@RequestParam("creatpathinput") String creatpathinput,
			@RequestParam("isfile") boolean isfile,
			@RequestParam(value="pathid",required=false) Long pathid,
			Model model){

		fileService.reName(creatpathinput, renamefp, pathid, isfile);
		model.addAttribute("type", type);
		return "forward:/filetypeload";
	}


	@RequestMapping("/filereturnback")
	public String filereturnback(@SessionAttribute("userId") Long userid,
			@RequestParam("type") String type,
			@RequestParam(value="checkpathids[]",required=false) List<Long> checkpathids,
			@RequestParam(value="checkfileids[]",required=false) List<Long> checkfileids,
			Model model){
		if (checkfileids!=null) {

			fileService.fileReturnBack(checkfileids,userid);
		}
		if (checkpathids!=null) {
			fileService.pathReturnBack(checkpathids, userid);
		}

		model.addAttribute("type", type);
		return "forward:/filetypeload";

	}





}
