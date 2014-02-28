package fi.aalto.cse.hedwig.controller;

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

	public void processCommand(String command) {
		String[] values = command.split(":");
		System.out.println(values[0]);
		System.out.println(values[1]);
		CommandList Comm = CommandList.valueOf(values[0].toUpperCase());

		switch (Comm) {
		case FLY:
			if (!isFlying) {
				System.out.println("send fly");
				//droneControlService.triggerTakeOff();
				//droneControlService.setProgressiveCommandEnabled(true);
				isFlying = true;
			}
			break;
		case FORWARD:
			droneControlService.moveForward(Float.parseFloat(values[1]));
			break;
		case DROP:
			if (isFlying) {
				//droneControlService.triggerTakeOff();
				System.out.println("send drop");
			}
			break;
		case TURNRIGHT:
			droneControlService.turnRight(Float.parseFloat(values[1]));
			break;
		case TURNLEFT:
			droneControlService.turnLeft(Float.parseFloat(values[1]));
			break;
		case EMERGENCY:
			droneControlService.triggerEmergency();
			break;
		case MOVERIGHT:
			droneControlService.moveRight(Float.parseFloat(values[1]));
			break;
		case MOVELEFT:
			droneControlService.moveLeft(Float.parseFloat(values[1]));
			break;
		case MOVEBACK:
			droneControlService.moveBackward(Float.parseFloat(values[1]));
			break;
		case MOVEUP:
			droneControlService.moveUp(Float.parseFloat(values[1]));
			break;
		case MOVEDOWN:
			droneControlService.moveDown(Float.parseFloat(values[1]));
			break;
		default: // Do something?
		}
	}

	/**
	 * Take off
	 */
	public void fly() {
		droneControlService.triggerTakeOff();

		// Set this first, else the drone won't flight
		// droneControlService.setProgressiveCommandEnabled(true);
		/*
		 * try { Thread.sleep(10000); droneControlService.moveForward(0.1f);
		 * Thread.sleep(5000); droneControlService.moveForward(0.05f);
		 * Thread.sleep(5000); droneControlService.moveForward(0);
		 * Thread.sleep(5000);
		 * 
		 * } catch (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } finally {
		 * droneControlService.triggerTakeOff(); }
		 */
	}

	/**
	 * @param view
	 */
	public void drop() {
		droneControlService.triggerEmergency();
	}
}
