package edu.oregonstate.cope.fileSender;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import edu.oregonstate.cope.clientRecorder.util.LoggerInterface;

public class DeleteOldFilesUtil {

	private static final String ZIP_LIBS_REGEX = ".*\\.zip(-libs)?";
	private static final String EVENTFILE_REGEX = ".*" + File.separator + "eventFiles" + File.separator + ".*";
	private String rootPath;
	private LoggerInterface logger;

	public DeleteOldFilesUtil(String rootPath, LoggerInterface copeLogger) {
		this.rootPath = rootPath;
		this.logger = copeLogger;
	}

	public void deleteFilesOlderThanNdays(int daysBack, Date referenceDate) {

		Calendar calendarDate = Calendar.getInstance();
		calendarDate.setTime(referenceDate);
		calendarDate.add(Calendar.DAY_OF_MONTH, -daysBack);

		long purgeTime = calendarDate.getTimeInMillis();

		File rootDirectory = new File(rootPath);

		deleteFilesInDirByPattern(rootDirectory, EVENTFILE_REGEX, purgeTime);

		deleteFilesInDirByPattern(rootDirectory, ZIP_LIBS_REGEX, purgeTime);
	}

	public void deleteFilesInDirByPattern(File dir, final String pattern, final long purgeTime) {
		try {
			Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {

					File file = path.toFile();

					boolean nameMatches = file.getAbsolutePath().matches(pattern);
					boolean isOld = file.lastModified() < purgeTime;

					if (isOld && nameMatches) {
						try {
							FileUtils.forceDelete(file);
						} catch (Exception e) {
							logger.error(this, "Unable to delete file: " + file);
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			logger.error(this, e.getMessage(), e);
		}
	}
}
