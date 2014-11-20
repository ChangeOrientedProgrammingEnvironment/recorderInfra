package edu.oregonstate.cope.fileSender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import org.java_bandwidthlimiter.StreamManager;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import edu.oregonstate.cope.clientRecorder.util.COPELogger;

public class SFTPUploader {
	
	private ChannelSftp channelSftp = null;
	private Session session = null;
	
	private String host = "";
	private int port;
	private String username = "";
	private String password = "";
	
	private long uploadLimit = 200;
	private boolean shouldLimit = true;
	private StreamManager streamManager = null;
	
	private void initializeSession(String host, int port, String username, String password) throws UnknownHostException, JSchException {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		
		JSch jsch = new JSch();
		this.session = jsch.getSession(this.username, this.host, this.port);
		this.session.setPassword(this.password);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		this.session.setConfig(config);
		this.session.setServerAliveInterval(70000);
		this.session.connect();
		Channel channel = this.session.openChannel("sftp");
		channel.connect();
		this.channelSftp = (ChannelSftp) channel;
		
		streamManager = new StreamManager(uploadLimit * StreamManager.OneKbps * 8);
		streamManager.enable();
	}
	
	public SFTPUploader(String host, int port, String username, String password) throws UnknownHostException, JSchException {
		this.initializeSession(host, port, username, password);
	}
	
	public void upload(String localPath, String remotePath) throws FileNotFoundException, SftpException, JSchException {
		if(this.channelSftp != null) {
			this.uploadPathToFTP(localPath, remotePath);			
		}
	}
	
	public void createRemoteDir(String path) throws SftpException {
		String[] folders = path.split( java.util.regex.Pattern.quote(File.separator) );
		for ( String folder : folders ) {
		    if ( folder.length() > 0 ) {
		        try {
		            this.channelSftp.cd( folder );
		        }
		        catch ( SftpException e ) {
		        	this.channelSftp.mkdir( folder );
		            this.channelSftp.cd( folder );
		        }
		    }
		}
	}
	
	private void uploadPathToFTP(String localPath, String remotePath) throws FileNotFoundException, SftpException, JSchException {
		File[] files = new File(localPath).listFiles();

		for(File file : files) {
			if(file.isFile()) {
				InputStream fileInputStream = new FileInputStream(file);
				InputStream inputStream;
				if (shouldLimit) {
					inputStream = streamManager.registerStream(fileInputStream);
				} else {
					inputStream = fileInputStream;
				}
				this.channelSftp.put(inputStream, file.getName());
				try {
					fileInputStream.close();
				} catch (IOException e) {
					COPELogger.getInstance().error(this, "Error closing the input stream", e);
				}
			} else {	
				this.createRemoteDir(file.getName());
				this.uploadPathToFTP(localPath + File.separator + file.getName(),  file.getName());
				this.channelSftp.cd("..");
			}
		}
	}
}
