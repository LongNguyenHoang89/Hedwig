package fi.aalto.cse.harry.processing;

public class RectanleDimensions {

	private int x;
	private int y;
	private int width;
	private int height;

	public RectanleDimensions(int x, int y, int width, int height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	

	@Override
	public String toString() {
		return "RectanleDimensions [x=" + x + ", y=" + y + ", width=" + width
				+ ", height=" + height + "]";
	}
}