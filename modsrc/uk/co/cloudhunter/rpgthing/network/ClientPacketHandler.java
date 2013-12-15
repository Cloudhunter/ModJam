package uk.co.cloudhunter.rpgthing.network;

import uk.co.cloudhunter.rpgthing.RPGThing;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.Player;

public class ClientPacketHandler implements IRPGNetworkHandler {

	@Override
	public void accept(ModPacket packet, Player player) {
		if (packet.getType().equals("partyline"))
			RPGThing.getProxy().partylineNetwork.accept(packet, player, false);
		if (packet.getType().equals("player"))
			RPGThing.getProxy().playerNetwork.accept(packet, player, false);

	}
}
