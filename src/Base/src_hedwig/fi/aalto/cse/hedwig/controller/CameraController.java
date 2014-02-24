package fi.aalto.cse.hedwig.controller;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import fi.aalto.cse.hedwig.HedwigLog;
import fi.aalto.cse.hedwig.activity.VideoStreamActivity;
import fi.aalto.cse.hedwig.video.VideoRenderer;

public class CameraController {

    // Activity calling this controllers
    private Activity context;
    
    // View for the video - testing purpose, we'll stream it anyway
    private GLSurfaceView glView;
    
    // Renderer of video - important
    private VideoRenderer renderer;
    
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
	renderer = new VideoRenderer(context, null);
	initGLSurfaceView();
    }
    
    private void initGLSurfaceView() {
	HedwigLog.logFunction(this, "initGLSurfaceView");
	if (glView != null) {
	    glView.setRenderer(renderer);
	}
    }

}
