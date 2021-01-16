/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-7 下午4:51
 * History:
 */
package com.beetle.mybatis.archiver;

import javax.annotation.Resource;

import org.junit.Test;

import com.beetle.mybatis.BaseTestCase;
import com.mars.kit.archiver.conf.ArchiveConfig;
import com.mars.kit.archiver.executor.ArchiverEngineExecutor;


/**
 * TabelCheckHadderTest.java
 * @author ColleyMa
 * @version 19-5-7 下午4:51
 */
public class ArchiverDataTest extends BaseTestCase {
    @Resource
    private ArchiverEngineExecutor dataArchiveEngineExecutor;
    
 

    @Test
    public void testTableCheck() {
        ArchiveConfig config = new ArchiveConfig();
        //归档表
        config.setSrcTblName("karl_bargain_puzzle_archive");
        //归档目标表
        config.setDesTblName("karl_bargain_puzzle");
        //where条件
        config.setWhere("gmt_create<'2019-08-10'");
        //批次归档
        config.setLimit(100);
        //是否删除归档表数据
        config.setPurge(true);
        
        //批次执行
        config.setBulk(true);
        //单次执行时 设置事务提交数
        config.setTxnSize(100);
        //失败重试次数
        config.setRetries(1);
        //到达处理数，停止归档
        config.setProgressSize(800);
        //每次归档了limit个行记录后的休眠 ,默认5s （单位是ms)
        config.setSleep(200L);
        
        //设置是否发归档邮件
        config.setSendEmail(true);
        config.setRecipients("inno_backend_94@jd.com");
        
        dataArchiveEngineExecutor.archive(config);
        

    }
}
