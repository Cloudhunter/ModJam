package uk.co.cloudhunter.rpgthing;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "RPGThing", name = "RPGThing", version = "0.1")
@NetworkMod
public class RPGThing 
{
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		LoggerRPG.setupLogger(event);
		LoggerRPG.info("RPGThing starting up! PreInit."); // much modjam, very pause :(
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		LoggerRPG.info("RPGThing starting up! Init.");
	}
	
}
