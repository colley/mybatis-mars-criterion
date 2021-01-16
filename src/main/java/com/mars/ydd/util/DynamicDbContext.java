package com.mars.ydd.util;

import java.util.Properties;

/**
 * @author Hikin Yao
 * @version 1.0
 */
public class DynamicDbContext {
    /**
     * 主库类型
     */
    private static String MASTER_DB_TYPE = "master_db";
    /**
     * 备库
     */
    private static String SLAVE_DB_TYPE = "slave_db";

    private static final ThreadLocal<String> bsDBContext = new ThreadLocal<String>();

    public static boolean switchToMasterDB() {
        bsDBContext.set(MASTER_DB_TYPE);
        return true;
    }

    public static boolean switchToSlaveDB() {
        bsDBContext.set(SLAVE_DB_TYPE);
        return true;
    }

    public static boolean isMasterDB() {
        boolean result = false;
        String dbType = bsDBContext.get();
        if (dbType != null && dbType.equals(MASTER_DB_TYPE)) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public static void reset() {
        bsDBContext.remove();
    }
    
    public static String getDbUrlinfo(Properties properties){
    	String driverClassName = properties.getProperty("driverClassName");
    	String url = properties.getProperty("url");
    	if(driverClassName.contains("MySQLDriverMonitorProxy")){
    		String urlString = url.substring(url.indexOf("//")+2);
    		String domian_port = urlString.substring(0, urlString.indexOf("/"));
    		return domian_port;
    	}else if(driverClassName.contains("OracleDriverMonitorProxy")){
    		if(url.indexOf("//")!=-1){
    			String urlString = url.substring(url.indexOf("//")+2);
        		String domian_port = urlString.substring(0, urlString.indexOf("/"));
        		return domian_port;
    		}
    		String urlString = url.substring(url.indexOf("@")+1);
    		String domian_port = urlString.substring(0, urlString.lastIndexOf(":"));
//    		String port = domian.substring(1,domian.indexOf(":"));
    		return domian_port;
    	}
    	return "localhost";
    }
    
}
