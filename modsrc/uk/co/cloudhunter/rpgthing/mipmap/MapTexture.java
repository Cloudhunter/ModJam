package uk.co.cloudhunter.rpgthing.mipmap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class MapTexture {

	private int gl_id;
	public final int w;
	public final int h;
	private final IntBuffer pixelBuf;
	
	public int textureRegions;
    public int textureSize;

	public MapTexture(int width, int height, int fillColour, int minFilter, int maxFilter, int textureWrap) {
		this.w = width;
		this.h = height;
		gl_id = GL11.glGenTextures();
		pixelBuf = ByteBuffer.allocateDirect(w * h * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		fill(0, 0, w, h, fillColour);
		pixelBuf.position(0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl_id);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE,
				this.pixelBuf);
		param(minFilter, maxFilter, textureWrap);
	}

	public void param(int minFilter, int maxFilter, int textureWrap) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl_id);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, textureWrap);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, textureWrap);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, maxFilter);
	}

	public synchronized void fill(int x, int y, int w, int h, int colour) {
		int offset = (y * this.w) + x;
		for (int j = 0; j < h; j++) {
			this.pixelBuf.position(offset + (j * this.w));
			for (int i = 0; i < w; i++) {
				this.pixelBuf.put(colour);
			}
		}
	}

	public synchronized void updateTexture() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl_id);
		pixelBuf.position(0);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.w, this.h, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE,
				this.pixelBuf);
	}

}
