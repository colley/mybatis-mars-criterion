/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-6-3 上午10:59
 * History:
 */
package com.mars.kit.common.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import freemarker.cache.StringTemplateLoader;

import freemarker.template.Configuration;
import freemarker.template.Template;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * FreemarkerHelper.java
 * @author ColleyMa
 * @version 19-6-3 上午10:59
 */
public class FreemarkerHelper {
    private static FreemarkerHelper instance = null;
    private static final String TEMPLATESPATH = "";
    private static final String ENCODING_UTF8 = "utf-8";
    private static Lock lock = new ReentrantLock();
    private Log logger = LogFactory.getLog(FreemarkerHelper.class);
    private Configuration tempConfiguration;
    private Configuration stringTemplatecfg;
    private ThreadLocal<Configuration> stringTemplatecfgThreadLocal = new ThreadLocal<Configuration>();

    public static FreemarkerHelper getInstance() {
        lock.lock();

        try {
            if (instance == null) {
                instance = new FreemarkerHelper();
            }
        } finally {
            lock.unlock();
        }

        return instance;
    }

    private void initStringTemplatecfg() {
        if (logger.isDebugEnabled()) {
            logger.debug("init Freemarker Configuration start");
        }

        lock.lock();

        try {
            if (stringTemplatecfg == null) {
                stringTemplatecfg = new Configuration(Configuration.VERSION_2_3_22);
            }

            stringTemplatecfg.setTemplateLoader(new StringTemplateLoader());
            stringTemplatecfg.setTemplateUpdateDelay(15 * 60);
            stringTemplatecfg.setClassicCompatible(true);
            stringTemplatecfg.setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            stringTemplatecfg.setNumberFormat("0.####");
            stringTemplatecfg.setDefaultEncoding(ENCODING_UTF8);
        } catch (Exception e) {
            logger.error("init Freemarker Configuration", e);
        } finally {
            lock.unlock();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("init Freemarker Configuration end");
        }
    }

    private void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("init Freemarker Configuration start");
        }

        lock.lock();

        try {
            tempConfiguration = new Configuration(Configuration.VERSION_2_3_22);
            tempConfiguration.setClassicCompatible(true);
            tempConfiguration.setTemplateUpdateDelay(15 * 60);
            tempConfiguration.setClassLoaderForTemplateLoading(FreemarkerHelper.class.getClassLoader(), TEMPLATESPATH);
            // String pathName = FreemarkerHelper.class.getResource("/").getPath() + TEMPLATESPATH;
            // tempConfiguration.setDirectoryForTemplateLoading(new File(pathName));
            tempConfiguration.setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            tempConfiguration.setNumberFormat("");
            tempConfiguration.setDefaultEncoding(ENCODING_UTF8);
        } catch (Exception e) {
            logger.error("init Freemarker Configuration", e);
        } finally {
            lock.unlock();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("init Freemarker Configuration end");
        }
    }

    public Template getStringTemplate(String name, String templateContent)
        throws Exception {
        if (stringTemplatecfg == null) {
            initStringTemplatecfg();
        }

        if (stringTemplatecfgThreadLocal.get() == null) {
            stringTemplatecfgThreadLocal.set(stringTemplatecfg);
        }

        Configuration templatecfg = stringTemplatecfgThreadLocal.get();
        StringTemplateLoader stringLoader = (StringTemplateLoader) templatecfg.getTemplateLoader();
        stringLoader.putTemplate(name, templateContent);

        return templatecfg.getTemplate(name, ENCODING_UTF8);
    }

    public String stringTemplate2String(Object root, String stringTemplate) {
        if (logger.isDebugEnabled()) {
            logger.debug("start generate String");
        }

        StringWriter sw = new StringWriter();

        try {
            String templateName = MD5Support.MD5(stringTemplate);
            Template templateStr = this.getStringTemplate(templateName, stringTemplate);
            templateStr.process(root, sw);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            stringTemplatecfgThreadLocal.set(null);
        }

        return sw.toString();
    }

    public String stringTemplate2String(Object root, String name, String stringTemplate) {
        if (logger.isDebugEnabled()) {
            logger.debug("start generate String");
        }

        StringWriter sw = new StringWriter();

        try {
            Template templateStr = this.getStringTemplate(name, stringTemplate);
            templateStr.process(root, sw);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            stringTemplatecfgThreadLocal.set(null);
        }

        return sw.toString();
    }

    public String template2String(Object root, String templateFile) {
        if (logger.isDebugEnabled()) {
            logger.debug("start generate String");
        }

        if (tempConfiguration == null) {
            init();
        }

        StringWriter sw = new StringWriter();

        try {
            Template template = tempConfiguration.getTemplate(templateFile);
            template.process(root, sw);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return sw.toString();
    }

    public String template2File(String path, String fileName, Object root, String templateFile) {
        if (tempConfiguration == null) {
            init();
        }

        creatDirs(path);

        Writer out = null;
        File afile = new File(path + File.separator + fileName);

        try {
            Template template = tempConfiguration.getTemplate(templateFile);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(afile), ENCODING_UTF8));
            template.process(root, out);
            out.flush();
        } catch (Exception e) {
            logger.error("themplate2File failed", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                logger.error("themplate2File failed", e);
            }
        }

        return afile.getPath();
    }

    public static boolean creatDirs(String path) {
        File aFile = new File(path);

        if (!aFile.exists()) {
            return aFile.mkdirs();
        } else {
            return true;
        }
    }

    public static void save(final OutputStream os, final String content)
        throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(os);
        bos.write(content.getBytes());
        bos.flush();
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        FreemarkerHelper fre = FreemarkerHelper.getInstance();
        String templateContent = "<#list error as errormap> " + "<li class=\"ui-list-item\"><p>执行参数：${(errormap['ctp'])?if_exists}</p>" +
            "<p>${(errormap['error'])?if_exists}</p>" + "</li>" + "</#list>";
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, String>> error = new ArrayList<Map<String, String>>();

        for (int i = 0; i < 3; i++) {
            Map<String, String> errormap = new HashMap<String, String>();
            errormap.put("ctp", "dddddddddddddd");
            errormap.put("error", "errormaperrormaperrormaperrormap");
            error.add(errormap);
        }

        map.put("error", error);

        String str = fre.stringTemplate2String(map, templateContent);
        System.out.println(str);
    }
}
