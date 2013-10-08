package com.ekingstar.eams.installer.panels.finish;

import javax.swing.JCheckBox;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.data.UninstallDataWriter;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.finish.FinishPanel;

public class TomcatFinishPanel extends FinishPanel{
	private static final long serialVersionUID = -6222702475828718202L;

	protected JCheckBox startTomcatOnClose;
	
	public TomcatFinishPanel(Panel panel, InstallerFrame parent, GUIInstallData installData, Resources resources, UninstallDataWriter uninstallDataWriter, Log log) {
		super(panel, parent, installData, resources, uninstallDataWriter, log);
	}
}
