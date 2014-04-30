package fi.aalto.cse.harry.worker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fi.aalto.cse.harry.structure.ImageQueue;
import fi.aalto.cse.harry.structure.RectangleDimensionsQueue;
import fi.aalto.cse.harry.structure.RectangleDimensions;
import fi.aalto.cse.harry.ui.ImagePanel;

public class DisplayImageInPanelExecutor {

	private static DisplayImageInPanelExecutor INSTANCE;

	private static final int NO_THREADS = 1;

	private DisplayImageInPanelExecutor(ImagePanel panel) {
		ExecutorService executor = Executors.newFixedThreadPool(NO_THREADS);
		for (int i = 0; i < NO_THREADS; i++) {
			executor.execute(new DisplayImageRunnable(panel));
		}
	}

	public static void initialize(ImagePanel panel) {
		synchronized (DisplayImageInPanelExecutor.class) {
			if (INSTANCE == null) {
				synchronized (DisplayImageInPanelExecutor.class) {
					INSTANCE = new DisplayImageInPanelExecutor(panel);
				}
			}
		}
	}
	
	/**
	 * Access the ImageQueue and gets the image. Then checks if there is rectangle
	 * dimensions list in RectangleDimensionsQueue. If there is draws image with
	 * rectangles.
	 * 
	 */
	private class DisplayImageRunnable implements Runnable {
		private ImagePanel framePanel;

		public DisplayImageRunnable(ImagePanel panel) {
			framePanel = panel;
		}

		@Override
		public void run() {
			while (true) {
				if (isEmpty()) {
					sleepTime();
				} else {
					BufferedImage buf = getImageFromQueue();
					displayImageWithRectangles(buf);
				}
			}
		}

		private void displayImageWithRectangles(BufferedImage image) {
			if (image == null) {
				return;
			}
			List<RectangleDimensions> rectDimensionsList = RectangleDimensionsQueue
					.getInstance().getRectangleDimensions();
			if (rectDimensionsList != null && rectDimensionsList.size() > 0) {
				drawRectangles(image, rectDimensionsList);
			}
			framePanel.setimage(image);
		}

		private void drawRectangles(BufferedImage image,
				List<RectangleDimensions> rectDimensionsList) {
			Graphics2D graph = image.createGraphics();
			graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			graph.setColor(Color.BLUE);
			graph.setStroke(new BasicStroke(1));
			for (RectangleDimensions rectDimensions : rectDimensionsList) {
				graph.draw(new Rectangle(rectDimensions.getX(), rectDimensions
						.getY(), rectDimensions.getWidth(), rectDimensions
						.getHeight()));
			}
			graph.dispose();
		}
		
		private void sleepTime() {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private BufferedImage getImageFromQueue() {
		ImageQueue imageQueue = ImageQueue.getInstance();
		return imageQueue.getImage();
	}

	private boolean isEmpty() {
		ImageQueue imageQueue = ImageQueue.getInstance();
		return imageQueue.isEmpty();
	}	
}