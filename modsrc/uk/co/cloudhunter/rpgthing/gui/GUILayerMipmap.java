package uk.co.cloudhunter.rpgthing.gui;

import uk.co.cloudhunter.rpgthing.mipmap.MapCache;
import uk.co.cloudhunter.rpgthing.mipmap.MapImageBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class GUILayerMipmap implements ILayerGUI {

	private Minecraft minecraft;
	private World world;

	private int heightOfWorld = 512;

	private Object radar;
	private Object palette;

	private MapCache mapCache;
	
	private MapImageBuffer mapLayer;
	
	public GUILayerMipmap() {

	}

	@Override
	public void render(RenderGameOverlayEvent event) {
		// TODO Auto-generated method stub

	}

}
