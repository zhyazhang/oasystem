package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.user.DeptDao;
import com.aifurion.oasystem.dao.user.PositionDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.user.Dept;
import com.aifurion.oasystem.entity.user.Position;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/19 14:19
 */

@Service
public class DeptServiceImpl implements DeptService {


    @Autowired
    DeptDao deptdao;
	@Autowired
    UserDao userDao;
    @Autowired
    PositionDao positionDao;


	@Override
	public Dept findOne(Long id) {
		return deptdao.findById(id).get();
	}


	@Override
	public List<Dept> findAll() {

		List<Dept> depts = (List<Dept>) deptdao.findAll();

		return depts;
	}

	@Override
    public void deptManage(Model model) {

        List<Dept> depts = (List<Dept>) deptdao.findAll();
        model.addAttribute("depts", depts);

    }

    @Override
    public boolean addDept(Dept dept, String xg, BindingResult br, Model model) {

		if(!br.hasErrors()){
			Dept adddept = deptdao.save(dept);
			if("add".equals(xg)){
				Position jinli = new Position();
				jinli.setDeptid(adddept.getDeptId());
				jinli.setName("经理");
				Position wenyuan = new Position();
				wenyuan.setDeptid(adddept.getDeptId());
				wenyuan.setName("文员");
				positionDao.save(jinli);
				positionDao.save(wenyuan);
			}
			if(adddept!=null){
				model.addAttribute("success",1);
				return false;
			}
		}
		model.addAttribute("errormess","错误！~");

        return true;
    }

    @Override
    public void changeDept(Long deptId, Model model) {

        if(deptId!=null){
			Dept dept = deptdao.findById(deptId).get();
			model.addAttribute("dept",dept);
		}


    }

    @Override
    public void readDept(Long deptId, Model model) {

        Dept dept = deptdao.findById(deptId).get();
		User deptmanage = null;
		if(dept.getDeptmanager()!=null){
			deptmanage = userDao.findById(dept.getDeptmanager()).get();
			model.addAttribute("deptmanage",deptmanage);
		}
		List<Dept> depts = (List<Dept>) deptdao.findAll();
		List<Position> positions = positionDao.findByDeptidAndNameNotLike(1L, "%经理");
		List<User> formaluser = new ArrayList<>();
		List<User> deptusers = userDao.findByDept(dept);

		for (User deptuser : deptusers) {
			Position position = deptuser.getPosition();
			if(!position.getName().endsWith("经理")){
				formaluser.add(deptuser);
			}
		}
		model.addAttribute("positions",positions);
		model.addAttribute("depts",depts);
		model.addAttribute("deptuser",formaluser);

		model.addAttribute("dept",dept);
		model.addAttribute("isread",1);

    }

    @Override
    public void depTanDpositionChange(Long positionid, Long changedeptid, Long userid, Long deptid, Model model) {


        User user = userDao.findById(userid).get();
		Dept changedept = deptdao.findById(changedeptid).get();
		Position position = positionDao.findById(positionid).get();
		user.setDept(changedept);
		user.setPosition(position);
		userDao.save(user);
		model.addAttribute("deptid",deptid);
    }

    @Override
    public void deletDept(Long deletedeptid) {

        Dept dept = deptdao.findById(deletedeptid).get();
		List<Position> ps = positionDao.findByDeptid(deletedeptid);
		for (Position position : ps) {
			positionDao.delete(position);
		}
        deptdao.delete(dept);

    }

    @Override
    public void deptManageChange(Long positionid, Long changedeptid, Long oldmanageid, Long newmanageid, Long deptid, Model model) {


		Dept deptnow = deptdao.findById(deptid).get();
		if(oldmanageid!=null){
			User oldmanage = userDao.findById(oldmanageid).get();

			Position namage = oldmanage.getPosition();

			Dept changedept = deptdao.findById(changedeptid).get();
			Position changeposition = positionDao.findById(positionid).get();

			oldmanage.setDept(changedept);
			oldmanage.setPosition(changeposition);
			userDao.save(oldmanage);

			if(newmanageid!=null){
				User newmanage = userDao.findById(newmanageid).get();
				newmanage.setPosition(namage);
				deptnow.setDeptmanager(newmanageid);
				deptdao.save(deptnow);
				userDao.save(newmanage);
			}else{
				deptnow.setDeptmanager(null);
				deptdao.save(deptnow);
			}

		}else{
			User newmanage = userDao.findById(newmanageid).get();
			Position manage = positionDao.findByDeptidAndNameLike(deptid, "%经理").get(0);
			newmanage.setPosition(manage);
			deptnow.setDeptmanager(newmanageid);
			deptdao.save(deptnow);
			userDao.save(newmanage);
		}

		model.addAttribute("deptid",deptid);



    }
}
