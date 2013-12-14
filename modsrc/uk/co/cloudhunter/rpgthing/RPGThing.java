package uk.co.cloudhunter.rpgthing;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "RPGThing", name = "RPGThing", version = "0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels = "rpgthing",
		packetHandler = uk.co.cloudhunter.rpgthing.network.DefaultPacketHandler.class)
public class RPGThing {

	private static RPGThing instance;
	private static LoggerRPG log;

	public static RPGThing getInstance() {
		return RPGThing.instance;
	}

	public static RPGCommonProxy getProxy() {
		return proxy;
	}

	public static LoggerRPG getLog() {
		return log;
	}

	public static String assetKey() {
		return "rpgthing";
	}

	public static String networkChannel() {
		return "rpgthing";
	}

	public static class Blocks {

	}

	public static class Items {

	}

	public static class GUIs {
		private static HashMap<String, Class<?>> renderLayers = new HashMap<String, Class<?>>();

		public static void registerGUI(String name, Class<?> clazz) {
			getLog().fine("Host registering GUI: %s (%s)", name, clazz.getName());
			renderLayers.put(name, clazz);
		}

		public static Set<Entry<String, Class<?>>> getEntries() {
			return renderLayers.entrySet();
		}
	}

	@SidedProxy(clientSide = "uk.co.cloudhunter.rpgthing.RPGClientProxy",
			serverSide = "uk.co.cloudhunter.rpgthing.RPGCommonProxy")
	public static RPGCommonProxy proxy;

	public RPGThing() {
		RPGThing.instance = this;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		RPGThing.log = new LoggerRPG();
		RPGThing.log.setupLogger(event);
		getLog().info("RPGThing starting up! PreInit.");
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
