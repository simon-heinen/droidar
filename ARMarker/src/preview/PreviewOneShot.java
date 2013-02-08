package preview;

import markerDetection.DetectionThread;
import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

public class PreviewOneShot extends Preview {
	@SuppressWarnings("unused")
	private byte[] b0;
	@SuppressWarnings("unused")
	private byte[] b1;
	@SuppressWarnings("unused")
	private byte[] b2;
	@SuppressWarnings("unused")
	private byte[] b3;
	@SuppressWarnings("unused")
	private byte[] b4;
	@SuppressWarnings("unused")
	private byte[] b5;
	@SuppressWarnings("unused")
	private byte[] b6;
	@SuppressWarnings("unused")
	private byte[] b7;
	@SuppressWarnings("unused")
	private byte[] b8;
	@SuppressWarnings("unused")
	private byte[] b9;
	@SuppressWarnings("unused")
	private byte[] b10;
	@SuppressWarnings("unused")
	private byte[] b11;
	@SuppressWarnings("unused")
	private byte[] b12;
	@SuppressWarnings("unused")
	private byte[] b13;
	@SuppressWarnings("unused")
	private byte[] b14;
	@SuppressWarnings("unused")
	private byte[] b15;
	@SuppressWarnings("unused")
	private byte[] b16;
	@SuppressWarnings("unused")
	private byte[] b17;
	@SuppressWarnings("unused")
	private byte[] b18;
	@SuppressWarnings("unused")
	private byte[] b19;
	@SuppressWarnings("unused")
	private byte[] b20;
	@SuppressWarnings("unused")
	private byte[] b21;
	@SuppressWarnings("unused")
	private byte[] b22;
	@SuppressWarnings("unused")
	private byte[] b23;
	@SuppressWarnings("unused")
	private byte[] b24;
	@SuppressWarnings("unused")
	private byte[] b25;
	@SuppressWarnings("unused")
	private byte[] b26;
	@SuppressWarnings("unused")
	private byte[] b27;
	@SuppressWarnings("unused")
	private byte[] b28;
	@SuppressWarnings("unused")
	private byte[] b29;
	@SuppressWarnings("unused")
	private byte[] b30;
	@SuppressWarnings("unused")
	private byte[] b31;
	@SuppressWarnings("unused")
	private byte[] b32;
	@SuppressWarnings("unused")
	private byte[] b33;
	@SuppressWarnings("unused")
	private byte[] b34;
	@SuppressWarnings("unused")
	private byte[] b35;
	@SuppressWarnings("unused")
	private byte[] b36;
	@SuppressWarnings("unused")
	private byte[] b37;
	@SuppressWarnings("unused")
	private byte[] b38;
	@SuppressWarnings("unused")
	private byte[] b39;
	@SuppressWarnings("unused")
	private byte[] b40;
	@SuppressWarnings("unused")
	private byte[] b41;
	@SuppressWarnings("unused")
	private byte[] b42;
	@SuppressWarnings("unused")
	private byte[] b43;
	@SuppressWarnings("unused")
	private byte[] b44;
	@SuppressWarnings("unused")
	private byte[] b45;
	@SuppressWarnings("unused")
	private byte[] b46;
	@SuppressWarnings("unused")
	private byte[] b47;
	@SuppressWarnings("unused")
	private byte[] b48;
	@SuppressWarnings("unused")
	private byte[] b49;
	private int index;

	public PreviewOneShot(Context context, DetectionThread thread,
			Camera.Size size) {
		super(context, thread, size);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		w = optimalSize.width;
		h = optimalSize.height;
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPreviewSize(w, h);
		mCamera.setParameters(parameters);

		Log.d("AR", "Preview size: " + optimalSize.width + "x"
				+ optimalSize.height);
		if (first) {
			myThread.setImageSizeAndRun(h,w);
			first = false;
		}
		mCamera.setOneShotPreviewCallback(this);
		mCamera.startPreview();
	}

	public void onPreviewFrame(byte[] data, Camera camera) {
		if (index < 10) {
			if (index == 0)
				b0 = data;
			if (index == 1)
				b1 = data;
			if (index == 2)
				b2 = data;
			if (index == 3)
				b3 = data;
			if (index == 4)
				b4 = data;
			if (index == 5)
				b5 = data;
			if (index == 6)
				b6 = data;
			if (index == 7)
				b7 = data;
			if (index == 8)
				b8 = data;
			if (index == 9)
				b9 = data;
		} else if (index < 20) {
			if (index == 10)
				b10 = data;
			if (index == 11)
				b11 = data;
			if (index == 12)
				b12 = data;
			if (index == 13)
				b13 = data;
			if (index == 14)
				b14 = data;
			if (index == 15)
				b15 = data;
			if (index == 16)
				b16 = data;
			if (index == 17)
				b17 = data;
			if (index == 18)
				b18 = data;
			if (index == 19)
				b19 = data;
		} else if (index < 30) {
			if (index == 20)
				b20 = data;
			if (index == 21)
				b21 = data;
			if (index == 22)
				b22 = data;
			if (index == 23)
				b23 = data;
			if (index == 24)
				b24 = data;
			if (index == 25)
				b25 = data;
			if (index == 26)
				b26 = data;
			if (index == 27)
				b27 = data;
			if (index == 28)
				b28 = data;
			if (index == 29)
				b29 = data;
		} else if (index < 40) {
			if (index == 30)
				b30 = data;
			if (index == 31)
				b31 = data;
			if (index == 32)
				b32 = data;
			if (index == 33)
				b33 = data;
			if (index == 34)
				b34 = data;
			if (index == 35)
				b35 = data;
			if (index == 36)
				b36 = data;
			if (index == 37)
				b37 = data;
			if (index == 38)
				b38 = data;
			if (index == 39)
				b39 = data;
		} else if (index < 50) {
			if (index == 40)
				b40 = data;
			if (index == 41)
				b41 = data;
			if (index == 42)
				b42 = data;
			if (index == 43)
				b43 = data;
			if (index == 44)
				b44 = data;
			if (index == 45)
				b45 = data;
			if (index == 46)
				b46 = data;
			if (index == 47)
				b47 = data;
			if (index == 48)
				b48 = data;
			if (index == 49)
				b49 = data;
		}
		index++;
		if (index == 50) {
			b0 = null;
			b1 = null;
			b2 = null;
			b3 = null;
			b4 = null;
			b5 = null;
			b6 = null;
			b7 = null;
			b8 = null;
			b9 = null;
			b10 = null;
			b11 = null;
			b12 = null;
			b13 = null;
			b14 = null;
			b15 = null;
			b16 = null;
			b17 = null;
			b18 = null;
			b19 = null;
			b20 = null;
			b21 = null;
			b22 = null;
			b23 = null;
			b24 = null;
			b25 = null;
			b26 = null;
			b27 = null;
			b28 = null;
			b29 = null;
			b30 = null;
			b31 = null;
			b32 = null;
			b33 = null;
			b34 = null;
			b35 = null;
			b36 = null;
			b37 = null;
			b38 = null;
			b39 = null;
			b40 = null;
			b41 = null;
			b42 = null;
			b43 = null;
			b44 = null;
			b45 = null;
			b46 = null;
			b47 = null;
			b48 = null;
			b49 = null;
			index = 0;
		}

		if (myThread.busy == false) {
			myThread.nextFrame(data);

		}

	}

	@Override
	public void reAddCallbackBuffer(byte[] data) {
		if(!paused){
			mCamera.setOneShotPreviewCallback(this);
		}
	}
}
