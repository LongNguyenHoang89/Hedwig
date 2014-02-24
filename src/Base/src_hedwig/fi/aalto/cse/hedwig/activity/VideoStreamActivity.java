package fi.aalto.cse.hedwig.activity;

import fi.aalto.cse.hedwig.HedwigLog;
import fi.aalto.cse.hedwig.controller.CameraController;
import android.app.Activity;
import android.os.Bundle;

/**
 * 
 * @author Long
 *
 */
public class VideoStreamActivity extends Activity{

    private CameraController view;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {	
	HedwigLog.logFunction(this, "onCreate");
	super.onCreate(savedInstanceState);
	
	view = new CameraController(this);
    }
}
