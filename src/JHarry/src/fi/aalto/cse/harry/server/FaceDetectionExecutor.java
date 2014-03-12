package fi.aalto.cse.harry.server;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fi.aalto.cse.harry.processing.FaceDetection;
import fi.aalto.cse.harry.processing.RectanleDimensions;

public class FaceDetectionExecutor {

	private static FaceDetectionExecutor INSTANCE;

	private static final int NO_THREADS = 1;

	private FaceDetectionExecutor() {
		ExecutorService executor = Executors.newFixedThreadPool(NO_THREADS);
		for (int i = 0; i < NO_THREADS; i++) {
			executor.execute(new FaceDetectionRunnable());
		}
	}

	public static void initialize() {
		synchronized (FaceDetectionExecutor.class) {
			if (INSTANCE == null) {
				synchronized (FaceDetectionExecutor.class) {
					INSTANCE = new FaceDetectionExecutor();
				}
			}
		}
	}

	/**
	 * Peeks image from ImageQueue, apply face detection algorithm on it. Face
	 * detection algorithm returns list of rectangle dimensions on the image
	 * where faces are detected. List of rectangle dimensions is store in
	 * {@link #RectangleDimensionsQueue}. List of rectangle dimensions is then
	 * accessed by {@link #DisplayImageInPanelExecutor} to render image with
	 * rectangles.
	 * 
	 * 
	 */
	private class FaceDetectionRunnable implements Runnable {
		private FaceDetection faceDetection;

		public FaceDetectionRunnable() {
			faceDetection = new FaceDetection();
		}

		@Override
		public void run() {
			while (true) {
				if (isEmpty()) {
					sleepTime();
				} else {
					BufferedImage buf = getImageFromQueue();
					if (buf != null) {
						// System.out.println("Face detection in progress.");
						List<RectanleDimensions> rectDimensionsList = faceDetection
								.detectAndDisplay(buf);
						// Add dimension to queue.
						RectangleDimensionsQueue.getInstance()
								.addRectangleDimensions(rectDimensionsList);
					}
				}
			}
		}
		
		private void sleepTime() {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// It does not remove image from the image queue just get the top most image
	// buffer.
	private BufferedImage getImageFromQueue() {
		ImageQueue imageQueue = ImageQueue.getInstance();
		return imageQueue.peekImage();
	}

	private boolean isEmpty() {
		ImageQueue imageQueue = ImageQueue.getInstance();
		return imageQueue.isEmpty();
	}
}