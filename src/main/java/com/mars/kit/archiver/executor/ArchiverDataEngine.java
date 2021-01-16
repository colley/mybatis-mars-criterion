/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-13 下午2:26
 * History:
 */
package com.mars.kit.archiver.executor;

import com.mars.kit.archiver.conf.ArchiveConfig;


/**
 * ArchiverDataEngine.java
 * @author ColleyMa
 * @version 19-5-13 下午2:26
 */
public interface ArchiverDataEngine {
	
    public void archive(ArchiveConfig config);
}
