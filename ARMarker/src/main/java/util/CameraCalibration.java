package util;

import java.io.Serializable;

public class CameraCalibration implements Serializable {

	private static final long serialVersionUID = 558935391234172163L;
	public double[] cameraMatrix;
	public double[] distortionMatrix;
	
	
	public CameraCalibration() {
		
	}
	
	public static CameraCalibration defaultCalib(int w, int h){
		CameraCalibration defaultCalib = new CameraCalibration();
		defaultCalib.cameraMatrix = new double[] {(double)w, 0, ((double)(w>>1)),
												  0,   (double)w,  ((double)(h>>1)),
												  0, 0, 1};
		defaultCalib.distortionMatrix = new double[] {0, 0, 0, 0, 0};
		return defaultCalib;
	}
}
