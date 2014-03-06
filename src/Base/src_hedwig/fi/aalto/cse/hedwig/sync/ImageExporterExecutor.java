package fi.aalto.cse.hedwig.sync;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.util.Log;
import fi.aalto.cse.hedwig.Constant;

public class ImageExporterExecutor {

    private static final String LOG_TAG = ImageExporterExecutor.class
	    .getSimpleName();

    private static final ImageExporterExecutor INSTANCE = new ImageExporterExecutor();

    private static final int NO_THREADS = 1;

    private static final int IMG_QUALITY = 50;

    private ImageExporterExecutor() {
	ExecutorService executor = Executors.newFixedThreadPool(NO_THREADS);
	for (int i = 0; i < NO_THREADS; i++) {
	    executor.execute(new ImageExporterRunnable());
	}
    }

    public static void initialize() {
	// Dummy method to load the class.
    }

    private class ImageExporterRunnable implements Runnable {
	private DataOutputStream imageStreamToServer = null;

	public ImageExporterRunnable() {
	    try {
		InetAddress serverAddr = InetAddress
			.getByName(Constant.SERVER_IP);
		Socket clientSocket = new Socket(serverAddr, Constant.VIDEOPORT);
		imageStreamToServer = new DataOutputStream(
			clientSocket.getOutputStream());
	    } catch (UnknownHostException e1) {
		Log.e(LOG_TAG, "Unknown host exception while creating socket.");
	    } catch (IOException e1) {
		Log.e(LOG_TAG, "IO exception while creating socket.");
	    }
	}

	@Override
	public void run() {
	    while (true) {
		// Error while creating socket stream.
		if (imageStreamToServer == null) {
		    break;
		}
		if (isEmpty()) {
		    // Log.d(LOG_TAG, "Image queue is empty");
		    sleepTime();
		} else {
		    // Log.d(LOG_TAG,
		    // "Image data is going to be pushed to socket stream.");
		    Bitmap image = getImageFromQueue();

		    try {
			sendImage(image, imageStreamToServer);
			// Log.d(LOG_TAG,
			// "Image data is pushed to socket stream.");
			imageStreamToServer.flush();
		    } catch (IOException e) {
			Log.e(LOG_TAG,
				"Error when image data is flushed to socket stream.");
		    }
		}
	    }
	}

	/**
	 * We are sending a set of images to client, there is no way for client
	 * to distinguish each image. Solution is to send the length first, then
	 * image later
	 * 
	 * @param image
	 * @param out
	 * @throws IOException
	 */
	public void sendImage(Bitmap image, DataOutputStream out)
		throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    baos.flush();
	    image.compress(Bitmap.CompressFormat.JPEG, IMG_QUALITY, baos);
	    byte[] bytes = baos.toByteArray();
	    baos.close();
	    // Send number of byte first
	    out.writeInt(bytes.length);

	    // Send the actual image
	    out.write(bytes);
	    out.flush();
	}
    }

    private Bitmap getImageFromQueue() {
	ImageQueue imageQueue = ImageQueue.getInstance();
	return imageQueue.getImage();
    }

    private boolean isEmpty() {
	ImageQueue imageQueue = ImageQueue.getInstance();
	return imageQueue.isEmpty();
    }

    private void sleepTime() {
	try {
	    Thread.sleep(50);
	} catch (InterruptedException e) {
	    Log.e(LOG_TAG, "Thread has been interrupted.");
	}
    }
}