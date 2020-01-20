package nativeLib;

import org.opencv.android.OpenCVLoader;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_32FC2;
import static org.opencv.core.CvType.CV_32FC3;
import static org.opencv.core.CvType.CV_32SC1;
import static org.opencv.core.CvType.CV_64F;
import static org.opencv.core.CvType.CV_8UC1;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

public class notNativeLib {

	static {
//		System.loadLibrary("opencv");
		OpenCVLoader.initDebug();
	}

	final static double M_PI = 3.14159265358979323846;

//If this is defined a lot of information is printed into the log.
	final static boolean LOG_OUTPUT_ON = true;

//If this is on the image will be blurred with pyrdown and pyrup
//before the processing is done. Only can be used with the newer OpenCV
//version which is currently not included
	final static boolean PYR_BLUR_ON = false;

//Defines to make logging easier. All will be posted under the AR tag.
			//#define LOGV(x) __android_log_print(ANDROID_LOG_VERBOSE, "AR", x )
			//#define LOGD(x) __android_log_print(ANDROID_LOG_DEBUG  , "AR", x )
			//#define LOGI(x) __android_log_print(ANDROID_LOG_INFO   , "AR", x )
			//#define LOGW(x) __android_log_print(ANDROID_LOG_WARN   , "AR", x )
			//#define LOGE(x) __android_log_print(ANDROID_LOG_ERROR  , "AR", x )

//Defines the maximum frames used for calibration.
	final static int MAX_CALIB_FRAMES = 15;

//Defines the number of marker IDs. In this case a 9 bit code means 512 markers.
//Should be updates in order to get correct values in the Java code.
	final static int MAX_MARKER_INDEX = 4096;

//Defines the maximum number of markers detected in one frame. This is done
//To avoid frames which need too much computation.
	final static int MAX_DETECTED_MARKERS = 5;

	//The frame counter for the calibration.
	int frameCounter = MAX_CALIB_FRAMES - 1;

//Both matrices used to store image points for calibration.
	static Mat OPCalib = new Mat();  //cvCreateMat(4 * MAX_CALIB_FRAMES, 1, CV_32FC3);
	static Mat IPCalib = new Mat(); //cvCreateMat(4 * MAX_CALIB_FRAMES, 1, CV_32FC2);
	static Mat PointCnt = new Mat(); //cvCreateMat(MAX_CALIB_FRAMES, 1, CV_32SC1);

	//The intrinsic camera parameters.
/*double kMat[9] = { 3.3727007117788119e+02, 0.00, 160, 0.00,
 3.3727507565724787e+02, 120, 0.00, 0.00, 1.00 };*/
	static double[] kMat = new double[]{ 200, 0.00, 120, 0.00, 200, 80, 0.00, 0.00, 1.00 };

	//Camera distortion parameters.
	static double[] l = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0 };

	//Camera parameters that will be used when they are updated from the java code.
	static double[] kMatNew = new double[]{ 200, 0.00, 120, 0.00, 200, 80, 0.00, 0.00, 1.00 };
	static double[] lNew = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0 };
	static boolean newParams = false;

	//The used threshold in one frame. Written with dd because one d became a problem
//when used with a function :S
	int thresholdd = 0;

	//Possible threshold created by reading marker values.
	int markerThreshold = 0;

	//boolean to test if the markers is actually used.
	boolean useMarkerthreshold = false;

	// CvMat wrappers for intrinsic camera and distortion parameters
	static Mat K; //cvMat(3, 3, CV_64F, kMat);
	static Mat D; //cvMat(5, 1, CV_32F, l);

	/**
	 * Loads the pixels from the frame array into an IplImage and calculates a
	 * threshold which is stored in the 'thresholdd'-variable or it used the
	 * markerThreshold value instead of the newly calculated.
	 */
void loadPixels(byte[] pixels, Mat/*IplImage*/ tgray, int width, int height) {
		int x, y = 0;
		int thresholdLocal = 0;
//		char * tgrayData = tgray->imageData;
		byte[] tgrayData = new byte[width * height];
		tgray.get(0, 0, tgrayData);
		int min = 999;
		int max = 0;
		byte current;
		int currentIndex = 0;
		for (y = 0; y < height; y++) {
			for (x = 0; x < width; x++) {
				current = /*(int)*/ (byte) (pixels[currentIndex] & 0xFF);
				tgrayData[currentIndex] = current;
				thresholdLocal += current;
				currentIndex++;
			}
		}
		thresholdLocal /= (width * height);
		//If in the last frame a marker was detected, use the local threshold.
		if (useMarkerthreshold) {
			thresholdd = markerThreshold;
			useMarkerthreshold = false/*--*/;
		} else {
			//Otherwise use the global threshold.
			thresholdd = thresholdLocal;
		}
if (LOG_OUTPUT_ON) {
//#ifdef LOG_OUTPUT_ON
//		char tmp[50];
		System.out.printf("threshold: %d", thresholdd);
//		LOGD(tmp);
//#endif
}
	}

	//Get the current calibration data
	boolean getCalibration(double[] cameraMatrix, double[] distCoeff) {
//	JNIEXPORT jboolean JNICALL Java_nativeLib_NativeLib_getCalibration(
//		JNIEnv * env, jobject obj, jdoubleArray cameraMatrix, jdoubleArray distCoeff) {
	//return the current calibration data;
//	(*env)->SetDoubleArrayRegion(env, cameraMatrix, 0, 9, kMat);
//	(*env)->SetDoubleArrayRegion(env, distCoeff, 0, 5, l);
	return true;
}

	//Set new calibration data
	boolean setCalibration(double[] cameraMatrix, double[] distCoeff) {
//	JNIEXPORT jboolean JNICALL Java_de_rwth_ar_nativeLib_NativeLib_setCalibration(
//		JNIEnv * env, jobject obj, jdoubleArray cameraMatrix,
//		jdoubleArray distCoeff) {
	//Parse the calibration data.
//	(*env)->GetDoubleArrayRegion(env, cameraMatrix, 0, 9, kMatNew);
//	(*env)->GetDoubleArrayRegion(env, distCoeff, 0, 5, lNew);
	newParams = true;
	return true;
}

	//Set the loaded calibration data and retrieve some constants
	public static boolean initThread(int[] parameterArray, double[] cameraMatrix, double[] distCoeff) {
//	JNIEXPORT jboolean JNICALL Java_nativeLib_NativeLib_initThread(
//		JNIEnv * env, jobject obj, jintArray parameterArray,
//		jdoubleArray cameraMatrix, jdoubleArray distCoeff) {
//	(*env)->GetDoubleArrayRegion(env, cameraMatrix, 0, 9, kMatNew);
		kMatNew = GetDoubleArrayRegion(cameraMatrix, 0, 9);
//	(*env)->GetDoubleArrayRegion(env, distCoeff, 0, 5, lNew);
		lNew = GetDoubleArrayRegion(distCoeff, 0, 5);
	newParams = true;
	int[] constants = { MAX_MARKER_INDEX, MAX_CALIB_FRAMES };
//	(*env)->SetIntArrayRegion(env, parameterArray, 0, 2, constants);
		parameterArray = SetIntArrayRegion(0, 2, constants);

	OPCalib.create/*cvCreateMat*/(4 * MAX_CALIB_FRAMES, 1, CV_32FC3);
	IPCalib.create/*cvCreateMat*/(4 * MAX_CALIB_FRAMES, 1, CV_32FC2);
	PointCnt.create/*cvCreateMat*/(MAX_CALIB_FRAMES, 1, CV_32SC1);
	K = new Mat/*cvMat*/(3, 3, CV_64F/*, kMat*/); K.put(0, 0, kMat);
	D = new Mat/*cvMat*/(5, 1, CV_32F/*, l*/); D.put(0, 0, l);
	return true;
}

	/**
	 * detect the marker info and return the correct drawing information.
	 * calibrateNext tells if the next frame should be used for calibration or not.
	 */
	public int detectMarkers(byte[] frame, float[] returnInfo, int height, int width, boolean calibrateNext) {
//	JNIEXPORT jint JNICALL Java_nativeLib_NativeLib_detectMarkers(
//		JNIEnv * env, jobject obj, jbyteArray frame, jfloatArray returnInfo,
//		jint height, jint width, jboolean calibrateNext) {

			//New cameraparameters are set, use these for the next frame.
			if (newParams) {
				kMat[0] = kMatNew[0];
				kMat[1] = kMatNew[1];
				kMat[2] = kMatNew[2];
				kMat[3] = kMatNew[3];
				kMat[4] = kMatNew[4];
				kMat[5] = kMatNew[5];
				kMat[6] = kMatNew[6];
				kMat[7] = kMatNew[7];
				kMat[8] = kMatNew[8];
				l[0] = lNew[0];
				l[1] = lNew[1];
				l[2] = lNew[2];
				l[3] = lNew[3];
				l[4] = lNew[4];
				newParams = false;
			}

//			CvMemStorage* cvStorage = 0;
			char[] tmp = new char[250];
			int x, y = 0;
			int i, N = 9;
			double min;
		double max;
		double r;
		double s;
		double t;

		/*Will store all the rotation matrices and the number of them
			 *in the following order:
			 *[0] 			number of detected squares
			 *[i] - [i+15]	rotation matrix
			 *[i+16]		a rotation flag, either -90, -180 or -270 to address
			 *				markers detected with a wrong rotation.
			 *[i+17]  		number of marker.
			 *The maximum number of detected markers
			 *is defined by MAX_DETECTED_MARKER
			 */
			int returnValPnt = 1;
			float[] returnVals = new float[18 * MAX_DETECTED_MARKERS + 1];
			returnVals[0] = 0;

			//Stores the current calculated threshold from the markers
			int currentMarkerThresholdBlack = 0;
			int currentMarkerThresholdWhite = 256;
			int whiteCounter = 0;
			int blackCounter = 0;

			//rotation matrix and translation vector.
			float[] rotMat/*[9]*/ = new float[]{ 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f };
			float[] tv/*[3]*/ = new float[]{ 0.0f, 0.0f, -5.0f };

			//array size for an image with one, 1byte color channel.
			int imageSize = height * width;
			Size sz = new Size(width, height);

			//used to copy the jbyteArray into data that can be used in the c code.
			byte[] outHelper = new byte[imageSize];

//			IplImage* tgray = cvCreateImage(sz, 8, 1);
//			IplImage* gray = cvCreateImage(sz, 8, 1);
	Mat tgray = new Mat(sz, CV_8UC1);
	Mat gray = new Mat(sz, CV_8UC1);
			//Actually copy the data from the jbyteArray into the helper array.
			outHelper = GetByteArrayRegion(frame, 0, imageSize/*, (jbyte*) outHelper*/);

			//Convert the data into an IplImage here.
			loadPixels(outHelper, tgray, width, height);

//#ifdef PYR_BLUR_ON
//			IplImage* pyr = cvCreateImage( cvSize(width/2, height/2), 8, 1 );
//			cvPyrDown( tgray, pyr, 7 );
//			cvPyrUp( pyr, tgray, 7 );
//			cvReleaseImage(&pyr);
//#endif

					//Create a storage which will be used for some functions later on.
//					cvStorage = cvCreateMemStorage(0);

			//Sequences to store the detected contours.
//			CvSeq* contours;
	List<MatOfPoint> contours= new ArrayList<>();
//			CvSeq* result;
	MatOfPoint2f result = new MatOfPoint2f();
			// create empty sequence that will contain points -
			// 4 points per square (the square's vertices)
//			CvSeq* squares = cvCreateSeq(0, sizeof(CvSeq), sizeof(CvPoint), cvStorage);

			//Threshold the image into a binary image according to the threshold
//			cvThreshold(tgray, gray, thresholdd, 255, CV_THRESH_BINARY);
			Imgproc.threshold(tgray, gray, thresholdd, 255, THRESH_BINARY);
			//cvAdaptiveThreshold(tgray, gray, 255, CV_ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY,
			//                        5, 0);

			//Find the contours and return them in a simple list.
//			cvFindContours(gray, cvStorage, &contours, sizeof(CvContour), CV_RETR_LIST,bCV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
			Imgproc.findContours(gray, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//			//Counts the detected markers.
			int counter = 0;

			//Test each contour as long as there are contours and not too many
			//markers have been detected already.
			ListIterator<MatOfPoint> iterate = contours.listIterator();
			while (iterate.hasNext() && (counter < MAX_DETECTED_MARKERS)) {
				MatOfPoint contour = iterate.next();
//				//Approximate contour with accuracy proportional to the contour perimeter.
//				This means, bigger areas will be approximated more accurate.
//				result = cvApproxPoly(contours, sizeof(CvContour), cvStorage, CV_POLY_APPROX_DP, cvContourPerimeter(contours) * 0.02, 0);

				//Convert contours from MatOfPoint to MatOfPoint2f
				MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
				//Processing on mMOP2f1 which is in type MatOfPoint2f
				double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;

				Imgproc.approxPolyDP(contour2f, result, approxDistance, false);
				//Get the surface of the area included by the counter.
//				int surface = fabs(cvContourArea(result, CV_WHOLE_SEQ));
				double surface = Math.abs(Imgproc.contourArea(result, true));
				max = 0;
				min = 9999;
//				//Only consider contours which enclose an area bigger han 500 pixels and less than half of the image.
//				//Also check if the areas are convex.
//				if (result->total == 4 && surface > 500 && surface < (imageSize >> 1) && cvCheckContourConvexity(result)) {
				Point[] aResult = result.toArray();
				if (aResult.length == 4 && surface > 500 && surface < (imageSize >> 1) && Imgproc.isContourConvex(new MatOfPoint(aResult))) {

					Point pt0;//					CvPoint* pt0;
					Point pt1;//					CvPoint* pt1;
					Point pt2;//					CvPoint* pt2;
					Point pt3;//					CvPoint* pt3;
					double dx1;
					double dy1;
					double dx2;
					double dy2;
					s = 0;
					for (i = 2; i < 5; i++) {
						// find minimum angle between joint edges (maximum of cosine)
						pt1 = aResult[i]; //		pt1 = (CvPoint*) cvGetSeqElem(result, i);
						pt2 = aResult[i-2]; //	pt2 = (CvPoint*) cvGetSeqElem(result, i - 2);
						pt0 = aResult[i-1]; //	pt0 = (CvPoint*) cvGetSeqElem(result, i - 1);

						dx1 = pt1.x - pt0.x;
						dy1 = pt1.y - pt0.y;
						dx2 = pt2.x - pt0.x;
						dy2 = pt2.y - pt0.y;

						t = Math.abs((dx1 * dx2 + dy1 * dy2) / sqrt((dx1 * dx1 + dy1 * dy1)	* (dx2 * dx2 + dy2 * dy2) + 1e-10));

						r = Math.sqrt(dx1 * dx1 + dy1 * dy1);
						s = Math.max(s, t);
						min = Math.min(min, r);
						max = Math.max(max, r);
					}
					r = max / min;

					// if cosines of all angles are small
					// (all angles are ~90 degree) then assume it is a square.
					if ((s < 0.2 && r < 1.7) || (s < 0.5 && r < 1.5) || (s < 0.75 && r < 1.2)) {
						float x1, y1, x2, y2, x3, y3, x4, y4;
						pt0 = aResult[1]; //	pt0 = (CvPoint*) cvGetSeqElem(result, 1);
						pt1 = aResult[2]; //	pt1 = (CvPoint*) cvGetSeqElem(result, 2);
						pt2 = aResult[3]; //	pt2 = (CvPoint*) cvGetSeqElem(result, 3);
						pt3 = aResult[4]; //	pt3 = (CvPoint*) cvGetSeqElem(result, 4);

						//get the width of the image.
						int w = tgray.depth(); //tgray->widthStep;

						//Array to store the rotation vector.
						float[] rv = { 0.0f, 0.0f, 0.0f };

						//Some CvMat wrappers for the translation and rotation vector and the roation matrix.
Mat T = new Mat(3, 1, CV_32F); T.put(0, 0, tv); //		CvMat T = cvMat(3, 1, CV_32F, tv);
Mat RV = new Mat(3, 1, CV_32F); RV.put(0, 0, rv); //	CvMat RV = cvMat(3, 1, CV_32F, rv);
Mat R = new Mat(3, 3, CV_32F); R.put(0, 0, rotMat); //	CvMat R = cvMat(3, 3, CV_32F, rotMat);

						//Create some matrices and appropriate options to access the values.
Mat OP = new Mat(5, 1, CV_32FC3); //						CvMat *OP = cvCreateMat(5, 1, CV_32FC3);
List<Point3> op = new ArrayList<>(); //						CvPoint3D32f *op = (CvPoint3D32f *) OP->data.fl;

Mat IP = new Mat(5, 1, CV_32FC2); //						CvMat *IP = cvCreateMat(5, 1, CV_32FC2);
List<Point> ip = new ArrayList<>(); //						CvPoint2D32f *ip = (CvPoint2D32f *) IP->data.fl;

						if (LOG_OUTPUT_ON) {//#ifdef LOG_OUTPUT_ON
//						LOGD("k Matrix");
							System.out.printf("K(x,y,z) : (%3.2f, %3.2f, %3.2f)", kMat[0], kMat[1], kMat[2]);
//						LOGD(tmp);
							System.out.printf("K(x,y,z) : (%3.2f, %3.2f, %3.2f)", kMat[3], kMat[4], kMat[5]);
//						LOGD(tmp);
							System.out.printf("K(x,y,z) : (%3.2f, %3.2f, %3.2f)", kMat[6], kMat[7], kMat[8]);
//						LOGD(tmp);
						}//#endif

						//Set the points in an array.
						ip.get(0).x = pt0.x;
						ip.get(0).y = pt0.y;
						ip.get(1).x = pt1.x;
						ip.get(1).y = pt1.y;
						ip.get(2).x = pt2.x;
						ip.get(2).y = pt2.y;
						ip.get(3).x = pt3.x;
						ip.get(3).y = pt3.y;

						//Calculate an additional point in the center of the marker.
						//This is done by intersecting the two diagonal lines of the
						//square.
						double vertical;
						double a1;
						double a2;
						double b1;
						double b2;
						vertical = (ip.get(0).x - ip.get(2).x);
						if (Math.abs(vertical) < 0.009) {
							a1 = 1024;//signal this is a vertical line.
							b1 = ip.get(0).x;
						} else {
							a1 = (ip.get(0).y - ip.get(2).y) / vertical;
							b1 = ip.get(0).y - a1 * ip.get(0).x;
						}
						if (LOG_OUTPUT_ON) {//#ifdef LOG_OUTPUT_ON
							System.out.printf("a1,b1 : %4.3f, %4.3f", a1, b1);
//						LOGD(tmp);
						}//#endif
								vertical = (ip.get(1).x - ip.get(3).x);
						if (Math.abs(vertical) < 0.009) {
							a2 = 1024;//signal this is a vertical line.
							b2 = ip.get(1).x;
						} else {
							a2 = (ip.get(1).y - ip.get(3).y) / vertical;
							b2 = ip.get(1).y - a2 * ip.get(1).x;
						}
						if (LOG_OUTPUT_ON) {//#ifdef LOG_OUTPUT_ON
							System.out.printf("a2,b2 : %4.3f, %4.3f", a2, b2);
//						LOGD(tmp);
						}//#endif
						if (a1 != 1024 && a2 != 1024) {
							ip.get(4).x = (b1 - b2) / (a2 - a1);
							ip.get(4).y = a1 * ip.get(4).x + b1;
							if (LOG_OUTPUT_ON) {//#ifdef LOG_OUTPUT_ON
								System.out.printf("X,Y (perfect): %4.3f, %4.3f", ip.get(4).x, ip.get(4).y);
							}//#endif
						} else if (a2 != 1024) {
							ip.get(4).x = b1;
							ip.get(4).y = a2 * ip.get(4).x + b2;
							if (LOG_OUTPUT_ON) {//#ifdef LOG_OUTPUT_ON
								System.out.printf("X,Y (perfect): %4.3f, %4.3f", ip.get(4).x, ip.get(4).y);
							}//#endif
						} else {
							ip.get(4).x = b2;
							ip.get(4).y = a1 * ip.get(4).x + b1;
							if (LOG_OUTPUT_ON) {//#ifdef LOG_OUTPUT_ON
								System.out.printf("X,Y (perfect): %4.3f, %4.3f", ip.get(4).x, ip.get(4).y);
							}//#endif
						}
//#ifdef LOG_OUTPUT_ON
//						LOGD(tmp);
//#endif

						//Set the corresponding 3D virtual points.
						op.get(0).x = -1.00;
						op.get(0).y = 1.00;
						op.get(0).z = 0.00;

						op.get(1).x = 1.00;
						op.get(1).y = 1.00;
						op.get(1).z = 0.00;

						op.get(2).x = 1.00;
						op.get(2).y = -1.00;
						op.get(2).z = 0.00;

						op.get(3).x = -1.00;
						op.get(3).y = -1.00;
						op.get(3).z = 0.00;

						op.get(4).x = 0.00;
						op.get(4).y = 0.00;
						op.get(4).z = 0.00;
						if (LOG_OUTPUT_ON) {//#ifdef LOG_OUTPUT_ON
							System.out.print("OP Matrix");
							System.out.printf("OP1(x,y,z) : (%3.2f, %3.2f, %3.2f)", op.get(0).x, op.get(0).y, op.get(0).z);
//						LOGD(tmp);
							System.out.printf("OP2(x,y,z) : (%3.2f, %3.2f, %3.2f)", op.get(1).x, op.get(1).y, op.get(1).z);
//						LOGD(tmp);
							System.out.printf("OP3(x,y,z) : (%3.2f, %3.2f, %3.2f)", op.get(2).x, op.get(2).y, op.get(2).z);
//						LOGD(tmp);
							System.out.printf("OP4(x,y,z) : (%3.2f, %3.2f, %3.2f)", op.get(3).x, op.get(3).y, op.get(3).z);
//						LOGD(tmp);
							System.out.printf("OP5(x,y,z) : (%3.2f, %3.2f, %3.2f)", op.get(4).x, op.get(4).y, op.get(4).z);
//						LOGD(tmp);
							System.out.print("IP Matrix");
							System.out.printf("IP1(x,y) : (%3.2f, %3.2f)", ip.get(0).x, ip.get(0).y);
//						LOGD(tmp);
							System.out.printf("IP2(x,y) : (%3.2f, %3.2f)", ip.get(1).x, ip.get(1).y);
//						LOGD(tmp);
							System.out.printf("IP3(x,y) : (%3.2f, %3.2f)", ip.get(2).x, ip.get(2).y);
//						LOGD(tmp);
							System.out.printf("IP4(x,y) : (%3.2f, %3.2f)", ip.get(3).x, ip.get(3).y);
//						LOGD(tmp);
							System.out.printf("IP5(x,y) : (%3.2f, %3.2f)", ip.get(4).x, ip.get(4).y);
//						LOGD(tmp);
						}//#endif
						//Find the extrinsic camera parameters.
						// Meaning find the rotation and translation vector.
//						cvFindExtrinsicCameraParams2(OP, IP, &K, &D, &RV, &T);
//https://groups.google.com/forum/#!topic/android-opencv/0ImAjpAL5t4
//cvFindExtrinsicCameraParams2 is the old C name of the function. In C++ and Java API it is named solvePnP
//List<Point3> points3d = new ArrayList<Point3>();
//List<Point> points2d = new ArrayList<Point>();
//Mat intrinsics = Mat.eye(3, 3, CvType.CV_32F);
//Mat rvec = new Mat();
//Mat tvec = new Mat();
//Calib3d.solvePnP(points3d, points2d, intrinsics, new Mat(), rvec, tvec);
//						Calib3d.solvePnP(OP, IP, K, D, RV, T);
//						cvRodrigues2(&RV, &R, 0);
						Calib3d.Rodrigues(RV, R);
						if (LOG_OUTPUT_ON) {//#ifdef LOG_OUTPUT_ON
							System.out.printf("RV(x,y,z) : (%3.2f, %3.2f, %3.2f)", rv[0], rv[1], rv[2]);
//						LOGD(tmp);
							System.out.printf("TV(x,y,z) : (%3.2f, %3.2f, %3.2f)", tv[0], tv[1], tv[2]);
//						LOGD(tmp);
							System.out.print("Rotation Matrix:");
							System.out.printf("%3.2f, %3.2f, %3.2f", rotMat[0], rotMat[1], rotMat[2]);
//						LOGD(tmp);
							System.out.printf("%3.2f, %3.2f, %3.2f", rotMat[3], rotMat[4], rotMat[5]);
//						LOGD(tmp);
							System.out.printf("%3.2f, %3.2f, %3.2f", rotMat[6], rotMat[7], rotMat[8]);
//						LOGD(tmp);
						}//#endif

						//Now detect the actual marker info.
						//Stores the current pixel value.
						int currentSample = 0;

						float[] borderTestPntsX/*[20]*/ = { -0.8333f, -0.5000f, -0.1667f,
								0.1667f, 0.5000f, 0.8333f, 0.8333f, 0.8333f, 0.8333f, 0.8333f,
								0.8333f, 0.5000f, 0.1667f, -0.1667f, -0.5000f, -0.8333f,
								-0.8333f, -0.8333f, -0.8333f, -0.8333f };
						float[] borderTestPntsY/*[20]*/ = { 0.8333f, 0.8333f, 0.8333f, 0.8333f,
								0.8333f, 0.8333f, 0.5000f, 0.1667f, -0.1667f, -0.5000f,
								-0.8333f, -0.8333f, -0.8333f, -0.8333f, -0.8333f, -0.8333f,
								-0.5000f, -0.1667f, 0.1667f, 0.5000f };
						boolean borderCorrect = true;

						//Work variables
						int a, imageX, imageY, pointer = 0;
						double ipx;
						double ipy;
						double ipz = 0.0000f;
//						char* imageData = tgray->imageData;
						byte[] imageData = new byte[(int) (tgray.size().width*tgray.size().height)];
						tgray.get(0, 0, imageData);

						//Create 3D points and use the translation matrix to calculate the location within the image.
						//Then test the points on color.
						for (a = 0; a < 20; a++) {
							ipx = rotMat[0] * borderTestPntsX[a] + rotMat[1]
									* borderTestPntsY[a] /*+ rotMat[2] * 0.0 */+ tv[0];
							ipy = rotMat[3] * borderTestPntsX[a] + rotMat[4]
									* borderTestPntsY[a] /*+ rotMat[5] * 0.0 */+ tv[1];
							ipz = rotMat[6] * borderTestPntsX[a] + rotMat[7]
									* borderTestPntsY[a] /*+ rotMat[8] * 0.0 */+ tv[2];

							imageX = (int) (((kMat[0] * ipx) / ipz) + kMat[2]);
							imageY = (int) (((kMat[4] * ipy) / ipz) + kMat[5]);
							pointer = imageX + imageY * w;
							currentSample = 0;
							currentSample += imageData[pointer];
							currentSample += imageData[1 + pointer];
							currentSample += imageData[w + pointer];
							currentSample += imageData[w + 1 + pointer];
							if (currentSample < thresholdd * 4) {
								currentMarkerThresholdBlack += currentSample;
								blackCounter += 4;
							} else {
								//white border pixel means error!
								a = 20;
								borderCorrect = false;
							}
						}

						if (borderCorrect) {
							float[] orientationTestPntsX/*[4]*/ = { -0.5f, 0.5f, 0.5f, -0.5f };
							float[] orientationTestPntsY/*[4]*/ = { 0.5f, 0.5f, -0.5f, -0.5f };
							int orientation = -1;

							//The marker code.
							int code = -1;

							for (a = 0; a < 4; a++) {
								ipx = rotMat[0] * orientationTestPntsX[a] + rotMat[1]
										* orientationTestPntsY[a]
										/*+ rotMat[2] * 0.0 */+ tv[0];
								ipy = rotMat[3] * orientationTestPntsX[a] + rotMat[4]
										* orientationTestPntsY[a]
										/*+ rotMat[5] * 0.0 */+ tv[1];
								ipz = rotMat[6] * orientationTestPntsX[a] + rotMat[7]
										* orientationTestPntsY[a]
										/*+ rotMat[8] * 0.0 */+ tv[2];

								imageX = (int) (((kMat[0] * ipx) / ipz) + kMat[2]);
								imageY = (int) (((kMat[4] * ipy) / ipz) + kMat[5]);
								pointer = imageX + imageY * w;
								currentSample = 0;
								currentSample += imageData[pointer];
								currentSample += imageData[1 + pointer];
								currentSample += imageData[w + pointer];
								currentSample += imageData[w + 1 + pointer];
								if (currentSample < thresholdd * 4) {
									currentMarkerThresholdBlack += currentSample;
									blackCounter += 4;
									if (orientation == -1) {
										orientation = a;
									} else {
										//orientation pixels detected wrong.
										//marker is not correct.
										orientation = -2;
										a = 4;

									}
								} else {
									currentMarkerThresholdWhite += currentSample;
									whiteCounter += 4;
								}
							}

							if (orientation >= 0) {
								//Stores the points to go over the marker code.
								float[] testPntsX/*[12]*/ = { -0.1667f, 0.1667f, -0.5000f,
										-0.1667f, 0.1667f, 0.5000f, -0.5000f, -0.1667f,
										0.1667f, 0.5000f, -0.1667f, 0.1667f };
								float[] testPntsY/*[12]*/ = { 0.5000f, 0.5000f, 0.1667f, 0.1667f,
										0.1667f, 0.1667f, -0.1667f, -0.1667f, -0.1667f,
										-0.1667f, -0.5000f, -0.5000f };
								//Store the orientation change caused by a semi-random detection order.
								float orientationChange=0.0f;
								//Stores the order in which the code should be read.
								int[] order = new int[12];
								//tttpoiaia := twoToThePowerOfInvertedArrayIndexArray
								int[] tttpoiaia/*[12]*/ = { 2048, 1024, 512, 256, 128, 64,
										32, 16, 8, 4, 2, 1 };

								code = 0;
								if (orientation == 0) {
									int[] order2/*[12]*/ = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
											10, 11 };
									order = order2;
									orientationChange = 0.0f;

								} else if (orientation == 1) {
									int[] order2/*[12]*/ = { 5, 9, 1, 4, 8, 11, 0, 3, 7, 10,
											2, 6 };
									order = order2;
									orientationChange = 90.0f;

								} else if (orientation == 2) {
									int[] order2/*[12]*/ = { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2,
											1, 0 };
									order = order2;
									orientationChange = 180.0f;

								} else if (orientation == 3) {
									int[] order2/*[12]*/ = { 6, 2, 10, 7, 3, 0, 11, 8, 4, 1,
											9, 5 };
									order = order2;
									orientationChange = 270.0f;
								}

								for (a = 0; a < 12; a++) {
									ipx = rotMat[0] * testPntsX[order[a]] + rotMat[1]
											* testPntsY[order[a]] + rotMat[2] * 0.0
											+ tv[0];
									ipy = rotMat[3] * testPntsX[order[a]] + rotMat[4]
											* testPntsY[order[a]] + rotMat[5] * 0.0
											+ tv[1];
									ipz = rotMat[6] * testPntsX[order[a]] + rotMat[7]
											* testPntsY[order[a]] + rotMat[8] * 0.0
											+ tv[2];

									imageX = (int) (((kMat[0] * ipx) / ipz) + kMat[2]);
									imageY = (int) (((kMat[4] * ipy) / ipz) + kMat[5]);
									pointer = imageX + imageY * w;
									currentSample = 0;
									currentSample += imageData[pointer];
									currentSample += imageData[1 + pointer];
									currentSample += imageData[w + pointer];
									currentSample += imageData[w + 1 + pointer];

									if (currentSample < thresholdd * 4) {
										code += tttpoiaia[a];
										currentMarkerThresholdBlack += currentSample;
										blackCounter += 4;
									} else {
										currentMarkerThresholdWhite += currentSample;
										whiteCounter += 4;
									}
								}
								if (LOG_OUTPUT_ON) {//#ifdef LOG_OUTPUT_ON
									System.out.printf("Detected marker, id: %d", code);
//								LOGD(tmp);
								}//#endif
								//Only now a marker has been detected and the info can be
								//added to the return list.
								counter++;
								//If calibration needs to be done, use these points by storing them in the appropriate arrays.
								if (calibrateNext && frameCounter >= 0) {
									if (LOG_OUTPUT_ON) {//#ifdef LOG_OUTPUT_ON
										System.out.print("Adding points to list");
									}//#endif
//
//									CvPoint3D32f *opCalib =
//											(CvPoint3D32f *) OPCalib->data.fl;
//									CvPoint2D32f *ipCalib =
//											(CvPoint2D32f *) IPCalib->data.fl;
//									int *pointCnt = PointCnt->data.i;
Point3[] opCalib = new Point3[OPCalib.rows()];// OPCalib.get(0, 0, opCalib);
Point[] ipCalib = new Point[IPCalib.rows()]; //IPCalib.get(0, 0, ipCalib);
int[] pointCnt = new int[PointCnt.rows()];
									opCalib[frameCounter * 4].x = -1.0;
									opCalib[frameCounter * 4].y = 1.0;
									opCalib[frameCounter * 4].z = 0.0;

									opCalib[frameCounter * 4 + 1].x = 1.0;
									opCalib[frameCounter * 4 + 1].y = 1.0;
									opCalib[frameCounter * 4 + 1].z = 0.0;

									opCalib[frameCounter * 4 + 2].x = 1.0;
									opCalib[frameCounter * 4 + 2].y = -1.0;
									opCalib[frameCounter * 4 + 2].z = 0.0;

									opCalib[frameCounter * 4 + 3].x = -1.0;
									opCalib[frameCounter * 4 + 3].y = -1.0;
									opCalib[frameCounter * 4 + 3].z = 0.0;

									ipCalib[frameCounter * 4].x = pt0.x;
									ipCalib[frameCounter * 4].y = pt0.y;

									ipCalib[frameCounter * 4 + 1].x = pt1.x;
									ipCalib[frameCounter * 4 + 1].y = pt1.y;

									ipCalib[frameCounter * 4 + 2].x = pt2.x;
									ipCalib[frameCounter * 4 + 2].y = pt2.y;

									ipCalib[frameCounter * 4 + 3].x = pt3.x;
									ipCalib[frameCounter * 4 + 3].y = pt3.y;

									pointCnt[frameCounter] = 4;
									if (LOG_OUTPUT_ON) {//#ifdef LOG_OUTPUT_ON
									int itr;
									for(itr=0;itr<60;itr++) {
										System.out.printf("nr %d :OP= %3.2f, %3.2f IP =%3.2f, %3.2f", itr, opCalib[itr].x, opCalib[itr].y, ipCalib[itr].x, ipCalib[itr].y);
//										LOGD(tmp);
									}
									}//#endif
									frameCounter--;
								}

								//calculate the rotation matrix. Invert some values since it is needed for OpenGL.
								rv[1] = -1.0f * rv[1];
								rv[2] = -1.0f * rv[2];
//								cvRodrigues2(&RV, &R, 0);
								Calib3d.Rodrigues(RV, R);

								//Increase the detected marker count
								returnVals[0]++;

								orientationChange = (float) ((orientationChange*M_PI)/180.0f);

								float temp0;
								float temp3;
								float temp6;

								temp0= (float) (cos(orientationChange)*rotMat[0] + sin(orientationChange)*rotMat[1]);
								temp3= (float) (cos(orientationChange)*rotMat[3] + sin(orientationChange)*rotMat[4]);
								temp6= (float) (cos(orientationChange)*rotMat[6] + sin(orientationChange)*rotMat[7]);
								rotMat[1]= (float) (-sin(orientationChange)*rotMat[0] + cos(orientationChange)*rotMat[1]);
								rotMat[4]= (float) (-sin(orientationChange)*rotMat[3] + cos(orientationChange)*rotMat[4]);
								rotMat[7]= (float) (-sin(orientationChange)*rotMat[6] + cos(orientationChange)*rotMat[7]);

								rotMat[0]= temp0;
								rotMat[1]= -rotMat[1];
								rotMat[2]= -rotMat[2];
								rotMat[3]= temp3;
								rotMat[4]= -rotMat[4];
								rotMat[5]= -rotMat[5];
								rotMat[6]= temp6;
								rotMat[7]= -rotMat[7];
								rotMat[8]= -rotMat[8];

								orientationChange=0;

								//write the rotation matrix into the right part of the list.
								returnVals[returnValPnt++] = rotMat[0];
								returnVals[returnValPnt++] = rotMat[3];
								returnVals[returnValPnt++] = rotMat[6];
								returnVals[returnValPnt++] = 0;
								returnVals[returnValPnt++] = rotMat[1];
								returnVals[returnValPnt++] = rotMat[4];
								returnVals[returnValPnt++] = rotMat[7];
								returnVals[returnValPnt++] = 0;
								returnVals[returnValPnt++] = rotMat[2];
								returnVals[returnValPnt++] = rotMat[5];
								returnVals[returnValPnt++] = rotMat[8];
								returnVals[returnValPnt++] = 0;
								returnVals[returnValPnt++] = tv[0];
								returnVals[returnValPnt++] = -tv[1];
								returnVals[returnValPnt++] = -tv[2];
								returnVals[returnValPnt++] = 1.0f;

								//write the orientation change caused by random detection.
								returnVals[returnValPnt++] = orientationChange;

								//write the marker number.
								returnVals[returnValPnt++] = code;

							} else {
								// failed.
								//Do nothing?
							}
						} else {
							// failed.
						//Do nothing?
						}

						//Release the object point matrixes.
OP.release();//						cvReleaseMat(&OP);
IP.release();//						cvReleaseMat(&IP);
					}
				}

				// take the next contour
//				contours = contours->h_next;
			}

			//If one or more markers were detected use the marker threshold.
			if (counter > 0) {
				useMarkerthreshold = false;

				//Stores the current calculated threshold
				currentMarkerThresholdBlack /= blackCounter;
				currentMarkerThresholdWhite /= whiteCounter;
				markerThreshold = (currentMarkerThresholdBlack
						+ currentMarkerThresholdWhite) >> 1;
			} else {
				//No marker was detected decrease the length in which the marker threshold
				//Should still be used.
				if (useMarkerthreshold ) {
					useMarkerthreshold = false;
				}
			}
			//Release the storage created for certain methodes.
//			cvReleaseMemStorage(&cvStorage);

			//Convert the c data into java data again to pass it to the storage array.
//			// Java should then be able to access the data in the array.
//			(*env)->SetFloatArrayRegion(env, returnInfo, 0, returnValPnt, returnVals);
			returnInfo = SetFloatArrayRegion(0, returnValPnt, returnVals);

			//Release the frames, otherwise the memory will overflow and the app will crash.
//			cvReleaseImage(&gray);
//			cvReleaseImage(&tgray);
			gray.release();
			tgray.release();
			//return the number of calibration frames still to do.
			//Should be changed.
			return (MAX_CALIB_FRAMES - frameCounter - 1);
	}

	/**
	 * Actually copy the data from the jbyteArray into the helper array.
	 *
	 * @param frame
	 * @param i
	 * @param imageSize
	 * @return
	 */
	private byte[] GetByteArrayRegion(byte[] frame, int i, int imageSize) {
		return frame;
	}
	private static double[] GetDoubleArrayRegion(double[] frame, int i, int imageSize) {
		return frame;
	}
	private static float[] GetFloatArrayRegion(float[] frame, int i, int imageSize) {
		return frame;
	}
	private static float[] SetFloatArrayRegion(int i, int imageSize, float[] frame) {
		return frame;
	}
	private static double[] SetDoubleArrayRegion(int i, int imageSize, double[] frame) {
		return frame;
	}
	private static int[] SetIntArrayRegion(int i, int i1, int[] constants) {
		return constants;
	}
}
