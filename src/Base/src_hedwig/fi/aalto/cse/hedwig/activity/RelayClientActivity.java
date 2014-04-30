package fi.aalto.cse.hedwig.activity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.parrot.freeflight.R;
import com.parrot.freeflight.drone.DroneConfig;
import com.parrot.freeflight.receivers.DroneReadyReceiver;
import com.parrot.freeflight.receivers.DroneReadyReceiverDelegate;
import com.parrot.freeflight.service.DroneControlService;

import fi.aalto.cse.hedwig.Constant;
import fi.aalto.cse.hedwig.HedwigLog;
import fi.aalto.cse.hedwig.controller.DroneController;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.widget.TextView;

/**
 * Code was adapted partly from
 * com.parrot.freeflight.activities.ControlDroneActivity
 * com.parrot.freeflight.activities.ConnectActivity
 * 
 * Main Activity for Hedwig.
 * 
 * @author Long
 * @see http 
 *      ://examples.javacodegeeks.com/android/core/socket-core/android-socket
 *      -example
 */
public class RelayClientActivity extends Activity implements ServiceConnection,
	DroneReadyReceiverDelegate {

    // We need a socket to keep connection
    private Socket clientSocket;
    private PrintWriter socketOut;
    private DataInputStream socketIn;

    private TextView text;

    // Service controlling the drone
    private DroneControlService droneControlService;

    // drone controller
    private DroneController controller;

    // Receiver
    private BroadcastReceiver droneReadyReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_relay_client);

	// Get text viewer
	text = (TextView) findViewById(R.id.testTxt);

	// Register this receiver so we will receive event whenever the drone is
	// ready
	droneReadyReceiver = new DroneReadyReceiver(this);
	LocalBroadcastManager manager = LocalBroadcastManager
		.getInstance(getApplicationContext());
	manager.registerReceiver(droneReadyReceiver, new IntentFilter(
		DroneControlService.DRONE_STATE_READY_ACTION));

	// Bind to drone control service. We'll send command to this service to
	// control the drone.
	bindService(new Intent(this, DroneControlService.class), this,
		Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.relay_server, menu);
	return true;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
	// Connected to drone control service
	droneControlService = ((DroneControlService.LocalBinder) service)
		.getService();
	droneControlService.resume();
	DroneConfig config = droneControlService.getDroneConfig();
	controller = new DroneController(droneControlService);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
	// TODO Auto-generated method stub

    }

    @Override
    public void onDroneReady() {
	HedwigLog.logFunction(this, "onDroneReady");
	// Fork a new thread for client
	new Thread(new ClientThread()).start();

	// Drone is ready, we start new activity to render stream from camera
	Intent videoStreamActivity = new Intent(this, VideoStreamActivity.class);
	startActivity(videoStreamActivity);
    }

    class ClientThread implements Runnable {
	@Override
	public void run() {
	    HedwigLog.logFunction(this, "Run");
	    try {
		InetAddress serverAddr = InetAddress
			.getByName(Constant.SERVER_IP);

		clientSocket = new Socket(serverAddr, Constant.SERVERPORT);
		HedwigLog.log(clientSocket.getRemoteSocketAddress().toString());
		socketOut = new PrintWriter(clientSocket.getOutputStream(),
			true);

		socketIn = new DataInputStream(clientSocket.getInputStream());

		int command = -1;
		float commandValue;
		while ((command = socketIn.readInt()) > -1) {
		    commandValue = socketIn.readFloat();
		    socketIn.readInt();
		    controller.processCommand(command, commandValue);
		}

	    } catch (UnknownHostException e1) {
		HedwigLog.log(e1.getMessage());
	    } catch (IOException e1) {
		HedwigLog.log(e1.getMessage());
	    }

	}
    }

}
