package util;

/**
 * A wrapper for log-output. Useful to disable log output before shipping the
 * application (the debug information can still be collected internally and send
 * to the developer when an error appears)
 * 
 * @author Spobo
 * 
 */
public class Log {

	public interface LogInterface {

		void d(String logTag, String logText);

		void e(String logTag, String logText);

		void w(String logTag, String logText);

		void v(String logTag, String logText);

		void i(String logTag, String logText);

		void print(String logText);

	}

	/**
	 * @param callDepth
	 *            Normally 2 (or 1 if directly used). This method is calles from
	 *            a util class like a Log class you dont want the name of the
	 *            log method but the method which called the log method, so pass
	 *            2 as the call depth! (1 would be the Log class and 0 would be
	 *            the Thread.getStackTrace() method)
	 * @return
	 */
	public static String getCurrentMethod(int callDepth) {
		StackTraceElement x = Thread.currentThread().getStackTrace()[callDepth];
		return x.getClassName() + "." + x.getMethodName() + "(..): ("
				+ x.getClassName() + ".java:" + x.getLineNumber() + ")";
	}

	private static LogInterface instance;

	public static LogInterface getInstance() {
		if (instance == null)
			instance = newDefaultAndroidLog();
		return instance;
	}

	public static void setInstance(LogInterface instance) {
		Log.instance = instance;
	}

	private static LogInterface newDefaultAndroidLog() {
		return new LogInterface() {

			@Override
			public void w(String logTag, String logText) {
				android.util.Log.w(logTag, logText);
			}

			@Override
			public void v(String logTag, String logText) {
				android.util.Log.v(logTag, logText);
			}

			@Override
			public void i(String logTag, String logText) {
				android.util.Log.i(logTag, logText);
			}

			@Override
			public void e(String logTag, String logText) {
				android.util.Log.e(logTag, logText);
			}

			@Override
			public void d(String logTag, String logText) {
				android.util.Log.d(logTag, logText);
			}

			@Override
			public void print(String logText) {
				android.util.Log.d("Debug Output", logText);
			}
		};
	}

	public static void d(String logTag, String logText) {
		getInstance().d(logTag, logText);
	}

	public static void e(String logTag, String logText) {
		getInstance().e(logTag, logText);

	}

	public static void w(String logTag, String logText) {
		getInstance().w(logTag, logText);

	}

	public static void v(String logTag, String logText) {
		getInstance().v(logTag, logText);

	}

	public static void i(String logTag, String logText) {
		getInstance().i(logTag, logText);

	}

	public static void out(String logText) {
		getInstance().print(logText);
	}

	/**
	 * @param matrixName
	 * @param a
	 *            has to be a 4x4 matrix
	 * @return the debug string
	 */
	public static String floatMatrixToString(String matrixName, float[] a) {
		String s = "";
		s += "Matrix: " + matrixName + "\n";
		s += "\t " + a[0] + "," + a[1] + "," + a[2] + "," + a[3] + " \n";
		s += "\t " + a[4] + "," + a[5] + "," + a[6] + "," + a[7] + " \n";
		s += "\t " + a[8] + "," + a[9] + "," + a[10] + "," + a[11] + " \n";
		s += "\t " + a[12] + "," + a[13] + "," + a[14] + "," + a[15] + " \n";
		return s;
	}

}
