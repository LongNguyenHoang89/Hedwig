package fi.aalto.cse.harry.imageprocessing;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class CVUtils {

    /**
     * Convert from openCV Mat to java Buffer image
     * 
     * @param m
     * @return
     * @see http 
     *      ://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
     */
    public static BufferedImage MatToBufferedImage(Mat m) {
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

    /**
     * Convert buffer image to openCV mat
     * @param frame
     * @return
     */
    public static Mat BufferedImageToMat(BufferedImage frame) {
	Mat image = new Mat(frame.getHeight(), frame.getWidth(), CvType.CV_8UC3);
	byte[] pixels = ((DataBufferByte) frame.getRaster().getDataBuffer())
		.getData();
	image.put(0, 0, pixels);
	return image;
    }
}
