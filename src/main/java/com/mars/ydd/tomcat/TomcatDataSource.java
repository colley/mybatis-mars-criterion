package com.mars.ydd.tomcat;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;

import com.mars.ydd.AbstractDataSource;

public class TomcatDataSource extends AbstractDataSource {
//	private static final Logger logger = Logger.getLogger(TomcatDataSource.class);
	 private org.apache.tomcat.jdbc.pool.DataSource tomcatDataSource = null;

	 
	    public PrintWriter getLogWriter() throws SQLException {
	        return tomcatDataSource.getLogWriter();
	    }

	 
	    public void setLogWriter(PrintWriter out) throws SQLException {
	        tomcatDataSource.setLogWriter(out);
	    }

	 
	    public void setLoginTimeout(int seconds) throws SQLException {
	        tomcatDataSource.setLoginTimeout(seconds);
	    }

	 
	    public int getLoginTimeout() throws SQLException {
	        return tomcatDataSource.getLoginTimeout();
	    }

	 
	    public <T> T unwrap(Class<T> iface) throws SQLException {
	        return tomcatDataSource.unwrap(iface);
	    }

	 
	    public boolean isWrapperFor(Class<?> iface) throws SQLException {
	        return tomcatDataSource.isWrapperFor(iface);
	    }

	 
	    public Connection getConnection() throws SQLException {
	        return tomcatDataSource.getConnection();
	    }

	 
	    public Connection getConnection(String username, String password) throws SQLException {
	        return tomcatDataSource.getConnection(username, password);
	    }

	 
	    protected long getMaxWait() {
	        return tomcatDataSource.getPoolProperties().getMaxWait();
	    }

	 
	    protected void setProperties(Properties properties) {
	        if (tomcatDataSource != null) {
	            tomcatDataSource.close();
	        }
	        tomcatDataSource = new DataSource(DataSourceFactory.parsePoolProperties(properties));
	        super.setProperties(properties);
	    }

	 
	    public void close() {
	        super.close();
	        if (tomcatDataSource != null) {
	            tomcatDataSource.close();
	        }
	    }

	 
	    protected int getMaxActive() {
	        return tomcatDataSource.getMaxActive();
	    }

	 
	    protected javax.sql.DataSource getRealDataSource() {
	        return tomcatDataSource;
	    }

	 
	    protected ConnectionPool getTomcatConnPool() {
	        try {
	            return tomcatDataSource.createPool();
	        } catch (SQLException e) {
	            return null;
	        }
	    }


		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return tomcatDataSource.getParentLogger();
		}
}
