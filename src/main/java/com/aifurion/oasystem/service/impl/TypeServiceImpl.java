package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.formVaild.MapToList;
import com.aifurion.oasystem.common.formVaild.ResultEnum;
import com.aifurion.oasystem.common.formVaild.ResultVO;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 20:51
 */
@Service
public class TypeServiceImpl implements TypeService {


    @Autowired
    private TypeDao typeDao;


    @Override
    public SystemTypeList findOne(Long id) {
        return typeDao.findById(id).get();
    }

    @Override
    public void checkForm(HttpServletRequest req, SystemTypeList menu, ResultVO resultVO) {

        HttpSession session = req.getSession();
        Long menuId = null;

        if (!ResultEnum.SUCCESS.getCode().equals(resultVO.getCode())) {
			List<Object> list = new MapToList<>().mapToList(resultVO.getData());
			req.setAttribute("errormess", list.get(0).toString());
		}else {
			// 判断是否从编辑界面进来的，前面有"session.setAttribute("getId",getId);",在这里获取，并remove掉；
			if (!StringUtils.isEmpty(session.getAttribute("typeid"))) {
				menuId = (Long) session.getAttribute("typeid"); // 获取进入编辑界面的menuID值
				menu.setTypeId(menuId);
				session.removeAttribute("typeid");
			}
			// 执行业务代码
            typeDao.save(menu);
			req.setAttribute("success", "后台验证成功");
		}



    }


    @Override
    public Iterable<SystemTypeList> findAll() {

        Iterable<SystemTypeList> typeLists = typeDao.findAll();
        return typeLists;
    }

    @Override
    public List<SystemTypeList> findByTypeNameLikeOrTypeModelLike(String name, String string) {

        List<SystemTypeList> typeModelLike = typeDao.findByTypeNameLikeOrTypeModelLike(name, string);

        return typeModelLike;
    }

    @Override
    public SystemTypeList findById(Long id) {

        SystemTypeList systemTypeList = typeDao.findById(id).get();
        return systemTypeList;
    }

    @Override
    public void deleteById(Long id) {

        typeDao.deleteById(id);

    }
}
