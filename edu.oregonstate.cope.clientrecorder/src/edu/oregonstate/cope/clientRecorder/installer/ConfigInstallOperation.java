package edu.oregonstate.cope.clientRecorder.installer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

public class ConfigInstallOperation extends InstallerOperation {

	@Override
	protected void doNoFileExists(File workspaceFile, File permanentFile) throws IOException {
		recorder.getUninstaller().initUninstallInMonths(3);

		Files.copy(permanentFile.toPath(), workspaceFile.toPath());
	}

	@Override
	protected String getFileName() {
		return recorder.getInstallationConfigFilename();
	}
	
	@Override
	protected void doBothFilesExists(File workspaceFile, File permanentFile) {
		super.doBothFilesExists(workspaceFile, permanentFile);
		
		 try {
			if (!FileUtils.contentEquals(workspaceFile, permanentFile)) {
				FileUtils.copyFile(permanentFile, workspaceFile);
			}
		} catch (IOException e) {
			recorder.getLogger().error(this, e.getMessage(), e);
		}
	}

}