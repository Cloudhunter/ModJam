package uk.co.cloudhunter.rpgthing.network;

import uk.co.cloudhunter.rpgthing.RPGThing;
import cpw.mods.fml.common.network.Player;

public class ServerPacketHandler implements IRPGNetworkHandler {
	@Override
	public void accept(ModPacket packet, Player player) {
		if (packet.getType().equals("partyline"))
			RPGThing.getProxy().partylineNetwork.accept(packet, player, true);
		if (packet.getType().equals("player"))
			RPGThing.getProxy().playerNetwork.accept(packet, player, true);
	}
}
