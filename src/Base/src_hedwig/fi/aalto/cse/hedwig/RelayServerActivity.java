package fi.aalto.cse.hedwig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.parrot.freeflight.R;
import com.parrot.freeflight.service.DroneControlService;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

/**
 * Code was adapted partly from com.parrot.freeflight.activities.ControlDroneActivity
 * @author Long
 * @see http 
 *      ://examples.javacodegeeks.com/android/core/socket-core/android-socket
 *      -example
 */
public class RelayServerActivity extends Activity implements ServiceConnection{

    // We need a socket to keep connection
    private ServerSocket serverSocket;
    
    // Server should run in a separated thread
    private Thread serverThread = null;
    private Handler updateConversationHandler;
    private TextView text;
    
    // Service controlling the drone
    private DroneControlService droneControlService;
    
    // drone controller
    private DroneController controller;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_relay_server);

	updateConversationHandler = new Handler();
	
	// Get text viewer
	text = (TextView) findViewById(R.id.testTxt);
	
	// Fork a new thread for server
	this.serverThread = new Thread(new ServerThread());
	this.serverThread.start();
	
	// Bind to service
	bindService(new Intent(this, DroneControlService.class), this,Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.relay_server, menu);
	return true;
    }
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
	droneControlService = ((DroneControlService.LocalBinder) service).getService();
	droneControlService.resume();
	controller = new DroneController(droneControlService);
	
	//controller.fly(null);
	droneControlService.triggerTakeOff();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
	// TODO Auto-generated method stub
	
    }
    
    class ServerThread implements Runnable {
	public void run() {
	    Socket socket = null;
	    try {		
		serverSocket = new ServerSocket(Constant.SERVERPORT);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    while (!Thread.currentThread().isInterrupted()) {

		try {

		    socket = serverSocket.accept();

		    CommunicationThread commThread = new CommunicationThread(
			    socket);
		    new Thread(commThread).start();

		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    class CommunicationThread implements Runnable {

	private Socket clientSocket;

	private BufferedReader input;

	public CommunicationThread(Socket clientSocket) {

	    this.clientSocket = clientSocket;

	    try {

		this.input = new BufferedReader(new InputStreamReader(
			this.clientSocket.getInputStream()));

	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	public void run() {
	    while (!Thread.currentThread().isInterrupted()) {
		try {
		    String read = input.readLine();
		    if (read != null && !read.isEmpty()) {
			updateConversationHandler.post(new updateUIThread(read));
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}

    }

    class updateUIThread implements Runnable {
	private String msg;
	
	public updateUIThread(String str) {
	    this.msg = str;
	}

	@Override
	public void run() {
	    text.setText(text.getText().toString() + "Client Says: " + msg
		    + "\n");

	}
    }

}
