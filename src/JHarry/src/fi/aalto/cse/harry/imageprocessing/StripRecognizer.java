package fi.aalto.cse.harry.imageprocessing;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import fi.aalto.cse.harry.structure.RectangleDimensions;

public class StripRecognizer {

    private static int avgWidth = 0;

    public List<RectangleDimensions> recognize(BufferedImage imageFromClient) {
	long startTime = System.currentTimeMillis();
	// Load the native library.
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

	Mat webcam_image = convertBufferedImageToMat(imageFromClient);

	Mat hsv_image = new Mat();
	Mat thresholded = new Mat();
	Mat thresholded2 = new Mat();

	Mat array255 = new Mat(webcam_image.height(), webcam_image.width(),
		CvType.CV_8UC1);
	array255.setTo(new Scalar(255));

	Mat distance = new Mat(webcam_image.height(), webcam_image.width(),
		CvType.CV_8UC1);

	List<Mat> lhsv = new ArrayList<Mat>(3);
	Mat strips = new Mat();

	Scalar hsv_min = new Scalar(0, 50, 50, 0);
	Scalar hsv_max = new Scalar(6, 255, 255, 0);
	Scalar hsv_min2 = new Scalar(175, 50, 50, 0);
	Scalar hsv_max2 = new Scalar(179, 255, 255, 0);

	/*
	 * Scalar hsv_min = new Scalar(25, 50, 50, 0); Scalar hsv_max = new
	 * Scalar(31, 255, 255, 0); Scalar hsv_min2 = new Scalar(200, 50, 50,
	 * 0); Scalar hsv_max2 = new Scalar(204, 255, 255, 0);
	 */

	List<RectangleDimensions> rectDimensions = new ArrayList<RectangleDimensions>();

	if (!webcam_image.empty()) {

	    // One way to select a range of colors by Hue
	    Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);
	    Core.inRange(hsv_image, hsv_min, hsv_max, thresholded);
	    Core.inRange(hsv_image, hsv_min2, hsv_max2, thresholded2);
	    Core.bitwise_or(thresholded, thresholded2, thresholded);

	    Core.split(hsv_image, lhsv); // We get 3 2D one channel Mats
	    Mat S = lhsv.get(1);
	    Mat V = lhsv.get(2);
	    Core.subtract(array255, S, S);
	    Core.subtract(array255, V, V);
	    S.convertTo(S, CvType.CV_32F);
	    V.convertTo(V, CvType.CV_32F);
	    Core.magnitude(S, V, distance);
	    Core.inRange(distance, new Scalar(0.0), new Scalar(200.0),
		    thresholded2);
	    Core.bitwise_and(thresholded, thresholded2, thresholded);
	    // Apply the Hough Transform to find the circles
	    Imgproc.GaussianBlur(thresholded, thresholded, new Size(9, 9), 0, 0);

	    /*
	     * Imgproc.HoughCircles(thresholded, circles,
	     * Imgproc.CV_HOUGH_GRADIENT, 2, thresholded.height() / 4, 500, 50,
	     * 0, 0);
	     */

	    /*
	     * 
	     * int rows = circles.rows(); int elemSize = (int)
	     * circles.elemSize(); // Returns 12 (3 * // 4bytes in a // float)
	     * float[] data2 = new float[rows * elemSize / 4]; if (data2.length
	     * > 0) { circles.get(0, 0, data2); // Points to the first element
	     * // and reads the whole thing // into data2 for (int i = 0; i <
	     * data2.length; i = i + 3) { int radius = (int) data2[i + 2];
	     * 
	     * int x = ((int) data2[i] - radius); int y = ((int) data2[i + 1] -
	     * radius); int widthAndHeight = radius * 2;
	     * 
	     * rectDimensions.add(new RectangleDimensions(x, y, widthAndHeight,
	     * widthAndHeight)); } }
	     */

	    Imgproc.HoughLinesP(thresholded, strips, 1, 0.01, 80);

	    int rows = strips.rows();
	    int elemSize = (int) strips.elemSize();

	    double[] data3 = new double[rows * elemSize / 4];
	    if (data3.length > 0) {
		data3 = strips.get(0, 0);
		if (data3 != null && data3.length > 0) {
		    System.out.println("**************" + data3.length);

		    for (int i = 0; i < data3.length; i = i + 4) {
			double x1 = data3[i];
			double y1 = data3[i + 1];
			double x2 = data3[i + 2];
			double y2 = data3[i + 3];
			System.out.println("X1: " + x1 + ", Y1: " + y1
				+ ", X2: " + x2 + ", Y2: " + y2);
			/*
			 * Core.line(webcam_image, new Point(x1, y1), new
			 * Point(x2, y2), new Scalar(100, 10, 10), 3);
			 */
			int startX = (int) x1;
			// int startY = (int) y1;
			int startY = 0;
			int width = getWidth(x1, x2);
			// int height = getHeight(y1, y2);
			int height = imageFromClient.getHeight();

			System.out.println("startX: " + startX + ", startY: "
				+ startY + ", Width: " + width + ", Height: "
				+ height);
			long endTime = System.currentTimeMillis();
			System.out
				.println("Time taken for strip recognition in milliseconds : "
					+ (endTime - startTime));
			rectDimensions.add(new RectangleDimensions(startX,
				startY, width, height));
		    }
		}
	    }

	} else {
	    System.out.println(" --(!) No captured frame -- Break!");
	}
	return rectDimensions;
    }

    private Mat convertBufferedImageToMat(BufferedImage frame) {
	Mat image = new Mat(frame.getHeight(), frame.getWidth(), CvType.CV_8UC3);
	byte[] pixels = ((DataBufferByte) frame.getRaster().getDataBuffer())
		.getData();
	image.put(0, 0, pixels);
	return image;
    }

    private int getWidth(double x1, double x2) {
	int tmp = (int) (x2 - x1);
	tmp = (tmp < 0) ? (tmp * -1) : tmp;
	if (avgWidth == 0) {
	    avgWidth = tmp;
	}
	return (avgWidth + tmp) / 2;
    }
}