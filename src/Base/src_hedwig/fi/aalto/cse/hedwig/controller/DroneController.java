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

    private DroneControlService droneControlService;

    public DroneController(DroneControlService service) {
	this.droneControlService = service;
    }

    public void processCommand(String command)
    {
    	String[] values = command.split(":");
		System.out.println(values[0]);
		System.out.println(values[1]);
		if (values[0].equalsIgnoreCase("fly")) {
			droneControlService.triggerTakeOff();
			droneControlService.setProgressiveCommandEnabled(true);
		} else if (values[0].equalsIgnoreCase("forward")) {
			droneControlService.moveForward(Float.parseFloat(values[1]));
		} else if (values[0].equalsIgnoreCase("drop")) {
			droneControlService.triggerTakeOff();
		} else if (values[0].equalsIgnoreCase("turnright")) {
			droneControlService.turnRight(Float.parseFloat(values[1]));
		} else if (values[0].equalsIgnoreCase("emergency"))  {
			droneControlService.triggerEmergency();
		} else if (values[0].equalsIgnoreCase("stop")) {
			droneControlService.moveForward(0);
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
