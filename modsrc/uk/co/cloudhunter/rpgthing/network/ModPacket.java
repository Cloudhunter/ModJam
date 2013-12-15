package uk.co.cloudhunter.rpgthing.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.network.packet.Packet250CustomPayload;

public abstract class ModPacket {
	protected static final Class<?>[] classReferences = { int.class, Integer.class, boolean.class, Boolean.class,
			double.class, Double.class, float.class, Float.class, String.class, HashMap.class };

	protected static int getGenericID(Class<?> clazz) {
		for (int i = 0; i < classReferences.length; i++)
			if (classReferences[i].equals(clazz))
				return i;
		return -1;
	}

	protected static Class<?> getGeneric(int id) {
		if (id >= 0 && id < classReferences.length)
			return classReferences[id];
		return null;
	}

	public static ModPacket parse(byte bytes[]) throws IOException {
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(bytes));
		byte typeword = data.readByte();
		switch (typeword) {
			case 0:
				return StandardModPacket.createPacket(data);
			case 1:
				return BytePacket.createPacket(data);
			default:
				throw new IOException("Unknown packet typeword!");
		}
	}

	public abstract boolean getPacketIsForServer();

	public abstract Packet250CustomPayload toPacket();

	public abstract String getType();

}
