package logger;

import android.util.Log;

public class ARLogger {
	
	private static String LOG_TAG = "AugmentedReality";
	private static enum MessageType {
		INFO,
		DEBUG,
		WARNING,
		ERROR,
		EXCEPTION,
	};
	
	public static void debug(String message){
		debug("",message);
	}
	
	public static void debug(String id,String message){
			logMessage(MessageType.DEBUG,affixId(id,message),null);
	}
	
	public static void error(String message){
		error("",message);
	}
	
	public static void error(String id,String message){
		logMessage(MessageType.ERROR,affixId(id,message),null);
	}
	
	public static void exception(String message, Exception exception){
		exception("",message,exception);
	}
	
	public static void exception(String id,String message, Exception exception){
		logMessage(MessageType.EXCEPTION,affixId(id,message),exception);
	}
	
	public static void info(String message){
		info("",message);
	}
	
	public static void info(String id, String message){
		logMessage(MessageType.INFO,affixId(id,message),null);
	}
	
	public static void warn(String message){
		warn("",message);
	}
	
	public static void warn(String id,String message){
		logMessage(MessageType.WARNING,affixId(id,message),null);
	}
	
	public static String affixId(String id,String message){
		if(id.equals(""))
			return message;
		else
			return "[" + id + "]: " + message;
	}
	
	private static void logMessage(MessageType type,String message, Exception exception){
		switch(type){
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
