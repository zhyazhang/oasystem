package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.tool.Tool;
import com.aifurion.oasystem.dao.attendance.AttendanceDao;
import com.aifurion.oasystem.dao.note.AttachmentDao;
import com.aifurion.oasystem.dao.process.*;
import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.attendance.Attendance;
import com.aifurion.oasystem.entity.note.Attachment;
import com.aifurion.oasystem.entity.process.*;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.ProcedureService;
import com.aifurion.oasystem.service.ProcessService;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:07
 */

@Service
public class ProcedureServiceImpl implements ProcedureService {


    @Autowired
    private UserDao userDao;
    @Autowired
    private SubjectDao subjectDao;
    @Autowired
    private StatusDao statusDao;
    @Autowired
    private TypeDao typeDao;
    @Autowired
    private ReviewedDao reviewedDao;
    @Autowired
    private EvectionMoneyDao evectionMoneyDao;
    @Autowired
    private BursementDao bursementDao;
    @Autowired
    private ProcessListDao processListDao;
    @Autowired
    private DetailsBurseDao detailsBurseDao;
    @Autowired
    private ProcessService processService;
    @Autowired
    private TrafficDao trafficDao;
    @Autowired
    private AttachmentDao attachmentDao;
    @Autowired
    private StayDao stayDao;
    @Autowired
    private EvectionDao evectionDao;
    @Autowired
    private OvertimeDao overtimeDao;
    @Autowired
    private HolidayDao holidayDao;
    @Autowired
    private RegularDao regularDao;
    @Autowired
    private ResignDao resignDao;
    @Autowired
    private AttendanceDao attendanceDao;

    @Value("${attachment.roopath}")
    private String rootpath;

    @Override
    public void bursement(Model model, Long userId, HttpServletRequest request, int page, int size) {

        //查找类型
        List<SystemTypeList> uplist = typeDao.findByTypeModel("aoa_bursement");
        //查找费用科目生成树
        List<Subject> second = subjectDao.findByParentId(1L);
        List<Subject> sublist = subjectDao.findByParentIdNot(1L);
        processService.index6(model, userId, page, size);

        model.addAttribute("second", second);
        model.addAttribute("sublist", sublist);
        model.addAttribute("uplist", uplist);
    }


    @Override
    public String apply(MultipartFile filePath, HttpServletRequest req, Bursement bu, BindingResult br, Long userId) {


        User lu = userDao.findById(userId).get();//申请人
        User reuser = userDao.findByUserName(bu.getUsername());//审核人
        User zhuti = userDao.findByUserName(bu.getNamemoney());//证明人
        Integer allinvoice = 0;
        Double allmoney = 0.0;
        Long roleid = lu.getRole().getRoleId();//申请人角色id
        Long fatherid = lu.getFatherId();//申请人父id
        Long userid = reuser.getUserId();//审核人userid

        String val = req.getParameter("val");

        if (roleid >= 3L && Objects.equals(fatherid, userid)) {


            List<DetailsBurse> mm = bu.getDetails();
            for (DetailsBurse detailsBurse : mm) {
                allinvoice += detailsBurse.getInvoices();
                allmoney += detailsBurse.getDetailmoney();
                detailsBurse.setBurs(bu);
            }
            //在报销费用表里面set票据总数和总金额
            bu.setAllinvoices(allinvoice);
            bu.setAllMoney(allmoney);
            bu.setUsermoney(zhuti);
            //set主表
            ProcessList pro = bu.getProId();
            processService.index5(pro, val, lu, filePath, reuser.getUserName());
            bursementDao.save(bu);

            //存审核表
            processService.index7(reuser, pro);
        } else {
            return "common/proce";
        }
        return "redirect:/xinxeng";

    }


    @Override
    public void flowManage(Long userId, Model model, int page, int size) {

        Pageable pa = PageRequest.of(page, size);
        Page<ProcessList> pagelist = processListDao.findByuserId(userId, pa);
        List<ProcessList> prolist = pagelist.getContent();

        Iterable<SystemStatusList> statusname = statusDao.findByStatusModel("aoa_process_list");
        Iterable<SystemTypeList> typename = typeDao.findByTypeModel("aoa_process_list");
        model.addAttribute("typename", typename);
        model.addAttribute("page", pagelist);
        model.addAttribute("prolist", prolist);
        model.addAttribute("statusname", statusname);
        model.addAttribute("url", "shenser");


    }


    @Override
    public void ser(Long userId, Model model, HttpServletRequest req, int page, int size) {


        Pageable pa = PageRequest.of(page, size);
        String val = null;
        if (!StringUtil.isEmpty(req.getParameter("val"))) {
            val = req.getParameter("val");
        }
        Page<ProcessList> pagelist = null;
        List<ProcessList> prolist = null;
        SystemStatusList status = statusDao.findByStatusModelAndStatusName("aoa_process_list", val);
        if (StringUtil.isEmpty(val)) {
            //空查询
            pagelist = processListDao.findByuserId(userId, pa);
        } else if (!Objects.isNull(status)) {
            //根据状态和申请人查找流程
            pagelist = processListDao.findByuserIdandstatus(userId, status.getStatusId(), pa);
            model.addAttribute("sort", "&val=" + val);
        } else {
            //根据审核人，类型，标题模糊查询
            pagelist = processListDao.findByuserIdandstr(userId, val, pa);
            model.addAttribute("sort", "&val=" + val);
        }
        prolist = pagelist.getContent();
        Iterable<SystemStatusList> statusname = statusDao.findByStatusModel("aoa_process_list");
        Iterable<SystemTypeList> typename = typeDao.findByTypeModel("aoa_process_list");
        model.addAttribute("typename", typename);
        model.addAttribute("page", pagelist);
        model.addAttribute("prolist", prolist);
        model.addAttribute("statusname", statusname);
        model.addAttribute("url", "shenser");


    }

    @Override
    public void auding(Long userId, Model model, int page, int size) {


        User user = userDao.findById(userId).get();
        Page<AubUser> pagelist = processService.index(user, page, size, null, model);
        List<Map<String, Object>> prolist = processService.index2(pagelist, user);
        model.addAttribute("page", pagelist);
        model.addAttribute("prolist", prolist);
        model.addAttribute("url", "serch");

    }


    @Override
    public void serch(Long userId, Model model, HttpServletRequest req, int page, int size) {


        User user = userDao.findById(userId).get();

        String val = null;
        if (!StringUtil.isEmpty(req.getParameter("val"))) {
            val = req.getParameter("val");
        }
        Page<AubUser> pagelist = processService.index(user, page, size, val, model);
        List<Map<String, Object>> prolist = processService.index2(pagelist, user);
        model.addAttribute("page", pagelist);
        model.addAttribute("prolist", prolist);
        model.addAttribute("url", "serch");

    }


    @Override
    public String particular(Long userId, Model model, HttpServletRequest req) {

        User user = userDao.findById(userId).get();//审核人或者申请人
        User audit = null;//最终审核人
        String id = req.getParameter("id");
        Long proid = Long.parseLong(id);
        String typename = req.getParameter("typename");//类型名称
        String name = null;

        Map<String, Object> map = new HashMap<>();
        ProcessList process = processListDao.findById(proid).get();//查看该条申请
        Boolean flag = process.getUserId().getUserId().equals(userId);//判断是申请人还是审核人

        if (!flag) {
            name = "审核";
        } else {
            name = "申请";
        }
        map = processService.index3(name, user, typename, process);
        if (("费用报销").equals(typename)) {
            Bursement bu = bursementDao.findByProId(process);
            User prove = userDao.findById(bu.getUsermoney().getUserId()).get();//证明人
            if (!Objects.isNull(bu.getOperation())) {
                audit = userDao.findById(bu.getOperation().getUserId()).get();//最终审核人
            }
            List<DetailsBurse> detaillist = detailsBurseDao.findByBurs(bu);
            String type = typeDao.findname(bu.getTypeId());
            String money = Tool.numbertocn(bu.getAllMoney());
            model.addAttribute("prove", prove);
            model.addAttribute("audit", audit);
            model.addAttribute("type", type);
            model.addAttribute("bu", bu);
            model.addAttribute("money", money);
            model.addAttribute("detaillist", detaillist);
            model.addAttribute("map", map);
            return "process/serch";
        } else if (("出差费用").equals(typename)) {
            Double staymoney = 0.0;
            Double tramoney = 0.0;
            EvectionMoney emoney = evectionMoneyDao.findByProId(process);

            String money = Tool.numbertocn(emoney.getMoney());
            List<Stay> staylist = stayDao.findByEvemoney(emoney);
            for (Stay stay : staylist) {
                staymoney += stay.getStayMoney();
            }
            List<Traffic> tralist = trafficDao.findByEvection(emoney);
            for (Traffic traffic : tralist) {
                tramoney += traffic.getTrafficMoney();
            }
            model.addAttribute("staymoney", staymoney);
            model.addAttribute("tramoney", tramoney);
            model.addAttribute("allmoney", money);
            model.addAttribute("emoney", emoney);
            model.addAttribute("staylist", staylist);
            model.addAttribute("tralist", tralist);
            model.addAttribute("map", map);
            return "process/evemonserch";
        } else if (("出差申请").equals(typename)) {
            Evection eve = evectionDao.findByProId(process);
            model.addAttribute("eve", eve);
            model.addAttribute("map", map);
            return "process/eveserach";
        } else if (("加班申请").equals(typename)) {
            Overtime eve = overtimeDao.findByProId(process);
            String type = typeDao.findname(eve.getTypeId());
            model.addAttribute("eve", eve);
            model.addAttribute("map", map);
            model.addAttribute("type", type);
            return "process/overserch";
        } else if (("请假申请").equals(typename)) {
            Holiday eve = holidayDao.findByProId(process);
            String type = typeDao.findname(eve.getTypeId());
            model.addAttribute("eve", eve);
            model.addAttribute("map", map);
            model.addAttribute("type", type);
            return "process/holiserch";
        } else if (("转正申请").equals(typename)) {
            Regular eve = regularDao.findByProId(process);
            model.addAttribute("eve", eve);
            model.addAttribute("map", map);
            return "process/reguserch";
        } else if (("离职申请").equals(typename)) {
            Resign eve = resignDao.findByProId(process);
            model.addAttribute("eve", eve);
            model.addAttribute("map", map);
            return "process/resserch";
        }


        return "process/serch";


    }


    @Override
    public void auditing(Long userId, Model model, HttpServletRequest req, int page, int size) {

        User u = userDao.findById(userId).get();

        //流程id
        Long id = Long.parseLong(req.getParameter("id"));
        ProcessList process = processListDao.findById(id).get();

        Reviewed re = reviewedDao.findByProIdAndUserId(process.getProcessId(), u);//查找审核表

        String typename = process.getTypeNmae().trim();
        switch (typename) {
            case ("费用报销"):
                Bursement bu = bursementDao.findByProId(process);
                model.addAttribute("bu", bu);
                break;
            case ("出差费用"):
                EvectionMoney emoney = evectionMoneyDao.findByProId(process);
                model.addAttribute("bu", emoney);
                break;
            case ("转正申请"):
            case ("离职申请"):
                User zhuan = userDao.findById(process.getUserId().getUserId()).get();
                model.addAttribute("position", zhuan);
                break;
        }
        processService.user(page, size, model);
        List<Map<String, Object>> list = processService.index4(process);

        model.addAttribute("statusid", process.getStatusId());
        model.addAttribute("process", process);
        model.addAttribute("revie", list);
        model.addAttribute("size", list.size());
        model.addAttribute("statusid", process.getStatusId());
        model.addAttribute("ustatusid", re.getStatusId());
        model.addAttribute("positionid", u.getPosition().getId());
        model.addAttribute("typename", typename);
    }

    @Override
    public String save(Long userId, Model model, HttpServletRequest req, Reviewed reviewed) {

        User u = userDao.findById(userId).get();
        String name = null;
        String typename = req.getParameter("type");
        Long proid = Long.parseLong(req.getParameter("proId"));

        ProcessList pro = processListDao.findById(proid).get();//找到该条流程

        User shen = userDao.findById(pro.getUserId().getUserId()).get();//申请人
        if (!StringUtil.isEmpty(req.getParameter("liuzhuan"))) {
            name = req.getParameter("liuzhuan");
        }
        if (!StringUtil.isEmpty(name)) {
            //审核并流转
            User u2 = userDao.findByUserName(reviewed.getUsername());//找到下一个审核人
            if (("离职申请").equals(typename)) {
                if (u.getUserId().equals(pro.getUserId().getFatherId())) {
                    if (u2.getPosition().getId().equals(5L)) {
                        processService.save(proid, u, reviewed, pro, u2);
                    } else {
                        model.addAttribute("error", "请选财务经理。");
                        return "common/proce";
                    }
                } else {
                    if (u2.getPosition().getId().equals(7L)) {
                        processService.save(proid, u, reviewed, pro, u2);
                    } else {
                        model.addAttribute("error", "请选人事经理。");
                        return "common/proce";
                    }
                }

            } else if (("费用报销").equals(typename) || ("出差费用").equals(typename)) {

                if (u2.getPosition().getId().equals(5L)) {
                    processService.save(proid, u, reviewed, pro, u2);
                } else {
                    model.addAttribute("error", "请选财务经理。");
                    return "common/proce";
                }
            } else {
                if (u2.getPosition().getId().equals(7L)) {
                    processService.save(proid, u, reviewed, pro, u2);
                } else {
                    model.addAttribute("error", "请选人事经理。");
                    return "common/proce";
                }
            }

        } else {
            //审核并结案
            Reviewed re = reviewedDao.findByProIdAndUserId(proid, u);
            re.setAdvice(reviewed.getAdvice());
            re.setStatusId(reviewed.getStatusId());
            re.setReviewedTime(new Date());
            reviewedDao.save(re);
            pro.setStatusId(reviewed.getStatusId());//改变主表的状态
            processListDao.save(pro);
            if (("请假申请").equals(typename) || ("出差申请").equals(typename)) {
                if (reviewed.getStatusId() == 25) {
                    Attendance attend = new Attendance();
                    attend.setHolidayDays(pro.getProcseeDays());
                    attend.setHolidayStart(pro.getStartTime());
                    attend.setUser(pro.getUserId());
                    if (("请假申请").equals(typename)) {
                        attend.setStatusId(46L);
                    } else if (("出差申请").equals(typename)) {
                        attend.setStatusId(47L);
                    }
                    attendanceDao.save(attend);
                }
            }


        }


        if (("费用报销").equals(typename)) {
            Bursement bu = bursementDao.findByProId(pro);
            if (shen.getFatherId().equals(u.getUserId())) {
                bu.setManagerAdvice(reviewed.getAdvice());
                bursementDao.save(bu);
            }
            if (u.getPosition().getId() == 5) {
                bu.setFinancialAdvice(reviewed.getAdvice());
                bu.setBurseTime(new Date());
                bu.setOperation(u);
                bursementDao.save(bu);
            }
        } else if (("出差费用").equals(typename)) {
            EvectionMoney emoney = evectionMoneyDao.findByProId(pro);
            if (shen.getFatherId().equals(u.getUserId())) {
                emoney.setManagerAdvice(reviewed.getAdvice());
                evectionMoneyDao.save(emoney);
            }
            if (u.getPosition().getId() == 5) {
                emoney.setFinancialAdvice(reviewed.getAdvice());
                evectionMoneyDao.save(emoney);
            }
        } else if (("出差申请").equals(typename)) {
            Evection ev = evectionDao.findByProId(pro);
            if (shen.getFatherId().equals(u.getUserId())) {
                ev.setManagerAdvice(reviewed.getAdvice());
                evectionDao.save(ev);
            }
            if (u.getPosition().getId().equals(7L)) {
                ev.setPersonnelAdvice(reviewed.getAdvice());
                evectionDao.save(ev);
            }
        } else if (("加班申请").equals(typename)) {
            Overtime over = overtimeDao.findByProId(pro);
            if (shen.getFatherId().equals(u.getUserId())) {
                over.setManagerAdvice(reviewed.getAdvice());
                overtimeDao.save(over);
            }
            if (u.getPosition().getId().equals(7L)) {
                over.setPersonnelAdvice(reviewed.getAdvice());
                overtimeDao.save(over);
            }
        } else if (("请假申请").equals(typename)) {
            Holiday over = holidayDao.findByProId(pro);
            if (shen.getFatherId().equals(u.getUserId())) {
                over.setManagerAdvice(reviewed.getAdvice());
                holidayDao.save(over);
            }
            if (u.getPosition().getId().equals(7L)) {
                over.setPersonnelAdvice(reviewed.getAdvice());
                holidayDao.save(over);
            }
        } else if (("转正申请").equals(typename)) {
            Regular over = regularDao.findByProId(pro);
            if (shen.getFatherId().equals(u.getUserId())) {
                over.setManagerAdvice(reviewed.getAdvice());
                regularDao.save(over);
            }
            if (u.getPosition().getId().equals(7L)) {
                over.setPersonnelAdvice(reviewed.getAdvice());
                regularDao.save(over);
            }
        } else if (("离职申请").equals(typename)) {

            Resign over = resignDao.findByProId(pro);
            if (shen.getFatherId().equals(u.getUserId())) {

                over.setManagerAdvice(reviewed.getAdvice());
                resignDao.save(over);
            }
            if (u.getPosition().getId() == 5) {
                over.setPersonnelAdvice(reviewed.getAdvice());
                resignDao.save(over);
            } else if (u.getPosition().getId().equals(7L)) {
                over.setFinancialAdvice(reviewed.getAdvice());
                resignDao.save(over);
            }
        }
        return "redirect:/audit";
    }


    @Override
    public void evemoney(Model model, Long userId, HttpServletRequest req, int page, int size) {

        Long proid = Long.parseLong(req.getParameter("id"));//出差申请的id
        ProcessList prolist = processListDao.findbyuseridandtitle(userId, proid);//找这个用户的出差申请
        processService.index6(model, userId, page, size);
        model.addAttribute("prolist", prolist);


    }


    @Override
    public String moneyeve(MultipartFile filePath, HttpServletRequest req, EvectionMoney eve, BindingResult br, Long userId, Model model) {


        User lu = userDao.findById(userId).get();//申请人
        User shen = userDao.findByUserName(eve.getShenname());//审核人
        Long roleid = lu.getRole().getRoleId();//申请人角色id
        Long fatherid = lu.getFatherId();//申请人父id
        Long userid = shen.getUserId();//审核人userid
        String val = req.getParameter("val");
        Double allmoney = 0.0;
        if (roleid >= 3L && Objects.equals(fatherid, userid)) {
            List<Traffic> ss = eve.getTraffic();
            for (Traffic traffic : ss) {
                allmoney += traffic.getTrafficMoney();
                User u = userDao.findByUserName(traffic.getUsername());
                traffic.setUser(u);
                traffic.setEvection(eve);

            }
            List<Stay> mm = eve.getStay();
            for (Stay stay : mm) {
                allmoney += stay.getStayMoney() * stay.getDay();
                User u = userDao.findByUserName(stay.getNameuser());
                stay.setUser(u);
                stay.setEvemoney(eve);
            }

            eve.setMoney(allmoney);
            //set主表
            ProcessList pro = eve.getProId();
            processService.index5(pro, val, lu, filePath, shen.getUserName());
            evectionMoneyDao.save(eve);
            //存审核表
            processService.index7(shen, pro);
            return "redirect:/flowmanage";
        } else {
            return "common/proce";
        }

    }


    @Override
    public void evection(Model model, Long userId, HttpServletRequest request, int page, int size) {

        //查找类型
        List<SystemTypeList> outtype = typeDao.findByTypeModel("aoa_evection");
        processService.index6(model, userId, page, size);
        model.addAttribute("outtype", outtype);
    }

    @Override
    public String evec(MultipartFile filePath, HttpServletRequest req, Evection eve, BindingResult br, Long userId) {

        User lu = userDao.findById(userId).get();//申请人
        User shen = userDao.findByUserName(eve.getNameuser());//审核人
        Long roleid = lu.getRole().getRoleId();//申请人角色id
        Long fatherid = lu.getFatherId();//申请人父id
        Long userid = shen.getUserId();//审核人userid
        String val = req.getParameter("val");
        if (roleid >= 3L && Objects.equals(fatherid, userid)) {
            //set主表
            ProcessList pro = eve.getProId();
            processService.index5(pro, val, lu, filePath, shen.getUserName());
            evectionDao.save(eve);
            //存审核表
            processService.index7(shen, pro);
        } else {
            return "common/proce";
        }

        return "redirect:/xinxeng";

    }

    @Override
    public void overtime(Model model, Long userId, HttpServletRequest request, int page, int size) {

        //查找类型
        List<SystemTypeList> overtype = typeDao.findByTypeModel("aoa_overtime");
        processService.index6(model, userId, page, size);
        model.addAttribute("overtype", overtype);
    }


    @Override
    public String over(HttpServletRequest req, Overtime eve, BindingResult br, Long userId) {


        User lu = userDao.findById(userId).get();//申请人
        User shen = userDao.findByUserName(eve.getNameuser());//审核人
        Long roleid = lu.getRole().getRoleId();//申请人角色id
        Long fatherid = lu.getFatherId();//申请人父id
        Long userid = shen.getUserId();//审核人userid
        String val = req.getParameter("val");
        if (roleid >= 3L && Objects.equals(fatherid, userid)) {
            //set主表
            ProcessList pro = eve.getProId();
            processService.index8(pro, val, lu, shen.getUserName());
            overtimeDao.save(eve);
            //存审核表
            processService.index7(shen, pro);
            return "redirect:/xinxeng";
        } else {
            return "common/proce";
        }

    }

    @Override
    public void holiday(Model model, Long userId, HttpServletRequest request, int page, int size) {


        List<SystemTypeList> overtype = typeDao.findByTypeModel("aoa_holiday");
        processService.index6(model, userId, page, size);
        model.addAttribute("overtype", overtype);

    }

    @Override
    public String holi(MultipartFile filePath, HttpServletRequest req, Holiday eve, BindingResult br, Long userId, Model model) {
        User lu = userDao.findById(userId).get();//申请人
        User shen = userDao.findByUserName(eve.getNameuser());//审核人
        Long roleid = lu.getRole().getRoleId();//申请人角色id
        Long fatherid = lu.getFatherId();//申请人父id
        Long userid = shen.getUserId();//审核人userid
        String val = req.getParameter("val");
        if (roleid >= 3L && Objects.equals(fatherid, userid)) {
            SystemTypeList type = typeDao.findById(eve.getTypeId()).get();
            if (eve.getTypeId() == 40) {
                if (type.getTypeSortValue() < eve.getLeaveDays()) {
                    model.addAttribute("error", "婚假必须小于10天。");
                    return "common/proce";
                }
            } else if (eve.getTypeId() == 38) {
                if (type.getTypeSortValue() < eve.getLeaveDays()) {
                    model.addAttribute("error", "单次事假必须小于4天。");
                    return "common/proce";
                }
            } else if (eve.getTypeId() == 42) {
                if (type.getTypeSortValue() < eve.getLeaveDays()) {
                    model.addAttribute("error", "陪产假必须小于10天。");
                    return "common/proce";
                }
            } else {
                //set主表
                ProcessList pro = eve.getProId();
                processService.index5(pro, val, lu, filePath, shen.getUserName());
                holidayDao.save(eve);
                //存审核表
                processService.index7(shen, pro);

            }
        } else {
            return "common/proce";
        }
        return "redirect:/xinxeng";

    }

    @Override
    public String regu(HttpServletRequest req, Regular eve, BindingResult br, Long userId, Model model) {

        User lu = userDao.findById(userId).get();//申请人
        User shen = userDao.findByUserName(eve.getNameuser());//审核人
        Long roleid = lu.getRole().getRoleId();//申请人角色id
        Long fatherid = lu.getFatherId();//申请人父id
        Long userid = shen.getUserId();//审核人userid
        String val = req.getParameter("val");
        if (roleid >= 3L && Objects.equals(fatherid, userid)) {
            if (lu.getRole().getRoleId() == 6 || lu.getRole().getRoleId() == 7) {

                //set主表
                ProcessList pro = eve.getProId();
                processService.index8(pro, val, lu, shen.getUserName());
                regularDao.save(eve);
                //存审核表
                processService.index7(shen, pro);
            } else {
                model.addAttribute("error", "你不需要转正。。。");
                return "common/proce";
            }
        } else {
            return "common/proce";
        }

        return "redirect:/xinxeng";
    }


    @Override
    public String res(HttpServletRequest req, Resign eve, BindingResult br, Long userId, Model model) {
        User lu = userDao.findById(userId).get();//申请人
        User shen = userDao.findByUserName(eve.getNameuser());//审核人
        Long roleid = lu.getRole().getRoleId();//申请人角色id
        Long fatherid = lu.getFatherId();//申请人父id
        Long userid = shen.getUserId();//审核人userid
        String val = req.getParameter("val");
        if (roleid >= 3L && Objects.equals(fatherid, userid)) {
            //set主表
            ProcessList pro = eve.getProId();
            processService.index8(pro, val, lu, shen.getUserName());
            eve.setHandUser(userDao.findByUserName(eve.getHanduser()));
            resignDao.save(eve);
            //存审核表
            processService.index7(shen, pro);
            return "redirect:/xinxeng";
        } else {
            return "common/proce";
        }

    }


    @Override
    public String dele(HttpServletRequest req, Long userId, Model model) {
        User lu = userDao.findById(userId).get();//审核人
        Long proid = Long.parseLong(req.getParameter("id"));
        Reviewed rev = reviewedDao.findByProIdAndUserId(proid, lu);
        if (!Objects.isNull(rev)) {
            rev.setDel(true);
            reviewedDao.save(rev);
        } else {
            return "common/proce";
        }
        return "redirect:/audit";
    }

    @Override
    public void downFile(HttpServletResponse response, Long fileid) {


        try {
            Attachment attd = attachmentDao.findById(fileid).get();
            File file = new File(rootpath, attd.getAttachmentPath());
            response.setContentLength(attd.getAttachmentSize().intValue());
            response.setContentType(attd.getAttachmentType());
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(attd.getAttachmentName().getBytes("UTF-8"), "ISO8859-1"));
            processService.writefile(response, file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void image(Model model, HttpServletResponse response, Long userId, HttpServletRequest request) {

        FileInputStream input = null;

        ServletOutputStream sos = null;

        try {

            String startpath = new String(URLDecoder.decode(request.getRequestURI(), "utf-8"));

            String path = startpath.replace("/show", "");

            File f = new File(rootpath, path);
            sos = response.getOutputStream();
            input = new FileInputStream(f.getPath());
            byte[] data = new byte[(int) f.length()];
            IOUtils.readFully(input, data);
            // 将文件流输出到浏览器
            IOUtils.write(data, sos);


        } catch (Exception e) {

            try {
                assert input != null;
                input.close();
                sos.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            e.printStackTrace();

        }


    }
}

