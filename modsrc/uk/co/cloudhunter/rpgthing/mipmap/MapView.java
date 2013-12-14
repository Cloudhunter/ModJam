package uk.co.cloudhunter.rpgthing.mipmap;

public class MapView {

	private int zoom = 0;
	private int dimension = 0;
	protected int textureSize = 1024;

	private double x = 0;
	private double z = 0;

	private int width = 0;
	private int height = 0;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void pan(int dx, int dz) {
		setCenter(x + (dx * width), z + (dz * height));
	}

	public void setCenter(double dx, double dz) {
		this.x = dx;
		this.z = dz;
	}

	public double getX() {
		return x;
	}

	public double getZ() {
		return z;
	}

	public double getMinX() {
		return x - (width / 2);
	}

	public double getMaxX() {
		return x + (width / 2);
	}

	public double getMinZ() {
		return z - (height / 2);
	}

	public double getMaxZ() {
		return z + (height / 2);
	}

}
