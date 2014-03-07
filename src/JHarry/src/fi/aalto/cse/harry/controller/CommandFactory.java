package fi.aalto.cse.harry.controller;

import fi.aalto.cse.harry.protocol.Packet;

public class CommandFactory {
    private static final CommandFactory INSTANCE = new CommandFactory();
    private Packet currentCommand = null;
    public volatile boolean isReady = false;

    public static CommandFactory getInstance() {
	return INSTANCE;
    }

    public void initialize() {
	// Init this instance
    }

    /**
     * Set the currentcommand to something sothat we can retrieve it from other
     * thread
     * 
     * @param command
     */
    public void createCommand(Command command) {
	this.currentCommand = getCommand(command);
	this.isReady = true;
    }

    /**
     * We execute command from other thread, so we take the current command
     * packet and send it. Set it to null afterwards
     */
    public Packet executeCommand() {
	if (isReady) {
	    Packet value = this.currentCommand;
	    this.currentCommand = null;
	    this.isReady = false;
	    return value;
	} else
	    return null;
    }

    private Packet getCommand(Command command) {
	Packet commandPacket = null;
	switch (command) {
	case FLY:	   
	case DROP:	 
	case EMERGENCY:
	case STOP:
	    commandPacket = new Packet(command.getValue());
	    break;	
	case FORWARD:
	    commandPacket = new Packet(command.getValue(), 0.1f);
	    break;
	case TURNRIGHT:
	case TURNLEFT:
	    commandPacket = new Packet(command.getValue(), 0.5f);
	    break;
	}
	return commandPacket;
    }
}
