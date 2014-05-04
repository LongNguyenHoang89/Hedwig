package fi.aalto.cse.hedwig.sync;

import java.util.concurrent.LinkedBlockingQueue;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageQueue {

    private static final String LOG_TAG = ImageQueue.class.getSimpleName();

    private LinkedBlockingQueue<Bitmap> sImageQueue = null;

    private static final ImageQueue INSTANCE = new ImageQueue();

    private ImageQueue() {
	sImageQueue = new LinkedBlockingQueue<Bitmap>();	
    }

    public static ImageQueue getInstance() {
	return INSTANCE;
    }

    public boolean addImage(Bitmap bitmap) {
	//Log.d(LOG_TAG, "Added image to queue.");
	return sImageQueue.add(bitmap);
    }

    public Bitmap getImage() {
	//Log.d(LOG_TAG, "Removed image from queue.");
	return sImageQueue.remove();
    }

    public boolean isEmpty() {
	//Log.d(LOG_TAG, "Image queue is empty.");
	return sImageQueue.isEmpty();
    }
}