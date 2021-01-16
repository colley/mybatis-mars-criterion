/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-6 下午2:14
 * History:
 */
package com.mars.kit.archiver;

import com.mars.kit.archiver.conf.ArchiveItem;


/**
 * Handler.java
 * @author ColleyMa
 * @version 19-5-6 下午2:14
 */
public interface ArchiveHandler {
	
    public void handle(ArchiveItem item);
}
