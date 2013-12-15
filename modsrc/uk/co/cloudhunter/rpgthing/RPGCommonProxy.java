package uk.co.cloudhunter.rpgthing;

import java.util.ArrayList;

import uk.co.cloudhunter.rpgthing.core.Party;
import uk.co.cloudhunter.rpgthing.database.Database;
import uk.co.cloudhunter.rpgthing.network.ClientPacketHandler;
import uk.co.cloudhunter.rpgthing.network.IRPGNetworkHandler;
import uk.co.cloudhunter.rpgthing.network.ModPacket;
import uk.co.cloudhunter.rpgthing.network.ServerPacketHandler;
import uk.co.cloudhunter.rpgthing.util.TableFactory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class RPGCommonProxy implements IRPGNetworkHandler {

	protected Database database;
	protected TableFactory helperFactoryDatabase;

	protected ClientPacketHandler clientNetwork;
	protected ServerPacketHandler serverNetwork;


	public RPGCommonProxy() {
		this.database = new Database();
		this.serverNetwork = new ServerPacketHandler();
	}

	public Database getDatabase() {
		return database;
	}

	public void preInit(FMLPreInitializationEvent event) {
		this.helperFactoryDatabase = new TableFactory();
	}

	public void init(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(RPGThing.getProxy());
		for (ITickHandler o : RPGThing.getServerTickHandlers())
			TickRegistry.registerTickHandler(o, Side.SERVER);
	}

	@Override
	public void accept(ModPacket packet, Player player) {
		if (packet.getPacketIsForServer())
			serverNetwork.accept(packet, player);
		else
			return;
	}

	public void sendToServer(ModPacket packet) {
		throw new RuntimeException("Cannot send to server: this method was not overridden!!");
	}

	public void sendToAllPlayers(ModPacket packet) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server != null) {
			Packet250CustomPayload payload = packet.toPacket();
			payload.channel = RPGThing.networkChannel();
			server.getConfigurationManager().sendPacketToAllPlayers(payload);
		}
	}

	public void sendToPlayer(EntityPlayer player, ModPacket packet) {
		Packet250CustomPayload payload = packet.toPacket();
		payload.channel = RPGThing.networkChannel();
		PacketDispatcher.sendPacketToPlayer(payload, (Player) player);
	}

	@EventHandler
	private void onLivingDeathEvent(LivingDeathEvent event) {
		
	}

}
