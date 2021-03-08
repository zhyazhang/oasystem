package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.enums.LoginStateEnum;
import com.aifurion.oasystem.common.enums.UtilEnum;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.dao.user.UserLogDao;
import com.aifurion.oasystem.dao.user.UserLogRecordDao;
import com.aifurion.oasystem.entity.user.LoginRecord;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.LoginService;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Objects;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/11 21:15
 */

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserLogRecordDao userLogRecordDao;

    private String captcha = UtilEnum.CAPTCHA_KEY.getContent();

    @Override
    public LoginStateEnum loginCheck(HttpServletRequest request, String userName, String password, String code) {

        HttpSession session = request.getSession();
        String ip = "";

        User user = userDao.findOneUser(userName, password);
        if (Objects.isNull(user)) {

            return LoginStateEnum.notMatch;

        }

        if (user.getIsLock() == 1) {

            return LoginStateEnum.userLocked;

        }

        Object sessionId = session.getAttribute("userId");

        if (sessionId == user.getUserId()) {

            session.setAttribute("thisuser", user);
            return LoginStateEnum.hasLogin;

        } else {
            session.setAttribute("userId", user.getUserId());
            Browser browser = UserAgent.parseUserAgentString(request.getHeader("User-Agent")).getBrowser();
            Version version = browser.getVersion(request.getHeader("User-Agent"));
            String info = browser.getName() + "/" + version.getVersion();
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            LoginRecord record = new LoginRecord(ip, new Date(), info, user);

            userLogRecordDao.save(record);
            return LoginStateEnum.ok;

        }

    }
}
