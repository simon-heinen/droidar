package gui;

import system.ActivityConnector;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import de.rwth.R;

/**
 * This screen can be used to display an introduction to the user when the AR
 * view is displayed.
 * 
 * TODO improve and integrate into smartui
 * 
 * @author Spobo
 * 
 */
public class InfoScreen extends Activity {

	protected static final long AUTO_CLOSE_TIME = 3000;
	private static final String LOG_TAG = "InfoScreen";
	private InfoScreenSettings myInfoSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.infoscreen);

		Object infos = ActivityConnector.getInstance()
				.loadObjFromNewlyCreatedActivity(this);

		if (infos instanceof InfoScreenSettings) {
			myInfoSettings = (InfoScreenSettings) infos;
			addContent((ScrollView) findViewById(R.id.infoScreenScrollview));
		}

	}

	private void addContent(ScrollView s) {
		try {
			InfoScreenSettings infos = getInfoScreenSettings();
			if (infos.backgroundColor != null)
				s.setBackgroundColor(infos.backgroundColor.toIntARGB());
			LinearLayout linLayBox = infos.getLinLayout();
			fixPossibleParentProblem(linLayBox);
			s.addView(linLayBox);
			if (!infos.closeInstantly()) {
				infos.getLinLayout().addView(newCloseButton(infos));
			} else {
				infos.getLinLayout().addView(newLoadingInfo(infos));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void fixPossibleParentProblem(LinearLayout linLayBox) {
		if (linLayBox.getParent() != null) {
			Log.e(LOG_TAG,
					"The lin.layout of the info screen already had a parent!");
			if (linLayBox.getParent() instanceof LinearLayout) {
				((LinearLayout) linLayBox.getParent()).removeView(linLayBox);
			}
		}
	}

	private InfoScreenSettings getInfoScreenSettings() {
		if (myInfoSettings == null) {
			Log.e(LOG_TAG,
					"The info settings where null, created dummy info settings");
			myInfoSettings = new InfoScreenSettings(getApplicationContext());
			myInfoSettings.setCloseInstantly();
		}
		return myInfoSettings;
	}

	private View newLoadingInfo(InfoScreenSettings infos) {
		TextView t = new TextView(this);
		t.setText(infos.getLoadingText());
		return t;
	}

	private View newCloseButton(InfoScreenSettings infos) {
		Button b = new Button(this);
		b.setText(infos.getCloseButtonText());
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InfoScreen.this.finish();
			}
		});
		return b;
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(AUTO_CLOSE_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (getInfoScreenSettings().closeInstantly())
					InfoScreen.this.finish();
			}
		}).start();
	}

}
