package com.mars.kit.archiver.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mars.kit.archiver.ArchiveHandler;
import com.mars.kit.archiver.conf.ArchiveConfig;
import com.mars.kit.archiver.conf.ArchiveItem;
import com.mars.kit.archiver.executor.ArchiveExecutor;
import com.mars.kit.archiver.executor.impl.AloneArchiveExecutor;
import com.mars.kit.archiver.executor.impl.BatchArchiveExecutor;

public class ExecuteHandler implements ArchiveHandler {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());


	@Override
	public void handle(ArchiveItem item) {
		ArchiveConfig config = item.getConfig();
		//maxId 为null 没有数据需要归档
		if(item.getMaxId()==null) {
			return;
		}
		ArchiveExecutor executor= null;
		//判断是批次还是单条
		if(config.isBulk()) {
			executor= new BatchArchiveExecutor();
		}else {
			executor = new AloneArchiveExecutor();
		}
		executor.execute(item);	
	}
}
