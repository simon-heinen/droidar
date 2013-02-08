import tests.EfficientListTests;
import tests.SystemTests;
import tests.WorldTests;
import util.Log;
import util.Log.LogInterface;

public class DesktopTestRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			initLogging();
			new SystemTests().run();
			new EfficientListTests().run();
			new WorldTests().run();
			// new GameLogicTests().run();
			// new GeoTests().run();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Testing done");

	}

	/**
	 * Has to be done to disable logging when testing on the desktop and not on
	 * an android device because classes like the Android log are not available
	 * there
	 */
	private static void initLogging() {
		Log.setInstance(new LogInterface() {

			@Override
			public void w(String logTag, String logText) {

			}

			@Override
			public void v(String logTag, String logText) {

			}

			@Override
			public void i(String logTag, String logText) {

			}

			@Override
			public void e(String logTag, String logText) {

			}

			@Override
			public void d(String logTag, String logText) {

			}

			@Override
			public void print(String logText) {

			}
		});
	}

}
