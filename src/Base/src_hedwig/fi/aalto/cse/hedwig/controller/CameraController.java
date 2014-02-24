package fi.aalto.cse.hedwig.controller;

import com.parrot.freeflight.video.VideoStageRenderer;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import fi.aalto.cse.hedwig.HedwigLog;
import fi.aalto.cse.hedwig.activity.VideoStreamActivity;

public class CameraController {

    // Activity calling this controllers
    private Activity context;
    
    // Open GL view for the video - testing purpose, we'll stream it anyway
    private GLSurfaceView glView;
    
    private VideoStageRenderer renderer;
    
    /**
     * @param videoStreamActivity
     */
    public CameraController(VideoStreamActivity videoStreamActivity) {
	this.context = videoStreamActivity;
	glView = new GLSurfaceView(context);
	glView.setEGLContextClientVersion(2);
	
	// put the glview to the context, test only
	context.setContentView(glView);
	
	// Init video renderer
	renderer = new VideoStageRenderer(context, null);
	initGLSurfaceView();
    }
    
    private void initGLSurfaceView() {
	HedwigLog.logFunction(this, "initGLSurfaceView");
	if (glView != null) {
	    glView.setRenderer(renderer);
	}
    }

}
