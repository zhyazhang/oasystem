package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.note.AttachmentDao;
import com.aifurion.oasystem.dao.process.BursementDao;
import com.aifurion.oasystem.dao.process.ProcessListDao;
import com.aifurion.oasystem.dao.process.ReviewedDao;
import com.aifurion.oasystem.dao.process.SubjectDao;
import com.aifurion.oasystem.dao.role.RoleDao;
import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.user.DeptDao;
import com.aifurion.oasystem.dao.user.PositionDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.note.Attachment;
import com.aifurion.oasystem.entity.process.AubUser;
import com.aifurion.oasystem.entity.process.ProcessList;
import com.aifurion.oasystem.entity.process.Reviewed;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.user.Dept;
import com.aifurion.oasystem.entity.user.Position;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.MailService;
import com.aifurion.oasystem.service.ProcessService;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:21
 */

@Service
@Transactional
public class ProcessServiceImpl implements ProcessService {

    @Autowired
    private UserDao udao;
    @Autowired
    private DeptDao ddao;
    @Autowired
    private RoleDao rdao;
    @Autowired
    private PositionDao pdao;
    @Autowired
    private SubjectDao sudao;
    @Autowired
    private StatusDao sdao;
    @Autowired
    private TypeDao tydao;
    @Autowired
    private ReviewedDao redao;
    @Autowired
    private AttachmentDao AttDao;
    @Autowired
    private BursementDao budao;
    @Autowired
    private MailService mailService;
    @Autowired
    private ProcessListDao prodao;

    /**
     * 写文件 方法
     *
     * @param response
     * @param file
     * @throws IOException
     */
    public void writefile(HttpServletResponse response, File file) {
        ServletOutputStream sos = null;
        FileInputStream aa = null;
        try {
            aa = new FileInputStream(file);
            sos = response.getOutputStream();
            // 读取文件问字节码
            byte[] data = new byte[(int) file.length()];
            IOUtils.readFully(aa, data);
            // 将文件流输出到浏览器
            IOUtils.write(data, sos);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                sos.close();
                aa.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }


    public void user(int page, int size, Model model) {
        Pageable pa = PageRequest.of(page, size);
        //查看用户并分页
        Page<User> pageuser = udao.findAll(pa);
        List<User> userlist = pageuser.getContent();
        // 查询部门表
        Iterable<Dept> deptlist = ddao.findAll();
        // 查职位表
        Iterable<Position> poslist = pdao.findAll();
        model.addAttribute("page", pageuser);
        model.addAttribute("emplist", userlist);
        model.addAttribute("deptlist", deptlist);
        model.addAttribute("poslist", poslist);
        model.addAttribute("url", "names");
    }


    public Page<AubUser> index(User user, int page, int size, String val, org.springframework.ui.Model model) {
        Pageable pa = PageRequest.of(page, size);
        Page<AubUser> pagelist = null;
        Page<AubUser> pagelist2 = null;
        List<Sort.Order> orders = new ArrayList<>();
        User u = udao.findByUserName(val);//找用户
        SystemStatusList status = sdao.findByStatusModelAndStatusName("aoa_process_list", val);
        if (StringUtil.isEmpty(val)) {
            orders.add(new Sort.Order(Sort.Direction.DESC, "applyTime"));
            Sort sort = Sort.by(orders);
            pa = PageRequest.of(page, size, sort);
            pagelist = redao.findByUserIdOrderByStatusId(user, false, pa);
        } else if (!Objects.isNull(u)) {
            pagelist = redao.findprocesslist(user, u, false, pa);
            model.addAttribute("sort", "&val=" + val);
        } else if (!Objects.isNull(status)) {
            pagelist = redao.findbystatusprocesslist(user, status.getStatusId(), false, pa);
            model.addAttribute("sort", "&val=" + val);
        } else {
            pagelist2 = redao.findbytypenameprocesslist(user, val, false, pa);
            if (!pagelist2.hasContent()) {
                pagelist2 = redao.findbyprocessnameprocesslist(user, val, false, pa);
            }
            model.addAttribute("sort", "&val=" + val);
            return pagelist2;
        }
        return pagelist;
    }

    public List<Map<String, Object>> index2(Page<AubUser> page, User user) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<AubUser> prolist = page.getContent();
        for (AubUser aubUser : prolist) {
            String harryname = tydao.findname(aubUser.getDeeply());
            SystemStatusList status = sdao.findById(aubUser.getStatusId()).get();
            Map<String, Object> result = new HashMap<>();
            result.put("typename", aubUser.getTypeNmae());
            result.put("title", aubUser.getProcessName());
            result.put("pushuser", aubUser.getUserName());
            result.put("applytime", aubUser.getApplyTime());
            result.put("harry", harryname);
            result.put("statusname", status.getStatusName());
            result.put("statuscolor", status.getStatusColor());
            result.put("proid", aubUser.getProcessId());
            list.add(result);

        }
        return list;
    }

    /**
     * 审核人封装
     */
    public List<Map<String, Object>> index4(ProcessList process) {
        List<Map<String, Object>> relist = new ArrayList<>();
        List<Reviewed> revie = redao.findByReviewedTimeNotNullAndProId(process);
        for (int i = 0; i < revie.size(); i++) {
            Map<String, Object> result = new HashMap<>();
            User u = udao.findById(revie.get(i).getUserId().getUserId()).get();
            Position po = pdao.findById(u.getPosition().getId()).get();
            SystemStatusList status = sdao.findById(revie.get(i).getStatusId()).get();
            result.put("poname", po.getName());
            result.put("username", u.getUserName());
            result.put("retime", revie.get(i).getReviewedTime());
            result.put("restatus", status.getStatusName());
            result.put("statuscolor", status.getStatusColor());
            result.put("des", revie.get(i).getAdvice());
            result.put("img", u.getImgPath());
            result.put("positionid", u.getPosition().getId());
            relist.add(result);
        }
        return relist;
    }

    /**
     * process数据封装
     */

    public Map<String, Object> index3(String name, User user, String typename, ProcessList process) {
        System.out.println(name);
        Map<String, Object> result = new HashMap<>();
        String harryname = tydao.findname(process.getDeeply());
        result.put("proId", process.getProcessId());
        result.put("harryname", harryname);
        result.put("processName", process.getProcessName());
        result.put("processDescribe", process.getProcessDescribe());
        if (("审核").equals(name)) {
            result.put("username", process.getUserId().getUserName());//提单人员
            result.put("deptname", ddao.findname(process.getUserId().getDept().getDeptId()));//部门
        } else if (("申请").equals(name)) {
            result.put("username", user.getUserName());
            result.put("deptname", ddao.findname(process.getUserId().getDept().getDeptId()));
        }
        result.put("applytime", process.getApplyTime());
        if (!Objects.isNull(process.getProFileid())) {
            result.put("file", process.getProFileid());
        } else {
            result.put("file", "file");
        }
        result.put("name", name);
        result.put("typename", process.getTypeNmae());
        result.put("startime", process.getStartTime());
        result.put("endtime", process.getEndTime());
        result.put("tianshu", process.getProcseeDays());
        result.put("statusid", process.getStatusId());
        if (process.getProFileid() != null) {
            result.put("filepath", process.getProFileid().getAttachmentPath());
            if (process.getProFileid().getAttachmentType().startsWith("image")) {
                result.put("filetype", "img");
            } else {
                result.put("filetype", "appli");
            }
        }
        return result;
    }

    /**
     * 公用
     */
    public void index6(org.springframework.ui.Model model, Long id, int page, int size) {
        User lu = udao.findById(id).get();//申请人
        Pageable pa = PageRequest.of(page, size);
        List<SystemTypeList> harrylist = tydao.findByTypeModel("aoa_process_list");
        //查看用户并分页
        Page<User> pageuser = udao.findAll(pa);
        List<User> userlist = pageuser.getContent();
        // 查询部门表
        Iterable<Dept> deptlist = ddao.findAll();
        // 查职位表
        Iterable<Position> poslist = pdao.findAll();
        model.addAttribute("page", pageuser);
        model.addAttribute("emplist", userlist);
        model.addAttribute("deptlist", deptlist);
        model.addAttribute("poslist", poslist);
        model.addAttribute("url", "names");
        model.addAttribute("username", lu.getUserName());
        model.addAttribute("harrylist", harrylist);
    }

    /**
     * 存表
     *
     * @throws IOException
     * @throws IllegalStateException
     */
    public void index5(ProcessList pro, String val, User lu, MultipartFile filePath, String name) {

        pro.setTypeNmae(val);
        pro.setApplyTime(new Date());
        pro.setUserId(lu);
        pro.setStatusId(23L);
        pro.setShenuser(name);
        Attachment attaid = null;
        if (!StringUtil.isEmpty(filePath.getOriginalFilename())) {
            attaid = mailService.upload(filePath, lu);
            attaid.setModel("aoa_bursement");
            AttDao.save(attaid);
            pro.setProFileid(attaid);
        }
    }

    public void index8(ProcessList pro, String val, User lu, String name) {
        pro.setTypeNmae(val);
        pro.setApplyTime(new Date());
        pro.setUserId(lu);
        pro.setStatusId(23L);
        pro.setShenuser(name);
    }

    /**
     * 存主表
     */
    public void save(Long proid, User u, Reviewed reviewed, ProcessList pro, User u2) {
        Reviewed re = redao.findByProIdAndUserId(proid, u);
        re.setAdvice(reviewed.getAdvice());
        re.setStatusId(reviewed.getStatusId());
        re.setReviewedTime(new Date());
        re.setStatusId(reviewed.getStatusId());
        redao.save(re);


        Reviewed re2 = new Reviewed();
        re2.setProId(pro);
        re2.setUserId(u2);
        re2.setStatusId(23L);
        redao.save(re2);

        pro.getShenuser();
        pro.setShenuser(pro.getShenuser() + ";" + u2.getUserName());
        pro.setStatusId(24L);//改变主表的状态
        prodao.save(pro);
    }

    /**
     * 存审核表
     */
    public void index7(User reuser, ProcessList pro) {
        Reviewed revie = new Reviewed();
        revie.setUserId(reuser);
        revie.setStatusId(23L);
        revie.setProId(pro);
        redao.save(revie);
    }





}



