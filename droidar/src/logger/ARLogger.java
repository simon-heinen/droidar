package logger;

import android.util.Log;

/**
 * Logger Used by DroidAr Library. 
 *
 */
public final class ARLogger {
	
	private static String LOG_TAG = "AugmentedReality";
	
	/**
	 * Used to determine message what type of message is begin received.
	 */
	private static enum MessageType {
		VERBOSE,
		INFO,
		DEBUG,
		WARNING,
		ERROR,
		EXCEPTION,
	};
	
	private ARLogger() { }
	
	/**
	 * Create a debug message.
	 * @param message - message displayed
	 */
	public static void debug(String message) {
		debug("", message);
	}
	
	/**
	 * Create a debug message with an internal location.
	 * @param id - location or class name
	 * @param message - message displayed
	 */
	public static void debug(String id, String message) {
			logMessage(MessageType.DEBUG, affixId(id, message), null);
	}
	
	/**
	 * Create a error message.
	 * @param message - message displayed
	 */
	public static void error(String message) {
		error("", message);
	}
	
	/**
	 * Create a error message with an internal location.
	 * @param id - location or class name
	 * @param message - message displayed
	 */
	public static void error(String id, String message) {
		logMessage(MessageType.ERROR, affixId(id, message), null);
	}
	
	/**
	 * Create a exception message.
	 * @param message - message displayed
	 * @param exception - exception that will be displayed
	 */
	public static void exception(String message, Exception exception) {
		exception("", message, exception);
	}
	
	/**
	 * Create a exception message with an internal location.
	 * @param id - location or class name
	 * @param message - message displayed
	 * @param exception - exception that will be displayed
	 */
	public static void exception(String id, String message, Exception exception) {
		logMessage(MessageType.EXCEPTION, affixId(id, message), exception);
	}
	
	/**
	 * Create a info message.
	 * @param message - message displayed
	 */
	public static void info(String message) {
		info("", message);
	}
	
	/**
	 * Create a info message with an internal location.
	 * @param id - location or class name
	 * @param message - message displayed
	 */
	public static void info(String id, String message) {
		logMessage(MessageType.INFO, affixId(id, message), null);
	}
	
	/**
	 * Create a warning message.
	 * @param message - message displayed
	 */
	public static void warn(String message) {
		warn("", message);
	}
	
	/**
	 * Create a warning message with an internal location.
	 * @param id - location or class name
	 * @param message - message displayed
	 */
	public static void warn(String id, String message) {
		logMessage(MessageType.WARNING, affixId(id, message), null);
	}
	
	/**
	 * Create a verbose message with an internal location.
	 * @param id - location or class name
	 * @param message - message displayed
	 */
	public static void verbose(String id, String message) {
		logMessage(MessageType.WARNING, affixId(id, message), null);
	}

	
	private static String affixId(String id, String message) {
		if (id.equals("")) {
			return message;
		} else {
			return "[" + id + "]: " + message;
		}
	}
	
	private static void logMessage(MessageType type, String message, Exception exception) {
		switch(type) {
		case VERBOSE:
			Log.v(LOG_TAG, message);
			break;
		case INFO:
			Log.i(LOG_TAG, message);
			break;
		case DEBUG:
			Log.d(LOG_TAG, message);
			break;
		case WARNING:
			Log.w(LOG_TAG, message);
			break;
		case ERROR:
			Log.e(LOG_TAG, message);
			break;
		case EXCEPTION:
			Log.e(LOG_TAG, message, exception);
		default:
			break;
		}
	}
}
