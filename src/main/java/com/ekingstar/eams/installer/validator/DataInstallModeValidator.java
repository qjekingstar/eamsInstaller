package com.ekingstar.eams.installer.validator;

import com.ekingstar.eams.installer.util.InstallUtils;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.installer.DataValidator;

public class DataInstallModeValidator implements DataValidator{

	public Status validateData(InstallData installData) {
		InstallUtils.INSTALLDATA.put("dataInstallMode",installData.getVariable("dataInstallMode"));
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
