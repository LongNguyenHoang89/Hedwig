package fi.aalto.cse.harry.worker;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.opencv.core.Scalar;

import fi.aalto.cse.harry.controller.Command;
import fi.aalto.cse.harry.controller.CommandFactory;
import fi.aalto.cse.harry.controller.DroneMovementCommmandGenerator;
import fi.aalto.cse.harry.imageprocessing.CVUtils;
import fi.aalto.cse.harry.imageprocessing.ColorBlobDetector;
import fi.aalto.cse.harry.structure.ImageQueue;
import fi.aalto.cse.harry.structure.RectangleDimensionsQueue;
import fi.aalto.cse.harry.structure.RectangleDimensions;

public class ColorBlobRecognitionExecutor {

    private static ColorBlobRecognitionExecutor INSTANCE;

    private static final int NO_THREADS = 1;

    private DroneMovementCommmandGenerator droneMovCmdGen = null;

    private ColorBlobRecognitionExecutor() {
	ExecutorService executor = Executors.newFixedThreadPool(NO_THREADS);
	for (int i = 0; i < NO_THREADS; i++) {
	    executor.execute(new ColorBlobRecognitionRunnable());
	}
    }

    public static void initialize() {
	synchronized (ColorBlobRecognitionExecutor.class) {
	    if (INSTANCE == null) {
		synchronized (ColorBlobRecognitionExecutor.class) {
		    INSTANCE = new ColorBlobRecognitionExecutor();
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
    private class ColorBlobRecognitionRunnable implements Runnable {
	private ColorBlobDetector detector;
	private boolean isRed = true;

	public ColorBlobRecognitionRunnable() {
	    detector = new ColorBlobDetector();
	    // Pillow
	    // detector.setHsvRange(new Scalar(155, 140, 59), new Scalar(180,
	    // 247,253));

	}

	@Override
	public void run() {
	    while (true) {
		if (isEmpty()) {
		    sleepTime();
		} else {
		    BufferedImage buf = getImageFromQueue();
		    if (buf != null) {
			if (droneMovCmdGen == null) {
			    droneMovCmdGen = DroneMovementCommmandGenerator
				    .getInstance(buf.getWidth(),
					    buf.getHeight());
			}

			if (isRed) {
			    // Red stripe
			    detector.setHsvRange(new Scalar(125, 44, 159),
				    new Scalar(180, 212, 232));
			} else {
			    // Blue stripe
			    detector.setHsvRange(new Scalar(104, 58, 81),
				    new Scalar(121, 208, 255));
			}
			
			List<RectangleDimensions> rectDimensionsList = detector
				.process(CVUtils.BufferedImageToMat(buf));
			// List<RectangleDimensions> rectDimensionsList = new
			// ArrayList<RectangleDimensions>();
			// Add dimension to queue.
			if (rectDimensionsList.size() > 0) {
			    RectangleDimensions rectDimension = rectDimensionsList
				    .get(0);
			    droneMovCmdGen.generateCommand(rectDimension);
			    /*
			    if (CommandFactory.getInstance()
				    .getCurrentCommand() != null) {
				if (CommandFactory.getInstance()
					.getCurrentCommand().command == Command.STOP
					.getValue()) {
				    // We stop once, flip the stripe color
				    System.out.println("flip color");
				    isRed = !isRed;
				}
			    }*/
			    RectangleDimensionsQueue.getInstance().addRectangleDimensions(rectDimensionsList);
			}
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