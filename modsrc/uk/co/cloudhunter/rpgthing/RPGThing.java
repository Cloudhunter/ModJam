package uk.co.cloudhunter.rpgthing;

import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "RPGThing", name = "RPGThing", version = "0.1")
@NetworkMod
public class RPGThing 
{
	
	@Subscribe
	public void preInit(FMLPreInitializationEvent event)
	{
		LoggerRPG.setupLogger(event);
		LoggerRPG.info("RPGThing starting up! PreInit.");
	}

	@Subscribe
	public void init(FMLInitializationEvent event)
	{
		LoggerRPG.info("RPGThing starting up! Init.");
	}
	
}
