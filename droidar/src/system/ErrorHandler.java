package system;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Enumeration;
import java.util.Properties;

import util.Log;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.rwth.R;

/**
 * use the {@link tools.ErrorHandler} instead </br></br></br></br></br></br>
 * 
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
 * class=SpellE>system.ErrorHandler</span>&quot;</span></i><span style='color:
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
@Deprecated
public class ErrorHandler extends Activity implements UncaughtExceptionHandler {

	/**
	 * must be the same "x/y" string as in the AndroidManifest. </br></br>
	 * 
	 * 
	 * see {@link ErrorHandler} to understand where this is defined in the
	 * manifest
	 */
	public static final String DEFINED_TYPE = "errors/myUnhandleCatcher";

	private static Activity myCurrentActivity;
	private static UncaughtExceptionHandler defaultHandler;
	private static String myDeveloperMailAdress;
	private static String myMailSubject = "Error in DroidAR";
	private static final String PASSED_ERROR_TEXT_ID = "Error Text";
	private static final CharSequence ERROR_ACTIVITY_TITLE = "Error :(";
	private static final String DEV_MAIL = "dev mail";
	private static final String TITLE_MAIL = "title mail";

	private static final int ERROR_WINDOW_ID = R.layout.errorreports;
	private static final int ERROR_TEXT_VIEW_ID = R.id.errorText;
	private static final int ERROR_MAIL_BUTTON_ID = R.id.errorMailButton;
	private static final String LOG_TAG = "ErrorHandler";

	/**
	 * use the {@link ErrorHandler#ErrorHandler(Activity) constructor instead}.
	 * This constructor is required by the Android system and the
	 * {@link ErrorHandler} can only work properly if a activity is provided, so
	 * only use this constructor if you call
	 * {@link ErrorHandler#setCurrentActivity(Activity)} later!
	 */
	@Deprecated
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

	public static void showErrorLog(Activity a, Exception errorToShow,
			boolean keepBrokenProcessRunning) {
		showErrorActivity(a, throwableToString(errorToShow),
				keepBrokenProcessRunning);
	}

	public static String throwableToString(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter p = new PrintWriter(sw);
		t.printStackTrace(p);
		String s = sw.toString();
		p.close();
		return s;
	}

	private static void showErrorActivity(final Activity activity,
			final String errorText, boolean keepBrokenProcessRunning) {
		if (activity != null) {
			myCurrentActivity = activity;
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.putExtra(PASSED_ERROR_TEXT_ID, errorText);
			i.putExtra(DEV_MAIL, myDeveloperMailAdress);
			i.putExtra(TITLE_MAIL, myMailSubject);
			i.setType(DEFINED_TYPE);
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
		/*
		 * because this is a new process even the static fields will be reseted!
		 * the correct values can be restored by passing them in the intent
		 */
		myDeveloperMailAdress = getIntent().getExtras().getString(DEV_MAIL);
		myMailSubject = getIntent().getExtras().getString(TITLE_MAIL);
		loadErrorLayout(this, myErrorText);
	}

	private static void loadErrorLayout(Activity a, String myErrorText) {
		a.setContentView(ERROR_WINDOW_ID);
		a.setTitle(ERROR_ACTIVITY_TITLE);
		EditText myTextView = (EditText) a.findViewById(ERROR_TEXT_VIEW_ID);
		myErrorText = addDebugInfosToErrorMessage(a, myErrorText);
		if (myTextView != null && myErrorText != null) {
			myTextView.setText(myErrorText);
		}

		if (myDeveloperMailAdress != null) {
			enableMailButton(a, myTextView);
		}
	}

	private static void enableMailButton(final Activity a,
			final EditText myTextView) {
		Button b = (Button) a.findViewById(ERROR_MAIL_BUTTON_ID);
		b.setVisibility(View.VISIBLE);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMail(a, myTextView);
			}
		});
	}

	private static void sendMail(Activity a, EditText myTextView) {
		final Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { myDeveloperMailAdress });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				myMailSubject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
				myTextView.getText());
		a.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

	private static String addDebugInfosToErrorMessage(Activity a, String s) {
		s += "\n \n <Debug Infos>";
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

		s += " \n \n [More automatically received system infos]:";
		Properties p = System.getProperties();
		Enumeration keys = p.keys();
		String key = "";
		while (keys.hasMoreElements()) {
			key = (String) keys.nextElement();
			s += "\n > " + key + " = " + (String) p.get(key);
		}

		s += " \n \n [You can add a description of what you were doing here]:";
		s += " \n ...";
		return s;
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable ex) {
		Log.e(LOG_TAG, "A wild 'Uncaught exeption' appeares!");
		// Log.e(LOG_TAG, "Error=" + ex.toString());
		ex.printStackTrace();
		if (myCurrentActivity != null) {
			Log.e("ErrorHandler", "Starting error activity");
			showErrorActivity(myCurrentActivity, throwableToString(ex), false);
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

	public static void registerNewErrorHandler(Activity currentActivity) {
		Thread.setDefaultUncaughtExceptionHandler(new ErrorHandler(
				currentActivity));
	}
}
