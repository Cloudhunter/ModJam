package uk.co.cloudhunter.rpgthing;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import uk.co.cloudhunter.rpgthing.gui.ILayerGUI;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

public class RPGClientProxy extends RPGCommonProxy {
	private HashMap<String, ILayerGUI> layerGUI;

	public RPGClientProxy() {
		super();
		layerGUI = new HashMap<String, ILayerGUI>();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);

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

	@Override
	public void render(RenderGameOverlayEvent event) {
		// Render all layer objects
		for (ILayerGUI g : layerGUI.values())
			g.render(event);
	}
}
