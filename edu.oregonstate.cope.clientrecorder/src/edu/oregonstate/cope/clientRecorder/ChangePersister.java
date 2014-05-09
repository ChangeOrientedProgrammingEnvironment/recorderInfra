package edu.oregonstate.cope.clientRecorder;

import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import edu.oregonstate.cope.clientRecorder.fileOps.FileProvider;

/**
 * Defines and implements JSON event persistence format. A {@link FileProvider} must be
 * set in order for the ChangePersister to function.
 * 
 * <br>
 * This class is a Singleton.
 */
public class ChangePersister {

	private static final String SEPARATOR = "\n$@$";
	public static final Pattern ELEMENT_REGEX = Pattern.compile(Pattern.quote(SEPARATOR) + "(\\{.*?\\})");

	private FileProvider fileProvider;

	public ChangePersister(FileProvider fileProvider) {
		this.fileProvider = fileProvider;
	}

	// TODO This gets called on every persist. Maybe create a special
	// FileProvider that knows how to initialize things on file swap
	private void addInitEventIfAbsent() {
		if (fileProvider.isCurrentFileEmpty()) {
			JSONObject markerObject = createInitJSON();

			fileProvider.appendToCurrentFile(ChangePersister.SEPARATOR);
			fileProvider.appendToCurrentFile(markerObject.toJSONString());
		}
	}

	private JSONObject createInitJSON() {
		JSONObject markerObject = new JSONObject();
		markerObject.put("eventType", "FileInit");
		return markerObject;
	}

	public synchronized void persist(JSONObject jsonObject) throws RecordException {
		if (jsonObject == null) {
			throw new RecordException("Argument cannot be null");
		}

		addInitEventIfAbsent();

		fileProvider.appendToCurrentFile(ChangePersister.SEPARATOR);
		fileProvider.appendToCurrentFile(jsonObject.toJSONString());
	}
}
