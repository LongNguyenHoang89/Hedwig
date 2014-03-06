package fi.aalto.cse.harry.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.BorderLayout;

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
	frame.setBounds(100, 100, 1124, 868);
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
}
