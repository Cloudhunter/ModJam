package uk.co.cloudhunter.rpgthing.mipmap;

public class MapCache {

	private static int SIZE_DATA = 4;

	private static int BIT_HEIGHT = 0;
	private static int BIT_MATERIAL = 1;
	private static int BIT_LIGHT = 2;
	private static int BIT_BIOME = 3;

	private int width;
	private int height;

	private Object busy = new Object();

	private int[] data;

	public MapCache(int width, int height) {
		this.width = width;
		this.height = height;
		this.data = new int[SIZE_DATA * width * height];
	}

	public int getHeight(int x, int z) {
		return bit(x, z, BIT_HEIGHT);
	}

	public int getMaterial(int x, int z) {
		return bit(x, z, BIT_MATERIAL);
	}

	public int getLight(int x, int z) {
		return bit(x, z, BIT_LIGHT);
	}

	public int getBiome(int x, int z) {
		return bit(x, z, BIT_BIOME);
	}

	public int bit(int wx, int wz, int field) {
		return data[(wx + wz * width) * SIZE_DATA + field];
	}

	public void bitput(int wx, int wz, int field, int bit) {
		data[(wx + wz * width) * SIZE_DATA + field] = bit;
	}

}
