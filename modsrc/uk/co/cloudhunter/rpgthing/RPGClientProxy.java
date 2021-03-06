package uk.co.cloudhunter.rpgthing;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import uk.co.cloudhunter.rpgthing.core.Party;
import uk.co.cloudhunter.rpgthing.gui.ILayerGUI;
import uk.co.cloudhunter.rpgthing.network.ClientPacketHandler;
import uk.co.cloudhunter.rpgthing.network.ModPacket;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class RPGClientProxy extends RPGCommonProxy {
	private HashMap<String, ILayerGUI> layerGUI;

	public RPGClientProxy() {
		super();
		this.layerGUI = new HashMap<String, ILayerGUI>();
		this.clientNetwork = new ClientPacketHandler();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		RPGThing.GUIs.registerGUI("PlayerTiles", uk.co.cloudhunter.rpgthing.gui.GUILayerPlayerTiles.class);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		for (ITickHandler o : RPGThing.getClientTickHandlers())
			TickRegistry.registerTickHandler(o, Side.CLIENT);

		// Initialize all GUI layer objects
		for (Entry<String, Class<?>> entry : RPGThing.GUIs.getEntries()) {
			try {
				Constructor c = entry.getValue().getConstructor();
				ILayerGUI result = (ILayerGUI) c.newInstance();
				layerGUI.put(entry.getKey(), result);
				RPGThing.getLog().fine("RPGCommonProxy creating GUI: %s", entry.getKey());
			} catch (Throwable t) {
				RPGThing.getLog().warning(t, "Failed to initialize GUI!");
			}
		}
	}

	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void render(RenderGameOverlayEvent event) {
		for (ILayerGUI g : layerGUI.values())
			g.render(event);
	}

	@Override
	public void accept(ModPacket packet, Player player) {
		if (packet.getPacketIsForServer())
			serverNetwork.accept(packet, player);
		else
			clientNetwork.accept(packet, player);
	}

	@Override
	public void sendToServer(ModPacket packet) {
		RPGThing.getLog().info("Sending packet to server: " + packet.toString());
		Packet250CustomPayload payload = packet.toPacket();
		payload.channel = RPGThing.networkChannel();
		FMLClientHandler.instance().sendPacket(payload);
	}
}
