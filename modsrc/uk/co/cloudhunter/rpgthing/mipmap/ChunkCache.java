package uk.co.cloudhunter.rpgthing.mipmap;

import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache {
	private int x = 0;
	private int z = 0;
	private int width;
	private int height;
	private Chunk chunk;
	private boolean hasChanged;
	private boolean isLoaded;

	public ChunkCache(int x, int z) {
		this.x = x;
		this.z = z;
		hasChanged = true;

		chunk = Minecraft.getMinecraft().thePlayer.worldObj.getChunkFromChunkCoords(x, z);
		isLoaded = chunk.isChunkLoaded;
	}

	public void refresh() {
		poll();
		if (hasChanged)
			MapHelper.buildChunk(chunk, x, z);

		hasChanged = false;
		chunk.isModified = false;
	}

	public void poll() {
		if (!isLoaded) {
			chunk = Minecraft.getMinecraft().thePlayer.worldObj.getChunkFromChunkCoords(x, z);
			if (chunk.isChunkLoaded) {
				isLoaded = true;
				hasChanged = true;
			}

		} else if (isLoaded && !chunk.isChunkLoaded) {
			isLoaded = false;
			hasChanged = true;
		}

		if (chunk.isChunkLoaded && chunk.isModified)
			hasChanged = true;
	}
}
