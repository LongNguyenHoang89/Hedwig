package fi.aalto.cse.hedwig;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
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

    public void fly(View view) {
	droneControlService.triggerTakeOff();
    }

    public void drop(View view) {
	droneControlService.triggerEmergency();
    }
}
