package fi.aalto.cse.harry.structure;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Stores list of rectangle dimensions where faces were detected.
 * FaceDetectionExecutor adds entries to the Queue and
 * DisplayImageInPanelExecutor removes entries from the queue.
 * 
 */
public class RectangleDimensionsQueue {

	private LinkedBlockingQueue<List<RectangleDimensions>> sRectangleQueue = null;

	private static final RectangleDimensionsQueue INSTANCE = new RectangleDimensionsQueue();

	private RectangleDimensionsQueue() {
		sRectangleQueue = new LinkedBlockingQueue<List<RectangleDimensions>>();
	}

	public static RectangleDimensionsQueue getInstance() {
		return INSTANCE;
	}

	public boolean addRectangleDimensions(
			List<RectangleDimensions> rectDimensions) {
		return sRectangleQueue.add(rectDimensions);
	}

	public List<RectangleDimensions> getRectangleDimensions() {
		if (isEmpty()) {
			return null;
		}
		return sRectangleQueue.remove();
	}

	public boolean isEmpty() {
		return sRectangleQueue.isEmpty();
	}
}