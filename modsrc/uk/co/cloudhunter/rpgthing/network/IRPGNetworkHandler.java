package uk.co.cloudhunter.rpgthing.network;

import cpw.mods.fml.common.network.Player;

public interface IRPGNetworkHandler {
	public void accept(ModPacket packet, Player player);
}
