package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.role.RoleDao;
import com.aifurion.oasystem.dao.user.DeptDao;
import com.aifurion.oasystem.dao.user.PositionDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.role.Role;
import com.aifurion.oasystem.entity.user.Dept;
import com.aifurion.oasystem.entity.user.Position;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.UserService;
import com.github.pagehelper.util.StringUtil;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/23 9:55
 */

@Service
public class UserServiceImpl implements UserService {


	@Autowired
	private UserDao userDao;

	@Autowired
	private DeptDao deptDao;

	@Autowired
	private PositionDao positionDao;

	@Autowired
	private RoleDao roleDao;


	@Override
	public Long findPkId(Long taskId, Long userId) {
		return userDao.findpkId(taskId, userId);
	}

	@Override
	public User findUserByName(String name) {
		return userDao.findId(name);
	}

	@Override
	public Page<User> findByFatherId(Long id, Pageable pageable) {
		return userDao.findByFatherId(id, pageable);
	}

	@Override
	public User findOne(Long id) {
		return userDao.findById(id).get();
	}

	@Override
	public List<Position> selectdept(Long deptid) {
		return positionDao.findByDeptidAndNameNotLike(deptid, "%经理");
	}

	@Override
	public boolean useronlyname(String username) {
	 User user = userDao.findByUserName(username);
		return user == null;
	}

	@Override
	public void deleteUser(Long id, Model model) {

		User user = userDao.findById(id).get();
		user.setIsLock(1);
		userDao.save(user);
		model.addAttribute("success", 1);
	}

	@Override
    public void addUser(User user, Long deptid, Long positionid, Long roleid, boolean isbackpassword, Model model) {

		Dept dept = deptDao.findById(deptid).get();
		Position position = positionDao.findById(positionid).get();
		Role role = roleDao.findById(roleid).get();
		if(user.getUserId()==null){
            String pinyin= null;
            try {
                pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
            } catch (PinyinException e) {
                e.printStackTrace();
            }
            user.setPinyin(pinyin);
			user.setPassword("123456");
			user.setDept(dept);
			user.setRole(role);
			user.setPosition(position);
			user.setFatherId(dept.getDeptmanager());
			userDao.save(user);
		}else{
			User user2 = userDao.findById(user.getUserId()).get();
			user2.setUserTel(user.getUserTel());
			user2.setRealName(user.getRealName());
			user2.setEamil(user.getEamil());
			user2.setAddress(user.getAddress());
			user2.setUserEdu(user.getUserEdu());
			user2.setSchool(user.getSchool());
			user2.setIdCard(user.getIdCard());
			user2.setBank(user.getBank());
			user2.setThemeSkin(user.getThemeSkin());
			user2.setSalary(user.getSalary());
			user2.setFatherId(dept.getDeptmanager());
			if(isbackpassword){
				user2.setPassword("123456");
			}
			user2.setDept(dept);
			user2.setRole(role);
			user2.setPosition(position);
			userDao.save(user2);
		}

		model.addAttribute("success",1);


    }

    @Override
	public void getUserEdit(Long userid, Model model) {

		if (userid != null) {
            User user = userDao.findById(userid).get();
            model.addAttribute("where", "xg");
            model.addAttribute("user", user);
        }

        List<Dept> depts = (List<Dept>) deptDao.findAll();
        List<Position> positions = (List<Position>) positionDao.findAll();
        List<Role> roles = (List<Role>) roleDao.findAll();

        model.addAttribute("depts", depts);
        model.addAttribute("positions", positions);
        model.addAttribute("roles", roles);

	}

	@Override
	public void userPaging(Model model, int page, int size, String usersearch) {

		Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "dept"));
        Pageable pa = PageRequest.of(page, size, sort);
        Page<User> userspage = null;
        if (StringUtil.isEmpty(usersearch)) {
            userspage = userDao.findByIsLock(0, pa);
        } else {
            System.out.println(usersearch);
            userspage = userDao.findNameLike(usersearch, pa);
        }
        List<User> users = userspage.getContent();
        model.addAttribute("users", users);
        model.addAttribute("page", userspage);
        model.addAttribute("url", "usermanagepaging");



	}

	@Override
	public void userManage(Model model, int page, int size) {

		 Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "dept"));
        Pageable pa = PageRequest.of(page, size, sort);
        Page<User> userspage = userDao.findByIsLock(0, pa);
        List<User> users = userspage.getContent();
        model.addAttribute("users", users);
        model.addAttribute("page", userspage);
        model.addAttribute("url", "usermanagepaging");

	}

	@Override
	public Page<User> findMyEmployUser(int page, String baseKey, long parentid) {
	Pageable pa=PageRequest.of(page, 10);
		if (!StringUtils.isEmpty(baseKey)) {
			// 模糊查询
			return userDao.findbyFatherId(baseKey, parentid, pa);
		}
		else{
			return userDao.findByFatherId(parentid, pa);
		}
	}
}
