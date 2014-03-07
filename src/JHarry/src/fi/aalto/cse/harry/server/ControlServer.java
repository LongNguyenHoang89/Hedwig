package fi.aalto.cse.harry.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import fi.aalto.cse.harry.controller.CommandFactory;
import fi.aalto.cse.harry.protocol.Packet;

/**
 * @author Long
 *
 */
public class ControlServer {
    private Socket clientSocket;

    private DataInputStream in;
    private DataOutputStream out;
    
    /**
     * default constructor
     */
    public ControlServer(int port) {
	ServerSocket serverSocket;
		
	try {
	    serverSocket = new ServerSocket(port);
	    clientSocket = serverSocket.accept();
	    in = new DataInputStream(clientSocket.getInputStream());
	    out = new DataOutputStream(clientSocket.getOutputStream());	    	  
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
    public void SendPacket(Packet commandPacket){
	try {
	    out.flush();
	    out.writeInt(commandPacket.command);
	    out.writeFloat(commandPacket.param);
	    out.writeInt(commandPacket.payLoadLength);	    
	    if (commandPacket.payLoadLength != 0){
		out.write(commandPacket.body);
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
