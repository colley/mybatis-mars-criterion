package com.yihaodian.ydd.monitor.aspectj;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.lang.Boolean;

import com.yihaodian.ydd.AbstractDataSource;
import com.yihaodian.ydd.monitor.data.optrecdata.ConnectionOptRecData.CloseConn;
import com.yihaodian.ydd.monitor.data.optrecdata.ConnectionOptRecData.CommitTrans;
import com.yihaodian.ydd.monitor.data.optrecdata.ConnectionOptRecData.CreateTrans;
import com.yihaodian.ydd.monitor.data.optrecdata.ConnectionOptRecData.ErrTrans;
import com.yihaodian.ydd.monitor.data.optrecdata.ConnectionOptRecData.GetConn;
import com.yihaodian.ydd.monitor.data.optrecdata.ConnectionOptRecData.GetConnErr;
import com.yihaodian.ydd.monitor.data.optrecdata.ConnectionOptRecData.GetConnSuc;
import com.yihaodian.ydd.monitor.data.optrecdata.ConnectionOptRecData.PhyCloseConn;
import com.yihaodian.ydd.monitor.data.optrecdata.ConnectionOptRecData.PhyCreateConn;
import com.yihaodian.ydd.monitor.data.optrecdata.ConnectionOptRecData.PhyCreateConnErr;
import com.yihaodian.ydd.monitor.data.optrecdata.ConnectionOptRecData.PhyCreateConnSuc;
import com.yihaodian.ydd.monitor.data.optrecdata.ConnectionOptRecData.RollbackTrans;
import com.yihaodian.ydd.monitor.processor.MonitorDataProcessor;
import com.yihaodian.ydd.monitor.proxy.CallableStatementMonitorProxy;
import com.yihaodian.ydd.monitor.proxy.PhysicalConnectionMonitorProxy;
import com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy;
import com.yihaodian.ydd.monitor.proxy.ProxyedConnectionMonitorProxy;
import com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy;
import com.yihaodian.ydd.monitor.proxy.driver.JdbcDriverMonitorProxy;

public aspect ConnectionMonitorAspectJ {

    Statement around(PhysicalConnectionMonitorProxy cmp) : execution(Statement com.yihaodian.ydd.monitor.proxy.PhysicalConnectionMonitorProxy.createStatement(..)) && target(cmp) {
        return new StatementMonitorProxy(proceed(cmp), cmp.getMonitorDataProcessor());
    }

    PreparedStatement around(PhysicalConnectionMonitorProxy cmp, String sql) : execution(PreparedStatement com.yihaodian.ydd.monitor.proxy.PhysicalConnectionMonitorProxy.prepareStatement(String,..)) && target(cmp) && args(sql,..) {
        return new PreparedStatementMonitorProxy(proceed(cmp, sql), cmp.getMonitorDataProcessor(), sql);
    }

    CallableStatement around(PhysicalConnectionMonitorProxy cmp, String sql) : execution(CallableStatement com.yihaodian.ydd.monitor.proxy.PhysicalConnectionMonitorProxy.prepareCall(String,..)) && target(cmp) && args(sql,..) {
        return new CallableStatementMonitorProxy(proceed(cmp, sql), cmp.getMonitorDataProcessor(), sql);
    }

    pointcut recordPCreateConnection(JdbcDriverMonitorProxy jdmp) : execution(java.sql.Connection com.yihaodian.ydd.monitor.proxy.driver.JdbcDriverMonitorProxy.connect(..) throws SQLException) && target(jdmp);

    // pCreateConnCount
    Connection around(JdbcDriverMonitorProxy jdmp) throws SQLException : recordPCreateConnection(jdmp) {
        MonitorDataProcessor processor = jdmp.getCurMDP();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro();
        Boolean dssdSwitch = false;
        if(switchPro!=null){
         dssdSwitch = (Boolean)switchPro.get("dssdSwitch");
        }
            if(dssdSwitch){
            processor.putOperationRecord(new PhyCreateConn(processor));
            }
            try {
                Connection connection = new PhysicalConnectionMonitorProxy(proceed(jdmp), processor);
                if(dssdSwitch){
                processor.putOperationRecord(new PhyCreateConnSuc(processor));
                }
                return connection;
            } catch (SQLException e) {
                if(dssdSwitch){
                processor.putOperationRecord(new PhyCreateConnErr(processor));
                }
                throw e;
            }
        } else {
            return proceed(jdmp);
        }
    }

    // pCreateConnErrCount
//	after() throwing(SQLException e) : recordPCreateConnection() {
//		MonitorDataProcessor processor = AbstractDataSource.getCurrentMonitorDataProcessor();
//		processor.putOperationRecord(new ConnectionOptRecData( OperationType.PCREATECONNERRCOUNT));
//	}

    // pCloseConnCount
    before(PhysicalConnectionMonitorProxy cmp) : execution(void com.yihaodian.ydd.monitor.proxy.PhysicalConnectionMonitorProxy.close()) && target(cmp) {
        MonitorDataProcessor processor = cmp.getMonitorDataProcessor();
        if(processor!=null){
        Properties switchPro = processor.getSwitchPro(); 
        Boolean dssdSwitch = false;
        if(switchPro!=null){
         dssdSwitch = (Boolean)switchPro.get("dssdSwitch");
        }
        if(dssdSwitch){
        processor.putOperationRecord(new PhyCloseConn(processor));
        }
        }
    }

    pointcut recordCreateConnection(AbstractDataSource ads) : call(java.sql.Connection com.yihaodian.ydd.AbstractDataSource.getConnectionWithLock(..) throws SQLException) && target(ads) && within(com.yihaodian.ydd.DynamicDataSource)
        /*&& !within(com.yihaodian.ydd.DynamicDataSource.AdsFailoverCheckThread) && cflow(execution(* com.yihaodian.ydd.DynamicDataSource.getConnection(..)))*/;

    // getConnCount || getConnWaitTimeSum || getConnMaxWaitTime
    Connection around(AbstractDataSource ads) throws SQLException : recordCreateConnection(ads) {
        MonitorDataProcessor processor = ads.getMonitorDataProcessor();
        if(processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean dssdSwitch = false;
        if(switchPro!=null){
         dssdSwitch = (Boolean)switchPro.get("dssdSwitch");
        }        
            Connection connection = null;
            long currentTimeMillis = System.currentTimeMillis();
            if(dssdSwitch){
            processor.putOperationRecord(new GetConn(processor, currentTimeMillis));
            }
            long startTime = System.currentTimeMillis();
            try {
                connection = proceed(ads);
                if(dssdSwitch){
                processor.putOperationRecord(new GetConnSuc(processor, currentTimeMillis, System.currentTimeMillis()
                        - startTime));
                        }
                return new ProxyedConnectionMonitorProxy(connection, processor);
            } catch (SQLException e) {
                if(dssdSwitch){
                processor.putOperationRecord(new GetConnErr(processor, currentTimeMillis));
                }
                throw e;
            }
        } else {
            return proceed(ads);
        }
    }

    // getConnErrCount
//    after(AbstractDataSource ads) throwing(SQLException e) : recordCreateConnection(ads) {
//        MonitorDataProcessor processor = ads.getMonitorDataProcessor();
//        if (processor != null) {
//            processor.putOperationRecord(new ConnectionOptRecData( OperationType.GETCONNERRCOUNT));
//        }
//    }

    // closeConnCount
    before(ProxyedConnectionMonitorProxy cmp) : execution(void com.yihaodian.ydd.monitor.proxy.ProxyedConnectionMonitorProxy.close()) && target(cmp) {
        MonitorDataProcessor processor = cmp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean dssdSwitch = false;
        if(switchPro!=null){
         dssdSwitch = (Boolean)switchPro.get("dssdSwitch");
        }
        if(dssdSwitch){
        processor.putOperationRecord(new CloseConn(processor));
        }
        }
    }

    //	pointcut recordBeginTrans(ConnectionMonitorProxy cmp, boolean autoCommit) : execution(void com.yihaodian.ydd.monitor.proxy.PhysicalConnectionMonitorProxy.setAutoCommit(boolean)) && args(autoCommit) && target(cmp);

    // createTransCount
    before(PhysicalConnectionMonitorProxy cmp, boolean autoCommit) : execution(void com.yihaodian.ydd.monitor.proxy.PhysicalConnectionMonitorProxy.setAutoCommit(boolean)) && args(autoCommit) && target(cmp) {
        try {
            if (autoCommit == false && cmp.getAutoCommit() == true) {
                MonitorDataProcessor processor = cmp.getMonitorDataProcessor();
                if (processor != null) {
                Properties switchPro = processor.getSwitchPro(); 
                Boolean dssdSwitch = false;
                if(switchPro!=null){
                  dssdSwitch = (Boolean)switchPro.get("dssdSwitch");
                 }
                if(dssdSwitch){
                processor.putOperationRecord(new CreateTrans(processor));
                }
            }
            }
        } catch (SQLException e) {
        }
    }

    pointcut recordCommitTrans(PhysicalConnectionMonitorProxy cmp) : execution(void com.yihaodian.ydd.monitor.proxy.PhysicalConnectionMonitorProxy.commit()) && target(cmp);

    // commitTransCount
    before(PhysicalConnectionMonitorProxy cmp) : recordCommitTrans(cmp) {
        MonitorDataProcessor processor = cmp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean dssdSwitch = false;
        if(switchPro!=null){
         dssdSwitch = (Boolean)switchPro.get("dssdSwitch");
        }
        if(dssdSwitch){
        processor.putOperationRecord(new CommitTrans(processor));
        }
        }
    }

    // errTransCount
    after(PhysicalConnectionMonitorProxy cmp) throwing() : recordCommitTrans(cmp) {
        MonitorDataProcessor processor = cmp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean dssdSwitch = false;
        if(switchPro!=null){
         dssdSwitch = (Boolean)switchPro.get("dssdSwitch");
        }
        if(dssdSwitch){
        processor.putOperationRecord(new ErrTrans(processor));
        }
        }
    }

    //	pointcut recordRollbackTrans() : execution(void com.yihaodian.ydd.monitor.proxy.PhysicalConnectionMonitorProxy.rollback());

    // rollbackTransCount
    before(PhysicalConnectionMonitorProxy cmp) : execution(void com.yihaodian.ydd.monitor.proxy.PhysicalConnectionMonitorProxy.rollback()) && target(cmp) {
        MonitorDataProcessor processor = cmp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean dssdSwitch = false;
        if(switchPro!=null){
         dssdSwitch = (Boolean)switchPro.get("dssdSwitch");
        }
        if(dssdSwitch){
        processor.putOperationRecord(new RollbackTrans(processor));
        }
        }
    }

}