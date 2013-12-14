package uk.co.cloudhunter.rpgthing;

import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class LoggerRPG {
	private static Logger logger;

	public void setupLogger(FMLPreInitializationEvent event) {
		logger = event.getModLog();
	}

	public void info(String... msg) {
		Object[] args = new Object[msg.length - 1];
		System.arraycopy(msg, 1, args, 0, msg.length - 1);
		logger.info(String.format(msg[0], args));
	}

	public void warning(String... msg) {
		Object[] args = new Object[msg.length - 1];
		System.arraycopy(msg, 1, args, 0, msg.length - 1);
		logger.warning(String.format(msg[0], args));
	}

	public void warning(Throwable t, String... msg) {
		Object[] args = new Object[msg.length - 1];
		System.arraycopy(msg, 1, args, 0, msg.length - 1);
		logger.log(Level.WARNING, String.format(msg[0], args), t);
	}

	public void fine(String... msg) {
		Object[] args = new Object[msg.length - 1];
		System.arraycopy(msg, 1, args, 0, msg.length - 1);
		logger.fine(String.format(msg[0], args));
	}
}
