package uk.co.cloudhunter.rpgthing.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import uk.co.cloudhunter.rpgthing.RPGThing;
import net.minecraft.network.packet.Packet250CustomPayload;

public class BytePacket extends ModPacket {

	private DataInputStream instream;
	private DataOutputStream outstream;
	private ByteArrayOutputStream outbuff;
	private boolean toServer = false;

	public static BytePacket createPacket(DataInputStream data) throws IOException {
		BytePacket pkt = new BytePacket(data);
		pkt.setIsForServer(data.readByte() == 1);
		return pkt;
	}

	public BytePacket() {
		this.outbuff = new ByteArrayOutputStream();
		this.outstream = new DataOutputStream(outbuff);
	}

	public BytePacket(DataInputStream data) {
		this.instream = data;
	}

	public DataInputStream getIn() {
		return instream;
	}

	public DataOutputStream getOut() {
		return outstream;
	}

	public void setIsForServer(boolean b) {
		this.toServer = b;
	}

	@Override
	public boolean getPacketIsForServer() {
		return toServer;
	}

	public Packet250CustomPayload toPacket() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		bytes.write(1);
		bytes.write((toServer) ? 1 : 0);
		try {
			bytes.write(outbuff.toByteArray());
		} catch (IOException e) {
			RPGThing.getLog().warning(e, "Failed to write packet!");
		}
		pkt.data = bytes.toByteArray();
		pkt.length = pkt.data.length;
		return pkt;
	}

	@Override
	public String getType() {
		return "bytepacket";
	}

	@Override
	public String toString() {
		return "bytepacket";
	}

}
