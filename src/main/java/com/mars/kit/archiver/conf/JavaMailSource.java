/**
 * Copyright NewHeight Co.,Ltd. (C)2016
 * File Name: JavaMailSource.java
 * Encoding: UTF-8
 * Date: 16-10-18 上午10:50
 * History:
 */
package com.mars.kit.archiver.conf;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;
import com.mars.kit.common.util.FreemarkerHelper;
import com.mars.kit.common.util.NetUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import org.springframework.util.Assert;


/**
 * 
 * @author mayuanchao
 * @version 1.0  Date: 16-10-18 上午10:50
*/
public class JavaMailSource implements java.io.Serializable {
    private static final long serialVersionUID = 7440606769222742018L;
    private static Pattern emailPattern = Pattern.compile("[_a-zA-Z0-9.\\-]+@([_a-zA-Z0-9\\-]+\\.)+[a-zA-Z0-9]{2,3}");
    private String subject; //发件主题
    private List<String> carbonCopy; //抄送地址
    private List<String> blindCarbonCopy; //密送
    private List<File> attachements; //附件
   
    private Set<String> recipients; //收件人  可以是多个逗号分隔
    private Map<String, Object> contents;
    private String messageContents; //发件模板
    private String messageTemplateFile; //模板文件，位于template/mail 下
    private String sourceCode;

    public boolean validate() {
        Assert.isTrue(CollectionUtils.isNotEmpty(getRecipients()), "recipients is empty,please check!");
        Assert.isTrue(StringUtils.isNotEmpty(subject), "subject is empty,please check!");

        if ((getRecipients().size() > 20) || (NetUtils.getIp() == null)) {
            return false;
        }

        return true;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public List<String> getCarbonCopy() {
        return carbonCopy;
    }

    public void setCarbonCopy(List<String> carbonCopy) {
        this.carbonCopy = carbonCopy;
    }

    public List<String> getBlindCarbonCopy() {
        return blindCarbonCopy;
    }

    public void setBlindCarbonCopy(List<String> blindCarbonCopy) {
        this.blindCarbonCopy = blindCarbonCopy;
    }

    public List<File> getAttachements() {
        return attachements;
    }

    public void setAttachements(List<File> attachements) {
        this.attachements = attachements;
    }


    public Set<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<String> recipients) {
        this.recipients = recipients;
    }

    public Map<String, Object> getContents() {
        return contents;
    }

    public void setContents(Map<String, Object> contents) {
        this.contents = contents;
    }

    public String getMessageContents() {
        return messageContents;
    }

    public void setMessageContents(String messageContents) {
        this.messageContents = messageContents;
    }

    public String getMessageTemplateFile() {
        return messageTemplateFile;
    }

    public void setMessageTemplateFile(String messageTemplateFile) {
        this.messageTemplateFile = messageTemplateFile;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        Matcher m = emailPattern.matcher(email);

        if (m.matches()) {
            return true;
        }

        return false;
    }

    public String formatContent() {
        if (MapUtils.isEmpty(contents)) {
            return getMessageContents();
        }

        FreemarkerHelper freemarker = FreemarkerHelper.getInstance();
        String mailContent = "";

        if (StringUtils.isNotEmpty(messageTemplateFile)) {
            mailContent = freemarker.template2String(getContents(), messageTemplateFile);

            return mailContent;
        }

        if (StringUtils.isNotEmpty(messageContents)) {
            mailContent = freemarker.stringTemplate2String(getContents(), messageContents);
        }

        return mailContent;
    }
    
    
    private JavaMailSource(Builder builder) {
        this.subject = builder.subject;
        this.carbonCopy = builder.carbonCopy;
        this.blindCarbonCopy = builder.blindCarbonCopy;
        this.attachements = builder.attachements;
        this.recipients = builder.recipients;
        this.contents = builder.contents;
        this.messageContents = builder.messageContents;
        this.messageTemplateFile = builder.messageTemplateFile;
        this.sourceCode = builder.sourceCode;
    }

    public static class Builder {
        private String subject;
        private List<String> carbonCopy;
        private List<String> blindCarbonCopy;
        private List<File> attachements;
        private Set<String> recipients = Sets.newHashSet();
        private Map<String, Object> contents;
        private String messageContents;
        private String messageTemplateFile;
        private String sourceCode;

        public Builder subject(String subject) {
            this.subject = subject;

            return this;
        }

       

        public Builder carbonCopy(List<String> carbonCopy) {
            this.carbonCopy = carbonCopy;

            return this;
        }

        public Builder blindCarbonCopy(List<String> blindCarbonCopy) {
            this.blindCarbonCopy = blindCarbonCopy;

            return this;
        }

        public Builder attachements(List<File> attachements) {
            this.attachements = attachements;

            return this;
        }

        public Builder recipients(Set<String> recipients) {
            this.recipients = recipients;

            return this;
        }

        public Builder recipients(String recipients) {
            if (StringUtils.isNotEmpty(recipients)) {
                String[] recipient = StringUtils.split(recipients, ",");
                Map<String, Byte> emailTemps = new HashMap<String, Byte>();

                if (ArrayUtils.isNotEmpty(recipient)) {
                    for (String temp : recipient) {
                        if (isValidEmail(temp)) {
                            if (emailTemps.get(temp.trim()) == null) {
                                this.recipients.add(temp.trim());
                                emailTemps.put(temp.trim(), (byte) 1);
                            }
                        }
                    }
                }
            }

            return this;
        }

        public Builder contents(Map<String, Object> contents) {
            this.contents = contents;

            return this;
        }

        public Builder messageContents(String messageContents) {
            this.messageContents = messageContents;

            return this;
        }

        public Builder messageTemplateFile(String messageTemplateFile) {
            this.messageTemplateFile = messageTemplateFile;

            return this;
        }

        public Builder sourceCode(String sourceCode) {
            this.sourceCode = sourceCode;

            return this;
        }

        public JavaMailSource build() {
            return new JavaMailSource(this);
        }
    }
}
