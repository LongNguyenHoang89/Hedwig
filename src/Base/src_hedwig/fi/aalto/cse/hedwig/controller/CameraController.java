package fi.aalto.cse.hedwig.controller;

import com.parrot.freeflight.video.VideoStageRenderer;
import com.parrot.freeflight.video.VideoStageView;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.view.ViewGroup.LayoutParams;
import fi.aalto.cse.hedwig.HedwigLog;
import fi.aalto.cse.hedwig.activity.VideoStreamActivity;
import fi.aalto.cse.hedwig.video.VideoRenderer;

public class CameraController {

    // Activity calling this controllers
    private Activity context;
    // Renderer of video - important
    private VideoStageRenderer renderer;

    private VideoStageView canvasView;

    /**
     * @param videoStreamActivity
     */
    public CameraController(VideoStreamActivity videoStreamActivity) {
	this.context = videoStreamActivity;

	// Init video renderer
	renderer = new VideoStageRenderer(context, null);
	canvasView = new VideoStageView(context);
	canvasView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		LayoutParams.MATCH_PARENT));
	initCanvasSurfaceView();

	// put the glview to the context, test only
	context.setContentView(canvasView);
    }

    private void initCanvasSurfaceView() {
	if (canvasView != null) {
	    canvasView.setRenderer(renderer);
	}
    }

}
