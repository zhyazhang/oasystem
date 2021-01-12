package com.aifurion.oasystem.service;

import com.aifurion.oasystem.common.enums.LoginStateEnum;
import com.aifurion.oasystem.entity.user.Dept;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/11 21:15
 */
public interface LoginService {

	/**
	 * 用户登录检查
	 *
	 * @param userName
	 * @param password
	 * @param code
	 * @return
	 */


	LoginStateEnum loginCheck(HttpServletRequest request, String userName, String password, String code);


}
