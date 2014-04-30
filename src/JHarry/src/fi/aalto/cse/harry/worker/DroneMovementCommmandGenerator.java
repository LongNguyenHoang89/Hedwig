package fi.aalto.cse.harry.worker;

import fi.aalto.cse.harry.controller.Command;
import fi.aalto.cse.harry.controller.CommandFactory;
import fi.aalto.cse.harry.structure.RectangleDimensions;

public class DroneMovementCommmandGenerator {

	private static DroneMovementCommmandGenerator INSTANCE = null;

	// Should be odd number.
	private static final double STRIP_SIZE = 5;

	private double safeRegionStartX;
	private double safeRegionWidth;
	private double safeRegionEndX;

	private DroneMovementCommmandGenerator(int imageWidth, int imageHeight) {
		this.safeRegionWidth = (imageWidth / STRIP_SIZE);
		this.safeRegionStartX = this.safeRegionWidth * (STRIP_SIZE / 2.00);
		this.safeRegionEndX = safeRegionStartX + safeRegionWidth;
		System.out.println("Image width : " + imageWidth);
		System.out.println("Safe region width : " + safeRegionWidth);
		System.out.println("Safe region start X : " + safeRegionStartX);
		System.out.println("Safe region end X : " + safeRegionEndX);
	}

	public static DroneMovementCommmandGenerator getInstance(int imageWidth,
			int imageHeight) {
		if (INSTANCE == null) {
			synchronized (DroneMovementCommmandGenerator.class) {
				if (INSTANCE == null) {
					INSTANCE = new DroneMovementCommmandGenerator(imageWidth,
							imageHeight);
				}
			}
		}
		return INSTANCE;
	}

	public void generateCommand(RectangleDimensions rectDimensions) {
		int rectStartX = rectDimensions.getX();
		int rectEndX = rectDimensions.getX() + rectDimensions.getWidth();
		if (rectStartX >= safeRegionStartX && rectEndX <= safeRegionEndX) {
			System.out
					.println("Do nothing Drone is flying right over the strip");
			setCommand(Command.FORWARD);
		} else if ((rectStartX < safeRegionStartX)
				&& (rectEndX > safeRegionEndX)) {
			// This may happen when the drone has been tilted w.r.t. the strip.
			System.out.println("Lost the strip. Stop now.");
			setCommand(Command.STOP);
		} else if (rectStartX < safeRegionStartX) {
			System.out.println("Turn left.");
			setCommand(Command.TURNLEFT);
		} else if (rectEndX > safeRegionEndX) {
			System.out.println("Turn right.");
			setCommand(Command.TURNRIGHT);
		} else {
			System.out.println("Lost the strip. Stop now.");
			setCommand(Command.STOP);
		}
	}
	
	private void setCommand(Command cmd) {
		CommandFactory.getInstance().createCommand(cmd);		
	}
}