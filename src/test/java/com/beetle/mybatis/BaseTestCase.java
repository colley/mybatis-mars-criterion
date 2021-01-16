package com.beetle.mybatis;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public abstract class BaseTestCase {
    static {
    	org.apache.ibatis.logging.LogFactory.useLog4JLogging(); 
    }
}