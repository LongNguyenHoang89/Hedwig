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
	createCommand(Command.FLY, 10000);
	createCommand(Command.TURNRIGHT, 10000);
	createCommand(Command.DROP, 0);
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
