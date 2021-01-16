package com.mars.ydd.dbcp;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mars.ydd.AbstractDataSource;


public class DbcpDataSource extends AbstractDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbcpDataSource.class);

    private BasicDataSource dbcpDataSource = null;

    public PrintWriter getLogWriter() throws SQLException {
        return dbcpDataSource.getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        dbcpDataSource.setLogWriter(out);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        dbcpDataSource.setLoginTimeout(seconds);
    }

    public int getLoginTimeout() throws SQLException {
        return dbcpDataSource.getLoginTimeout();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return dbcpDataSource.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dbcpDataSource.isWrapperFor(iface);
    }

    public Connection getConnection() throws SQLException {
        return dbcpDataSource.getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return dbcpDataSource.getConnection(username, password);
    }

    @Override
    protected long getMaxWait() {
        return dbcpDataSource.getMaxWait();
    }

    @Override
    protected void setProperties(Properties properties) {
        try {
            dbcpDataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(properties);
            super.setProperties(properties);
        } catch (Exception e) {
            throw new RuntimeException("Dbcp datasource create error.", e);
        }
    }

    public void close() {
        if (dbcpDataSource != null) {
            try {
                if (!dbcpDataSource.isClosed()) {
                    dbcpDataSource.close();
                }
            } catch (SQLException e) {
                LOGGER.error("Dbcp datasource close error.", e);
            }
        }
    }

    @Override
    protected int getMaxActive() {
        return dbcpDataSource.getMaxActive();
    }

    @Override
    protected DataSource getRealDataSource() {
        return dbcpDataSource;
    }

    @Override
    protected ConnectionPool getTomcatConnPool() {
        return null;
    }

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
}
