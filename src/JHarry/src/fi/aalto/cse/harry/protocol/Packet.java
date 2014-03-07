package fi.aalto.cse.harry.protocol;

/**
 * Protocol packet
 * @author Long
 *
 */
public class Packet {
    public int command;
    public float param;
    public int payLoadLength;
    public byte[] body;
    
    public Packet(int command){
	this.command = command;
	param = 0f;
	payLoadLength = 0;
	body = null;
    }
    
    public Packet(int command, float param){
	this.command = command;
	this.param = param;
	payLoadLength = 0;
	body = null;
    }
}