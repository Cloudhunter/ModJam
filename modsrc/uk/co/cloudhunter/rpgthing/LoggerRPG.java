package uk.co.cloudhunter.rpgthing;

import java.util.logging.Logger;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class LoggerRPG 
{
	public static Logger logger;
	
	public static void setupLogger(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
	}
}
