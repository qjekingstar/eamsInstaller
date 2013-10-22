package com.ekingstar.eams.installer.listener;

import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;

import com.ekingstar.eams.installer.enumeration.DatabaseType;
import com.ekingstar.eams.installer.util.InstallUtils;
import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.data.AutomatedInstallData;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.Pack;
import com.izforge.izpack.api.data.PackFile;
import com.izforge.izpack.api.event.InstallerListener;
import com.izforge.izpack.api.event.ProgressListener;
import com.izforge.izpack.api.handler.AbstractUIProgressHandler;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.compiler.packager.IPackager;

public class SQLScriptExecuteListener implements InstallerListener {
    protected final InstallData installData;

    protected final Resources resources;
    
    private DataSource dataSource;
    
    private DatabaseType dbType;
    
    private String installPath;
    
	public SQLScriptExecuteListener(InstallData installData, Resources resources) {
		this.installData = installData;
		this.resources = resources;
	}
	
	private void init(){
		if(null==dataSource){
			installPath = installData.getInstallPath().replaceAll("\\\\", "/");
			if (installPath.endsWith("/")) {
				installPath = installPath.substring(0, installPath.length() - 1);
			}
			if("DEFAULT_DB".equals(InstallUtils.getVariable(installData, "dataInstallMode"))){
				InstallUtils.setVariable(installData,"dbType", "H2");
				InstallUtils.setVariable(installData,"dbName", "eams");
				InstallUtils.setVariable(installData,"dbUser", "eams");
				InstallUtils.setVariable(installData,"dbPassword", "eams");
				InstallUtils.setVariable(installData,"maxActive", "100");
				InstallUtils.setVariable(installData,"maxIdle", "30");
				InstallUtils.setVariable(installData,"maxWait", "1000");
				installData.refreshVariables();
			}
			
			dbType = DatabaseType.valueOf(InstallUtils.getVariable(installData,"dbType"));
			dataSource = new UnpooledDataSource(dbType.getDriverClassName(), InstallUtils.getVariable(installData,"dbUrl"), InstallUtils.getVariable(installData,"dbUser"), InstallUtils.getVariable(installData,"dbPassword"));			
		}
	}

	public void notify(String position, int state, IXMLElement data, IPackager packager) {

	}

	public void initialise() {
		// TODO Auto-generated method stub

	}

	public void beforePacks(List<Pack> packs) {

	}

	public void beforePack(Pack pack, int index) {
		
	}
	
	private boolean executeScript(File errorLogDir,int index,String scriptType){
		Reader scriptReader = null;
		try {
			scriptReader = new InputStreamReader(resources.getInputStream(dbType.toString().toLowerCase()+"."+scriptType+"."+index+".sql"));
		} catch (Exception e) {
			return false;
		}
		String logFileName = errorLogDir.getAbsolutePath()+"/"+index+".error.log";
		File logFile = new File(logFileName);
		if(!logFile.exists()){
			try {
				if(!logFile.createNewFile()){
					throw new RuntimeException("cannot create "+logFileName);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
		}
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			ScriptRunner runner = new ScriptRunner(conn);
			//执行整个sql脚本
			runner.setSendFullScript(false);
			runner.setDelimiter(";");
			//记录错误日志，mybatis会抛出RuntimeSqlException
		    runner.setErrorLogWriter(new PrintWriter(logFile));
		    //不记录日志
		    runner.setLogWriter(null);
		    //执行脚本
	    	/*Arrays.sort(scripts, new Comparator<File>() {
				public int compare(File o1, File o2) {
					String o1Version = StringUtils.substringBeforeLast(o1.getName(),".sql");
					String o2Version = StringUtils.substringBeforeLast(o2.getName(),".sql");
					return o1Version.compareTo(o2Version);
				}
			});*/
	    	runner.runScript(scriptReader);
	    	return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if(null!=conn){
				try {
					conn.close();									
				} catch (Exception e2) {
				}
			}
		}
	}
	
	private void executeScripts(final String scriptType) {
		init();
		File errorLogDir = new File(installPath+"/installLogs/"+dbType.toString().toLowerCase()+"/"+scriptType);
		if(!errorLogDir.exists()){
			if(!errorLogDir.mkdirs()){
				throw new RuntimeException("cannot create "+installPath+"/installLogs/"+dbType.toString().toLowerCase()+"/"+scriptType);
			}
		}
		int i=1;
		while (executeScript(errorLogDir,i, scriptType)) {
			i++;
		}
	}	

	public void afterPack(Pack pack, int index) {
		if(pack.getInstallGroups().contains("createTable")){
			executeScripts("create");
		}
		if(pack.getInstallGroups().contains("initDatas")){
			executeScripts("init");
		}
	}

	public void afterPacks(List<Pack> packs, ProgressListener listener) {

	}

	public void beforeDir(File dir, PackFile packFile, Pack pack) {

	}

	public void afterDir(File dir, PackFile packFile, Pack pack) {

	}

	public boolean isFileListener() {
		return false;
	}

	public void beforeFile(File file, PackFile packFile, Pack pack) {

	}

	public void afterFile(File file, PackFile packFile, Pack pack) {

	}

	public void afterInstallerInitialization(AutomatedInstallData data) throws Exception {

	}

	public void beforePacks(AutomatedInstallData data, Integer packs, AbstractUIProgressHandler handler) throws Exception {

	}

	public void beforePack(Pack pack, Integer i, AbstractUIProgressHandler handler) throws Exception {

	}

	public void beforeDir(File dir, PackFile packFile) throws Exception {

	}

	public void afterDir(File dir, PackFile packFile) throws Exception {

	}

	public void beforeFile(File file, PackFile packFile) throws Exception {

	}

	public void afterFile(File file, PackFile packFile) throws Exception {

	}

	public void afterPack(Pack pack, Integer i, AbstractUIProgressHandler handler) throws Exception {

	}

	public void afterPacks(AutomatedInstallData data, AbstractUIProgressHandler handler) throws Exception {

	}
	
	/*public static void main(String[] args) throws Exception {
		File file = new File("/home/qj/workspaces/eams/installer/env/install/sql/oracle/createTable.sql");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Map<String,String> tablenames = new LinkedHashMap<String,String>();
		
		String line = null;
		while(null!=(line=reader.readLine())){
			String prefix = ""; 
			if(line.startsWith("drop table ")){
				prefix = "drop table ";
			} else if(line.startsWith("drop sequence ")){
				prefix = "drop sequence ";
			} else if(line.startsWith("create table ")){
				prefix = "create table ";
			} else if(line.startsWith("alter table ")){
				prefix = "alter table ";
			} else if(line.startsWith("create sequence ")){
				prefix = "create sequence ";
			}
			String name = StringUtils.substringBefore(StringUtils.substringAfter(line, prefix), " ");
			tablenames.put(name.toLowerCase(),name);
		}
		for(String name: tablenames.values()){
			if(name.length()>30){
				System.out.println(name);
			}
		}
	}*/
	/*public static void main(String[] args) throws Exception {
		File file = new File("/home/qj/workspaces/eams/installer/env/install/sql/oracle/initDatas.sql");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while(null!=(line=reader.readLine())){
			if(!line.startsWith("values")){
				sb.append("\n");
			}
			sb.append(line);
		}
		FileUtils.write(file, sb.toString());
	}*/
}
