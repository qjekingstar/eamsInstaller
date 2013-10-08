package com.ekingstar.eams.installer.validator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import com.ekingstar.eams.installer.enumeration.DatabaseType;
import com.ekingstar.eams.installer.util.InstallUtils;
import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.adaptator.IXMLParser;
import com.izforge.izpack.api.adaptator.impl.XMLParser;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.installer.DataValidator;

public class AppConfigPrepare implements DataValidator{
	public Status validateData(InstallData installData) {
		InstallUtils.setVariable(installData, "dataInstallMode", installData.getVariable("dataInstallMode"));
		if("false".equals(installData.getVariable("modify.izpack.install")) && "CUSTOM_DB".equals(installData.getVariable("dataInstallMode"))){
			InstallUtils.replaceNullVariablToEmpty(installData,"ctxPath");
			InstallUtils.replaceNullVariablToEmpty(installData,"dbType");
			InstallUtils.replaceNullVariablToEmpty(installData,"dbUrl");
			InstallUtils.replaceNullVariablToEmpty(installData,"dbUser");
			InstallUtils.replaceNullVariablToEmpty(installData,"dbPassword");
			InstallUtils.replaceNullVariablToEmpty(installData,"maxActive");
			InstallUtils.replaceNullVariablToEmpty(installData,"maxIdle");
			InstallUtils.replaceNullVariablToEmpty(installData,"maxWait");
			installData.refreshVariables();
			return Status.OK;
		}
		StringBuilder pathBuilder = new StringBuilder(installData.getInstallPath());
		String fileSeparator = System.getProperty("file.separator");
		if (pathBuilder.charAt(pathBuilder.length() - 1) != fileSeparator.charAt(0)) {
			pathBuilder.append(fileSeparator);
		}
		pathBuilder.append("tomcat").append(fileSeparator).append("conf").append(fileSeparator);
		pathBuilder.append("Catalina").append(fileSeparator).append("localhost").append(fileSeparator).append(installData.getVariable("appContextXMLPath"));
		IXMLParser parser = new XMLParser();
		try {
			IXMLElement context = parser.parse(new FileInputStream(pathBuilder.toString()));
			if(context.hasChildren()){
				List<IXMLElement> resources = context.getChildrenNamed("Resource");
				if(null!=resources && !resources.isEmpty()){
					IXMLElement resource = resources.get(0);
					String driverClassName = resource.getAttribute("driverClassName");
					DatabaseType dbType = DatabaseType.valueOfDriverClassName(driverClassName);
					if(dbType==DatabaseType.ORACLE){
						installData.setVariable("dbType.select.ORACLE", "true");
						installData.setVariable("dbType.select.H2", "false");
					}else if (dbType==DatabaseType.H2) {
						installData.setVariable("dbType.select.H2", "true");
						installData.setVariable("dbType.select.ORACLE", "false");
					}
					
					String ctxPath = context.getAttribute("path");
					if(null!=ctxPath && ctxPath.trim().startsWith("/")){
						ctxPath = ctxPath.trim().substring(1);
					}
					InstallUtils.setVariable(installData,"ctxPath", ctxPath);
					InstallUtils.setVariable(installData,"dbType", dbType.toString());
					InstallUtils.setVariable(installData,"dbUrl", resource.getAttribute("url"));
					InstallUtils.setVariable(installData,"dbUser", resource.getAttribute("username"));
					InstallUtils.setVariable(installData,"dbPassword", resource.getAttribute("password"));
					InstallUtils.setVariable(installData,"maxActive", resource.getAttribute("maxActive"));
					InstallUtils.setVariable(installData,"maxIdle", resource.getAttribute("maxIdle"));
					InstallUtils.setVariable(installData,"maxWait", resource.getAttribute("maxWait"));
				}
			}
			installData.refreshVariables();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Status.OK;
	}

	public String getErrorMessageId() {
		// TODO Auto-generated method stub
		return null;
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
