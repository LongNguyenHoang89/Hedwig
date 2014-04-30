package fi.aalto.cse.hedwig.controller;

import java.util.Arrays;

import android.view.View;

import com.parrot.freeflight.service.DroneControlService;



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

	public DroneController(DroneControlService service) {
		this.droneControlService = service;
	}

	public void processCommand(int command, float commandValue) {
		switch(command) {
		case 0:
			droneControlService.triggerTakeOff();
			break;
		case 1:
			droneControlService.triggerTakeOff();
			droneControlService.setProgressiveCommandEnabled(true);
			break;
		case 2:
			droneControlService.moveForward(0);
			droneControlService.setYaw(0);
			break;
		case 21:
			droneControlService.moveForward(commandValue);
			break;
		case 12:
			droneControlService.turnRight(commandValue);
			break;
		case 11:
			droneControlService.turnLeft(commandValue);
			break;
		case 5:
			droneControlService.moveRight(commandValue);
			break;
		case 6:
			droneControlService.moveLeft(commandValue);
			break;
		case 7:
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
