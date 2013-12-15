package uk.co.cloudhunter.rpgthing.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import uk.co.cloudhunter.rpgthing.RPGThing;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

public class StandardModPacket extends ModPacket {

	public static StandardModPacket createPacket(DataInputStream data) throws IOException {
		StandardModPacket pkt = new StandardModPacket();
		pkt.readData(data);
		return pkt;
	}

	private String typeof;
	private HashMap<Object, Object> values;
	private volatile boolean isPacketForServer;

	public StandardModPacket() {
		values = new HashMap<Object, Object>();
	}

	public String getType() {
		return typeof;
	}

	public Object getValue(String name) {
		return values.get(name);
	}

	public HashMap<Object, Object> getValues() {
		return values;
	}

	public void setValue(String name, Object value) {
		values.put(name, value);
	}

	public void setType(String t) {
		this.typeof = t;
	}

	public void setIsForServer(boolean state) {
		isPacketForServer = state;
	}

	public boolean getPacketIsForServer() {
		return isPacketForServer;
	}

	public void writeData(DataOutputStream data) throws IOException {
		data.writeByte((byte) 0);
		data.writeByte((byte) 1);
		if (typeof.length() > 512)
			throw new IOException("packetType too long");
		Packet.writeString(typeof, data);
		data.writeByte((isPacketForServer) ? 1 : 0);
		synchronized (values) {
			writeHashMap(values, data);
		}
	}

	public void readData(DataInputStream data) throws IOException {
		if (data.readByte() != (byte) 1)
			throw new IOException("Bad packet");
		typeof = Packet.readString(data, 512);
		isPacketForServer = (data.readByte() == 1);
		synchronized (values) {
			values = readHashMap(data);
		}
	}

	public static void writeValue(Object o, DataOutputStream data) throws IOException {
		int intValueOf = ModPacket.getGenericID(o.getClass());
		if (intValueOf == -1)
			throw new IOException("Weird value, cannot pack " + o.getClass().getName());
		else {
			data.writeInt(intValueOf);
			switch (intValueOf) {
				case 0:
				case 1:
					data.writeInt((Integer) o);
					break;
				case 2:
				case 3:
					data.writeByte((Boolean) o ? 1 : 0);
					break;
				case 4:
				case 5:
					data.writeDouble((Double) o);
					break;
				case 6:
				case 7:
					data.writeFloat((Float) o);
					break;
				case 8:
					Packet.writeString((String) o, data);
					break;
				case 9:
					writeHashMap((HashMap) o, data);
					break;
			}
		}
	}

	public static void writeHashMap(HashMap<?, ?> values, DataOutputStream data) throws IOException {
		int count = 0;
		for (Entry<?, ?> entry : values.entrySet())
			if (entry.getKey() != null && entry.getValue() != null)
				count++;
		data.writeInt(count);
		for (Entry<?, ?> entry : values.entrySet())
			if (entry.getKey() != null && entry.getValue() != null) {
				writeValue(entry.getKey(), data);
				writeValue(entry.getValue(), data);
			}
	}

	public static Object readValue(DataInputStream data) throws IOException {
		int typeAsInt = data.readInt();
		if (typeAsInt == -1)
			return null;
		else {
			Class<?> classValueOf = getGeneric(typeAsInt);
			if (classValueOf.equals(int.class) || classValueOf.equals(Integer.class))
				return data.readInt();
			else if (classValueOf.equals(boolean.class) || classValueOf.equals(Boolean.class))
				return (data.readByte() != 0);
			else if (classValueOf.equals(double.class) || classValueOf.equals(Double.class))
				return data.readDouble();
			else if (classValueOf.equals(float.class) || classValueOf.equals(Float.class))
				return data.readFloat();
			else if (classValueOf.equals(String.class))
				return Packet.readString(data, 8192);
			else if (classValueOf.equals(HashMap.class))
				return readHashMap(data);
			else
				throw new IOException("Weird value!");

		}
	}

	public static HashMap readHashMap(DataInputStream data) throws IOException {
		int size = data.readInt();
		HashMap<Object, Object> result = new HashMap<Object, Object>();
		for (int i = 0; i < size; i++) {
			result.put(readValue(data), readValue(data));
		}
		return result;
	}

	public Packet250CustomPayload toPacket() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		try {
			writeData(data);
		} catch (IOException e) {
			RPGThing.getLog().warning(e, "Failed to write packet!");
		}
		pkt.data = bytes.toByteArray();
		pkt.length = pkt.data.length;
		return pkt;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("StandardModPacket -> ").append(mtos(values)).toString();
	}

	public String mtos(Map<?, ?> map) {
		StringBuilder sb = new StringBuilder();
		sb.append(" { ");
		Iterator<?> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>) iter.next();
			sb.append(entry.getKey());
			sb.append('=').append('"');
			if (entry.getValue() instanceof HashMap)
				sb.append(mtos((HashMap<?, ?>) entry.getValue()));
			else
				sb.append(entry.getValue());
			sb.append('"');
			if (iter.hasNext()) {
				sb.append(',').append(' ');
			}
		}
		sb.append(" } ");
		return sb.toString();
	}
}
