package uk.co.cloudhunter.rpgthing.gui;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Anything which isn't actually a GUI (eg, screen overlay). Use this. Be happy. Rejoice.
 * 
 * @author AfterLifeLochie
 */
public interface ILayerGUI {
	public void render(RenderGameOverlayEvent event);
}
