package fi.aalto.cse.hedwig.video;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.provider.MediaStore;
import android.util.Log;

import com.parrot.freeflight.ui.gl.GLBGVideoSprite;

import fi.aalto.cse.hedwig.Constant;
import fi.aalto.cse.hedwig.HedwigLog;

/**
 * @author Long
 * @see http://developer.android.com/guide/topics/graphics/opengl.html
 * 
 */
public class VideoRenderer implements Renderer {

    private GLBGVideoSprite bgSprite;

    private float fps;

    // **********************
    private float[] mVMatrix = new float[16];
    private float[] mProjMatrix = new float[16];

    private int program;

    private final String vertexShaderCode = "uniform mat4 uMVPMatrix;   \n"
	    + "attribute vec4 vPosition; \n"
	    + "attribute vec2 aTextureCoord;\n"
	    + "varying vec2 vTextureCoord;\n" + "void main(){              \n"
	    + "  gl_Position = uMVPMatrix * vPosition; \n"
	    + "  vTextureCoord = aTextureCoord;\n"
	    + "}                         \n";

    private final String fragmentShaderCode = "precision mediump float;  \n"
	    + "varying vec2 vTextureCoord;\n" + "uniform sampler2D sTexture;\n"
	    + "uniform float fAlpha ;\n" + "void main(){              \n"
	    + " vec4 color = texture2D(sTexture, vTextureCoord); \n"
	    + " gl_FragColor = vec4(color.xyz, color.w * fAlpha );\n"
	    + " //gl_FragColor = vec4(0.6, 0.7, 0.2, 1.0); \n"
	    + "}                         \n";

    private long startTime;

    private long endTime;

    private Context mContext;

    private int count = 1;

    // ***********************

    public VideoRenderer(Context context, Bitmap initialTexture) {
	bgSprite = new GLBGVideoSprite(context.getResources());
	bgSprite.setAlpha(1.0f);
	mContext = context;
    }

    @SuppressLint("WrongCall")
    public void onDrawFrame(GL10 gl) {
	// Limiting framerate in order to save some CPU time
	endTime = System.currentTimeMillis();
	long dt = endTime - startTime;

	if (dt < Constant.FRAME_INTERVAL_IN_MILISECOND) // 33
	    try {
		Thread.sleep(Constant.FRAME_INTERVAL_IN_MILISECOND - dt);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }

	startTime = System.currentTimeMillis();

	// Drawing scene
	bgSprite.onDraw(gl, 0, 0);

    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
	HedwigLog.logFunction(this, "onSurfaceChanged - gl");
	GLES20.glViewport(0, 0, width, height);
	Matrix.orthoM(mProjMatrix, 0, 0, width, 0, height, 0, 2f);

	bgSprite.setViewAndProjectionMatrices(mVMatrix, mProjMatrix);
	bgSprite.onSurfaceChanged(gl, width, height);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	HedwigLog.logFunction(this, "onSurfaceCreated");
	startTime = System.currentTimeMillis();

	GLES20.glEnable(GLES20.GL_BLEND);
	GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
	int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
		fragmentShaderCode);

	program = GLES20.glCreateProgram();
	GLES20.glAttachShader(program, vertexShader);
	GLES20.glAttachShader(program, fragmentShader);

	GLES20.glLinkProgram(program);
	bgSprite.init(gl, program);

	Matrix.setLookAtM(mVMatrix, 0, /* x */0, /* y */0, /* z */1.5f, 0f, 0f,
		-5f, 0, 1f, 0.0f);
    }

    /*
     * public float getFPS() { return fps; }
     * 
     * public boolean updateVideoFrame() { HedwigLog.logFunction(this,
     * "updateVideoFrame"); return bgSprite.updateVideoFrame(); }
     */
    private int loadShader(int type, String code) {
	int shader = GLES20.glCreateShader(type);
	GLES20.glShaderSource(shader, code);
	GLES20.glCompileShader(shader);

	int[] compiled = new int[1];
	GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
	if (compiled[0] == 0) {
	    Log.e("opengl", "Could not compile shader");
	    Log.e("opengl", GLES20.glGetShaderInfoLog(shader));
	    Log.e("opengl", code);
	}

	return shader;
    }
}
