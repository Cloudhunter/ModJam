package uk.co.cloudhunter.rpgthing;

import java.util.logging.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class LoggerRPG {
	private static Logger logger;

	public void setupLogger(FMLPreInitializationEvent event) {
		logger = event.getModLog();
	}

	public void info(String msg) {
		logger.info(msg);
	}

	public void warning(String msg) {
		logger.warning(msg);
	}

	public void fine(String msg) {
		logger.fine(msg);
	}
}
