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
	if ("Fly".equalsIgnoreCase(command)){
	    this.fly();
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
