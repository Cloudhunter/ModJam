package uk.co.cloudhunter.rpgthing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import uk.co.cloudhunter.rpgthing.core.PlayerDataNetworkHelper;
import uk.co.cloudhunter.rpgthing.database.Database;
import uk.co.cloudhunter.rpgthing.network.ClientPacketHandler;
import uk.co.cloudhunter.rpgthing.network.IRPGNetworkHandler;
import uk.co.cloudhunter.rpgthing.network.ModPacket;
import uk.co.cloudhunter.rpgthing.network.ServerPacketHandler;
import uk.co.cloudhunter.rpgthing.partyline.PartylineNetworkHelper;
import uk.co.cloudhunter.rpgthing.util.TableFactory;
import uk.co.cloudhunter.rpgthing.util.TaskFactory;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class RPGCommonProxy implements IRPGNetworkHandler {

	protected Database database;
	protected TableFactory helperFactoryDatabase;
	protected TaskFactory helperTaskFactory;

	protected ClientPacketHandler clientNetwork;
	protected ServerPacketHandler serverNetwork;

	public PartylineNetworkHelper partylineNetwork;
	public PlayerDataNetworkHelper playerNetwork;

	public Map<Integer, Collection> cachedPotionEffects = new HashMap<Integer, Collection>();

	public RPGCommonProxy() {
		this.database = new Database();
		this.serverNetwork = new ServerPacketHandler();
		this.partylineNetwork = new PartylineNetworkHelper();
		this.playerNetwork = new PlayerDataNetworkHelper();
		GameRegistry.registerPlayerTracker(new PlayerHandler());
	}

	public Database getDatabase() {
		return database;
	}

	public void preInit(FMLPreInitializationEvent event) {
		this.helperFactoryDatabase = new TableFactory();
		this.helperTaskFactory = new TaskFactory();
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
		RPGThing.getLog().info("Sending packet to all: " + packet.toString());
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server != null) {
			Packet250CustomPayload payload = packet.toPacket();
			payload.channel = RPGThing.networkChannel();
			server.getConfigurationManager().sendPacketToAllPlayers(payload);
		}
	}

	public void sendToPlayer(EntityPlayer player, ModPacket packet) {
		RPGThing.getLog().info("Sending packet to player: " + packet.toString());
		Packet250CustomPayload payload = packet.toPacket();
		payload.channel = RPGThing.networkChannel();
		PacketDispatcher.sendPacketToPlayer(payload, (Player) player);
	}

	public Collection getCachedPotionEffects(int id) {
		return cachedPotionEffects.get(id);
	}

	// for future trading
	@ForgeSubscribe
	public void onEntityInteract(EntityInteractEvent event) {
		if (event.target instanceof EntityPlayer)
			RPGThing.getLog().info("%s interacted with %s!", event.entityPlayer.username,
					((EntityPlayer) event.target).username);
	}

	@ForgeSubscribe
	public void onLivingEntityDeath(LivingDeathEvent event) {
		Entity entity = event.entityLiving;
		Entity actor = event.source.getEntity();

		if (actor instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) actor;
			if (entity instanceof EntityMob) {
				int expValue = ((EntityMob) entity).experienceValue;
				uk.co.cloudhunter.rpgthing.core.Player p = uk.co.cloudhunter.rpgthing.core.Player.getPlayer(
						player.username, false);
				p.addExperience(expValue);
			}
		}

	}

	@ForgeSubscribe
	public void onLivingUpdate(LivingUpdateEvent event) {
		EntityLivingBase el = event.entityLiving;
		cachedPotionEffects.put(Integer.valueOf(el.entityId), el.getActivePotionEffects());
	}
	
	public class PlayerHandler implements IPlayerTracker
	{

		@Override
		public void onPlayerLogin(EntityPlayer player) {}

		@Override
		public void onPlayerLogout(EntityPlayer player) {
			uk.co.cloudhunter.rpgthing.core.Player.getPlayer(player.username, false).hasDisconnected = true;
		}

		@Override
		public void onPlayerChangedDimension(EntityPlayer player) {}

		@Override
		public void onPlayerRespawn(EntityPlayer player) {}
		
	}
}