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

public class RecorderFacade implements RecorderFacadeInterface {
	private static final String LOG_FILE_NAME = "log";
	private static final String WORKSPACE_CONFIG_FILENAME = "config";
	private static final String INSTALLATION_CONFIG_FILENAME = "config-install";

	private Properties workspaceProperties;
	private Properties installationProperties;

	private ClientRecorder clientRecorder;
	private Uninstaller uninstaller;
	private LoggerInterface copeLogger;
	private String workspaceDirectory;
	
	private boolean isFirstStart = false;
	private StorageManager storageManager;
	private ChangePersister changePersister;

	public RecorderFacade(StorageManager manager, String IDE) {
		this.storageManager = manager;
		
		initLogger();
		
		workspaceDirectory = manager.getLocalStorage().getAbsolutePath();
		
		if (!isWorkspaceKnown()) {
			isFirstStart = true;
			getToKnowWorkspace();
		}
		
		initFileLogging(workspaceDirectory);
		
		initProperties(workspaceDirectory, manager.getBundleStorage().getAbsolutePath());
		initUninstaller();
		initPersister(manager.getVersionedLocalStorage().getAbsolutePath());
		initClientRecorder(IDE);
	}

	private void initFileLogging(String rootDirectory) {
		initLogger();
		copeLogger.enableFileLogging(rootDirectory, LOG_FILE_NAME);
	}

	private void initLogger() {
		copeLogger = COPELogger.getInstance();
		//copeLogger = new ConsoleLogger();
		
		// copeLogger.logOnlyErrors();
		copeLogger.logEverything();
		copeLogger.disableConsoleLogging();
	}

	private void initClientRecorder(String IDE) {
		clientRecorder = new ClientRecorder(changePersister);
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
		
		changePersister = new ChangePersister(eventFileProvider);
	}

	/* (non-Javadoc)
	 * @see edu.oregonstate.cope.clientRecorder.RecorderFacadeInterface#getClientRecorder()
	 */
	@Override
	public ClientRecorder getClientRecorder() {
		return clientRecorder;
	}

	/* (non-Javadoc)
	 * @see edu.oregonstate.cope.clientRecorder.RecorderFacadeInterface#getWorkspaceProperties()
	 */
	@Override
	public Properties getWorkspaceProperties() {
		return workspaceProperties;
	}

	/* (non-Javadoc)
	 * @see edu.oregonstate.cope.clientRecorder.RecorderFacadeInterface#getInstallationProperties()
	 */
	@Override
	public Properties getInstallationProperties() {
		return installationProperties;
	}

	/* (non-Javadoc)
	 * @see edu.oregonstate.cope.clientRecorder.RecorderFacadeInterface#getUninstaller()
	 */
	@Override
	public Uninstaller getUninstaller() {
		return uninstaller;
	}

	/* (non-Javadoc)
	 * @see edu.oregonstate.cope.clientRecorder.RecorderFacadeInterface#getLogger()
	 */
	@Override
	public LoggerInterface getLogger() {
		return copeLogger;
	}

	/* (non-Javadoc)
	 * @see edu.oregonstate.cope.clientRecorder.RecorderFacadeInterface#getInstallationConfigFilename()
	 */
	@Override
	public String getInstallationConfigFilename() {
		return INSTALLATION_CONFIG_FILENAME;
	}

	/* (non-Javadoc)
	 * @see edu.oregonstate.cope.clientRecorder.RecorderFacadeInterface#getWorkspaceID()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see edu.oregonstate.cope.clientRecorder.RecorderFacadeInterface#getWorkspaceIdFile()
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see edu.oregonstate.cope.clientRecorder.RecorderFacadeInterface#isFirstStart()
	 */
	@Override
	public boolean isFirstStart() {
		return isFirstStart;
	}
	
	/* (non-Javadoc)
	 * @see edu.oregonstate.cope.clientRecorder.RecorderFacadeInterface#getStorageManager()
	 */
	@Override
	public StorageManager getStorageManager(){
		return storageManager;
	}

}
