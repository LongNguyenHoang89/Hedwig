package fi.aalto.cse.hedwig.activity;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.parrot.freeflight.R;
import com.parrot.freeflight.drone.DroneProxy;
import com.parrot.freeflight.drone.NavData;
import com.parrot.freeflight.receivers.DroneReadyReceiver;
import com.parrot.freeflight.receivers.DroneReadyReceiverDelegate;
import com.parrot.freeflight.service.DroneControlService;

import fi.aalto.cse.hedwig.Constant;
import fi.aalto.cse.hedwig.HedwigLog;
import fi.aalto.cse.hedwig.controller.DroneController;
import fi.aalto.cse.hedwig.sync.ImageExporterExecutor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
	DroneReadyReceiverDelegate, ResultAppender {

    private Socket clientSocket;
    private BufferedOutputStream socketOut;
    private DataInputStream socketIn;

    // Service controlling the drone
    private DroneControlService droneControlService;

    // drone controller
    private DroneController controller;

    // Receiver
    private BroadcastReceiver droneReadyReceiver;

    TextView serverTextView;
    TextView cportTextView;
    TextView vportTextView;
    Button connectButton;
    ProgressBar connectionProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_relay_client);
	setupActivity();
	initDroneControlService();
    }

    private void initDroneControlService() {
	// Register this receiver so we will receive event whenever the drone is ready
	droneReadyReceiver = new DroneReadyReceiver(this);
	LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
	manager.registerReceiver(droneReadyReceiver, new IntentFilter(DroneControlService.DRONE_STATE_READY_ACTION));

	// Bind to drone control service. We'll send command to this service to
	// control the drone.
	bindService(new Intent(this, DroneControlService.class), this, Context.BIND_AUTO_CREATE);
    }

    private void setupActivity() {
	serverTextView = (TextView) findViewById(R.id.serverIPField);
	cportTextView = (TextView) findViewById(R.id.controlPortField);
	vportTextView = (TextView) findViewById(R.id.videoPortField);
	connectionProgressBar = (ProgressBar) findViewById(R.id.connectingProgressBar);
	serverTextView.setText(String.valueOf(Constant.SERVER_IP));
	cportTextView.setText(String.valueOf(Constant.SERVERPORT));
	vportTextView.setText(String.valueOf(Constant.VIDEOPORT));
	connectionProgressBar.setVisibility(View.GONE);
	connectButton = (Button) findViewById(R.id.connectButton);
	connectButton.setEnabled(false);
	connectButton.setBackgroundColor(0xFFD0D0D0);
    }

    public void connectButtonClick(View v) {
	String serverIP = serverTextView.getText().toString();
	int serverPort = Integer.parseInt(cportTextView.getText().toString());
	int videoPort = Integer.parseInt(vportTextView.getText().toString());

	// Fork a client thread to receive command from server
	ClientThread ct = new ClientThread(RelayClientActivity.this, serverIP, serverPort, videoPort);
	new Thread(ct).start();

	// Start new activity to render stream from camera
	Intent videoStreamActivity = new Intent(this, VideoStreamActivity.class);
	startActivity(videoStreamActivity);
	updateUI("2");
    }

    @Override
    public void updateUI(String flag) {
	if (Integer.parseInt(flag) == 1) {
	    Log.e("Connected", "Connected to server");
	    connectButton.setText("Connected");
	    connectButton.setEnabled(false);
	    connectButton.setBackgroundColor(0xFFD0D0D0);
	    serverTextView.setEnabled(false);
	    cportTextView.setEnabled(false);
	    vportTextView.setEnabled(false);
	    connectionProgressBar.setVisibility(View.GONE);
	} else if (Integer.parseInt(flag) == 0) {
	    Log.e("Disconnected", "Disconnected from server");
	    Toast toast = Toast.makeText(getApplicationContext(),
		    "Lost connection to server. Please re-connect!",
		    Toast.LENGTH_LONG);
	    toast.show();
	    connectButton.setEnabled(true);
	    connectButton.setText("Connect");
	    connectButton.setBackgroundColor(0xFF1565B2);
	    serverTextView.setEnabled(true);
	    cportTextView.setEnabled(true);
	    vportTextView.setEnabled(true);
	    connectionProgressBar.setVisibility(View.GONE);
	} else if (Integer.parseInt(flag) == 2) {
	    Log.e("Connecting...", "Connecting to server...");
	    connectionProgressBar.setVisibility(View.VISIBLE);
	    connectButton.setEnabled(false);
	    connectButton.setBackgroundColor(0xFFD0D0D0);
	    serverTextView.setEnabled(false);
	    cportTextView.setEnabled(false);
	    vportTextView.setEnabled(false);
	}
    }

    @Override
    public void displayError(String msg) {
	Log.e("Error", "Connection Error");
	Toast toast = Toast.makeText(getApplicationContext(), msg,
		Toast.LENGTH_LONG);
	toast.show();
	connectButton.setEnabled(true);
	connectButton.setText("Connect");
	connectButton.setBackgroundColor(0xFF1565B2);
	serverTextView.setEnabled(true);
	cportTextView.setEnabled(true);
	vportTextView.setEnabled(true);
	connectionProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void appendCommand(String value) {

	if (value.matches("Connected")) {
	    return;
	}

	final ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
	final TextView list = (TextView) findViewById(R.id.commandList);
	list.append(value);
	scroll.post(new Runnable() {
	    @Override
	    public void run() {
		scroll.fullScroll(View.FOCUS_DOWN);
	    }
	});
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
	// DroneConfig config = droneControlService.getDroneConfig();
	controller = new DroneController(droneControlService, this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
	// TODO Auto-generated method stub

    }

    @Override
    public void onDroneReady() {
	// Hedwig uses vertical camera.
	Log.e("RelayClientActivity", "Switching camera to vertical");
	droneControlService.switchCamera();
	Log.e("RelayClientActivity", "Switched camera to vertical");

	HedwigLog.logFunction(this, "onDroneReady");

	// Drone is ready, we enable the connect button
	connectButton.setEnabled(true);
	connectButton.setBackgroundColor(0xFF1565B2);
    }

    class ClientThread implements Runnable {
	protected static final int SHOW_UI = 111;
	protected static final int UPDATE_UI = 222;
	protected static final int ERROR_UI = 333;
	private boolean connected = false;
	private ResultAppender gui;
	private Handler uiHandler;
	private String serverIP;
	private int serverPort;
	private int videoPort;
	int counter = 0;

	public ClientThread(final ResultAppender gui, String serverIP,
		int serverPort, int videoPort) {
	    this.serverIP = serverIP;
	    this.serverPort = serverPort;
	    this.videoPort = videoPort;
	    this.gui = gui;
	    this.uiHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
		    if (msg.what == SHOW_UI) {
			gui.appendCommand((String) msg.obj);
			super.handleMessage(msg);
		    } else if (msg.what == UPDATE_UI) {
			gui.updateUI((String) msg.obj);
		    } else if (msg.what == ERROR_UI) {
			gui.displayError((String) msg.obj);
		    }
		}
	    };
	}

	@Override
	public void run() {
	    // HedwigLog.logFunction(this, "Run");
	    Log.e("Client Thread", "Started");
	    try {
		Log.e("Connecting", "Establishing connection to server");

		clientSocket = new Socket();
		clientSocket.connect(
			new InetSocketAddress(serverIP, serverPort), 5000);

		// Initialize image executor.
		ImageExporterExecutor.getInstance().initialize(serverIP,videoPort);
		
		// HedwigLog.log(clientSocket.getRemoteSocketAddress().toString());
		socketOut = new BufferedOutputStream(
			clientSocket.getOutputStream());
		socketIn = new DataInputStream(clientSocket.getInputStream());

		// If no exception till this point, it means socket created
		// successfully
		// Need to disable connect button, so sending a "Connected"
		// message to UI
		Message msgUI = uiHandler.obtainMessage(UPDATE_UI, "1");
		msgUI.sendToTarget();
		connected = true;
		int command = -1;
		float commandValue;
		while ((command = socketIn.readInt()) > -1) {
		    commandValue = socketIn.readFloat();
		    socketIn.readInt();
		    Date dNow = new Date();
		    SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
		    String msg = (String.valueOf(counter) + ": "
			    + ft.format(dNow) + " - " + getCommandName(command)
			    + " - " + String.valueOf(commandValue) + "\n");
		    msgUI = uiHandler.obtainMessage(SHOW_UI, msg);
		    msgUI.sendToTarget();
		    counter++;
		    controller.processCommand(command, commandValue);
		}

		// If code reaches here, there is a problem with connection to
		// server.
		// Need to handle appropriately
		Log.e("Error", "Lost connection to server?");
		msgUI = uiHandler.obtainMessage(UPDATE_UI, "0");
		msgUI.sendToTarget();

	    } catch (UnknownHostException e1) {
		// HedwigLog.log(e1.getMessage());
		Log.e("Exception UKH", e1.toString());
		Message msgUI = uiHandler.obtainMessage(ERROR_UI,
			"Unable to connect. Unknown Host!");
		msgUI.sendToTarget();
	    } catch (IOException e1) {
		// HedwigLog.log(e1.getMessage());
		Log.e("Exception IO", e1.toString());
		if (connected) {
		    Message msgUI = uiHandler.obtainMessage(ERROR_UI,
			    "Lost connection to server. Please reconnect!");
		    msgUI.sendToTarget();
		    connected = false;
		} else {
		    Message msgUI = uiHandler.obtainMessage(ERROR_UI,
			    "Unable to connect. Is server running?");
		    msgUI.sendToTarget();
		    connected = false;
		}

	    } catch (Exception ex) {
		Log.e("Exception", ex.toString());
		Message msgUI = uiHandler.obtainMessage(ERROR_UI,
			"Unknown error. Please check connection parameters.");
		msgUI.sendToTarget();
	    }

	    Log.e("Client Thread", "Terminated");
	}

	private String getCommandName(int value) {
	    switch (value) {
	    case 1:
		return "FLY";
	    case 0:
		return "DROP";
	    case 2:
		return "STOP";
	    case 12:
		return "TURNRIGHT";
	    case 11:
		return "TURNLEFT";
	    case 21:
		return "FORWARD";
	    case 99:
		return "EMERGENCY";
	    default:
		return "UNKONWN";
	    }
	}
    }

}
