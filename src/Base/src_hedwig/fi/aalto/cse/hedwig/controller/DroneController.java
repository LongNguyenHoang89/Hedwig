package fi.aalto.cse.hedwig.controller;

import java.util.Arrays;

import android.view.View;

import com.parrot.freeflight.drone.DroneProxy;
import com.parrot.freeflight.drone.NavData;
import com.parrot.freeflight.service.DroneControlService;

import fi.aalto.cse.hedwig.activity.RelayClientActivity;

/**
 * Hedwig controlling library for the drone.
 * 
 * @author Long
 * @see com.parrot.freeflight.activities.ControlDroneActivity
 */
public class DroneController {
    public enum CommandList {
	FLY, FORWARD, MOVERIGHT, MOVELEFT, TURNRIGHT, TURNLEFT, DROP, EMERGENCY, MOVEDOWN, MOVEUP, MOVEBACK;
    }

    public boolean isFlying;

    private DroneControlService droneControlService;
    private RelayClientActivity applicationContext;
    
    public DroneController(DroneControlService service, RelayClientActivity context) {
	this.droneControlService = service;
	this.applicationContext = context;
    }

    public void processCommand(int command, float commandValue) {
	switch (command) {
	case 0:
	    droneControlService.triggerTakeOff();
	    break;
	case 1:
	    droneControlService.triggerTakeOff();
	    // droneControlService.setProgressiveCommandEnabled(true);
	    break;
	case 2:
	    droneControlService.setYaw(0);
	    droneControlService.moveForward(0);
	    droneControlService.setProgressiveCommandEnabled(false);	    
	    break;
	case 21:
	    droneControlService.setProgressiveCommandEnabled(true);
	    droneControlService.moveForward(commandValue);
	    break;
	case 12:
	    droneControlService.turnRight(commandValue);
	    break;
	case 11:
	    droneControlService.turnLeft(commandValue);
	    break;
	case 5:
	    droneControlService.setProgressiveCommandEnabled(true);
	    droneControlService.moveRight(commandValue);
	    break;
	case 6:
	    droneControlService.setProgressiveCommandEnabled(true);
	    droneControlService.moveLeft(commandValue);
	    break;
	case 7:
	    droneControlService.setProgressiveCommandEnabled(true);
	    droneControlService.moveBackward(commandValue);
	    break;
	case 8:
	    droneControlService.moveUp(commandValue);
	    break;
	case 9:
	    droneControlService.moveDown(commandValue);
	    break;
	case 99:
	    droneControlService.triggerEmergency();
	    break;
	}
	
	DroneProxy.getInstance(applicationContext.getApplicationContext()).updateNavdata();
	NavData na = DroneProxy.getInstance(applicationContext.getApplicationContext()).getNavdata();
	
    }

    /**
     * Take off
     */
    public void fly() {
	droneControlService.triggerTakeOff();
    }

    /**
     * @param view
     */
    public void drop() {
	droneControlService.triggerEmergency();
    }
}
