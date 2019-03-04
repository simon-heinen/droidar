package system;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera.Parameters;

public class CameraViewForOldDevices extends CameraView {

	public CameraViewForOldDevices(Context context) {
		super(context);
	}

	@Override
	public void setPreviewAccordingToScreenOrientation(int width, int height) {
		Parameters p = myCamera.getParameters();
		int orientation = getContext().getResources().getConfiguration().orientation;
		System.out.println("orientation=" + orientation);
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			p.set("orientation", "portrait");
			p.set("rotation", 90);
			p.setPreviewSize(height, width);
			myCamera.setParameters(p);
		}
	}

}
