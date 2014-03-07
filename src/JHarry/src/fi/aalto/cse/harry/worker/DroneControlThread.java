package fi.aalto.cse.harry.worker;

import fi.aalto.cse.harry.Constants;
import fi.aalto.cse.harry.controller.CommandFactory;
import fi.aalto.cse.harry.server.ControlServer;

public class DroneControlThread implements Runnable {
    
    private ControlServer droneControlServer;
    private CommandFactory factory;
    public DroneControlThread(CommandFactory factory){
	this.factory = factory;
    }
    
    @Override
    public void run() {
	droneControlServer = new ControlServer(Constants.CONTROL_SOCKET_PORT);
	System.out.println("Drone connected");
	while(true){
	    if (factory.isReady){		
		droneControlServer.SendPacket(factory.executeCommand());
		System.out.println("command sent");
	    }
	}
    }

}
