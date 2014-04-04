package edu.oregonstate.cope.fileSender;

import java.io.File;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;

import com.jcraft.jsch.JSchException;

import edu.oregonstate.cope.clientRecorder.Properties;
import edu.oregonstate.cope.clientRecorder.util.COPELogger;

public class FileSenderJob implements Job
{
	private static final String LAST_UPLOAD_DATE = "lastUploadDate";
	private static final String LAST_UPLOAD_DATE_FORMAT = "yyyy-MM-dd HH:mm";
			
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SchedulerContext schedulerContext;
		try {
			schedulerContext = context.getScheduler().getContext();
			FileSenderParams fileSenderParams = (FileSenderParams) schedulerContext.get("FileSenderParams");
			COPELogger logger = (COPELogger) fileSenderParams.getCopeLogger();
			FTPConnectionProperties ftpProperties = new FTPConnectionProperties(fileSenderParams);
			try {
				
				File storageManager = fileSenderParams.getStorageManager();
				Properties workspaceProperties = fileSenderParams.getWorkspaceProperties();
				String workspaceID = fileSenderParams.getWorkspaceID();
				
				SimpleDateFormat formatter = new SimpleDateFormat(LAST_UPLOAD_DATE_FORMAT);
				String lastUploadDateStr = workspaceProperties.getProperty(LAST_UPLOAD_DATE);
				if(lastUploadDateStr != null) {
					Date lastUploadDate = formatter.parse(lastUploadDateStr);
					// delete files created at least 2 days earlier before last upload date
					DeleteOldFilesUtil deleteUtil = new DeleteOldFilesUtil(storageManager.getAbsolutePath(), logger);
					deleteUtil.deleteFilesOlderThanNdays(2, lastUploadDate);
				}
				
				logger.info(this, "Connecting to host " + ftpProperties.getHost() + " ...");
				
				SFTPUploader uploader = new SFTPUploader(
					ftpProperties.getHost(), 
					ftpProperties.getUsername(), 
					ftpProperties.getPassword()
				);
				String localPath = storageManager.getAbsolutePath();
				// using eclipse workspace ID as a remote dir to store data
				String remotePath = "COPE" + File.separator + workspaceID;
				
				logger.info(this, "Sending files from " + localPath + " to " + ftpProperties.getHost() + ":" + remotePath + " ...");
				
				uploader.createRemoteDir(remotePath);
				uploader.upload(localPath, remotePath);
				workspaceProperties.addProperty(LAST_UPLOAD_DATE, formatter.format(new Date()));
				
				logger.info(this, "Upload finished");
				
			} catch (UnknownHostException | JSchException e) {
				logger.warning(FTPConnectionProperties.class, "Cannot connect to host: " + ftpProperties.getHost());
			} catch (Exception e) {
				logger.error(FTPConnectionProperties.class, e.getMessage(), e);
			} 
		} catch (SchedulerException e1) {
			e1.printStackTrace();
		}
	}
	
}