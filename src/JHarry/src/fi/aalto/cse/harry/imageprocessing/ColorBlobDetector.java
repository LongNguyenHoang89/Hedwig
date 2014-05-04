package fi.aalto.cse.harry.imageprocessing;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import fi.aalto.cse.harry.structure.RectangleDimensions;

public class ColorBlobDetector {
 // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.1;
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(25, 50, 50, 0);
    private Mat mSpectrum = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();

    private Scalar CONTOUR_COLOR = new Scalar(255,0,0,255);
    
    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();

    public void setColorRadius(Scalar radius) {
	mColorRadius = radius;
    }

    public void setHsvColor(Scalar hsvColor) {
	double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]
		- mColorRadius.val[0]
		: 0;
	double maxH = (hsvColor.val[0] + mColorRadius.val[0] <= 255) ? hsvColor.val[0]
		+ mColorRadius.val[0]
		: 255;

	mLowerBound.val[0] = minH;
	mUpperBound.val[0] = maxH;

	mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
	mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

	mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
	mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

	mLowerBound.val[3] = 0;
	mUpperBound.val[3] = 255;

	Mat spectrumHsv = new Mat(1, (int) (maxH - minH), CvType.CV_8UC3);

	for (int j = 0; j < maxH - minH; j++) {
	    byte[] tmp = { (byte) (minH + j), (byte) 255, (byte) 255 };
	    spectrumHsv.put(0, j, tmp);
	}

	Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }

    public Mat getSpectrum() {
	return mSpectrum;
    }

    public void setMinContourArea(double area) {
	mMinContourArea = area;
    }

    public void setHsvRange(Scalar low, Scalar up) {
	mLowerBound = low;
	mUpperBound = up;
    }

    public BufferedImage test(Mat rgbaImage) {
	Imgproc.pyrDown(rgbaImage, mPyrDownMat);
	Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);
	//return CVUtils.MatToBufferedImage(mPyrDownMat);
	Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);
	//return CVUtils.MatToBufferedImage(mHsvMat);
	Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
	return CVUtils.MatToBufferedImage(mMask);

	//Imgproc.dilate(mMask, mDilatedMask, new Mat());
	//return CVUtils.MatToBufferedImage(mDilatedMask);
    }
    
    public List<RectangleDimensions> process(Mat rgbaImage) {
	Imgproc.pyrDown(rgbaImage, mPyrDownMat);
	Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

	Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);
	// return MatToBufferedImage(mHsvMat);
	Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
	// return MatToBufferedImage(mMask);

	Imgproc.dilate(mMask, mDilatedMask, new Mat());
	// return MatToBufferedImage(mDilatedMask);

	List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

	Imgproc.findContours(mDilatedMask, contours, mHierarchy,
		Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

	// Find max contour area
	double maxArea = 0;
	Iterator<MatOfPoint> each = contours.iterator();
	while (each.hasNext()) {
	    MatOfPoint wrapper = each.next();
	    double area = Imgproc.contourArea(wrapper);
	    if (area > maxArea)
		maxArea = area;
	}

	// Filter contours by area and resize to fit the original image size
	mContours.clear();
	each = contours.iterator();
	while (each.hasNext()) {
	    MatOfPoint contour = each.next();
	    if (Imgproc.contourArea(contour) > mMinContourArea * maxArea) {
		Core.multiply(contour, new Scalar(4, 4), contour);
		mContours.add(contour);
	    }
	}

	Imgproc.drawContours(rgbaImage, contours, -1, CONTOUR_COLOR);

	// Get the rectangle around the image
	List<RectangleDimensions> rectDimensions = new ArrayList<RectangleDimensions>();
	if (mContours.size() > 0) {
	    Rect rect = Imgproc.boundingRect(getLargestContour(mContours));

	    double x1 = rect.x;
	    double x2 = rect.x + rect.width;
	    double y1 = rect.y;
	    double y2 = rect.y + rect.height;

	    Core.rectangle(rgbaImage, new Point(x1, y1), new Point(x2, y2),
		    new Scalar(100, 10, 10));

	    rectDimensions.add(new RectangleDimensions((int) x1, (int) y1, rect.width, rect.height));
	    //System.out.println("Width: " + rect.width + " Height: " + rect.height);
	}

	//return CVUtils.MatToBufferedImage(rgbaImage);
	return rectDimensions;
    }

    public List<MatOfPoint> getContours() {
	return mContours;
    }

    private MatOfPoint getLargestContour(List<MatOfPoint> contours) {
	double area = 0;
	MatOfPoint matOfPoint = null;
	for (MatOfPoint point : contours) {
	    double tmp = Imgproc.contourArea(point);
	    if (tmp > area) {
		matOfPoint = point;
		area = tmp;
	    }
	}
	return matOfPoint;
    }
}
