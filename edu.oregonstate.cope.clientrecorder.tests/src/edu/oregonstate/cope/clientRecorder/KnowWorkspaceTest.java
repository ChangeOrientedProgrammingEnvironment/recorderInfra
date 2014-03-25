package edu.oregonstate.cope.clientRecorder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class KnowWorkspaceTest {

	private final class TestStorageManager implements StorageManager {
		
		public TestStorageManager() {
			onlyFolder = new File("./outputFiles");
			onlyFolder.mkdir();
		}

		@Override
		public File getVersionedLocalStorage() {
			return onlyFolder;
		}

		@Override
		public File getVersionedBundleStorage() {
			return onlyFolder;
		}

		@Override
		public File getLocalStorage() {
			return onlyFolder;
		}

		@Override
		public File getBundleStorage() {
			return onlyFolder;
		}
		
		public void clean() {
			File[] files = onlyFolder.listFiles();
			for (File file : files) {
				if (file.getName().equals("workspace_id"))
					assertTrue(file.delete());
			}
		}
	}

	private File onlyFolder;
	private RecorderFacade recorder;
	private TestStorageManager testStorageManager;

	@Before
	public void setUp() {
		testStorageManager = new TestStorageManager();
		recorder = new RecorderFacade(testStorageManager, "");
	}
	
	@After
	public void tearDown() {
		testStorageManager.clean();
	}

	@Test
	public void testIDontKnowThisWorkspace() {
		File wid = new File("./outputFiles/workspace_id");
		if (wid.exists())
			assertTrue(wid.delete());
		assertFalse(recorder.isWorkspaceKnown());
	}

	@Test
	public void testKnowThisWorkspace() {
		recorder.getToKnowWorkspace();
		assertTrue(recorder.isWorkspaceKnown());
	}
}
