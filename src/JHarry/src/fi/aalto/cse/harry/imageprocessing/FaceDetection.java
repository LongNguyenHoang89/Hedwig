package fi.aalto.cse.harry.imageprocessing;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import fi.aalto.cse.harry.structure.RectanleDimensions;

public class FaceDetection {

    private String face_cascade_name = "resources/lbpcascade_frontalface.xml";

    private CascadeClassifier face_cascade;

    public FaceDetection() {
	// Load the cascades
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	face_cascade = new CascadeClassifier(face_cascade_name);
    }

    public List<RectanleDimensions> detectAndDisplay(BufferedImage frame) {
	Mat image = new Mat(frame.getHeight(), frame.getWidth(), CvType.CV_8UC3);
	byte[] pixels = ((DataBufferByte) frame.getRaster().getDataBuffer())
		.getData();
	image.put(0, 0, pixels);

	// Detect faces in the frame.
	// MatOfRect is a special container class for Rect.
	// long start_time = System.nanoTime();
	MatOfRect faceDetections = new MatOfRect();
	face_cascade.detectMultiScale(image, faceDetections);

	List<RectanleDimensions> rectangleDimensionList = new ArrayList<RectanleDimensions>();

	// Draw a bounding box around each face.
	for (Rect rect : faceDetections.toArray()) {
	    /*
	     * Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x
	     * + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
	     */

	    // Create rectangle dimensions
	    RectanleDimensions rectDimensions = new RectanleDimensions(rect.x,
		    rect.y, rect.width, rect.height);
	    System.out.println(rectDimensions.toString());
	    rectangleDimensionList.add(rectDimensions);

	}
	// long end_time = System.nanoTime();
	// double difference = (end_time - start_time)/1e6;
	// System.out.println(difference);

	// return MatToBufferedImage(image);
	return rectangleDimensionList;
    }

    // http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
    private BufferedImage MatToBufferedImage(Mat m) {
	int type = BufferedImage.TYPE_BYTE_GRAY;
	if (m.channels() > 1) {
	    type = BufferedImage.TYPE_3BYTE_BGR;
	}
	int bufferSize = m.channels() * m.cols() * m.rows();
	byte[] b = new byte[bufferSize];
	m.get(0, 0, b); // get all the pixels
	BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
	final byte[] targetPixels = ((DataBufferByte) image.getRaster()
		.getDataBuffer()).getData();
	System.arraycopy(b, 0, targetPixels, 0, b.length);
	return image;

    }
}
