package com.aifurion.oasystem.common;

import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.PublicKey;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/22 10:58
 */

@Component
public class CommonMethods {

    @Autowired
    private TypeDao typeDao;

    @Autowired
    private StatusDao statusDao;

    public void setTypeStatus(HttpServletRequest request, String typeTable, String statusTable) {

        List<SystemTypeList> typeLists = (List<SystemTypeList>) typeDao.findByTypeModel(typeTable);
        List<SystemStatusList> statusLists = (List<SystemStatusList>) statusDao.findByStatusModel(statusTable);
        request.setAttribute("typelist", typeLists);
        request.setAttribute("statuslist", statusLists);

    }



    public static void setSomething(String baseKey, Object type, Object status, Object time, Object icon, Model model) {
        if (!StringUtils.isEmpty(icon)) {
            model.addAttribute("icon", icon);
            if (!StringUtils.isEmpty(type)) {
                model.addAttribute("type", type);
                if ("1".equals(type)) {
                    model.addAttribute("sort", "&type=1&icon=" + icon);
                } else {
                    model.addAttribute("sort", "&type=0&icon=" + icon);
                }
            }
            if (!StringUtils.isEmpty(status)) {
                model.addAttribute("status", status);
                if ("1".equals(status)) {
                    model.addAttribute("sort", "&status=1&icon=" + icon);
                } else {
                    model.addAttribute("sort", "&status=0&icon=" + icon);
                }
            }
            if (!StringUtils.isEmpty(time)) {
                model.addAttribute("time", time);
                if ("1".equals(time)) {
                    model.addAttribute("sort", "&time=1&icon=" + icon);
                } else {
                    model.addAttribute("sort", "&time=0&icon=" + icon);
                }
            }
        }
        if (!StringUtils.isEmpty(baseKey)) {
            model.addAttribute("sort", "&baseKey=" + baseKey);
        }

    }


}
