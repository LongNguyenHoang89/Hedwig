package fi.aalto.cse.harry.controller;

import fi.aalto.cse.harry.structure.RectangleDimensions;

public class DroneMovementCommmandGenerator {

	private static DroneMovementCommmandGenerator INSTANCE = null;

	// Should be odd number.
	private static final double STRIP_SIZE = 5;

	private double safeRegionStartX;
	private double safeRegionWidth;
	private double safeRegionEndX;
	private double imageWidth;

	private static boolean temporary = false;

	private DroneMovementCommmandGenerator(int imageWidth, int imageHeight) {
		this.imageWidth = imageWidth;
		this.safeRegionWidth = (imageWidth / STRIP_SIZE);
		this.safeRegionStartX = this.safeRegionWidth * (STRIP_SIZE / 2.00);
		this.safeRegionEndX = safeRegionStartX + safeRegionWidth;
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
		//System.out.println("Image width : " + imageWidth);
		//System.out.println("Safe region width : " + safeRegionWidth);
		//System.out.println("Safe region start X : " + safeRegionStartX);
		//System.out.println("Safe region end X : " + safeRegionEndX);
		//System.out.println("Rectangle start X : " + rectStartX);
		//System.out.println("Rectangle end X : " + rectEndX);

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

		/*double size = rectDimensions.getWidth() * rectDimensions.getHeight();
		if (size > (40 * 40)) {
			// we have detected the pillow, let's stop
			if (!temporary) {
				System.out.println("Detect the pillow, stop now.");
				// setCommand(Command.STOP);
				temporary = true;
			}
		}*/	
	}

	private void setCommand(Command cmd) {
		CommandFactory.getInstance().createCommand(cmd);
	}
}