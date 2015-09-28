package edu.oregonstate.cope.fileSender;

import java.io.File;

import edu.oregonstate.cope.clientRecorder.Properties;
import edu.oregonstate.cope.clientRecorder.util.LoggerInterface;

public class FileSenderParams {
	private LoggerInterface copeLogger;
	private File storageManager;
	private Properties workspaceProperties;
	private String workspaceID;

	public FileSenderParams(LoggerInterface copeLogger, File storageManager, Properties workspaceProperties, String workspaceID) {
		this.setWorkspaceID(workspaceID);
		this.setCopeLogger(copeLogger);
		this.setStorageManager(storageManager);
		this.setWorkspaceProperties(workspaceProperties);
	}

	public LoggerInterface getCopeLogger() {
		return copeLogger;
	}

	public void setCopeLogger(LoggerInterface copeLogger) {
		this.copeLogger = copeLogger;
	}

	public File getStorageManager() {
		return storageManager;
	}

	public void setStorageManager(File storageManager) {
		this.storageManager = storageManager;
	}

	public Properties getWorkspaceProperties() {
		return workspaceProperties;
	}

	public void setWorkspaceProperties(Properties workspaceProperties) {
		this.workspaceProperties = workspaceProperties;
	}

	public String getWorkspaceID() {
		return workspaceID;
	}

	public void setWorkspaceID(String workspaceID) {
		this.workspaceID = workspaceID;
	}
}
