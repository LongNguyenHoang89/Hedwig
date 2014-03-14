package fi.aalto.cse.harry.worker;

import java.awt.image.BufferedImage;

import fi.aalto.cse.harry.Constants;
import fi.aalto.cse.harry.server.StreamingServer;
import fi.aalto.cse.harry.structure.ImageQueue;
import fi.aalto.cse.harry.ui.ImagePanel;

/**
 * 
 */
public class UpdateFrameThread implements Runnable {
	private ImageQueue imageQueue;

	public UpdateFrameThread(ImagePanel panel) {		
		imageQueue = ImageQueue.getInstance();		
		// Just to initialize ImageExporterExecutor and DisplayImageInPanelExecutor
		FaceDetectionExecutor.initialize();
		DisplayImageInPanelExecutor.initialize(panel);
	}

	@Override
	public void run() {
		try {
			StreamingServer server = new StreamingServer(
					Constants.VIDEO_SOCKET_PORT);
			while (true) {
				if (server.in.available() != 0) {
					BufferedImage buf = server.ReadImage();
					if (buf != null) {
						// Add image for rendering and face detection
						imageQueue.addImage(buf);											
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}