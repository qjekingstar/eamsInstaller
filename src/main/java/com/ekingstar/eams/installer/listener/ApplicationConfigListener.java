package com.ekingstar.eams.installer.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ekingstar.eams.installer.enumeration.DatabaseType;
import com.ekingstar.eams.installer.util.InstallUtils;
import com.izforge.izpack.api.data.AutomatedInstallData;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.Pack;
import com.izforge.izpack.api.data.PackFile;
import com.izforge.izpack.api.event.InstallerListener;
import com.izforge.izpack.api.event.ProgressListener;
import com.izforge.izpack.api.handler.AbstractUIProgressHandler;
import com.izforge.izpack.api.resource.Resources;

import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class ApplicationConfigListener implements InstallerListener {
	protected final InstallData installData;

	protected final Resources resources;
	
	protected String installPath;

	protected String appContextPath = "eams";

	private Configuration config;

	private Map<String, Object> attributes = new HashMap<String, Object>();

	private boolean inited = false;

	public ApplicationConfigListener(InstallData installData, Resources resources) {
		this.installData = installData;
		this.appContextPath = installData.getInfo().getAppName();
		this.resources = resources;
	}

	private void init() throws Exception {
		if (!inited) {
			installPath = installData.getInstallPath().replaceAll("\\\\", "/");
			if (installPath.endsWith("/")) {
				installPath = installPath.substring(0, installPath.length() - 1);
			}
			if ("CUSTOM_DB".equals(InstallUtils.getVariable(installData, "dataInstallMode"))) {
				appContextPath = InstallUtils.getVariable(installData, "ctxPath");
			} else {
				Map<String, Object> urlArgs = new HashMap<String, Object>();
				String installPath = installData.getInstallPath().replaceAll("\\\\", "/");
				if (installPath.endsWith("/")) {
					installPath = installPath.substring(0, installPath.length() - 1);
				}
				urlArgs.put("dbHost", installPath + "/db");
				urlArgs.put("dbName", appContextPath);
				InstallUtils.setVariable(installData,"dbType", "H2");
				InstallUtils.setVariable(installData,"dbUrl", DatabaseType.H2.getUrl(urlArgs));
				InstallUtils.setVariable(installData,"dbUser", "eams");
				InstallUtils.setVariable(installData,"dbPassword", "eams");
				InstallUtils.setVariable(installData,"ctxPath", appContextPath);
				InstallUtils.setVariable(installData,"maxActive", "100");
				InstallUtils.setVariable(installData,"maxIdle", "30");
				InstallUtils.setVariable(installData,"maxWait", "1000");
				installData.refreshVariables();
			}
			config = new Configuration();
			/*StringBuilder templateSource = new StringBuilder();
			List<String> lines = IOUtils.readLines(resources.getInputStream("appContext.ftl"));
			for (String line : lines) {
				templateSource.append(line).append("\r\n");
			}
			StringTemplateLoader tl = new StringTemplateLoader();
			tl.putTemplate("appContext.ftl", templateSource.toString());*/
			
			URLTemplateLoader tl = new URLTemplateLoader() {
				@Override
				protected URL getURL(String name) {
					try {
						return resources.getURL(name);						
					} catch (Exception e) {
						return null;
					}
				}
			};
			
			config.setTemplateLoader(tl);
			DatabaseType dbType = DatabaseType.valueOf(InstallUtils.getVariable(installData,"dbType"));
			
			String jdbcUrl = InstallUtils.getVariable(installData,"dbUrl");
			attributes.put("dbType", dbType);
			attributes.put("jdbcUrl", jdbcUrl);
			attributes.put("dbUser", InstallUtils.getVariable(installData,"dbUser"));
			attributes.put("dbPassword", InstallUtils.getVariable(installData,"dbPassword"));
			attributes.put("ctxPath", InstallUtils.getVariable(installData,"ctxPath"));
			attributes.put("maxActive", InstallUtils.getVariable(installData,"maxActive"));
			attributes.put("maxIdle", InstallUtils.getVariable(installData,"maxIdle"));
			attributes.put("maxWait", InstallUtils.getVariable(installData,"maxWait"));
			inited = true;
		}
	}

	public void initialise() {

	}

	public void beforePacks(List<Pack> packs) {
		// TODO Auto-generated method stub

	}

	public void beforePack(Pack pack, int index) {
		// TODO Auto-generated method stub

	}

	public void afterPack(Pack pack, int index) {
		if (pack.getInstallGroups().contains("addWar")) {
			try {
				init();
				File webapps = new File(installPath + "/tomcat/webapps/");
				File[] warFiles = webapps.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".war");
					}
				});
				if(null==warFiles || warFiles.length==0){
					File[] eams = webapps.listFiles(new FilenameFilter() {
						public boolean accept(File file, String name) {
							return file.isDirectory() && name.equals("eams");
						}
					});
					if(null!=eams && eams.length>0){
						eams[0].renameTo(new File(webapps.getAbsolutePath()+"/"+appContextPath));
						File js16R7 = new File(eams[0].getAbsolutePath()+"/WEB-INF/lib/js-1.6R7.jar");
						if(js16R7.exists()){
							js16R7.delete();
						}
					}
				}else{
					warFiles[0].renameTo(new File(webapps.getAbsolutePath() + "/" + appContextPath + ".war"));					
				}
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else if(pack.getInstallGroups().contains("updateAppCtxConfig")){
			try {
				init();
				File file = new File(installPath + "/tomcat/conf/Catalina/localhost/" + appContextPath + ".xml");
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter writer = new FileWriter(file);
				Template t = config.getTemplate("appContext.ftl");
				t.process(attributes, writer);
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	public void afterPacks(List<Pack> packs, ProgressListener listener) {
		
	}

	public void beforeDir(File dir, PackFile packFile, Pack pack) {
		// TODO Auto-generated method stub

	}

	public void afterDir(File dir, PackFile packFile, Pack pack) {
		// TODO Auto-generated method stub

	}

	public boolean isFileListener() {
		// TODO Auto-generated method stub
		return false;
	}

	public void beforeFile(File file, PackFile packFile, Pack pack) {
		// TODO Auto-generated method stub

	}

	public void afterFile(File file, PackFile packFile, Pack pack) {

	}

	public void afterInstallerInitialization(AutomatedInstallData data) throws Exception {
		// TODO Auto-generated method stub

	}

	public void beforePacks(AutomatedInstallData data, Integer packs, AbstractUIProgressHandler handler) throws Exception {
		// TODO Auto-generated method stub

	}

	public void beforePack(Pack pack, Integer i, AbstractUIProgressHandler handler) throws Exception {
		// TODO Auto-generated method stub

	}

	public void beforeDir(File dir, PackFile packFile) throws Exception {
		// TODO Auto-generated method stub

	}

	public void afterDir(File dir, PackFile packFile) throws Exception {
		// TODO Auto-generated method stub

	}

	public void beforeFile(File file, PackFile packFile) throws Exception {
		// TODO Auto-generated method stub

	}

	public void afterFile(File file, PackFile packFile) throws Exception {
		// TODO Auto-generated method stub

	}

	public void afterPack(Pack pack, Integer i, AbstractUIProgressHandler handler) throws Exception {
		// TODO Auto-generated method stub

	}

	public void afterPacks(AutomatedInstallData data, AbstractUIProgressHandler handler) throws Exception {
		// TODO Auto-generated method stub

	}
}
