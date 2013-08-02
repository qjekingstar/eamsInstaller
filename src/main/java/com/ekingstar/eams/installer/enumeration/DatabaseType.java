package com.ekingstar.eams.installer.enumeration;

import java.util.Map;

public enum DatabaseType {
	ORACLE("jdbc:oracle:thin:@${server}:${port}:${db}", "oracle.jdbc.OracleDriver", "Oracle", 1521), 
	DB2("jdbc:db2://${server}:${port}/${db}", "com.ibm.db2.jcc.DB2Driver", "DB2", 50000), 
	SQLSERVER("jdbc:jtds:sqlserver://${server}:${port};databaseName=${db}", "net.sourceforge.jtds.jdbc.Driver", "SQL Server", 1433), 
	MYSQL("jdbc:mysql://${server}:${post}/${db}", "com.mysql.jdbc.Driver", "MySQL", 3306), 
	H2("jdbc:h2:${server}/${db}", "org.h2.Driver", "H2 Database", true);

	private String driverClassName;

	private String urlTemplate;

	private Integer defaultPort;

	private String title;

	private boolean fileDB = false;

	DatabaseType(String urlTemplate, String driverClassName, String title, Integer defaultPort) {
		this.urlTemplate = urlTemplate;
		this.driverClassName = driverClassName;
		this.title = title;
		this.defaultPort = defaultPort;
	}

	DatabaseType(String urlTemplate, String driverClassName, String title, boolean fileDB) {
		this.urlTemplate = urlTemplate;
		this.driverClassName = driverClassName;
		this.title = title;
		this.fileDB = fileDB;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getUrlTemplate() {
		return urlTemplate;
	}

	public static DatabaseType valueOfDriverClassName(String driverClassName){
		DatabaseType[] values = values();
		for (DatabaseType databaseType : values) {
			if(databaseType.getDriverClassName().equals(driverClassName)){
				return databaseType;
			}
		}
		return null;
	}
	
	public String getUrl(Map<String, Object> urlArgs) {
		String url = this.urlTemplate.replace("${server}", urlArgs.get("dbHost").toString());
		if (!fileDB) {
			url = url.replace("${port}", urlArgs.get("dbPort").toString());
		}
		return url.replace("${db}", urlArgs.get("dbName").toString());
	}

	public boolean isFileDB() {
		return fileDB;
	}

	public Integer getDefaultPort() {
		return defaultPort;
	}

	public String getTitle() {
		return title;
	}
}
