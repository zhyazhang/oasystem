package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.formVaild.BindingResultVOUtil;
import com.aifurion.oasystem.common.formVaild.MapToList;
import com.aifurion.oasystem.common.formVaild.ResultEnum;
import com.aifurion.oasystem.common.formVaild.ResultVO;
import com.aifurion.oasystem.dao.index.IndexDao;
import com.aifurion.oasystem.dao.role.RoleDao;
import com.aifurion.oasystem.dao.role.RolepowerListDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.role.Role;
import com.aifurion.oasystem.entity.role.Rolemenu;
import com.aifurion.oasystem.entity.role.Rolepowerlist;
import com.aifurion.oasystem.entity.system.SystemMenu;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.MenuSysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 11:24
 */

@Service
@Transactional(rollbackOn = Exception.class)
public class MenuSysServiceImpl implements MenuSysService {


    @Autowired
    private RolepowerListDao rolepowerListDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private IndexDao indexDao;


    @Autowired
    private RoleDao roleDao;

    //新增与修改菜单管理的内容

    @Override
    public SystemMenu save(SystemMenu menu) {

        SystemMenu systemMenu = indexDao.save(menu);

        return systemMenu;
    }

    //上下移动按钮改变其他的排序值

    @Override
    public int changeSortOtherId(Integer sortId, Integer arithNum, Long parentId) {

        int i = indexDao.changeSortOtherId(sortId, arithNum, parentId);


        return i;
    }

    @Override
    public int changeSortSelfId(Integer sortId, Integer arithNum, Long menuId) {

        int i = indexDao.changeSortSelfId(sortId, arithNum, menuId);
        return i;
    }

    @Override
    public void findMenuSys(HttpServletRequest req, User user) {

        List<Rolemenu> parentMenus1 = rolepowerListDao.findByParentDisplayAll(0L, user.getRole().getRoleId(), true, true);

        List<Rolemenu> parentMenus2 = rolepowerListDao.findByParentsDisplay(0L, user.getRole().getRoleId(), true, true);

        req.setAttribute("oneMenuAll", parentMenus1);
        req.setAttribute("twoMenuAll", parentMenus2);
    }

    @Override
    public void findAllMenuSys(HttpServletRequest req) {

        //查找所有父级
        Iterable<SystemMenu> menuAll1 = indexDao.findByParentIdOrderBySortId(0L);

        //查找所有子级
        Iterable<SystemMenu> menuAll2 = indexDao.findByParentIdNotOrderBySortId(0L);


        req.setAttribute("oneMenuAll", menuAll1);
        req.setAttribute("twoMenuAll", menuAll2);

    }

    @Override
    public int deleteThis(Long menuId) {

        int i = indexDao.deleteThis(menuId);
        return i;
    }


    @Override
    public void getMenuTable(HttpServletRequest req) {

        if(!StringUtils.isEmpty(req.getParameter("name"))){
			req.setAttribute("oneMenuAll", indexDao.findByMenuNameLike("%"+req.getParameter("name")+"%"));
		}
		else{
			findAllMenuSys(req);
		}

    }


    @Override
    public void changeSortId(HttpServletRequest req, Long userId) {

        User user=userDao.findById(userId).get();
		Long parentId = Long.parseLong(req.getParameter("parentid"));
		Long menuId = Long.parseLong(req.getParameter("menuid"));
		Integer sortId = Integer.parseInt(req.getParameter("sortid"));
		Integer arithNum = Integer.parseInt(req.getParameter("num"));
		findMenuSys(req,user);

    }


    @Override
    public void menuEdit(HttpServletRequest req) {

        if(!StringUtils.isEmpty(req.getAttribute("errormess"))){
			req.setAttribute("errormess", req.getAttribute("errormess"));
		}
		if(!StringUtils.isEmpty(req.getAttribute("success"))){
			req.setAttribute("success", req.getAttribute("success"));
		}

		List<SystemMenu> parentList=indexDao.findByParentIdOrderBySortId(0L);
		req.setAttribute("parentList", parentList);
		HttpSession session = req.getSession();
		session.removeAttribute("getId");
        SystemMenu menuObj = new SystemMenu();
        if (!StringUtils.isEmpty(req.getParameter("id"))) {
            Long getId = Long.parseLong(req.getParameter("id"));
            menuObj = indexDao.findById(getId).get();

            if (!StringUtils.isEmpty(req.getParameter("add"))) {
                Long getAdd = menuObj.getMenuId();
                String getName = menuObj.getMenuName();
                req.setAttribute("getAdd", getAdd);
                req.setAttribute("getName", getName);

            } else {
                session.setAttribute("getId", getId);

            }


        } else {
            menuObj.setParentId(0L);
        }

        req.setAttribute("menuObj", menuObj);


    }


    @Override
    public void validForm(HttpServletRequest req, SystemMenu menu, BindingResult br) {


        HttpSession session = req.getSession();
		Long menuId = null;
		req.setAttribute("menuObj", menu);

		// 这里返回ResultVO对象，如果校验通过，ResultEnum.SUCCESS.getCode()返回的值为200；否则就是没有通过；
		ResultVO res = BindingResultVOUtil.hasErrors(br);
		// 校验失败
		if (!ResultEnum.SUCCESS.getCode().equals(res.getCode())) {
			List<Object> list = new MapToList<>().mapToList(res.getData());
			req.setAttribute("errormess", list.get(0).toString());
		}
		// 校验通过，下面写自己的逻辑业务
		else {
			// 判断是否从编辑界面进来的，前面有"session.setAttribute("getId",getId);",在这里获取，并remove掉；
			if (!StringUtils.isEmpty(session.getAttribute("getId"))) {
				menuId = (Long)session.getAttribute("getId"); // 获取进入编辑界面的menuID值
				menu.setMenuId(menuId);
				session.removeAttribute("getId");
				save(menu);
			}else{
				//执行新增 的代码；
				save(menu);
				List<Role> roles=roleDao.findAll();
				for (Role role : roles) {
					if(role.getRoleId()==1){
						rolepowerListDao.save(new Rolepowerlist(role, menu, true));
					}else{
						rolepowerListDao.save(new Rolepowerlist(role, menu, false));
					}
				}

			}
			//执行业务代码
			req.setAttribute("success", "后台验证成功");
		}


    }
}
