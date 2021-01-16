package com.mars.kit.archiver.conf;

import java.util.Properties;

public class EmailConfig  implements java.io.Serializable{
	private static final long serialVersionUID = 4942380702935547240L;

	public static final String DEFAULT_SENDERNAME = "notify告警";

    private Properties javaMailProperties = new Properties();
	
	private String host;
	
	private int port;
	
	private String defaultEncoding;
	
	
	private String username;
	
	private String password;
	
	/****/
    private String senderName = DEFAULT_SENDERNAME;
	
	private String sender; //发件人

	public Properties getJavaMailProperties() {
		return javaMailProperties;
	}

	public void setJavaMailProperties(Properties javaMailProperties) {
		this.javaMailProperties = javaMailProperties;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
}
