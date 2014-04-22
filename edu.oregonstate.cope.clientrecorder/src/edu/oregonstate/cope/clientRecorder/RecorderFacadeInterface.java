package edu.oregonstate.cope.clientRecorder;

import java.io.File;

import edu.oregonstate.cope.clientRecorder.util.LoggerInterface;

public interface RecorderFacadeInterface {

	public ClientRecorder getClientRecorder();

	public Properties getWorkspaceProperties();

	public Properties getInstallationProperties();

	public Uninstaller getUninstaller();

	public LoggerInterface getLogger();

	public String getInstallationConfigFilename();

	public String getWorkspaceID();

	public File getWorkspaceIdFile();

	public boolean isFirstStart();

	public StorageManager getStorageManager();
}