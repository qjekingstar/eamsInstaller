package com.ekingstar.eams.installer.validator;

import java.io.File;
import java.io.FilenameFilter;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.installer.DataValidator;

public class AppContextXMLExistsValidator implements DataValidator{

	public Status validateData(InstallData installData) {
		StringBuilder pathBuilder = new StringBuilder(installData.getInstallPath());
		String fileSeparator = System.getProperty("file.separator");
		if (pathBuilder.charAt(pathBuilder.length() - 1) != fileSeparator.charAt(0)) {
			pathBuilder.append(fileSeparator);
		}
		pathBuilder.append("tomcat").append(fileSeparator).append("conf").append(fileSeparator);
		pathBuilder.append("Catalina").append(fileSeparator).append("localhost").append(fileSeparator);
		File appContextDir = new File(pathBuilder.toString());
		File[] appContextXmlArr = appContextDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml");
			}
		});
		if(null==appContextXmlArr){
			return Status.ERROR;
		}
		return Status.OK;
	}

	public String getErrorMessageId() {
		//return "eams.validator.appContextXml.notExists";
		return "没有找到应用配置文件!";
	}

	public String getWarningMessageId() {
		return null;
	}

	public boolean getDefaultAnswer() {
		return false;
	}

}
