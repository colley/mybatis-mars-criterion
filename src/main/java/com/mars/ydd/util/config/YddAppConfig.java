package com.mars.ydd.util.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.SystemPropertyUtils;

/**
 * 用于ydd的app级别的配置。
 */
public class YddAppConfig implements InitializingBean {

    /**
     * 应用名称
     */
    private String appId;

    /**
     * 配置文件的根目录（绝对路径）。 目录中可包含各个子目录和配置文件。
     */
    private String configFolderFullPath;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getConfigFolderFullPath() {
        return configFolderFullPath;
    }

    public void setConfigFolderFullPath(String configFolderFullPath) {
        this.configFolderFullPath = configFolderFullPath;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(StringUtils.isBlank(this.appId)) {
            throw new RuntimeException("appId should not be blank.");
        }
        this.appId = SystemPropertyUtils.resolvePlaceholders(this.appId);
        if(StringUtils.isNotBlank(this.configFolderFullPath)) {
            this.configFolderFullPath = SystemPropertyUtils.resolvePlaceholders(this.configFolderFullPath);
        }

//        if(StringUtils.isBlank(this.configFolderFullPath)) {
//            throw new RuntimeException("configFolderFullPath should not be blank.");
//        }
    }

}
