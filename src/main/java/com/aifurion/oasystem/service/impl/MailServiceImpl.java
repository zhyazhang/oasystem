package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.mail.InMailDao;
import com.aifurion.oasystem.dao.mail.MailReceiverDao;
import com.aifurion.oasystem.dao.mail.MailNumberDao;
import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.entity.mail.Inmaillist;
import com.aifurion.oasystem.entity.mail.Mailnumber;
import com.aifurion.oasystem.entity.mail.Pagemail;
import com.aifurion.oasystem.entity.note.Attachment;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.MailService;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 9:44
 */

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private StatusDao statusDao;
    @Autowired
    private TypeDao typeDao;
    @Autowired
    private MailNumberDao mailNumberDao;
    @Autowired
    private MailReceiverDao mailReceiverDao;

    @Autowired
    private InMailDao inMailDao;

    @Value("${file.root.path}")
    private String rootpath;

    public static Pattern p = Pattern.compile("[\u4e00-\u9fa5]");

    @Override
    public void UserpanelController() {


        try {
            rootpath = ResourceUtils.getURL("classpath:").getPath().replace("/target/classes/", "/static/attachment");
            //System.out.println(rootpath);

        } catch (IOException e) {
            System.out.println("获取项目路径异常");
        }

    }

    @Override
    public Page<Pagemail> recive(int page, int size, User tu, String val, String title) {

        Page<Pagemail> pagelist = null;
        Pageable pa = PageRequest.of(page, size);
        List<Sort.Order> orders = new ArrayList<>();
        SystemStatusList status = statusDao.findByStatusModelAndStatusName("aoa_in_mail_list", val);
        SystemTypeList type = typeDao.findByTypeModelAndTypeName("aoa_in_mail_list", val);
        if (("收件箱").equals(title)) {
            if (StringUtil.isEmpty(val)) {
                orders.add(new Sort.Order(Sort.Direction.ASC, "read"));
                Sort sort = Sort.by(orders);
                pa = PageRequest.of(page, size, sort);
                pagelist = mailReceiverDao.findmail(tu, false, pa);
            } else if (!Objects.isNull(status)) {
                pagelist = mailReceiverDao.findmailbystatus(tu, status.getStatusId(), false, pa);
            } else if (!Objects.isNull(type)) {
                pagelist = mailReceiverDao.findmailbytype(tu, type.getTypeId(), false, pa);
            } else {
                pagelist = mailReceiverDao.findmails(tu, false, val, pa);
            }
        } else {
            if (StringUtil.isEmpty(val)) {
                orders.add(new Sort.Order(Sort.Direction.ASC, "read"));
                Sort sort = Sort.by(orders);
                pa = PageRequest.of(page, size, sort);
                pagelist = mailReceiverDao.findmail(tu, true, pa);
            } else if (!Objects.isNull(status)) {
                pagelist = mailReceiverDao.findmailbystatus(tu, status.getStatusId(), true, pa);
            } else if (!Objects.isNull(type)) {
                pagelist = mailReceiverDao.findmailbytype(tu, type.getTypeId(), false, pa);
            } else {
                pagelist = mailReceiverDao.findmails(tu, true, val, pa);
            }
        }
        return pagelist;
    }

    @Override
    public List<Map<String, Object>> mail(Page<Pagemail> mail) {
        List<Pagemail> maillist = mail.getContent();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < maillist.size(); i++) {
            Map<String, Object> result = new HashMap<>();
            String typename = typeDao.findname(maillist.get(i).getMailType());
            SystemStatusList status = statusDao.findById(maillist.get(i).getMailStatusid()).get();
            result.put("typename", typename);
            result.put("statusname", status.getStatusName());
            result.put("statuscolor", status.getStatusColor());
            result.put("star", maillist.get(i).getStar());
            result.put("read", maillist.get(i).getRead());
            result.put("time", maillist.get(i).getMailCreateTime());
            result.put("reciver", maillist.get(i).getInReceiver());
            result.put("title", maillist.get(i).getMailTitle());
            result.put("mailid", maillist.get(i).getMailId());
            result.put("fileid", maillist.get(i).getMailFileid());
            list.add(result);

        }
        return list;

    }

    @Override
    public Page<Inmaillist> inmail(int page, int size, User tu, String val, String title) {

        Page<Inmaillist> pagemail = null;
        Pageable pa = PageRequest.of(page, size);
        List<Sort.Order> orders = new ArrayList<>();
        SystemStatusList status = statusDao.findByStatusModelAndStatusName("aoa_in_mail_list", val);
        SystemTypeList type = typeDao.findByTypeModelAndTypeName("aoa_in_mail_list", val);
        if (("发件箱").equals(title)) {
            if (StringUtil.isEmpty(val)) {
                orders.add(new Sort.Order(Sort.Direction.DESC, "mailStatusid"));
                Sort sort = Sort.by(orders);
                pa = PageRequest.of(page, size, sort);
                pagemail = inMailDao.findByPushAndMailUseridAndDelOrderByMailCreateTimeDesc(true, tu, false, pa);
            } else if (!Objects.isNull(status)) {
                pagemail = inMailDao.findByMailUseridAndMailStatusidAndPushAndDelOrderByMailCreateTimeDesc(tu, status.getStatusId(), true, false, pa);
            } else if (!Objects.isNull(type)) {
                pagemail = inMailDao.findByMailUseridAndMailTypeAndPushAndDelOrderByMailCreateTimeDesc(tu, type.getTypeId(), true, false, pa);
            } else {
                pagemail = inMailDao.findbymailUseridAndPushAndDel(tu, true, false, val, pa);
            }
        } else {
            //草稿箱
            if (StringUtil.isEmpty(val)) {
                orders.add(new Sort.Order(Sort.Direction.DESC, "mailStatusid"));
                Sort sort = Sort.by(orders);
                pa = PageRequest.of(page, size, sort);
                pagemail = inMailDao.findByPushAndMailUseridAndDelOrderByMailCreateTimeDesc(false, tu, false, pa);
            } else if (!Objects.isNull(status)) {
                pagemail = inMailDao.findByMailUseridAndMailStatusidAndPushAndDelOrderByMailCreateTimeDesc(tu, status.getStatusId(), false, false, pa);
            } else if (!Objects.isNull(type)) {
                pagemail = inMailDao.findByMailUseridAndMailTypeAndPushAndDelOrderByMailCreateTimeDesc(tu, type.getTypeId(), true, false, pa);
            } else {
                pagemail = inMailDao.findbymailUseridAndPushAndDel(tu, false, false, val, pa);
            }
        }
        return pagemail;
    }

    @Override
    public List<Map<String, Object>> maillist(Page<Inmaillist> mail) {
        List<Inmaillist> maillist = mail.getContent();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < maillist.size(); i++) {
            Map<String, Object> result = new HashMap<>();
            String typename = typeDao.findname(maillist.get(i).getMailType());
            SystemStatusList status = statusDao.findById(maillist.get(i).getMailStatusid()).get();
            result.put("typename", typename);
            result.put("statusname", status.getStatusName());
            result.put("statuscolor", status.getStatusColor());
            result.put("star", maillist.get(i).getStar());
            result.put("read", true);
            result.put("time", maillist.get(i).getMailCreateTime());
            result.put("reciver", maillist.get(i).getInReceiver());
            result.put("title", maillist.get(i).getMailTitle());
            result.put("mailid", maillist.get(i).getMailId());
            result.put("fileid", maillist.get(i).getMailFileid());
            list.add(result);

        }
        return list;
    }

    @Override
    public Page<Mailnumber> index(int page, int size, User tu, String val, Model model) {
        Page<Mailnumber> account = null;
        List<Sort.Order> orders = new ArrayList<>();
        Pageable pa = PageRequest.of(page, size);
        if (StringUtil.isEmpty(val)) {
            orders.addAll(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "status"), new Sort.Order(Sort.Direction.DESC, "mailCreateTime")));
            Sort sort = Sort.by(orders);
            pa = PageRequest.of(page, size, sort);
            account = mailNumberDao.findByMailUserId(tu, pa);
        } else if (("类型").equals(val)) {
            account = mailNumberDao.findByMailUserIdOrderByMailType(tu, pa);
            model.addAttribute("sort", "&val=" + val);
        } else if (("状态").equals(val)) {
            account = mailNumberDao.findByMailUserIdOrderByStatus(tu, pa);
            model.addAttribute("sort", "&val=" + val);
        } else if (("创建时间").equals(val)) {
            account = mailNumberDao.findByMailUserIdOrderByMailCreateTimeDesc(tu, pa);
            model.addAttribute("sort", "&val=" + val);
        } else {
            //名字的模糊查询
            account = mailNumberDao.findByMailUserNameLikeAndMailUserId(val, tu, pa);
            model.addAttribute("sort", "&val=" + val);
        }
        return account;
    }

    @Override
    public List<Map<String, Object>> up(Page<Mailnumber> num) {

        List<Mailnumber> account = num.getContent();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < account.size(); i++) {
            Map<String, Object> result = new HashMap<>();
            SystemStatusList status = statusDao.findById(account.get(i).getStatus()).get();
            result.put("accountid", account.get(i).getMailNumberId());
            result.put("typename", typeDao.findname(account.get(i).getMailType()));
            result.put("statusname", status.getStatusName());
            result.put("statuscolor", status.getStatusColor());
            result.put("accountname", account.get(i).getMailUserName());
            result.put("creattime", account.get(i).getMailCreateTime());
            list.add(result);
        }
        return list;

    }

    @Override
    public Attachment upload(MultipartFile file, User mu) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM");
        File root = new File(rootpath, simpleDateFormat.format(new Date()));
        File savepath = new File(root, mu.getUserName());

        if (!savepath.exists()) {
            savepath.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        if (!StringUtil.isEmpty(fileName)) {
            String suffix = FilenameUtils.getExtension(fileName);
            String newFileName = UUID.randomUUID().toString().toLowerCase() + "." + suffix;
            File targetFile = new File(savepath, newFileName);
            try {
                file.transferTo(targetFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Attachment attachment = new Attachment();
            attachment.setAttachmentName(file.getOriginalFilename());
            attachment.setAttachmentPath(targetFile.getAbsolutePath().replace("\\", "/").replace(rootpath, ""));
            attachment.setAttachmentShuffix(suffix);
            attachment.setAttachmentSize(file.getSize());
            attachment.setAttachmentType(file.getContentType());
            attachment.setUploadTime(new Date());
            attachment.setUserId(mu.getUserId() + "");

            return attachment;
        }
        return null;
    }

    @Override
    public void deleteAccount(Long id) {
        mailNumberDao.deleteById(id);

    }

    @Override
    public boolean isContainChinese(String str) {


        Matcher m = p.matcher(str);
        return m.find();
    }

    @Override
    public void pushmail(String account, String password, String reciver, String name, String title, String content, String affix, String filename) {

        String file = null;
        if (!StringUtil.isEmpty(affix)) {
            File root = new File(rootpath, affix);
            file = root.getAbsolutePath();
        }
        // 发件人的 邮箱 和 密码（替换为自己的邮箱和密码）
        String myEmailAccount = account;
        String myEmailPassword = password;

        // 网易163邮箱的 SMTP 服务器地址为: smtp.163.com
        //qq  smtp.qq.com
        String myEmailSMTPHost = "smtp.qq.com";

        // 收件人邮箱（替换为自己知道的有效邮箱）
        //  String receiveMailAccount = "1533047354@qq.com";

        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证

        // 开启 SSL 安全连接。
        // SMTP 服务器的端口 (非 SSL 连接的端口一般默认为 25, 可以不添加, 如果开启了 SSL 连接,
        //                  需要改为对应邮箱的 SMTP 服务器的端口, 具体可查看对应邮箱服务的帮助,
        //                  QQ邮箱的SMTP(SLL)端口为465或587, 其他邮箱自行去查看)
        final String smtpPort = "465";
        props.setProperty("mail.smtp.port", smtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", smtpPort);


        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log

        // 3. 创建一封邮件
        MimeMessage message;
        try {
            message = createMimeMessage(session, myEmailAccount, reciver, name, title, content, file, filename);

            // 4. 根据 Session 获取邮件传输对象
            Transport transport = session.getTransport();

            // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
            transport.connect(myEmailAccount, myEmailPassword);

            // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(message, message.getAllRecipients());

            // 7. 关闭连接
            transport.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public MimeMessage createMimeMessage(Session session, String sendMail,
                                         String receiveMail, String name,
                                         String title, String content,
                                         String affix, String filename)
            throws UnsupportedEncodingException, MessagingException {

        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人（昵称有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改昵称）
        message.setFrom(new InternetAddress(sendMail, name, "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "XX用户", "UTF-8"));

        // 4. Subject: 邮件主题（标题有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改标题）
        message.setSubject(title, "UTF-8");

        if (!StringUtil.isEmpty(affix)) {

            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();
            // 设置邮件的文本内容
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(content, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);
            // 添加附件
            BodyPart messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(affix);//附件路径
            // 添加附件的内容
            messageBodyPart.setDataHandler(new DataHandler(source));
            // 添加附件的标题
            // 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
            sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
            messageBodyPart.setFileName("=?GBK?B?" + enc.encode(filename.getBytes()) + "?=");
            multipart.addBodyPart(messageBodyPart);
            // 将multipart对象放到message中
            message.setContent(multipart, "text/html;charset=UTF-8");
        } else {
            // 5. Content: 邮件正文（可以使用html标签）（内容有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改发送内容）
            message.setContent(content, "text/html;charset=UTF-8");
        }
        // 6. 设置发件时间
        message.setSentDate(new Date());
        // 7. 保存设置
        message.saveChanges();
        return message;

    }


}
