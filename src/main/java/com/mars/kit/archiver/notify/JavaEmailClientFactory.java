/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-6-3 上午11:21
 * History:
 */
package com.mars.kit.archiver.notify;

import java.io.File;
import java.util.Date;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.mars.kit.archiver.conf.EhCacheUtils;
import com.mars.kit.archiver.conf.EmailConfig;
import com.mars.kit.archiver.conf.JavaMailSource;
import com.mars.kit.common.util.MD5Support;
import com.mars.kit.common.util.NetUtils;
import com.mars.kit.criterion.model.ResponseData;
import com.mars.kit.criterion.model.ResponseData.RESPONSE_CODE;


/**
 * JavaEmailClientFactory.java
 *
 * @author ColleyMa
 * @version 19-6-3 上午11:21
*/
public class JavaEmailClientFactory implements InitializingBean {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private EmailConfig emailConfig;
    private JavaMailSenderImpl mailSender;

    
    public void setEmailConfig(EmailConfig emailConfig) {
		this.emailConfig = emailConfig;
	}
    

	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.mailSender == null) {
			this.mailSender = new JavaMailSenderImpl();
			this.mailSender.setDefaultEncoding(emailConfig.getDefaultEncoding());
			this.mailSender.setHost(emailConfig.getHost());
			this.mailSender.setPort(emailConfig.getPort());
			this.mailSender.setJavaMailProperties(emailConfig.getJavaMailProperties());
			this.mailSender.setPassword(emailConfig.getPassword());
			this.mailSender.setUsername(emailConfig.getUsername());
		}
	}

    public ResponseData<String> sendMessage(JavaMailSource mailSource) {
        ResponseData<String> retResult = new ResponseData<String>();
        try {
            //设置服务器IP
            mailSource.getContents().put("serverIp", NetUtils.getIpStr());
            mailSource.getContents().put("adminName", emailConfig.getSender());

            Assert.notNull(mailSender, "JavaMailSender IS NULL,Please check spring is config right");
            mailSource.validate();
            String content = mailSource.formatContent();
            StringBuilder stringBuilder = new StringBuilder()
            		.append(JSON.toJSONString(mailSource.getRecipients())).append("_")
                    .append(mailSource.getSubject())
                    .append("_").append(mailSource.formatContent());
            String md5Str = MD5Support.MD5(stringBuilder.toString());
            //不能重复发邮件
            if (isSendedInAppointedTime(md5Str)) {
                retResult.setRetCode(RESPONSE_CODE.OPERATE_DUPLICATE);
                return retResult;
            }

            MimeMessage mailMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true, "UTF-8");
            helper.setSentDate(new Date());
            helper.setFrom(emailConfig.getSender(), emailConfig.getSenderName());
            helper.setSubject(mailSource.getSubject());
            helper.setText(content, true);
            helper.setTo(mailSource.getRecipients().toArray(new String[mailSource.getRecipients().size()]));

            //如果配置了抄送功能
            if (CollectionUtils.isNotEmpty(mailSource.getCarbonCopy())) {
                helper.setCc(mailSource.getCarbonCopy().toArray(new String[mailSource.getCarbonCopy().size()]));
            }

            //设置了密送地址
            if (CollectionUtils.isNotEmpty(mailSource.getBlindCarbonCopy())) {
                helper.setBcc(mailSource.getBlindCarbonCopy().toArray(new String[mailSource.getBlindCarbonCopy().size()]));
            }

            //设置了附件
            if (CollectionUtils.isNotEmpty(mailSource.getAttachements())) {
                for (File file : mailSource.getAttachements()) {
                    helper.addAttachment(MimeUtility.encodeText(file.getName()), file);
                }
            }

            mailSender.send(helper.getMimeMessage());
            EhCacheUtils.getInstance().put(md5Str, Integer.valueOf(1));
        } catch (Exception e) {
            retResult.setRetCode(RESPONSE_CODE.SYS_ERROR);
            logger.error("sendMail【 " + mailSource.getSubject() + "】  error!", e);
        }

        return retResult;
    }

    private static boolean isSendedInAppointedTime(String md5Str) {
        Object obj = EhCacheUtils.getInstance().get(md5Str);

        if (obj != null) {
            return true;
        }

        return false;
    }
}
