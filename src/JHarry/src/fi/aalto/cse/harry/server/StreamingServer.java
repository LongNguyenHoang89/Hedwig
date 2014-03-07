package fi.aalto.cse.harry.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;

/**
 * @author Long Nguyen
 * @email LongNguyenHoang@outlook.com
 */
public class StreamingServer implements Runnable {

    private Socket clientSocket;

    public DataInputStream in;

    /**
     * default constructor
     */
    public StreamingServer(int port) {

	ServerSocket serverSocket;
	try {
	    serverSocket = new ServerSocket(port);
	    clientSocket = serverSocket.accept();
	    in = new DataInputStream(clientSocket.getInputStream());
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public BufferedImage ReadImage() {
	BufferedImage img;
	try {
	    // Read size first
	    int size = in.readInt();
	    // System.out.println(size);
	    
	    // Now get the picture
	    byte[] pictureBuffer = new byte[size];

	    in.readFully(pictureBuffer);

	    // Now create picture object
	    img = ImageIO.read((new ByteArrayInputStream(pictureBuffer)));
	    return img;
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }


    @Override
    public void run() {
	// TODO Auto-generated method stub

    }
}
