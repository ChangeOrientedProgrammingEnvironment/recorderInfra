package edu.oregonstate.cope.clientRecorder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import edu.oregonstate.cope.clientRecorder.fileOps.EventFilesProvider;
import edu.oregonstate.cope.clientRecorder.fileOps.SimpleFileProvider;
import edu.oregonstate.cope.clientRecorder.util.COPELogger;
import edu.oregonstate.cope.clientRecorder.util.LoggerInterface;

public class RecorderFacade {
	private static final String LOG_FILE_NAME = "log";
	private static final String WORKSPACE_CONFIG_FILENAME = "config";
	private static final String INSTALLATION_CONFIG_FILENAME = "config-install";

	private static class Instance {
		public static final RecorderFacade _instance = new RecorderFacade();
	}

	private Properties workspaceProperties;
	private Properties installationProperties;

	private ClientRecorder clientRecorder;
	private Uninstaller uninstaller;
	private LoggerInterface copeLogger;
	private String workspaceDirectory;

	private RecorderFacade() {
		initLogger();
	}

	public RecorderFacade initialize(StorageManager manager, String IDE) {
		workspaceDirectory = manager.getLocalStorage().getAbsolutePath();
		
		initFileLogging(workspaceDirectory);
		
		initProperties(workspaceDirectory, manager.getBundleStorage().getAbsolutePath());
		initUninstaller();
		initPersister(manager.getVersionedLocalStorage().getAbsolutePath());
		initClientRecorder(IDE);
		
		return this;
	}

	private void initFileLogging(String rootDirectory) {
		initLogger();
		copeLogger.enableFileLogging(rootDirectory, LOG_FILE_NAME);
	}

	private void initLogger() {
		copeLogger = new COPELogger();
		//copeLogger = new ConsoleLogger();
		
		// copeLogger.logOnlyErrors();
		copeLogger.logEverything();
		copeLogger.disableConsoleLogging();
	}

	private void initClientRecorder(String IDE) {
		clientRecorder = new ClientRecorder();
		clientRecorder.setIDE(IDE);
	}

	private void initUninstaller() {
		uninstaller = new Uninstaller(installationProperties);
	}

	private void initProperties(String workspaceDirectory, String permanentDirectory) {
		workspaceProperties = createProperties(workspaceDirectory, WORKSPACE_CONFIG_FILENAME);
		installationProperties = createProperties(permanentDirectory, INSTALLATION_CONFIG_FILENAME);
	}

	private Properties createProperties(String rootDirectory, String fileName) {
		SimpleFileProvider configFileProvider = new SimpleFileProvider(fileName);
		configFileProvider.setRootDirectory(rootDirectory);
		return new Properties(configFileProvider);
	}

	private void initPersister(String rootDirectory) {
		EventFilesProvider eventFileProvider = new EventFilesProvider();
		eventFileProvider.setRootDirectory(rootDirectory);
		ChangePersister.instance().setFileManager(eventFileProvider);
	}

	public ClientRecorder getClientRecorder() {
		return clientRecorder;
	}

	public Properties getWorkspaceProperties() {
		return workspaceProperties;
	}

	public Properties getInstallationProperties() {
		return installationProperties;
	}

	public Uninstaller getUninstaller() {
		return uninstaller;
	}

	public LoggerInterface getLogger() {
		return copeLogger;
	}

	public static RecorderFacade instance() {
		return Instance._instance;
	}

	public String getInstallationConfigFilename() {
		return INSTALLATION_CONFIG_FILENAME;
	}

	public String getWorkspaceID() {
		File workspaceIdFile = getWorkspaceIdFile();
		String workspaceID = "";
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(workspaceIdFile));
			workspaceID = reader.readLine();
			reader.close();
		} catch (IOException e) {
		}
		return workspaceID;
	}

	public File getWorkspaceIdFile() {
		return new File(workspaceDirectory + File.separator + "workspace_id");
	}
	
	protected void getToKnowWorkspace() {
		try {
			File workspaceIdFile = getWorkspaceIdFile();
			workspaceIdFile.createNewFile();
			String workspaceID = UUID.randomUUID().toString();
			BufferedWriter writer = new BufferedWriter(new FileWriter(workspaceIdFile));
			writer.write(workspaceID);
			writer.close();
		} catch (IOException e) {
			getLogger().error(this, e.getMessage(), e);
		}
	}
	
	protected boolean isWorkspaceKnown() {
		File workspaceIdFile = getWorkspaceIdFile();
		return workspaceIdFile.exists();
	}

}
