package uk.co.cloudhunter.rpgthing.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import uk.co.cloudhunter.rpgthing.RPGThing;
import uk.co.cloudhunter.rpgthing.core.Party;
import uk.co.cloudhunter.rpgthing.core.Player;
import uk.co.cloudhunter.rpgthing.mipmap.MapImageBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class GUILayerMipmap extends Gui implements ILayerGUI {

	private Minecraft mc;

	private int displayWidthCache;
	private int displayHeightCache;
	private int displayScaleCache;

	private ScaledResolution res;

	public GUILayerMipmap() {
		super();
		mc = Minecraft.getMinecraft();
	}

	private long frameGlowCount = 0;

	@Override
	public void render(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE)
			return;

		if (mc.displayWidth != displayWidthCache || mc.displayHeight != displayHeightCache
				|| mc.gameSettings.guiScale != displayScaleCache) {
			res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			displayWidthCache = mc.displayWidth;
			displayHeightCache = mc.displayHeight;
			displayScaleCache = mc.gameSettings.guiScale;
		}

		Player p = Player.getPlayer(mc.thePlayer.username, true);
		if (p.getParty() != null) {
			Party party = p.getParty();
			Player[] partyPlayers = party.getPlayers();
			List<EntityPlayer> worldPlayers = mc.theWorld.playerEntities;
			for (int i = 0; i < partyPlayers.length; i++) {
				String playerName = partyPlayers[i].getName();
				if (playerName.equals(mc.thePlayer.username))
					continue;
				renderPlayer(partyPlayers[i].getClientPlayer());
			}
		}
	}

	public void renderPlayer(EntityPlayer entity) {
		AbstractClientPlayer abstractClient = getAbstractFromEntity(entity);
		if (abstractClient == null)
			return;
		ResourceLocation loc = abstractClient.getLocationSkin();
		if (loc == null)
			return;

		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		double cx = res.getScaledWidth_double() / 2.0d;
		double cy = res.getScaledHeight_double() / 2.0d;

		Vec3 thatV = entity.getPosition(1.0f);
		Vec3 thisV = mc.thePlayer.getPosition(1.0f);
		Vec3 thatthisV = thisV.subtract(thatV);
		
		double thisthatAng = Math.atan2(thatthisV.zCoord, thatthisV.xCoord);
		double yaw = mc.thePlayer.rotationYawHead;
		while (yaw >= 180.0F)
			yaw -= 180.0F;
		double productInflect = (Math.PI * yaw / 180) + thisthatAng;

		double rad = 30.0d;
		double ddx = rad * Math.cos(productInflect) + cx;
		double ddy = rad * Math.sin(productInflect) + cy;
		
		
		
		GL11.glDisable(GL11.GL_BLEND);
	}

	private AbstractClientPlayer getAbstractFromEntity(EntityPlayer entity) {
		if (entity instanceof AbstractClientPlayer)
			return (AbstractClientPlayer) entity;
		return null;
	}
	
	public void emitQuad(double x, double y, double u, double v, double u2, double v2, double width, double height,
			double clamp) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, this.zLevel, u * clamp, v2 * clamp);
		tessellator.addVertexWithUV(x + width, y + height, this.zLevel, u2 * clamp, v2 * clamp);
		tessellator.addVertexWithUV(x + width, y, this.zLevel, u2 * clamp, v * clamp);
		tessellator.addVertexWithUV(x, y, this.zLevel, u * clamp, v * clamp);
		tessellator.draw();
	}

}
