package com.aifurion.oasystem.controller.panel;

import com.aifurion.oasystem.entity.process.Notepaper;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.NotepaperService;
import com.aifurion.oasystem.service.UserPanelService;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/3/8 18:37
 */

@Controller
public class UserPanelController {

    @Autowired
    private UserPanelService userPanelService;


    @Autowired
    private NotepaperService notepaperService;

    @Value("${img.rootpath}")
    private String rootpath;


    @RequestMapping("/userpanel")
    public String userPanel(@SessionAttribute("userId") Long userId, Model model, HttpServletRequest req,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size) {

        userPanelService.userPanel(userId, model, req, page, size);

        return "user/userpanel";
    }


    /**
     * 上下页
     */
    @RequestMapping("/panel")
    public String getPanel(@SessionAttribute("userId") Long userId, Model model,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size) {
        userPanelService.getPanel(userId, model, page, size);
        return "user/panel";
    }


    /**
     * 存便签
     */
    @RequestMapping("/writep")
    public String savePanel(Notepaper notepaper, @SessionAttribute("userId") Long userId, @RequestParam(value = "concent", required = false) String concent) {

        userPanelService.savePanel(notepaper, userId, concent);

        return "redirect:/userpanel";
    }


    /**
     * 删除便签
     */
    @RequestMapping("/notepaper")
    public String deletePaper(HttpServletRequest request, @SessionAttribute("userId") Long userId) {

        Long paperId = Long.parseLong(request.getParameter("id"));
        boolean permissions = userPanelService.deletePaper(paperId, userId);

        if (permissions) {
            notepaperService.deleteNotepaper(paperId);
        } else {
            return "redirect:/notlimit";
        }
        return "redirect:/userpanel";
    }


    /**
	 * 修改用户
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@RequestMapping("/saveuser")
	public String saveUser(@RequestParam("filePath") MultipartFile filePath, HttpServletRequest request, @Valid User user,
                          BindingResult br, @SessionAttribute("userId") Long userId) throws IllegalStateException, IOException{


        userPanelService.saveUser(filePath, request, user, br, userId);
		return "forward:/userpanel";

	}

	@RequestMapping("/image/**")
	public void image(Model model, HttpServletResponse response, @SessionAttribute("userId") Long userId, HttpServletRequest request)
			throws Exception {
		String projectPath = ClassUtils.getDefaultClassLoader().getResource("").getPath();
		String startpath = new String(URLDecoder.decode(request.getRequestURI(), "utf-8"));

		String path = startpath.replace("/image", "");

		File f = new File(rootpath, path);

		ServletOutputStream sos = response.getOutputStream();
		FileInputStream input = new FileInputStream(f.getPath());
		byte[] data = new byte[(int) f.length()];
		IOUtils.readFully(input, data);
		// 将文件流输出到浏览器
		IOUtils.write(data, sos);
		input.close();
		sos.close();
	}










}
