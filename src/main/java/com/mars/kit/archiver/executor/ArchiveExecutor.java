/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-8 下午5:07
 * History:
 */
package com.mars.kit.archiver.executor;

import java.util.List;
import java.util.Map;

import com.mars.kit.archiver.conf.ArchiveItem;


/**
 * ArchiveExecutor.java
 * @author ColleyMa
 * @version 19-5-8 下午5:07
 */
public interface ArchiveExecutor {
    public void execute(ArchiveItem item);

    /**
     * 归档数据
     * @param item
     */
    public int archiveItem(ArchiveItem item,List<Map<String, Object>> archiveData);

    /**
     * 归档成功后删除原始表数据
     * @param item
     */
    public int deleteItem(ArchiveItem item,List<Map<String, Object>> archiveData);
    
    
   
    
}
