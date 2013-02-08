#include "nativeLib_NativeLib.h"
#include <android/log.h>
#include "cv.h"
#include <stdio.h>
#include <math.h>


#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

//If this is defined a lot of information is printed into the log.
//#define LOG_OUTPUT_ON

//If this is on the image will be blurred with pyrdown and pyrup
//before the processing is done. Only can be used with the newer OpenCV
//version which is currently not included
//#define PYR_BLUR_ON

//Defines to make logging easier. All will be posted under the AR tag.
#define LOGV(x) __android_log_print(ANDROID_LOG_VERBOSE, "AR", x )
#define LOGD(x) __android_log_print(ANDROID_LOG_DEBUG  , "AR", x )
#define LOGI(x) __android_log_print(ANDROID_LOG_INFO   , "AR", x )
#define LOGW(x) __android_log_print(ANDROID_LOG_WARN   , "AR", x )
#define LOGE(x) __android_log_print(ANDROID_LOG_ERROR  , "AR", x )

//Defines the maximum frames used for calibration.
#define MAX_CALIB_FRAMES 15

//Defines the number of marker IDs. In this case a 9 bit code means 512 markers.
//Should be updates in order to get correct values in the Java code.
#define MAX_MARKER_INDEX 4096

//Defines the maximum number of markers detected in one frame. This is done
//To avoid frames which need too much computation.
#define MAX_DETECTED_MARKERS 5

//The frame counter for the calibration.
int frameCounter = MAX_CALIB_FRAMES - 1;

//Both matrices used to store image points for calibration.
CvMat *OPCalib = 0; //cvCreateMat(4 * MAX_CALIB_FRAMES, 1, CV_32FC3);
CvMat *IPCalib = 0; //cvCreateMat(4 * MAX_CALIB_FRAMES, 1, CV_32FC2);
CvMat *PointCnt = 0; //cvCreateMat(MAX_CALIB_FRAMES, 1, CV_32SC1);

//The intrinsic camera parameters.
/*double kMat[9] = { 3.3727007117788119e+02, 0.00, 160, 0.00,
 3.3727507565724787e+02, 120, 0.00, 0.00, 1.00 };*/
double kMat[9] = { 200, 0.00, 120, 0.00, 200, 80, 0.00, 0.00, 1.00 };

//Camera distortion parameters.
double l[5] = { 0.0, 0.0, 0.0, 0.0, 0.0 };

//Camera parameters that will be used when they are updated from the java code.
double kMatNew[9] = { 200, 0.00, 120, 0.00, 200, 80, 0.00, 0.00, 1.00 };
double lNew[5] = { 0.0, 0.0, 0.0, 0.0, 0.0 };
char newParams = 0;

//The used threshold in one frame. Written with dd because one d became a problem
//when used with a function :S
int thresholdd = 0;

//Possible threshold created by reading marker values.
int markerThreshold = 0;

//boolean to test if the markers is actually used.
char useMarkerthreshold = 0;

// CvMat wrappers for intrinsic camera and distortion parameters
CvMat K; //cvMat(3, 3, CV_64F, kMat);
CvMat D; //cvMat(5, 1, CV_32F, l);

/**
 * Loads the pixels from the frame array into an IplImage and calculates a
 * threshold which is stored in the 'thresholdd'-variable or it used the
 * markerThreshold value instead of the newly calculated.
 */
void loadPixels(char* pixels, IplImage* tgray, int width, int height) {
	int x, y = 0;
	int thresholdLocal = 0;
	char * tgrayData = tgray->imageData;
	int min = 999;
	int max = 0;
	int current;
	int currentIndex = 0;
	for (y = 0; y < height; y++) {
		for (x = 0; x < width; x++) {

			current = (int) (pixels[currentIndex] & 0xFF);
			tgrayData[currentIndex] = current;
			thresholdLocal += current;
			currentIndex++;

		}
	}
	thresholdLocal /= (width * height);
	//If in the last frame a marker was detected,
	//use the local threshold.
	if (useMarkerthreshold) {
		thresholdd = markerThreshold;
		useMarkerthreshold--;
	} else {
		//Otherwise use the global threshold.
		thresholdd = thresholdLocal;
	}

#ifdef LOG_OUTPUT_ON
	char tmp[50];
	sprintf(tmp, "threshold: %d", thresholdd);
	LOGD(tmp);
#endif

}
/**
 * detect the marker info and return the correct drawing information.
 *
 * calibrateNext tells if the next frame should be used for calibration or not.
 */

JNIEXPORT jint JNICALL Java_nativeLib_NativeLib_detectMarkers(
		JNIEnv * env, jobject obj, jbyteArray frame, jfloatArray returnInfo,
		jint height, jint width, jboolean calibrateNext) {

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
		newParams = 0;
	}

	CvMemStorage* cvStorage = 0;
	char tmp[250];
	int x, y = 0;
	int i, N = 9;
	float min, max, r, s, t;

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
	float returnVals[18 * MAX_DETECTED_MARKERS + 1];
	returnVals[0] = 0;

	//Stores the current calculated threshold from the markers
	int currentMarkerThresholdBlack = 0;
	int currentMarkerThresholdWhite = 256;
	int whiteCounter = 0;
	int blackCounter = 0;

	//rotation matrix and translation vector.
	float rotMat[9] = { 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0 };
	float tv[3] = { 0.0, 0.0, -5.0 };

	//array size for an image with one, 1byte color channel.
	int imageSize = height * width;
	CvSize sz = cvSize(width, height);

	//used to copy the jbyteArray into data that can be used in the c code.
	char outHelper[imageSize];

	IplImage* tgray = cvCreateImage(sz, 8, 1);
	IplImage* gray = cvCreateImage(sz, 8, 1);

	//Actually copy the data from the jbyteArray into the helper array.
	(*env)->GetByteArrayRegion(env, frame, 0, imageSize, (jbyte*) outHelper);

	//Convert the data into an IplImage here.
	loadPixels(outHelper, tgray, width, height);

#ifdef PYR_BLUR_ON
	IplImage* pyr = cvCreateImage( cvSize(width/2, height/2), 8, 1 );
	cvPyrDown( tgray, pyr, 7 );
	cvPyrUp( pyr, tgray, 7 );
	cvReleaseImage(&pyr);
#endif

	//Create a storage which will be used for some functions later on.
	cvStorage = cvCreateMemStorage(0);

	//Sequences to store the detected contours.
	CvSeq* contours;
	CvSeq* result;

	// create empty sequence that will contain points -
	// 4 points per square (the square's vertices)
	CvSeq* squares = cvCreateSeq(0, sizeof(CvSeq), sizeof(CvPoint), cvStorage);

	//Threshold the image into a binary image according to the threshold
	cvThreshold(tgray, gray, thresholdd, 255, CV_THRESH_BINARY);
	//cvAdaptiveThreshold(tgray, gray, 255, CV_ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY,
	//                        5, 0);

	//Find the contours and return them in a simple list.
	cvFindContours(gray, cvStorage, &contours, sizeof(CvContour), CV_RETR_LIST,
			CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));

	//Counts the detected markers.
	int counter = 0;

	// test each contour as long as there are contours and not too many
	//markers have been detected already.
	while (contours && (counter < MAX_DETECTED_MARKERS)) {
		//Approximate contour with accuracy proportional
		//to the contour perimeter. This means, bigger areas
		//will be approximated more accurate.
		result = cvApproxPoly(contours, sizeof(CvContour), cvStorage,
				CV_POLY_APPROX_DP, cvContourPerimeter(contours) * 0.02, 0);

		//Get the surface of the area included by the counter.
		int surface = fabs(cvContourArea(result, CV_WHOLE_SEQ));
		max = 0;
		min = 9999;
		//Only consider contours which enclose an area bigger
		//than 500 pixels and less than half of the image.
		//Also check if the areas are convex.
		if (result->total == 4 && surface > 500 && surface < (imageSize >> 1)
				&& cvCheckContourConvexity(result)) {

			CvPoint* pt0;
			CvPoint* pt1;
			CvPoint* pt2;
			CvPoint* pt3;
			float dx1;
			float dy1;
			float dx2;
			float dy2;
			s = 0;
			for (i = 2; i < 5; i++) {
				// find minimum angle between joint
				// edges (maximum of cosine)
				pt1 = (CvPoint*) cvGetSeqElem(result, i);
				pt2 = (CvPoint*) cvGetSeqElem(result, i - 2);
				pt0 = (CvPoint*) cvGetSeqElem(result, i - 1);

				dx1 = pt1->x - pt0->x;
				dy1 = pt1->y - pt0->y;
				dx2 = pt2->x - pt0->x;
				dy2 = pt2->y - pt0->y;

				t = fabs((dx1 * dx2 + dy1 * dy2) / sqrt((dx1 * dx1 + dy1 * dy1)
						* (dx2 * dx2 + dy2 * dy2) + 1e-10));

				r = sqrt(dx1 * dx1 + dy1 * dy1);
				s = s > t ? s : t;
				min = min < r ? min : r;
				max = max > r ? max : r;
			}
			r = max / min;

			// if cosines of all angles are small
			// (all angles are ~90 degree) then assume it is a square.
			if ((s < 0.2 && r < 1.7) || (s < 0.5 && r < 1.5) || (s < 0.75 && r
					< 1.2)) {
				float x1, y1, x2, y2, x3, y3, x4, y4;
				pt0 = (CvPoint*) cvGetSeqElem(result, 1);
				pt1 = (CvPoint*) cvGetSeqElem(result, 2);
				pt2 = (CvPoint*) cvGetSeqElem(result, 3);
				pt3 = (CvPoint*) cvGetSeqElem(result, 4);

				//get the width of the image.
				int w = tgray->widthStep;

				//Array to store the rotation vector.
				float rv[3] = { 0.0, 0.0, 0.0 };

				//Some CvMat wrappers for the translation and rotation vector
				//and the roation matrix.
				CvMat T = cvMat(3, 1, CV_32F, tv);
				CvMat RV = cvMat(3, 1, CV_32F, rv);
				CvMat R = cvMat(3, 3, CV_32F, rotMat);

				//Create some matrices and appropriate options to
				//access the values.
				CvMat *OP = cvCreateMat(5, 1, CV_32FC3);
				CvPoint3D32f *op = (CvPoint3D32f *) OP->data.fl;

				CvMat *IP = cvCreateMat(5, 1, CV_32FC2);
				CvPoint2D32f *ip = (CvPoint2D32f *) IP->data.fl;

#ifdef LOG_OUTPUT_ON
				LOGD("k Matrix");
				sprintf(tmp, "K(x,y,z) : (%3.2f, %3.2f, %3.2f)", kMat[0] , kMat[1], kMat[2]);
				LOGD(tmp);
				sprintf(tmp, "K(x,y,z) : (%3.2f, %3.2f, %3.2f)", kMat[3] , kMat[4], kMat[5]);
				LOGD(tmp);
				sprintf(tmp, "K(x,y,z) : (%3.2f, %3.2f, %3.2f)", kMat[6] , kMat[7], kMat[8]);
				LOGD(tmp);
#endif

				//Set the points in an array.
				ip[0].x = pt0->x;
				ip[0].y = pt0->y;
				ip[1].x = pt1->x;
				ip[1].y = pt1->y;
				ip[2].x = pt2->x;
				ip[2].y = pt2->y;
				ip[3].x = pt3->x;
				ip[3].y = pt3->y;

				//Calculate an additional point in the center of the marker.
				//This is done by intersecting the two diagonal lines of the
				//square.
				int vertical;
				float a1, a2, b1, b2;
				vertical = (ip[0].x - ip[2].x);
				if (fabs(vertical) < 0.009) {
					a1 = 1024;//signal this is a vertical line.
					b1 = ip[0].x;
				} else {
					a1 = (ip[0].y - ip[2].y) / vertical;
					b1 = ip[0].y - a1 * ip[0].x;
				}
#ifdef LOG_OUTPUT_ON
				sprintf(tmp, "a1,b1 : %4.3f, %4.3f", a1, b1);
				LOGD(tmp);
#endif
				vertical = (ip[1].x - ip[3].x);
				if (fabs(vertical) < 0.009) {
					a2 = 1024;//signal this is a vertical line.
					b2 = ip[1].x;
				} else {
					a2 = (ip[1].y - ip[3].y) / vertical;
					b2 = ip[1].y - a2 * ip[1].x;
				}
#ifdef LOG_OUTPUT_ON
				sprintf(tmp, "a2,b2 : %4.3f, %4.3f", a2, b2);
				LOGD(tmp);
#endif
				if (a1 != 1024 && a2 != 1024) {
					ip[4].x = (b1 - b2) / (a2 - a1);
					ip[4].y = a1 * ip[4].x + b1;
#ifdef LOG_OUTPUT_ON
					sprintf(tmp, "X,Y (perfect): %4.3f, %4.3f", ip[4].x, ip[4].y);
#endif
				} else if (a2 != 1024) {
					ip[4].x = b1;
					ip[4].y = a2 * ip[4].x + b2;
#ifdef LOG_OUTPUT_ON
					sprintf(tmp, "X,Y (perfect): %4.3f, %4.3f", ip[4].x, ip[4].y);
#endif
				} else {
					ip[4].x = b2;
					ip[4].y = a1 * ip[4].x + b1;
#ifdef LOG_OUTPUT_ON
					sprintf(tmp, "X,Y (perfect): %4.3f, %4.3f", ip[4].x, ip[4].y);
#endif
				}
#ifdef LOG_OUTPUT_ON
				LOGD(tmp);
#endif

				//Set the corresponding 3D virtual points.
				op[0].x = -1.00;
				op[0].y = 1.00;
				op[0].z = 0.00;

				op[1].x = 1.00;
				op[1].y = 1.00;
				op[1].z = 0.00;

				op[2].x = 1.00;
				op[2].y = -1.00;
				op[2].z = 0.00;

				op[3].x = -1.00;
				op[3].y = -1.00;
				op[3].z = 0.00;

				op[4].x = 0.00;
				op[4].y = 0.00;
				op[4].z = 0.00;
#ifdef LOG_OUTPUT_ON
				LOGD("OP Matrix");
				sprintf(tmp, "OP1(x,y,z) : (%3.2f, %3.2f, %3.2f)", op[0].x , op[0].y, op[0].z);
				LOGD(tmp);
				sprintf(tmp, "OP2(x,y,z) : (%3.2f, %3.2f, %3.2f)", op[1].x , op[1].y, op[1].z);
				LOGD(tmp);
				sprintf(tmp, "OP3(x,y,z) : (%3.2f, %3.2f, %3.2f)", op[2].x , op[2].y, op[2].z);
				LOGD(tmp);
				sprintf(tmp, "OP4(x,y,z) : (%3.2f, %3.2f, %3.2f)", op[3].x , op[3].y, op[3].z);
				LOGD(tmp);
				sprintf(tmp, "OP5(x,y,z) : (%3.2f, %3.2f, %3.2f)", op[4].x , op[4].y, op[4].z);
				LOGD(tmp);
				LOGD("IP Matrix");
				sprintf(tmp, "IP1(x,y) : (%3.2f, %3.2f)", ip[0].x , ip[0].y);
				LOGD(tmp);
				sprintf(tmp, "IP2(x,y) : (%3.2f, %3.2f)", ip[1].x , ip[1].y);
				LOGD(tmp);
				sprintf(tmp, "IP3(x,y) : (%3.2f, %3.2f)", ip[2].x , ip[2].y);
				LOGD(tmp);
				sprintf(tmp, "IP4(x,y) : (%3.2f, %3.2f)", ip[3].x , ip[3].y);
				LOGD(tmp);
				sprintf(tmp, "IP5(x,y) : (%3.2f, %3.2f)", ip[4].x , ip[4].y);
				LOGD(tmp);
#endif
				//Find the extrinsic camera parameters. Meaning find the
				//rotation and translation vector.
				cvFindExtrinsicCameraParams2(OP, IP, &K, &D, &RV, &T);
				cvRodrigues2(&RV, &R, 0);
#ifdef LOG_OUTPUT_ON
				sprintf(tmp, "RV(x,y,z) : (%3.2f, %3.2f, %3.2f)", rv[0], rv[1], rv[2]);
				LOGD(tmp);
				sprintf(tmp, "TV(x,y,z) : (%3.2f, %3.2f, %3.2f)", tv[0] , tv[1], tv[2]);
				LOGD(tmp);
				LOGD("Rotation Matrix:");
				sprintf(tmp, "%3.2f, %3.2f, %3.2f", rotMat[0] , rotMat[1], rotMat[2] );
				LOGD(tmp);
				sprintf(tmp, "%3.2f, %3.2f, %3.2f", rotMat[3], rotMat[4], rotMat[5] );
				LOGD(tmp);
				sprintf(tmp, "%3.2f, %3.2f, %3.2f", rotMat[6] , rotMat[7], rotMat[8] );
				LOGD(tmp);
#endif

				//Now detect the actual marker info.


				//Stores the current pixel value.
				int currentSample = 0;

				float borderTestPntsX[20] = { -0.8333, -0.5000, -0.1667,
						0.1667, 0.5000, 0.8333, 0.8333, 0.8333, 0.8333, 0.8333,
						0.8333, 0.5000, 0.1667, -0.1667, -0.5000, -0.8333,
						-0.8333, -0.8333, -0.8333, -0.8333 };
				float borderTestPntsY[20] = { 0.8333, 0.8333, 0.8333, 0.8333,
						0.8333, 0.8333, 0.5000, 0.1667, -0.1667, -0.5000,
						-0.8333, -0.8333, -0.8333, -0.8333, -0.8333, -0.8333,
						-0.5000, -0.1667, 0.1667, 0.5000 };
				char borderCorrect = 1;

				//Work variables
				int a, imageX, imageY, pointer = 0;
				float ipx, ipy, ipz = 0.0000;
				char* imageData = tgray->imageData;

				//Create 3D points and use the translation matrix
				//to calculate the location within the image.
				//Then test the points on color.

				for (a = 0; a < 20; a++) {
					ipx = rotMat[0] * borderTestPntsX[a] + rotMat[1]
							* borderTestPntsY[a] /*+ rotMat[2] * 0.0 */+ tv[0];
					ipy = rotMat[3] * borderTestPntsX[a] + rotMat[4]
							* borderTestPntsY[a] /*+ rotMat[5] * 0.0 */+ tv[1];
					ipz = rotMat[6] * borderTestPntsX[a] + rotMat[7]
							* borderTestPntsY[a] /*+ rotMat[8] * 0.0 */+ tv[2];

					imageX = ((kMat[0] * ipx) / ipz) + kMat[2];
					imageY = ((kMat[4] * ipy) / ipz) + kMat[5];
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
						borderCorrect = 0;
					}

				}

				if (borderCorrect) {
					float orientationTestPntsX[4] = { -0.5, 0.5, 0.5, -0.5 };
					float orientationTestPntsY[4] = { 0.5, 0.5, -0.5, -0.5 };
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

						imageX = ((kMat[0] * ipx) / ipz) + kMat[2];
						imageY = ((kMat[4] * ipy) / ipz) + kMat[5];
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
						float testPntsX[12] = { -0.1667, 0.1667, -0.5000,
								-0.1667, 0.1667, 0.5000, -0.5000, -0.1667,
								0.1667, 0.5000, -0.1667, 0.1667 };
						float testPntsY[12] = { 0.5000, 0.5000, 0.1667, 0.1667,
								0.1667, 0.1667, -0.1667, -0.1667, -0.1667,
								-0.1667, -0.5000, -0.5000 };
						//Store the orientation change caused by a semi-random
						//detection order.
						float orientationChange=0.0f;
						//Stores the order in which the code should be read.
						int* order;
						//tttpoiaia := twoToThePowerOfInvertedArrayIndexArray
						int tttpoiaia[12] = { 2048, 1024, 512, 256, 128, 64,
								32, 16, 8, 4, 2, 1 };

						code = 0;
						if (orientation == 0) {
							int order2[12] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
									10, 11 };
							order = order2;
							orientationChange = 0.0f;

						} else if (orientation == 1) {
							int order2[12] = { 5, 9, 1, 4, 8, 11, 0, 3, 7, 10,
									2, 6 };
							order = order2;
							orientationChange = 90.0f;

						} else if (orientation == 2) {
							int order2[12] = { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2,
									1, 0 };
							order = order2;
							orientationChange = 180.0f;

						} else if (orientation == 3) {
							int order2[12] = { 6, 2, 10, 7, 3, 0, 11, 8, 4, 1,
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

							imageX = ((kMat[0] * ipx) / ipz) + kMat[2];
							imageY = ((kMat[4] * ipy) / ipz) + kMat[5];
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
#ifdef LOG_OUTPUT_ON
						sprintf(tmp, "Detected marker, id: %d", code);
						LOGD(tmp);
#endif
						//Only now a marker has been detected and the info can be
						//added to the return list.

						counter++;
						//If calibration needs to be done, use these points
						//by storing them in the appropriate arrays.
						if (calibrateNext && frameCounter >= 0) {
#ifdef LOG_OUTPUT_ON
							LOGD("Adding points to list");
#endif

							CvPoint3D32f *opCalib =
									(CvPoint3D32f *) OPCalib->data.fl;
							CvPoint2D32f *ipCalib =
									(CvPoint2D32f *) IPCalib->data.fl;
							int *pointCnt = PointCnt->data.i;

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

							ipCalib[frameCounter * 4].x = pt0->x;
							ipCalib[frameCounter * 4].y = pt0->y;

							ipCalib[frameCounter * 4 + 1].x = pt1->x;
							ipCalib[frameCounter * 4 + 1].y = pt1->y;

							ipCalib[frameCounter * 4 + 2].x = pt2->x;
							ipCalib[frameCounter * 4 + 2].y = pt2->y;

							ipCalib[frameCounter * 4 + 3].x = pt3->x;
							ipCalib[frameCounter * 4 + 3].y = pt3->y;

							pointCnt[frameCounter] = 4;
#ifdef LOG_OUTPUT_ON
							int itr;
							for(itr=0;itr<60;itr++) {
								sprintf(tmp, "nr %d :OP= %3.2f, %3.2f IP =%3.2f, %3.2f", itr, opCalib[itr].x, opCalib[itr].y, ipCalib[itr].x, ipCalib[itr].y);
								LOGD(tmp);
							}
#endif
							frameCounter--;
						}

						//calculate the rotation matrix. Invert some values since
						//it is needed for OpenGL.
						rv[1] = -1.0f * rv[1];
						rv[2] = -1.0f * rv[2];
						cvRodrigues2(&RV, &R, 0);

						//Increase the detected marker count
						returnVals[0]++;

						orientationChange = (orientationChange*M_PI)/180.0f;


						float temp0;
						float temp3;
						float temp6;

						temp0= cos(orientationChange)*rotMat[0] + sin(orientationChange)*rotMat[1];
						temp3= cos(orientationChange)*rotMat[3] + sin(orientationChange)*rotMat[4];
						temp6= cos(orientationChange)*rotMat[6] + sin(orientationChange)*rotMat[7];
						rotMat[1]= -sin(orientationChange)*rotMat[0] + cos(orientationChange)*rotMat[1];
						rotMat[4]= -sin(orientationChange)*rotMat[3] + cos(orientationChange)*rotMat[4];
						rotMat[7]= -sin(orientationChange)*rotMat[6] + cos(orientationChange)*rotMat[7];

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
				cvReleaseMat(&OP);
				cvReleaseMat(&IP);

			}

		}

		// take the next contour
		contours = contours->h_next;
	}

	//If one or more markers were detected use the marker threshold.
	if (counter) {
		useMarkerthreshold = 0;

		//Stores the current calculated threshold
		currentMarkerThresholdBlack /= blackCounter;
		currentMarkerThresholdWhite /= whiteCounter;
		markerThreshold = (currentMarkerThresholdBlack
				+ currentMarkerThresholdWhite) >> 1;
	} else {
		//No marker was detected decrease the length in which the marker threshold
		//Should still be used.
		if (useMarkerthreshold > 0) {
			useMarkerthreshold--;
		}
	}
	//Release the storage created for certain methodes.
	cvReleaseMemStorage(&cvStorage);

	//Convert the c data into java data again to pass it to the storage array.
	// Java should then be able to access the data in the array.
	(*env)->SetFloatArrayRegion(env, returnInfo, 0, returnValPnt, returnVals);

	//Release the frames, otherwise the memory will overflow and the app
	//will crash.
	cvReleaseImage(&gray);
	cvReleaseImage(&tgray);

	//return the number of calibration frames still to do.
	//Should be changed.
	return (MAX_CALIB_FRAMES - frameCounter - 1);

}
/**
 * Start the calibration.
 */
JNIEXPORT jboolean JNICALL Java_nativeLib_NativeLib_calibrate(
		JNIEnv * env, jobject obj, jint height, jint width) {

	if (frameCounter == -1) {
#ifdef LOG_OUTPUT_ON
		LOGD("Calibrating camera parameters");
#endif
		K = cvMat(3, 3, CV_64F, kMat);
		D = cvMat(5, 1, CV_32F, l);
		cvCalibrateCamera2(OPCalib, IPCalib, PointCnt, cvSize(width, height),
				&K, &D, NULL, NULL, 0);
		cvReleaseMat(&OPCalib);
		cvReleaseMat(&IPCalib);
		cvReleaseMat(&PointCnt);
		OPCalib = cvCreateMat(4 * MAX_CALIB_FRAMES, 1, CV_32FC3);
		IPCalib = cvCreateMat(4 * MAX_CALIB_FRAMES, 1, CV_32FC2);
		PointCnt = cvCreateMat(MAX_CALIB_FRAMES, 1, CV_32SC1);

		frameCounter = MAX_CALIB_FRAMES - 1;
	}

	return 1;
}
//Get the current calibration data
JNIEXPORT jboolean JNICALL Java_nativeLib_NativeLib_getCalibration(
		JNIEnv * env, jobject obj, jdoubleArray cameraMatrix,
		jdoubleArray distCoeff) {
	//return the current calibration data;
	(*env)->SetDoubleArrayRegion(env, cameraMatrix, 0, 9, kMat);
	(*env)->SetDoubleArrayRegion(env, distCoeff, 0, 5, l);
	return 1;//true
}
//Set new calibration data
JNIEXPORT jboolean JNICALL Java_de_rwth_ar_nativeLib_NativeLib_setCalibration(
		JNIEnv * env, jobject obj, jdoubleArray cameraMatrix,
		jdoubleArray distCoeff) {
	//Parse the calibration data.
	(*env)->GetDoubleArrayRegion(env, cameraMatrix, 0, 9, kMatNew);
	(*env)->GetDoubleArrayRegion(env, distCoeff, 0, 5, lNew);
	newParams = 1;
	return 1;//true

}
//Set the loaded calibration data and retrieve some constants
JNIEXPORT jboolean JNICALL Java_nativeLib_NativeLib_initThread(
		JNIEnv * env, jobject obj, jintArray parameterArray,
		jdoubleArray cameraMatrix, jdoubleArray distCoeff) {
	(*env)->GetDoubleArrayRegion(env, cameraMatrix, 0, 9, kMatNew);
	(*env)->GetDoubleArrayRegion(env, distCoeff, 0, 5, lNew);
	newParams = 1;
	int constants[] = { MAX_MARKER_INDEX, MAX_CALIB_FRAMES };
	(*env)->SetIntArrayRegion(env, parameterArray, 0, 2, constants);
	OPCalib = cvCreateMat(4 * MAX_CALIB_FRAMES, 1, CV_32FC3);
	IPCalib = cvCreateMat(4 * MAX_CALIB_FRAMES, 1, CV_32FC2);
	PointCnt = cvCreateMat(MAX_CALIB_FRAMES, 1, CV_32SC1);
	K = cvMat(3, 3, CV_64F, kMat);
	D = cvMat(5, 1, CV_32F, l);
	return 1;//true


}
