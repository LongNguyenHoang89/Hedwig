package fi.aalto.cse.harry.ui;

import java.awt.image.BufferedImage;

import fi.aalto.cse.harry.Constants;
import fi.aalto.cse.harry.processing.FaceDetection;
import fi.aalto.cse.harry.server.StreamingServer;

/**
 * @author Long
 * 
 */
public class UpdateFrameThread implements Runnable {
    private ImagePanel framePanel;
    private FaceDetection faceDetection;
    
    public UpdateFrameThread(ImagePanel panel) {
        framePanel = panel;
        faceDetection = new FaceDetection();
    }

    @Override
    public void run() {
        try {
    	StreamingServer server = new StreamingServer(
    		Constants.VIDEO_SOCKET_PORT);

    	while (true) {
    	    if (server.in.available() != 0) {
    		BufferedImage buf = server.ReadImage();
     		buf = faceDetection.DetectAndDisplay(buf);
    		if (buf != null) {    		    
    		    framePanel.UpdateImage(buf);
    		}
    	    }
    	}

        } catch (Exception e) {
    	e.printStackTrace();
        }
    }
}