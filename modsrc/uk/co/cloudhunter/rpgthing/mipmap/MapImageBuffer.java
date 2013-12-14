package uk.co.cloudhunter.rpgthing.mipmap;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

public class MapImageBuffer extends BufferedImage {

	private ByteBuffer buffer;
	public byte[] bytes;
	public int index = 0;
	private Object lock = new Object();

	private static boolean enableFramebuffer = GLContext.getCapabilities().GL_EXT_framebuffer_object;

	public MapImageBuffer(int width, int height, int imageType) {
		super(width, height, imageType);
		bytes = ((DataBufferByte) getRaster().getDataBuffer()).getData();
		buffer = ByteBuffer.allocateDirect(bytes.length).order(ByteOrder.nativeOrder());
	}

	public void flush() {
		if (index != 0)
			GL11.glDeleteTextures(index);
	}

	public void clear() {
		for (int t = 0; t < bytes.length; t++)
			bytes[t] = 0;
		write();
	}

	public void write() {
		if (index != 0) {
			GL11.glDeleteTextures(index);
		}
		index = GL11.glGenTextures();
		buffer.clear();

		synchronized (lock) {
			buffer.put(bytes);
		}
		buffer.position(0).limit(bytes.length);

		if (!enableFramebuffer) {
			for (int t = 0; t < getWidth(); t++) {
				buffer.put(t * 4 + 3, (byte) 0);
				buffer.put(t * getWidth() * 4 + 3, (byte) 0);
			}
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, index);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, 6408, getWidth(), getHeight(), 0, 32993, 5121, buffer);
	}

	public void bit(int x, int y, int bit) {
		int index = (x + y * getWidth()) * 4;

		bytes[index] = (byte) (bit >> 0);
		bytes[index + 1] = (byte) (bit >> 8);
		bytes[index + 2] = (byte) (bit >> 16);
		bytes[index + 3] = (byte) (bit >> 24);
	}

}
