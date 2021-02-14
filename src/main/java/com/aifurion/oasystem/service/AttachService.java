package com.aifurion.oasystem.service;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 17:43
 */

public interface AttachService {

    Integer updateAttachment(String attname, String attpath, String shu, Long size, String type, Date uptime, Long attid);



}
