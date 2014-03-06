package fi.aalto.cse.harry.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import fi.aalto.cse.harry.Constants;
import fi.aalto.cse.harry.server.StreamingServer;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

public class Main {

    private JFrame frame;
    private ImagePanel imagePanel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    Main window = new Main();
		    window.frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the application.
     */
    public Main() {
	initialize();
	initSocket();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	frame = new JFrame();
	frame.setBounds(100, 100, 450, 300);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	imagePanel = new ImagePanel();
	frame.getContentPane().add(imagePanel, BorderLayout.CENTER);
    }

    /**
     * Initialize socket server
     */
    private void initSocket() {
	new Thread(new UpdateFrameThread(imagePanel)).start();
    }

    /**
     * @author Long
     * 
     */
    class UpdateFrameThread implements Runnable {
	private ImagePanel framePanel;

	public UpdateFrameThread(ImagePanel panel) {
	    framePanel = panel;
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
			    System.out.println("image");
			    framePanel.UpdateImage(buf);
			}
		    }
		}

	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

    }
}
