package com.aifurion.oasystem.controller.address;

import com.aifurion.oasystem.entity.note.Director;
import com.aifurion.oasystem.entity.note.DirectorUser;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/17 11:03
 */


@Controller
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/addrmanage")
    public String addressManage(@SessionAttribute("userId") Long userId, Model model,
                                @RequestParam(value="page",defaultValue="0") int page,
                                @RequestParam(value="size",defaultValue="10") int size) {

        addressService.addressManage(userId, model, page, size);
        return "address/addrmanage";

    }


    @PostMapping("/changethistype")
    @ResponseBody
    public Boolean changeType(@RequestParam(value="did")Long did,
											@SessionAttribute("userId") Long userId,
											@RequestParam(value="catalog")String catalog) {

        addressService.changeType(did, userId, catalog);
        return true;
    }


    @PostMapping("/inmessshow")
    public String inAdressShow(Model model,@RequestParam(value="userId")Long userId) {

        addressService.inAddressShow(model, userId);
        return "address/inmessshow";
    }

    @PostMapping("/outmessshow")
    public String outAddressShow(Model model,@RequestParam("director") Long director,@SessionAttribute("userId") Long userId) {
        boolean flag = addressService.outAddressShow(model, director, userId);

        if (flag) {
            return "address/outmessshow";
        } else {
            return "redirect:/notlimit";
        }
    }


    @PostMapping("/changetypename")
    public String changeTypeName(@RequestParam(value="typename")String newName,
			@RequestParam(value="oldtypename")String oldName,
			@SessionAttribute("userId") Long userId) {

        addressService.changeTypeName(newName, oldName, userId);

        return "redirect:/outaddresspaging";
    }



    @PostMapping("/addaddress")
    public String addAddress(HttpServletRequest req, @RequestParam(value="did",required=false) Long did, HttpSession session,
                             @SessionAttribute("userId") Long userId, Model model) {
        boolean b = addressService.addAddress(req, did, session, userId, model);
        if (b) {

            return "address/addressedit";
        } else {

            return "redirect:/notlimit";

        }
    }


    @PostMapping("/savaaddress")
    public String saveAddress(@Valid Director director, DirectorUser directorUser, BindingResult br, @RequestParam("file") MultipartFile file, HttpSession session,
                              Model model, @SessionAttribute("userId") Long userId, HttpServletRequest req) {

        addressService.saveAddress(director, directorUser, br, file, session, model, userId, req);

        return "redirect:/addrmanage";
    }


    @PostMapping("/deletedirector")
    public String deleteDirector(@SessionAttribute("userId") Long userId,
                                 Model model,
                                 @RequestParam(value = "did", required = false) Long did) {
        boolean b = addressService.deleteDirector(userId, model, did);
        if (b) {
            return "redirect:/outaddresspaging";
        } else {
            return "redirect:/notlimit";
        }
    }


    @PostMapping("/deletetypename")
    public String deleteTypeName(@RequestParam(value="typename") String typename,@SessionAttribute("userId") Long userId) {

        addressService.deleteTypeName(typename, userId);

        return "redirect:/outaddresspaging";

    }


    @PostMapping("/addtypename")
    public String addTypeName(@RequestParam(value = "typename", required = false) String typename,
                              @RequestParam(value = "oldtypename", required = false) String oldtypename,
                              @SessionAttribute("userId") Long userId, Model model) {

        addressService.addTypeName(typename, oldtypename, userId, model);
        return "address/addtypename";

    }


    @GetMapping("/sharemess")
    public String shareMessage(@RequestParam(value="page",defaultValue="0")int page,
			@RequestParam(value="size",defaultValue="5")int size,
			@RequestParam(value="baseKey",required=false) String baseKey,
			@RequestParam(value="catalog",required=false)String catalog,
			@RequestParam(value="duid",required=false)Long duid,
			Model model,@SessionAttribute("userId") Long userId) {

        addressService.shareMessage(page, size, baseKey, catalog, duid, model, userId);


        return "address/sharemess";

    }


    @GetMapping("/mesharemess")
    public String myShareMessage(@RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "5") int size,
                                 @RequestParam(value = "baseKey", required = false) String baseKey,
                                 Model model, @SessionAttribute("userId") Long userId) {

        addressService.myShareMessage(page, size, baseKey, model, userId);
        return "address/mesharemess";


    }



    @PostMapping("/shareother")
    public String shareOther(@SessionAttribute("userId") Long userId,
                             @RequestParam(value = "directors[]") Long[] directors,
                             Model model,
                             @RequestParam(value = "sharedirector") Long sharedirector) {

        List<User> users = addressService.shareOther(userId, directors, model, sharedirector);

        if (users.size() > 0) {

            model.addAttribute("users", users);
            return "address/userhas";
        } else {
            return "address/sharesuccess";
        }


    }



    @GetMapping("/modalshare")
    public String modalShare(@RequestParam(value = "page", defaultValue = "0") int page, Model model,
                             @RequestParam(value = "size", defaultValue = "10") int size) {

        addressService.modalShare(page, model, size);

        return "address/modalshare";

    }


    @PostMapping("/modalpaging")
    public String modalpaging(@RequestParam(value="page",defaultValue="0") int page,Model model,
			@RequestParam(value="size",defaultValue="10") int size,
			@RequestParam(value="baseKey",required=false) String baseKey) {
        addressService.modalpaging(page, model, size, baseKey);

        return "address/shareuser";

    }


    @RequestMapping("/outaddresspaging")
    public String outaddresspaging(@RequestParam(value="pageNum",defaultValue="1") int page,Model model,
			@RequestParam(value="baseKey",required=false) String baseKey,
			@RequestParam(value="outtype",required=false) String outtype,
			@RequestParam(value="alph",defaultValue="ALL") String alph,
			@SessionAttribute("userId") Long userId) {

        addressService.outaddresspaging(page, model, baseKey, outtype, alph, userId);


       return "address/outaddress";
    }


    @RequestMapping("/inaddresspaging")
    public String inaddresspaging(@RequestParam(value="page",defaultValue="0") int page,Model model,
			@RequestParam(value="size",defaultValue="10") int size,
			@RequestParam(value="baseKey",required=false) String baseKey,
			@RequestParam(value="alph",defaultValue="ALL") String alph) {

        addressService.inaddresspaging(page, model, size, baseKey, alph);

        return "address/inaddrss";

    }














}
