package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.CommonMethods;
import com.aifurion.oasystem.common.formVaild.BindingResultVOUtil;
import com.aifurion.oasystem.common.formVaild.MapToList;
import com.aifurion.oasystem.common.formVaild.ResultEnum;
import com.aifurion.oasystem.common.formVaild.ResultVO;
import com.aifurion.oasystem.config.StringtoDate;
import com.aifurion.oasystem.dao.note.AttachmentDao;
import com.aifurion.oasystem.dao.plan.PlanDao;
import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.note.Attachment;
import com.aifurion.oasystem.entity.plan.Plan;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.FileService;
import com.aifurion.oasystem.service.PlanService;
import com.aifurion.oasystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/22 10:42
 */

@Service
public class PlanServiceImpl implements PlanService {

    @Autowired
    private PlanDao planDao;
    @Autowired
    private TypeDao typeDao;
    @Autowired
    private StatusDao statusDao;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private AttachmentDao attachmentDao;

    @Autowired
    private CommonMethods commonMethods;

    private DefaultConversionService defaultConversionService = new DefaultConversionService();


    private List<Plan> planList;
    private List<User> userList;
    private Date startDate, endDate;
    private String choose2;


    @Override
    public void savePlan(MultipartFile file, HttpServletRequest req, Plan plan2, BindingResult br) {

        defaultConversionService.addConverter(new StringtoDate());
        // 格式化开始日期和结束日期
        Date start = defaultConversionService.convert(plan2.getStartTime(), Date.class);
        Date end = defaultConversionService.convert(plan2.getEndTime(), Date.class);
        Attachment attachment = null;
        Long attid = null;
        Plan plan = null;

        HttpSession session = req.getSession();
        long userid = Long.parseLong(String.valueOf(session.getAttribute("userId")));
        User user = userService.findOne(userid);

        // 获取到类型和状态id
        String type = req.getParameter("type");
        String status = req.getParameter("status");
        long typeid = typeDao.findByTypeModelAndTypeName("aoa_plan_list", type).getTypeId();
        long statusid = statusDao.findByStatusModelAndStatusName("aoa_plan_list", status).getStatusId();
        long pid = Long.parseLong(String.valueOf(req.getParameter("pid")));

        // 这里返回ResultVO对象，如果校验通过，ResultEnum.SUCCESS.getCode()返回的值为200；否则就是没有通过；
        ResultVO res = BindingResultVOUtil.hasErrors(br);
        if (!ResultEnum.SUCCESS.getCode().equals(res.getCode())) {
            List<Object> list = new MapToList<>().mapToList(res.getData());
            req.setAttribute("errormess", list.get(0).toString());
        }
        // 校验通过，下面写自己的逻辑业务
        else {
            // 新建
            if (pid == -1) {
                if (!file.isEmpty()) {
                    attachment = (Attachment) fileService.saveFile(file, user, null, false);
                    attid = attachment.getAttachmentId();
                } else if (file.isEmpty())
                    attid = null;

                plan = new Plan(typeid, statusid, attid, start, end, new Date(), plan2.getTitle(), plan2.getLabel(),
                        plan2.getPlanContent(), plan2.getPlanSummary(), null, user);
                planDao.save(plan);
            }
            if (pid > 0) {
                plan = findOne(pid);
                if (plan.getAttachId() == null) {
                    if (!file.isEmpty()) {
                        attachment = (Attachment) fileService.saveFile(file, user, null, false);
                        attid = attachment.getAttachmentId();
                        plan.setAttachId(attid);
                        planDao.save(plan);
                    }
                }
                if (plan.getAttachId() != null) {

                    fileService.updateAtt(file, user, null, plan.getAttachId());
                }
                planDao.updatesome(typeid, statusid, start, end, plan2.getTitle(), plan2.getLabel(),
                        plan2.getPlanContent(), plan2.getPlanSummary(), pid);

            }
            req.setAttribute("success", "后台验证成功");
        }
        req.setAttribute("plan2", plan2);


    }

    @Override
    public void save(Plan plan) {
        planDao.save(plan);


    }

    @Override
    public Plan findOne(Long id) {
        return planDao.findById(id).get();
    }

    @Override
    public void sortPaging(Model model, HttpServletRequest request, Long userid, int page, String baseKey, String type, String status, String time, String icon) {


        CommonMethods.setSomething(baseKey, type, status, time, icon, model);
        Page<Plan> page2 = paging(page, baseKey, userid, type, status, time);

        commonMethods.setTypeStatus(request, "aoa_plan_list", "aoa_plan_list");


        model.addAttribute("plist", page2.getContent());
        model.addAttribute("page", page2);
        model.addAttribute("url", "planviewtable");


    }

    @Override
    public String deletePlan(Long realUserId, Long pid) {


        long userid = planDao.findById(pid).get().getUser().getUserId();
        if (userid == realUserId) {
            planDao.delete(pid);
            return "redirect:/planview";
        } else {
            return "redirect:/notlimit";
        }
    }

    @Override
    public void planTablePaging(HttpServletRequest request, Model model, HttpSession session, int page, String baseKey) {

        List<SystemTypeList> type = (List<SystemTypeList>) typeDao.findByTypeModel("aoa_plan_list");
        List<SystemStatusList> status = (List<SystemStatusList>) statusDao.findByStatusModel("aoa_plan_list");
        List<Plan> plans = new ArrayList<>();
        // 利用set过滤掉重复的plan_user_id 因为set不能重复
        Set<Long> number = new HashSet();
        Plan plan2;
        long typeid = 13;
        long choose;
        defaultConversionService.addConverter(new StringtoDate());
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");

        Date start = defaultConversionService.convert(starttime, Date.class);
        Date end = defaultConversionService.convert(endtime, Date.class);
        String choose1 = request.getParameter("choose");
        //分页的时候记住
        if (start == null && end == null && choose1 == null) {
            start = startDate;
            end = endDate;
            choose1 = choose2;
        }
        if (start != null && end != null && choose1 != null) {
            startDate = start;
            endDate = end;
            choose2 = choose1;
        }
        // 1是日计划2是周计划3是月计划
        if (choose1 == null || choose1.length() == 0)
            choose = 1l;
        else
            choose = Long.parseLong(choose1);
        if (choose == 1) {
            typeid = 13l;
        }
        if (choose == 2) {
            typeid = 14l;
        }
        if (choose == 3) {
            typeid = 15l;
        }
        planList = (List<Plan>) planDao.findAll();
        long userid = Long.parseLong(String.valueOf(session.getAttribute("userId")));

        Page<User> uListpage = userService.findMyEmployUser(page, baseKey, userid);
        for (Plan plan : planList) {
            number.add(plan.getUser().getUserId());
        }

        // 找到相对应的计划记录
        for (Long num : number) {
            plan2 = planDao.findlatest(start, end, num, typeid);
            if (plan2 != null)
                plans.add(plan2);
        }

        // 将用户名和list绑定在一起
        Map<String, Plan> uMap = new HashMap<>();
        for (User user : uListpage) {
            if (plans.size() == 0)
                uMap.put(user.getUserName(), null);
            for (Plan plan : plans) {
                if (Objects.equals(user.getUserId(), plan.getUser().getUserId())) {
                    uMap.put(user.getUserName(), plan);
                    break;
                } else {
                    uMap.put(user.getUserName(), null);
                }
            }

        }


        //记住开始时间和结束时间以及选择
        model.addAttribute("starttime", starttime);
        model.addAttribute("endtime", endtime);
        model.addAttribute("choose", choose1);

        model.addAttribute("uMap", uMap);
        model.addAttribute("type", type);
        model.addAttribute("status", status);
        model.addAttribute("plans", plans);
        model.addAttribute("plist", planList);
        model.addAttribute("ulist", uListpage.getContent());
        model.addAttribute("page", uListpage);
        model.addAttribute("url", "realplantable");


    }

    public Page<Plan> paging(int page, String baseKey, long userid, Object type, Object status, Object time) {
        Pageable pa = PageRequest.of(page, 10);
        if (!StringUtils.isEmpty(baseKey)) {
            return planDao.findBybasekey(baseKey, userid, pa);
        }
        if (!StringUtils.isEmpty(type)) {
            if (type.toString().equals("0")) {
                //降序
                return planDao.findByUserOrderByTypeIdDesc(userid, pa);
            } else {
                //升序
                return planDao.findByUserOrderByTypeIdAsc(userid, pa);
            }
        }
        if (!StringUtils.isEmpty(status)) {
            if (status.toString().equals("0")) {
                return planDao.findByUserOrderByStatusIdDesc(userid, pa);
            } else {
                return planDao.findByUserOrderByStatusIdAsc(userid, pa);
            }
        }
        if (!StringUtils.isEmpty(time)) {
            if (time.toString().equals("0")) {
                return planDao.findByUserOrderByCreateTimeDesc(userid, pa);
            } else {
                return planDao.findByUserOrderByCreateTimeAsc(userid, pa);
            }
        } else {

            return planDao.findByUserOrderByCreateTimeDesc(userid, pa);
        }


    }


}
