package fi.aalto.cse.harry.structure;

import java.awt.image.BufferedImage;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Stores images to be displayed and processed for face detection.
 * 
 * 
 *
 */
public class ImageQueue {

	private LinkedBlockingQueue<BufferedImage> sImageQueue = null;

	private static final ImageQueue INSTANCE = new ImageQueue();

	private ImageQueue() {
		sImageQueue = new LinkedBlockingQueue<BufferedImage>();
	}

	public static ImageQueue getInstance() {
		return INSTANCE;
	}

	public boolean addImage(BufferedImage image) {
		return sImageQueue.add(image);
	}

	public BufferedImage getImage() {
		if(isEmpty()) {
			return null;
		}
		return sImageQueue.remove();
	}
	
	public BufferedImage peekImage() {
		if(isEmpty()) {
			return null;
		}
		return sImageQueue.peek();
	}

	public boolean isEmpty() {
		return sImageQueue.isEmpty();
	}
}