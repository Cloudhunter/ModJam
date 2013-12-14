package uk.co.cloudhunter.rpgthing.gui;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import uk.co.cloudhunter.rpgthing.RPGThing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class GUILayerPlayerTiles extends Gui implements ILayerGUI {

	private Minecraft theMinecraft;

	private static final int playerIconSize = 24;
	private static final int playerLabelTextSize = 9;

	private static final int buffIconSize = 6;
	private static final int buffIconMargin = 2;

	private static final int buffIconsPerRow = 4;

	public GUILayerPlayerTiles() {
		super();
		theMinecraft = Minecraft.getMinecraft();
	}

	@Override
	public void render(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE)
			return;

		theMinecraft.getTextureManager().bindTexture(
				new ResourceLocation(RPGThing.assetKey(), "/textures/gui/player-frame.png"));
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		emitQuad(8, 8, 0, 0, 1, 1, 24, 24, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);

		
		
		theMinecraft.fontRenderer.drawStringWithShadow(theMinecraft.thePlayer.username, 36, 11, 0x00FFFFFF);
		renderPlayer(theMinecraft.thePlayer, 20f, 16f, playerIconSize / 3);
		renderPlayerBuffs(theMinecraft.thePlayer, 36, 22);
	}

	public void renderPlayer(EntityLivingBase entity, float x, float y, float scale) {
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 50.0F);
		GL11.glScalef(-scale, scale, scale);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		RenderHelper.enableStandardItemLighting();
		RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public void renderPlayerBuffs(EntityLivingBase entity, int x, int y) {
		if (!entity.getActivePotionEffects().isEmpty()) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			int i = 0;
			for (Iterator iterator = entity.getActivePotionEffects().iterator(); iterator.hasNext(); i++) {
				PotionEffect potioneffect = (PotionEffect) iterator.next();
				Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				theMinecraft.getTextureManager().bindTexture(
						new ResourceLocation("textures/gui/container/inventory.png"));
				if (potion.hasStatusIcon()) {
					int index = potion.getStatusIconIndex();
					int dx = x + (i % buffIconsPerRow) * (buffIconSize + 2 * buffIconMargin);
					int dy = y + (i / buffIconsPerRow) * (buffIconSize + 2 * buffIconMargin);
					emitQuad(dx, dy, (index % 8) * 18, 198 + (index / 8) * 18, 18 + (index % 8) * 18,
							18 + 198 + (index / 8) * 18, buffIconSize, buffIconSize, 0.00390625F);
				}
			}
		}
	}

	public void emitQuad(double x, double y, double u, double v, double u2, double v2, double width, double height,
			double clamp) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, this.zLevel, u * clamp, v2 * clamp);
		tessellator.addVertexWithUV(x + height, y + height, this.zLevel, u2 * clamp, v2 * clamp);
		tessellator.addVertexWithUV(x + height, y, this.zLevel, u2 * clamp, v * clamp);
		tessellator.addVertexWithUV(x, y, this.zLevel, u * clamp, v * clamp);
		tessellator.draw();
	}

}
