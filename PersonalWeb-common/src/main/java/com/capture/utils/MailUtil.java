package com.capture.utils;

import com.capture.utils.extend.MailResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @ClassName: MailUtil
 * @Author: pengfeizhang
 * @Description: 邮箱发送工具类
 * @Date: 2022/3/9 上午10:28
 * @Version: 1.0
 */

@Component
public class MailUtil {

    @Autowired
    private MailResource mailResource;



    /**
     * 外网邮件发送
     *
     * @param to 收件人邮箱地址 收件人@xx.com
     * @param code 传入的验证码
     */
    public void sendMail(String to, String code) throws Exception{
        String myEmail = mailResource.getMyMail();
        // Session对象:
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", "smtp.qq.com"); // 设置主机地址
        // smtp.163.com
        // smtp.qq.com
        // smtp.sina.com
        props.setProperty("mail.smtp.auth", "true");// 认证
        // 2.产生一个用于邮件发送的Session对象
        Session session = Session.getInstance(props);

        // Message对象:
        Message message = new MimeMessage(session);
        // 设置发件人：
        // 4.设置消息的发送者
        Address fromAddr = new InternetAddress(myEmail);
        message.setFrom(fromAddr);

        // 5.设置消息的接收者 nkpxcloxbtpxdjai
        Address toAddr = new InternetAddress(to);
        // TO 直接发送 CC抄送 BCC密送
        message.setRecipient(MimeMessage.RecipientType.TO, toAddr);

        // 6.设置邮件标题
        message.setSubject("来自capture.xyz的安全验证码");
        // 7.设置正文
        message.setContent("capture欢迎胡说八道:\n\n您的验证码为：" + code+"\n请尽快填写！", "text/html;charset=UTF-8");

        // 8.准备发送，得到火箭
        Transport transport = session.getTransport("smtp");
        // 9.设置火箭的发射目标（第三个参数就是你的邮箱授权码）
        transport.connect("smtp.qq.com", myEmail, mailResource.getAuthCode());
        // 10.发送
        transport.sendMessage(message, message.getAllRecipients());

        // Transport对象:
        // Transport.send(message);
    }

    public String generateRandomCode(int length) {
        String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            // 0 ~ s.length()-1
            int index = (new java.util.Random()).nextInt(s.length());
            // 处理重复字符：每个新的随机字符在 sb 中使用indexOf()查找下标值，-1为没找到，即不重复
            Character ch = s.charAt(index);
            if (sb.indexOf(ch.toString()) < 0) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
