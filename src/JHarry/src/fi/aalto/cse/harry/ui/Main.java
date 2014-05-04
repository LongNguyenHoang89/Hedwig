package fi.aalto.cse.harry.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import fi.aalto.cse.harry.controller.Command;
import fi.aalto.cse.harry.controller.CommandFactory;
import fi.aalto.cse.harry.worker.DroneControlThread;
import fi.aalto.cse.harry.worker.TestControlThread;
import fi.aalto.cse.harry.worker.UpdateFrameThread;

import java.awt.BorderLayout;

import javax.swing.JButton;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	initialize();
	initSocket();
	//initTestCommand();
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
	imagePanel.setLayout(null);

	JButton btnRun = new JButton("RUN");
	btnRun.setBounds(416, 16, 78, 29);
	imagePanel.add(btnRun);

	JButton btnDrop = new JButton("DROP");
	btnDrop.setBounds(509, 16, 88, 29);
	imagePanel.add(btnDrop);

	JButton btnEmergency = new JButton("EMERGENCY");
	btnEmergency.setBounds(612, 16, 143, 29);
	imagePanel.add(btnEmergency);

	JButton btnForward = new JButton("FORWARD");
	btnForward.setBounds(506, 680, 115, 29);
	imagePanel.add(btnForward);

	JButton btnRight = new JButton("RIGHT");
	btnRight.setBounds(653, 725, 115, 29);
	imagePanel.add(btnRight);

	JButton btnLeft = new JButton("LEFT");
	btnLeft.setBounds(354, 725, 115, 29);
	imagePanel.add(btnLeft);

	JButton btnStop = new JButton("STOP");
	btnStop.setBounds(509, 725, 115, 29);
	imagePanel.add(btnStop);
	
	
	btnRun.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		CommandFactory.getInstance().createCommand(Command.FLY);
	    }
	});
	
	btnDrop.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		CommandFactory.getInstance().createCommand(Command.DROP);
	    }
	});
	
	btnEmergency.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		CommandFactory.getInstance().createCommand(Command.EMERGENCY);
	    }
	});
	
	btnStop.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		CommandFactory.getInstance().createCommand(Command.STOP);
	    }
	});
	
	btnForward.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		CommandFactory.getInstance().createCommand(Command.FORWARD);
	    }
	});
	
	btnLeft.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		CommandFactory.getInstance().createCommand(Command.TURNLEFT);
	    }
	});
	
	btnRight.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		CommandFactory.getInstance().createCommand(Command.TURNRIGHT);
	    }
	});
    }

    /**
     * Initialize socket server
     */
    private void initSocket() {
	// Init command factory
	CommandFactory.getInstance().initialize();
	new Thread(new UpdateFrameThread(imagePanel)).start();
	new Thread(new DroneControlThread(CommandFactory.getInstance()))
		.start();
    }

    /**
     * Initialize a test command thread
     */
    private void initTestCommand() {
	// Init command factory
	new Thread(new TestControlThread(CommandFactory.getInstance())).start();
    }
}
