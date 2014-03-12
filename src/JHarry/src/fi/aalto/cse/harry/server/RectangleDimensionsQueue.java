package fi.aalto.cse.harry.server;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import fi.aalto.cse.harry.processing.RectanleDimensions;

/**
 * Stores list of rectangle dimensions where faces were detected.
 * FaceDetectionExecutor adds entries to the Queue and
 * DisplayImageInPanelExecutor removes entries from the queue.
 * 
 */
public class RectangleDimensionsQueue {

	private LinkedBlockingQueue<List<RectanleDimensions>> sRectangleQueue = null;

	private static final RectangleDimensionsQueue INSTANCE = new RectangleDimensionsQueue();

	private RectangleDimensionsQueue() {
		sRectangleQueue = new LinkedBlockingQueue<List<RectanleDimensions>>();
	}

	public static RectangleDimensionsQueue getInstance() {
		return INSTANCE;
	}

	public boolean addRectangleDimensions(
			List<RectanleDimensions> rectDimensions) {
		return sRectangleQueue.add(rectDimensions);
	}

	public List<RectanleDimensions> getRectangleDimensions() {
		if (isEmpty()) {
			return null;
		}
		return sRectangleQueue.remove();
	}

	public boolean isEmpty() {
		return sRectangleQueue.isEmpty();
	}
}