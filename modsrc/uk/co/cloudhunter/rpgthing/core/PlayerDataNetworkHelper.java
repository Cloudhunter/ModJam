package uk.co.cloudhunter.rpgthing.core;

import uk.co.cloudhunter.rpgthing.network.ModPacket;
import uk.co.cloudhunter.rpgthing.network.StandardModPacket;
import cpw.mods.fml.common.network.Player;

public class PlayerDataNetworkHelper {

	public void accept(ModPacket packet, Player player, boolean isServer) {
		StandardModPacket sp = (StandardModPacket) packet;
		if (!isServer) {
			String req = (String) sp.getValue("payload");
			if (req.equals("player-data")) {
				String target = (String) sp.getValue("player-name");
				uk.co.cloudhunter.rpgthing.core.Player p = uk.co.cloudhunter.rpgthing.core.Player.getPlayer(target, true);
				p.readFromPacket(sp, "player-data");
			}
		}
	}

}
