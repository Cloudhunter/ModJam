package uk.co.cloudhunter.rpgthing.network;

import java.io.IOException;

import uk.co.cloudhunter.rpgthing.RPGThing;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class DefaultPacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		try {
			RPGThing.getProxy().accept(ModPacket.parse(packet.data), player);
		} catch (IOException ioex) {
			RPGThing.getLog().warning(ioex, "IOException when handling packet!");
		}
	}

}
