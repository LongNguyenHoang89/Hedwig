package fi.aalto.cse.harry.worker;

import fi.aalto.cse.harry.controller.Command;
import fi.aalto.cse.harry.controller.CommandFactory;

public class TestControlThread implements Runnable {

    private CommandFactory factory;

    public TestControlThread(CommandFactory factory) {
	this.factory = factory;
    }

    @Override
    public void run() {
	while (true) {
	    if (factory.droneConnected) {
		createCommand(Command.FLY, 10000);
		createCommand(Command.FORWARD, 3000);
		createCommand(Command.STOP, 0);
		createCommand(Command.TURNRIGHT, 15000);
		createCommand(Command.STOP, 0);
		createCommand(Command.FORWARD, 3000);
		createCommand(Command.STOP, 0);
		createCommand(Command.DROP, 0);
		break;
	    }
	}
    }

    private void createCommand(Command c, int time) {
	if (!factory.isReady) {
	    factory.createCommand(c);
	    try {
		Thread.sleep(time);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
}
