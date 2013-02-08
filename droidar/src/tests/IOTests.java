package tests;

import util.IO;
import util.IO.Settings;
import android.app.Activity;

public class IOTests extends SimpleTesting {

	private Activity myTargetActivity;

	public IOTests(Activity targetActivity) {
		myTargetActivity = targetActivity;
	}

	@Override
	public void run() throws Exception {
		settingsTest();
		serializableIOTest();
	}

	private void serializableIOTest() throws Exception {
		if (IO.getSDCard() != null) {
			String path = IO.getSDCard() + "/test.txt";
			String[] x = new String[4];
			x[2] = "aaa";
			x[3] = "bbb";
			IO.saveSerializable(path, x);
			String[] y = (String[]) IO.loadSerializable(path);
			assertTrue(null == y[0]);
			assertTrue(null == y[1]);
			assertEquals(x[2], y[2]);
			assertEquals(x[3], y[3]);
			assertEquals(x.length, y.length);
		}
	}

	private void settingsTest() throws Exception {
		Settings s = new IO.Settings(myTargetActivity, "testSettings");
		String stringKey = "skey";
		String stringValue = "svalue";
		s.storeString(stringKey, stringValue);
		assertTrue(stringValue.equals(s.loadString(stringKey)));
	}

}
