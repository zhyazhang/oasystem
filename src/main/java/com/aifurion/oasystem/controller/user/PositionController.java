package com.aifurion.oasystem.controller.user;

import com.aifurion.oasystem.entity.user.Dept;
import com.aifurion.oasystem.entity.user.Position;
import com.aifurion.oasystem.service.DeptService;
import com.aifurion.oasystem.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/23 9:34
 */
@Controller
public class PositionController {


    @Autowired
    private DeptService deptService;

    @Autowired
    private PositionService positionService;


    @RequestMapping("/positionmanage")
	public String positionmanage(Model model) {

		List<Position> positions = (List<Position>) positionService.findAll();
		model.addAttribute("positions",positions);
		return "user/positionmanage";
	}


	@GetMapping( "/positionedit" )
	public String positioneditget(@RequestParam(value = "positionid",required=false) Long positionid, Model model){
		if(positionid!=null){
			Position position = positionService.findOne(positionid);
			Dept dept = deptService.findOne(position.getDeptid());
			model.addAttribute("positiondept",dept);
			model.addAttribute("position",position);
		}
		List<Dept> depts = (List<Dept>) deptService.findAll();
		model.addAttribute("depts", depts);
		return "user/positionedit";
	}

	@PostMapping("/positionedit" )
	public String positioneditpost(Position position,Model model){

		Position position1 = positionService.save(position);

		if(position1!=null){
			model.addAttribute("success",1);
			return "/positionmanage";
		}

		model.addAttribute("errormess","数据插入失败");
		return "user/positionedit";
	}

	@RequestMapping("/removeposition")
	public String removeposition(@RequestParam("positionid") Long positionid,Model model){
		positionService.delete(positionid);
		model.addAttribute("success",1);
		return "/positionmanage";
	}







}
