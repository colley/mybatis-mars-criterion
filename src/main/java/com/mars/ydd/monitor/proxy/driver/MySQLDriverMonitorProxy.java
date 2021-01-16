package com.mars.ydd.monitor.proxy.driver;

import java.io.Serializable;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;

public class MySQLDriverMonitorProxy extends JdbcDriverMonitorProxy  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2686524321307265285L;
	private final Driver mysqlDriver;
    private final MySqlExceptionSorter exceptionSorter = new MySqlExceptionSorter();

    public MySQLDriverMonitorProxy() {
    	 super();
         try {
             //mysqlDriver = new com.mysql.jdbc.Driver();
             mysqlDriver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
         }catch (Exception e) {
         	throw new RuntimeException("Init com.mysql.jdbc.Driver fail.", e);
 		}
    }

    @Override
    protected Driver getRealDriver() {
        return mysqlDriver;
    }

    @Override
    protected boolean isExceptionFatal(SQLException e) {
        return exceptionSorter.isExceptionFatal(e);
    }

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

}