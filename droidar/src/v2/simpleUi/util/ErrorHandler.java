package v2.simpleUi.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import v2.simpleUi.M_Button;
import v2.simpleUi.M_Caption;
import v2.simpleUi.M_Checkbox;
import v2.simpleUi.M_Container;
import v2.simpleUi.M_InfoText;
import v2.simpleUi.M_TextInput;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Register the {@link ErrorHandler} like this: </br>
 * Thread.setDefaultUncaughtExceptionHandler(new ErrorHandler(currentActivity));
 * </br></br>
 * 
 * Or use the {@link ErrorHandler#registerNewErrorHandler(Activity)}
 * method.</br></br>
 * 
 * To add email support, call
 * {@link ErrorHandler#enableEmailReports(String, String)} </br></br>
 * 
 * And dont forget to add the ErrorReports.xml Layout file to your res/layout
 * folder!</br></br>
 * 
 * The ErrorHandler has to be registered in the AndroidManifest.xml like this:
 * 
 * <p class=MsoNormal style='margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal;mso-layout-grid-align:none;text-autospace:none'>
 * <span style='font-size: 10.0pt;font-family:"Courier New";color:teal;mso-ansi
 * -language:DE'>&lt;</span><span class=SpellE><span
 * style='font-size:10.0pt;font-family:"Courier New";
 * color:#3F7F7F;mso-ansi-language:DE'>activity</span></span><span
 * style='font-size:10.0pt;font-family:"Courier New";mso-ansi-language:DE'>
 * <span class=SpellE><span
 * style='color:#7F007F'>android:name</span></span><span
 * style='color:black'>=</span><i><span style='color:#2A00FF'>&quot;<span
 * class=SpellE>system.ErrorHandler</span>&quot;</span></i> <span
 * class=SpellE><span style='color:#7F007F'>android:process</span></span><span
 * style='color:black'>=</span><i><span style='color:#2A00FF'>&quot;:<span
 * class=SpellE>myexeptionprocess</span>&quot;</span></i><o:p></o:p></span>
 * </p>
 * 
 * <p class=MsoNormal style='margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal;mso-layout-grid-align:none;text-autospace:none'>
 * <span style='font-size:
 * 10.0pt;font-family:"Courier New";mso-ansi-language:DE'><span
 * style='mso-tab-count: 1'>      </span><span class=SpellE><span
 * style='color:#7F007F'>android:taskAffinity</span></span><span
 * style='color:black'>=</span><i><span style='color:#2A00FF'>&quot;<span
 * class=SpellE>tools.ErrorHandler</span>&quot;</span></i><span style='color:
 * teal'>&gt;</span><o:p></o:p></span>
 * </p>
 * 
 * <p class=MsoNormal style='margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal;mso-layout-grid-align:none;text-autospace:none'>
 * <span style='font-size:
 * 10.0pt;font-family:"Courier New";color:black;mso-ansi-language:DE'><span
 * style='mso-tab-count:1'>      </span></span><span style='font-size:10.0pt;
 * font-family:"Courier New";color:teal;mso-ansi-language :DE'>&lt;</span><span
 * class=SpellE><span style='font-size:10.0pt;font-family:"Courier New";
 * color:#3F7F7F;mso-ansi-language:DE'>intent</span></span><span
 * style='font-size: 10.0pt;font-family:"Courier New";color:#3F7F7F;mso-ansi-
 * language:DE'>-filter</span><span style='font-size:10.0pt;font-family:"Courier
 * New";color:teal;mso-ansi-language: DE'>&gt;</span><span
 * style='font-size:10.0pt;font-family:"Courier New";
 * mso-ansi-language:DE'><o:p></o:p></span>
 * </p>
 * 
 * <p class=MsoNormal style='margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal;mso-layout-grid-align:none;text-autospace:none'>
 * <span style='font-size:
 * 10.0pt;font-family:"Courier New";color:black;mso-ansi-language:DE'><span
 * style='mso-tab-count:2'>            </span></span><span
 * style='font-size:10.0pt;
 * font-family:"Courier New";color:teal;mso-ansi-language :DE'>&lt;</span><span
 * class=SpellE><span style='font-size:10.0pt;font-family:"Courier New";
 * color:#3F7F7F;mso-ansi-language:DE'>category</span></span><span
 * style='font-size:10.0pt;font-family:"Courier New";mso-ansi-language:DE'>
 * <span class=SpellE><span
 * style='color:#7F007F'>android:name</span></span><span
 * style='color:black'>=</span><i><span style='color:#2A00FF'>&quot;<span
 * class=SpellE>android.intent.category.DEFAULT</span>&quot;</span></i> <span
 * style='color:teal'>/&gt;</span><o:p></o:p></span>
 * </p>
 * 
 * <p class=MsoNormal style='margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal;mso-layout-grid-align:none;text-autospace:none'>
 * <span style='font-size:
 * 10.0pt;font-family:"Courier New";color:black;mso-ansi-language:DE'><span
 * style='mso-tab-count:1'>      </span><span style='mso-tab-count:1'>     
 * </span></span><span style='font-size:10.0pt;font-family:"Courier
 * New";color:teal;mso-ansi-language: DE'>&lt;</span><span class=SpellE><span
 * style='font-size:10.0pt;font-family: "Courier New";color
 * :#3F7F7F;mso-ansi-language:DE'>action</span></span><span
 * style='font-size:10.0pt;font-family:"Courier New";mso-ansi-language:DE'>
 * <span class=SpellE><span
 * style='color:#7F007F'>android:name</span></span><span
 * style='color:black'>=</span><i><span style='color:#2A00FF'>&quot;<span
 * class=SpellE>android.intent.action.VIEW</span>&quot;</span></i> <span
 * style='color:teal'>/&gt;</span><o:p></o:p></span>
 * </p>
 * 
 * <p class=MsoNormal style='margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal;mso-layout-grid-align:none;text-autospace:none'>
 * <span style='font-size:
 * 10.0pt;font-family:"Courier New";color:black;mso-ansi-language:DE'><span
 * style='mso-tab-count:1'>      </span><span style='mso-tab-count:1'>     
 * </span></span><span style='font-size:10.0pt;font-family:"Courier
 * New";color:teal;mso-ansi-language: DE'>&lt;</span><span class=SpellE><span
 * style='font-size:10.0pt;font-family:
 * "Courier New";color:#3F7F7F;mso-ansi-language:DE'>data</span></span><span
 * style='font-size:10.0pt;font-family:"Courier New";mso-ansi-language:DE'>
 * <span class=SpellE><span
 * style='color:#7F007F'>android:mimeType</span></span><span
 * style='color:black'>=</span><i><span style='color:#2A00FF'>&quot;<span
 * class=SpellE>errors</span>/<span
 * class=SpellE>myUnhandleCatcher</span>&quot;</span></i> <span
 * style='color:teal'>/&gt;</span><o:p></o:p></span>
 * </p>
 * 
 * <p class=MsoNormal style='margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal;mso-layout-grid-align:none;text-autospace:none'>
 * <span style='font-size:
 * 10.0pt;font-family:"Courier New";color:black;mso-ansi-language:DE'><span
 * style='mso-tab-count:1'>      </span></span><span style='font-size:10.0pt;
 * font-family:"Courier New";color:teal;mso-ansi-language :DE'>&lt;/</span><span
 * class=SpellE><span style='font-size:10.0pt;font-family:"Courier New";
 * color:#3F7F7F;mso-ansi-language:DE'>intent</span></span><span
 * style='font-size: 10.0pt;font-family:"Courier New";color:#3F7F7F;mso-ansi-
 * language:DE'>-filter</span><span style='font-size:10.0pt;font-family:"Courier
 * New";color:teal;mso-ansi-language: DE'>&gt;</span><span
 * style='font-size:10.0pt;font-family:"Courier New";
 * mso-ansi-language:DE'><o:p></o:p></span>
 * </p>
 * 
 * <p class=MsoNormal>
 * <span style='font-size:10.0pt;line-height:115%;font-family:
 * "Courier New";color:teal;mso-ansi-language:DE'>&lt;/</span><span
 * class=SpellE><span
 * style='font-size:10.0pt;line-height:115%;font-family:"Courier
 * New";color:#3F7F7F; mso-ansi-language:DE'>activity</span></span><span
 * style='font-size:10.0pt; line-height:115%;font-family:"Courier New";color:
 * teal;mso-ansi-language:DE'>&gt;</span>
 * </p>
 * 
 * @author Spobo
 * 
 */
public class ErrorHandler extends Activity implements UncaughtExceptionHandler {

	/**
	 * must be the same "x/y" string as in the AndroidManifest. </br></br>
	 * 
	 * 
	 * see {@link ErrorHandler} to understand where this is defined in the
	 * manifest
	 */
	public static String DATA_ANDROID_MIME_TYPE = "errors/myUnhandleCatcher";
	private static final String LOG_TAG = "ErrorHandler";
	private static final String PASSED_ERROR_TEXT_ID = "Error Text";
	private static final String DEV_MAIL_ID = "dev mail";
	private static final String MAIL_TITLE_ID = "title mail";
	private static final String PASSED_FILE_ID = "file path";

	private static Activity myCurrentActivity;
	private static UncaughtExceptionHandler defaultHandler;
	private static String myDeveloperMailAdress;
	private static String myMailSubject = "Error in DroidAR";

	/**
	 * DO NOT DELETE THIS CONSTURUCTOR!
	 * 
	 * use the {@link ErrorHandler#ErrorHandler(Activity) constructor instead}.
	 * This constructor is required by the Android system and the
	 * {@link ErrorHandler} can only work properly if a activity is provided, so
	 * only use this constructor if you call
	 * {@link ErrorHandler#setCurrentActivity(Activity)} later!
	 */
	@Deprecated
	// DO NOT DELETE THIS CONSTURUCTOR!
	public ErrorHandler() {
		if (defaultHandler == null) {
			defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		}
	}

	/**
	 * See {@link ErrorHandler} for details
	 * 
	 * @param a
	 */
	public ErrorHandler(Activity a) {
		setCurrentActivity(a);
		if (defaultHandler == null) {
			defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		}
	}

	public static void showErrorActivity(Activity a, Exception errorToShow,
			boolean keepBrokenProcessRunning) {
		showErrorActivity(a, throwableToString(errorToShow), null,
				keepBrokenProcessRunning);
	}

	/**
	 * @param a
	 * @param errorToShow
	 * @param optionalFilePathToSend
	 *            something like "file://"+"/sdcard/folderA/fileB.jpg"
	 * @param keepBrokenProcessRunning
	 */
	public static void showErrorActivity(Activity a, Exception errorToShow,
			String[] optionalFilePathToSend, boolean keepBrokenProcessRunning) {
		showErrorActivity(a, throwableToString(errorToShow),
				optionalFilePathToSend, keepBrokenProcessRunning);
	}

	public static String throwableToString(Throwable t) {
		if (t == null) {
			return "";
		}
		StringWriter sw = new StringWriter();
		PrintWriter p = new PrintWriter(sw);
		t.printStackTrace(p);
		String s = sw.toString();
		p.close();

		if (t.getCause() != null) {
			s += "\n\n Details about the cause for " + t + " was:\n"
					+ throwableToString(t.getCause());
		}

		return s;
	}

	public static void showErrorActivity(final Activity activity,
			final String errorText, String[] optionalFilePathsToSend,
			boolean keepBrokenProcessRunning) {
		if (activity != null) {
			myCurrentActivity = activity;
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.putExtra(PASSED_ERROR_TEXT_ID, errorText);
			i.putExtra(PASSED_FILE_ID, optionalFilePathsToSend);
			i.putExtra(DEV_MAIL_ID, myDeveloperMailAdress);
			i.putExtra(MAIL_TITLE_ID, myMailSubject);
			i.setType(DATA_ANDROID_MIME_TYPE);
			Log.e("ErrorHandler", "Starting from " + activity + " to "
					+ ErrorHandler.class);
			activity.startActivity(i);

			if (!keepBrokenProcessRunning) {
				/*
				 * After displaying the error in a new process the current
				 * process can be killed. This wont affect the
				 * ErrorHandler-activity because it is running in its own
				 * process (see AndroidManifest)
				 */
				activity.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(1);
			}

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String myErrorText = getIntent().getExtras().getString(
				PASSED_ERROR_TEXT_ID);
		String[] errorFiles = getIntent().getExtras().getStringArray(
				PASSED_FILE_ID);
		/*
		 * because this is a new process even the static fields will be reseted!
		 * the correct values can be restored by passing them in the intent
		 */
		myDeveloperMailAdress = getIntent().getExtras().getString(DEV_MAIL_ID);
		myMailSubject = getIntent().getExtras().getString(MAIL_TITLE_ID);
		setErrorContentView(this, myErrorText, errorFiles);
	}

	private static void setErrorContentView(final Activity a,
			String myErrorText, String[] errorFilePaths) {
		View v = loadModifier(a, myErrorText, myDeveloperMailAdress,
				addDebugInfosToErrorMessage(a), errorFilePaths);
		a.setContentView(v);
	}

	public static String mailText;
	private static String[] savedErrorFilePaths;
	private static boolean includeFiles = true;

	public static View loadModifier(final Activity a,
			final String exceptionText, String myDeveloperMailAdress,
			final String deviceDebugInformation, final String[] errorFilePaths) {

		savedErrorFilePaths = errorFilePaths;

		final M_Container c = new M_Container();
		if (myMailSubject != null && !myMailSubject.equals("")) {
			c.add(new M_Caption(myMailSubject));
		} else {
			c.add(new M_Caption("The application crashed"));
		}
		c.add(new M_InfoText(R.drawable.ic_dialog_alert,
				"We are sorry the application had a problem "
						+ "with your device. \n\n You can send "
						+ "the error to us so that we can fix this bug."));

		M_TextInput problemDescr = new M_TextInput(true, true, false) {

			@Override
			public boolean save(String newText) {
				mailText += "[" + getVarName() + "]\n" + newText;
				if (exceptionText != null) {
					mailText += "\n\n[Error Information]\n" + exceptionText;
				}
				return true;
			}

			@Override
			public String load() {
				return "";
			}

			@Override
			public String getVarName() {
				return "Your problem description";
			}
		};

		if (exceptionText != null && !exceptionText.equals("")) {
			problemDescr
					.setInfoText("You can add some information about the problem here..");
		} else {
			problemDescr.setInfoText("Write your problem down here...");
		}

		c.add(problemDescr);

		if (errorFilePaths != null) {
			c.add(new M_Checkbox() {

				@Override
				public boolean save(boolean newValue) {
					includeFiles = newValue;
					return true;
				}

				@Override
				public boolean loadVar() {
					return true;
				}

				@Override
				public CharSequence getVarName() {
					return "Include files to allow reconstructing the error";
				}
			});
		}

		if (myDeveloperMailAdress != null) {
			c.add(new M_Button("Send error mail to developers..") {

				@Override
				public void onClick(Context context, Button clickedButton) {
					mailText = ""; // clear mail text
					c.save();
					sendMail(a, mailText);
					a.finish();
				}
			});
		}

		if (exceptionText != null && !exceptionText.equals("")) {
			c.add(new M_TextInput(false, true, false) {

				@Override
				public boolean save(String newText) {
					// will never be called because editable flag is false
					return true;
				}

				@Override
				public String load() {
					return exceptionText;
				}

				@Override
				public String getVarName() {
					return "Error Information";
				}
			});
		}

		if (deviceDebugInformation != null) {
			c.add(new M_TextInput(true, true, true) {

				@Override
				public boolean save(String newText) {
					mailText += "\n\n\n[" + getVarName() + "]\n" + newText;
					return true;
				}

				@Override
				public String load() {
					return deviceDebugInformation;
				}

				@Override
				public String getVarName() {
					return "Device Information";
				}
			});
		}

		return c.getView(a);
	}

	public static void sendMail(Context context, String emailText) {
		// need to "send multiple" to get more than one attachment
		Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND_MULTIPLE);
		if (!includeFiles || savedErrorFilePaths == null
				|| savedErrorFilePaths.length == 1) {
			// if no files appended use the default intent type
			emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		}
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				myMailSubject);
		emailIntent.setType("*/*");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { myDeveloperMailAdress });
		emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
		if (includeFiles && savedErrorFilePaths != null) {
			// has to be an ArrayList
			ArrayList<Uri> uris = new ArrayList<Uri>();
			// convert from paths to Android friendly Parcelable Uri's
			for (int i = 0; i < savedErrorFilePaths.length; i++) {
				File fileIn = new File(savedErrorFilePaths[i]);
				Uri u = Uri.fromFile(fileIn);
				if (fileIn.exists()) {
					uris.add(u);
				}
			}
			emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		}
		context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

	private static String addDebugInfosToErrorMessage(Activity a) {

		String s = "";

		try {
			PackageInfo pInfo = a.getPackageManager().getPackageInfo(
					a.getPackageName(), PackageManager.GET_META_DATA);
			s += "\n APP Package Name: " + a.getPackageName();
			s += "\n APP Version Name: " + pInfo.versionName;
			s += "\n APP Version Code: " + pInfo.versionCode;
			s += "\n";
		} catch (NameNotFoundException e) {
		}

		s += "\n OS Version: " + System.getProperty("os.version") + " ("
				+ android.os.Build.VERSION.INCREMENTAL + ")";
		s += "\n OS API Level: " + android.os.Build.VERSION.SDK;
		s += "\n Device: " + android.os.Build.DEVICE;
		s += "\n Model (and Product): " + android.os.Build.MODEL + " ("
				+ android.os.Build.PRODUCT + ")";
		// TODO add application version!

		// more from
		// http://developer.android.com/reference/android/os/Build.html :
		s += "\n Manufacturer: " + android.os.Build.MANUFACTURER;
		s += "\n Other TAGS: " + android.os.Build.TAGS;

		s += "\n screenWidth: "
				+ a.getWindow().getWindowManager().getDefaultDisplay()
						.getWidth();
		s += "\n screenHeigth: "
				+ a.getWindow().getWindowManager().getDefaultDisplay()
						.getHeight();
		s += "\n Keyboard available: "
				+ (a.getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS);

		s += "\n Trackball available: "
				+ (a.getResources().getConfiguration().navigation == Configuration.NAVIGATION_TRACKBALL);
		s += "\n SD Card state: " + Environment.getExternalStorageState();

		Properties p = System.getProperties();
		Enumeration keys = p.keys();
		String key = "";
		while (keys.hasMoreElements()) {
			key = (String) keys.nextElement();
			s += "\n > " + key + " = " + (String) p.get(key);
		}

		return s;
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable ex) {
		Log.e(LOG_TAG, "A wild 'Uncaught exeption' appeares!");
		ex.printStackTrace();
		if (myCurrentActivity != null) {
			Log.e("ErrorHandler", "Starting error activity");
			showErrorActivity(myCurrentActivity, throwableToString(ex), null,
					false);
		} else {
			Log.e("ErrorHandler",
					"No current activity set -> error activity couldn't be started");
			defaultHandler.uncaughtException(thread, ex);
		}
	}

	public static void enableEmailReports(String developerEmailAdress,
			String emailTitle) {
		myDeveloperMailAdress = developerEmailAdress;
		myMailSubject = emailTitle;
	}

	public static void setCurrentActivity(Activity a) {
		myCurrentActivity = a;
	}

	/**
	 * @param a
	 * @param data_android_mimeType
	 *            something like 'errors/myUnhandleCatcher' <br>
	 *            set this value also in the manifest where the error handler
	 *            activity is registered
	 */
	public static void registerNewErrorHandler(Activity a,
			String data_android_mimeType) {
		registerNewErrorHandler(a);
		DATA_ANDROID_MIME_TYPE = data_android_mimeType;
	}

	/**
	 * use {@link ErrorHandler#registerNewErrorHandler(Activity, String)}
	 * instead
	 * 
	 * @param currentActivity
	 */
	@Deprecated
	public static void registerNewErrorHandler(Activity currentActivity) {
		Thread.setDefaultUncaughtExceptionHandler(new ErrorHandler(
				currentActivity));
	}

}
