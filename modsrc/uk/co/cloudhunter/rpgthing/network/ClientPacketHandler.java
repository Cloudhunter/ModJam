package uk.co.cloudhunter.rpgthing.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.Player;

public class ClientPacketHandler implements IRPGNetworkHandler {
	
	private Minecraft minecraft;
	private World world;
	
	private int heightOfWorld = 512;
	
	private Object radar;
	private Object palette;
	
	@Override
	public void accept(ModPacket packet, Player player) {
		// TODO Auto-generated method stub

	}
}
