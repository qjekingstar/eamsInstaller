package com.ekingstar.eams.installer.validator;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;

import com.ekingstar.eams.installer.enumeration.DatabaseType;
import com.ekingstar.eams.installer.util.InstallUtils;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.installer.DataValidator;
import com.izforge.izpack.api.installer.DataValidator.Status;

public class AppConfigValidator implements DataValidator{
	private String errorMessageId;
	
	public Status validateData(InstallData installData) {
		DatabaseType dbType = DatabaseType.valueOf(installData.getVariable("dbType"));
		String jdbcUrl = installData.getVariable("dbUrl");
		String dbUser = installData.getVariable("dbUser");
		String dbPassword = installData.getVariable("dbPassword");		
		if(InstallUtils.isBlank(jdbcUrl)){
			errorMessageId = "URL不能为空!";
			return Status.ERROR;
		}
		if(InstallUtils.isBlank(dbUser)){
			errorMessageId = "用户名不能为空!";
			return Status.ERROR;
		}
		if (!dbType.isFileDB()) {
			Connection conn = null;
			try {
				DataSource dataSource = new UnpooledDataSource(dbType.getDriverClassName(), jdbcUrl, dbUser, dbPassword);
				conn = dataSource.getConnection();
			} catch (Exception e) {
				errorMessageId = "数据库连接失败,请检查后再试!";
				return Status.ERROR;
			} finally{
				if(null!=conn){
					try {
						conn.close();
					} catch (SQLException e) {
					}
				}
			}
		}
		
		String ctxPath = null;
		if("false".equals(installData.getVariable("modify.izpack.install"))){
			ctxPath = installData.getVariable("ctxPath");
			if(InstallUtils.isBlank(ctxPath)){
				errorMessageId = "上下文不能为空!";
				return Status.ERROR;
			}
		}
		String maxActive = installData.getVariable("maxActive");
		String maxIdle = installData.getVariable("maxIdle");
		String maxWait = installData.getVariable("maxWait");
		
		if(null==InstallUtils.getInteger(maxActive)){
			errorMessageId = "请正确填写JNDI数据源最大连接数!";
			return Status.ERROR;
		}
		if(null==InstallUtils.getInteger(maxIdle)){
			errorMessageId = "请正确填写JNDI数据源最大空闲连接数!";
			return Status.ERROR;
		}
		if(null==InstallUtils.getInteger(maxWait)){
			errorMessageId = "请正确填写JNDI数据源最大等待连接数!";
			return Status.ERROR;
		}
		if(null!=ctxPath){
			InstallUtils.setVariable(installData, "ctxPath", ctxPath);
		}
		InstallUtils.setVariable(installData, "maxActive", maxActive);
		InstallUtils.setVariable(installData, "maxIdle", maxIdle);
		InstallUtils.setVariable(installData, "maxWait", maxWait);
		
		InstallUtils.setVariable(installData, "dbUrl", jdbcUrl);
		InstallUtils.setVariable(installData, "dbUser", dbUser);
		InstallUtils.setVariable(installData, "dbPassword", dbPassword);
		return Status.OK;
	}

	public String getErrorMessageId() {
		return errorMessageId;
	}

	public String getWarningMessageId() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getDefaultAnswer() {
		// TODO Auto-generated method stub
		return false;
	}

}
