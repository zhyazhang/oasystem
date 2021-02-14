package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.note.AttachmentDao;
import com.aifurion.oasystem.service.AttachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 17:44
 */

@Service
public class AttachServiceImpl implements AttachService {


    @Autowired
    private AttachmentDao attachmentDao;

    @Override
    public Integer updateAttachment(String attname, String attpath, String shu, Long size, String type, Date uptime, Long attid) {

        return attachmentDao.updateatt(attname, attpath, shu, size, type, uptime, attid);
    }
}
