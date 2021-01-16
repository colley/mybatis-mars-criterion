/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-16 下午2:54
 * History:
 */
package com.mars.kit.archiver;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.mars.kit.archiver.executor.ArchiverDataEngine;
import com.mars.kit.archiver.executor.ArchiverEngineExecutor;
import com.mars.kit.archiver.notify.ArchiveMonitor;
import com.mars.kit.common.IMarsTemplateExecutor;


/**
 * ArchiverClientFactoryBean.java
 *
 * @author ColleyMa
 * @version 19-5-16 下午2:54
*/
public class ArchiverClientFactoryBean implements FactoryBean<ArchiverDataEngine>, DisposableBean {
    /** spring事务*/
    private IMarsTemplateExecutor executor;
    
    private ArchiveMonitor monitor;

    @Override
    public ArchiverDataEngine getObject() throws Exception {
        return new ArchiverEngineExecutor(executor, monitor);
    }

    @Override
    public Class<?> getObjectType() {
        return ArchiverDataEngine.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public IMarsTemplateExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(IMarsTemplateExecutor executor) {
        this.executor = executor;
    }

    public ArchiveMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(ArchiveMonitor monitor) {
        this.monitor = monitor;
    }
    
    @Override
    public void destroy() throws Exception {
        // ignore
    }
}
