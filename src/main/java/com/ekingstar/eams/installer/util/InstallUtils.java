package com.ekingstar.eams.installer.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.izforge.izpack.api.data.InstallData;


public class InstallUtils {
	public static final Map<String,String> INSTALLDATA = new ConcurrentHashMap<String, String>();
	
	public static void replaceNullVariablToEmpty(InstallData installData,String name){
		String value = getVariable(installData, name);
		if(null==value){
			setVariable(installData, name, "");
		}
	}
	
	public static String getVariable(InstallData installData,String name){
		String value = INSTALLDATA.get(name);
		if(null==value){
			value = installData.getVariable(name);
			if(value!=null){
				INSTALLDATA.put(name, value);
			}
		}
		return value;
	}
	
	public static void setVariable(InstallData installData,String name,String value){
		INSTALLDATA.put(name, value);
		installData.setVariable(name, value);
	}
	
	public static Integer getInteger(String str){
		return getInteger(str,null);
	}
	
	public static Integer getInteger(String str,Integer defaultVal){
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return defaultVal;
		}
	}
	
	public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
}
