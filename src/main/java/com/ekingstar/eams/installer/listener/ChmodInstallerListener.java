/*
 * IzPack - Copyright 2001-2005 Julien Ponge, All Rights Reserved.
 * 
 * http://izpack.org/
 * http://izpack.codehaus.org/
 * 
 * Copyright 2004 Klaus Bartz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ekingstar.eams.installer.listener;

import java.io.File;
import java.util.List;

import com.izforge.izpack.api.data.AutomatedInstallData;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.Pack;
import com.izforge.izpack.api.data.PackFile;
import com.izforge.izpack.api.event.InstallerListener;
import com.izforge.izpack.api.event.ProgressListener;
import com.izforge.izpack.api.handler.AbstractUIProgressHandler;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.util.FileExecutor;
import com.izforge.izpack.util.OsVersion;

public class ChmodInstallerListener implements InstallerListener {
	protected final InstallData installData;

	protected final Resources resources;
	
	protected String installPath;
	
	private boolean inited = false;
	
	public ChmodInstallerListener(InstallData installData, Resources resources) {
		this.installData = installData;
		this.resources = resources;
	}
	
	public boolean isFileListener() {
		return true;
	}
	
	public void afterFile(File file, PackFile packFile, Pack pack) {
		if(file == null || OsVersion.IS_WINDOWS){
			return;
		}
		if(file.isFile() && file.getName().toLowerCase().endsWith(".sh")){
			File parent = file.getParentFile();
			if(null!=parent && parent.getName().equals("bin")){
				parent = parent.getParentFile();
				if(null!=parent && parent.getName().equals("tomcat")){
					try {
						chmod(file, 0754);	
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	private void chmod(File path, int permissions) throws Exception {
		init();
		String permStr = Integer.toOctalString(permissions);
		String[] params = { "chmod", permStr, path.getAbsolutePath() };
		String[] output = new String[2];
		FileExecutor fe = new FileExecutor();
		fe.executeCommand(params, output);
	}
	
	private void init() throws Exception {
		if (!inited) {
			installPath = installData.getInstallPath().replaceAll("\\\\", "/");
			if (installPath.endsWith("/")) {
				installPath = installPath.substring(0, installPath.length() - 1);
			}
			
			inited = true;
		}
	}
	
	public void afterDir(File dir, PackFile packFile, Pack pack) {
		
	}

	

	public void afterInstallerInitialization(AutomatedInstallData arg0)
			throws Exception {
		
	}

	public void afterPack(Pack arg0, int arg1) {
		
	}

	public void afterPack(Pack arg0, Integer arg1,
			AbstractUIProgressHandler arg2) throws Exception {
		
	}

	public void afterPacks(List<Pack> arg0, ProgressListener arg1) {
		
	}

	public void afterPacks(AutomatedInstallData arg0,
			AbstractUIProgressHandler arg1) throws Exception {
		
	}

	public void beforeDir(File arg0, PackFile arg1) throws Exception {
		
	}

	public void beforeDir(File arg0, PackFile arg1, Pack arg2) {
		
	}

	public void beforeFile(File arg0, PackFile arg1) throws Exception {
		
	}

	public void beforeFile(File arg0, PackFile arg1, Pack arg2) {
		
	}

	public void beforePack(Pack arg0, int arg1) {
		
	}

	public void beforePack(Pack arg0, Integer arg1,
			AbstractUIProgressHandler arg2) throws Exception {
		
	}

	public void beforePacks(List<Pack> arg0) {
		
	}

	public void beforePacks(AutomatedInstallData arg0, Integer arg1,
			AbstractUIProgressHandler arg2) throws Exception {
		
	}
	
	public void afterFile(File filePath, PackFile pf) throws Exception {
		
	}

	public void afterDir(File dirPath, PackFile pf) throws Exception {
		
	}

	public void initialise() {
		
	}
}
