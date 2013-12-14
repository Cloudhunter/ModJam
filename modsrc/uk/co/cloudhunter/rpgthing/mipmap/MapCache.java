package uk.co.cloudhunter.rpgthing.mipmap;

public class MapCache {

	private static int SIZE_DATA = 8;

	private static int BIT_HEIGHT = 0;
	private static int BIT_MATERIAL = 1;
	private static int BIT_META = 2;
	private static int BIT_TINT = 3;
	private static int BIT_LIGHT = 4;
	private static int BIT_BIOME = 5;

	private int width;
	private int height;

	private Object busy = new Object();
	
	
	private int[] data;
	
	public MapCache(int width, int height) {
		
	}

}
