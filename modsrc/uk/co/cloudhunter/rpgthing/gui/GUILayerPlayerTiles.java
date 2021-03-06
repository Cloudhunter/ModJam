package uk.co.cloudhunter.rpgthing.gui;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import uk.co.cloudhunter.rpgthing.RPGClientProxy;
import uk.co.cloudhunter.rpgthing.RPGThing;
import uk.co.cloudhunter.rpgthing.core.Party;
import uk.co.cloudhunter.rpgthing.core.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class GUILayerPlayerTiles extends Gui implements ILayerGUI {

	private Minecraft mc;

	private static final int playerIconSize = 24;
	private static final int playerLabelTextSize = 9;

	private static final int buffIconSize = 9;
	private static final int buffIconMargin = 1;

	private static final int buffIconsPerRow = 5;

	public GUILayerPlayerTiles() {
		super();
		mc = Minecraft.getMinecraft();
	}

	private long frameGlowCount = 0;

	@Override
	public void render(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE)
			return;
		renderPlayer(mc.thePlayer, mc.thePlayer.username, 12f, 0f, playerIconSize / 3);
		Player p = Player.getPlayer(mc.thePlayer.username, true);
		if (p.getParty() != null) {
			Party party = p.getParty();
			Player[] partyPlayers = party.getPlayers();
			List<EntityPlayer> worldPlayers = mc.theWorld.playerEntities;
			int renderLayer = 1;
			for (int i = 0; i < partyPlayers.length; i++) {
				String playerName = partyPlayers[i].getName();
				if (playerName.equals(mc.thePlayer.username))
					continue;
				renderPlayer(partyPlayers[i].getClientPlayer(), playerName, 12f, 26f * renderLayer, playerIconSize / 3);
				renderLayer++;
			}
		}
	}

	public void renderPlayer(EntityPlayer entity, String altName, float x, float y, float scale) {
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		String label = (entity != null) ? entity.username : altName;
		int dlen = mc.fontRenderer.getStringWidth(label) - 8;
		if (entity != null && entity.isDead)
			GL11.glColor3d(1.0, 0.25, 0.25);
		mc.getTextureManager().bindTexture(new ResourceLocation(RPGThing.assetKey(), "textures/gui/player-label.png"));
		emitQuad(x + 12, y, 12, 13, 32, 51, 10, 32, 0.015625d);
		emitQuad(x + 12 + 10, y, 26, 13, 36, 51, dlen, 32, 0.015625d);
		emitQuad(x + 12 + 10 + dlen, y, 32, 13, 52, 51, 10, 32, 0.015625d);
		mc.getTextureManager().bindTexture(new ResourceLocation(RPGThing.assetKey(), "textures/gui/player-frame.png"));
		emitQuad(x - 11, y + 10, 0.25f, 0.25f, 0.85f, 0.85f, 26, 26, 1.0F);

		float health = (entity != null) ? Math.max(0, entity.getHealth()) : 0;
		float damage = (entity != null) ? entity.getMaxHealth() - health : 1;
		float pxPerHealth = (dlen + 12) / ((entity != null) ? entity.getMaxHealth() : 1);
		drawRect((int) x + 16, (int) y + 13, (int) x + 16 + (int) (health * pxPerHealth), (int) y + 19, 0xFF00FF00);
		drawRect((int) x + 12 + 16 + dlen - (int) (damage * pxPerHealth), (int) y + 13, (int) x + 12 + 16 + dlen,
				(int) y + 19, 0xFFFF0000);

		GL11.glColor3d(1.0, 1.0, 1.0);
		mc.fontRenderer.drawStringWithShadow(label, (int) x + 18, (int) y + 12, 0x00FFFFFF);
		GL11.glDisable(GL11.GL_BLEND);

		if (entity != null) {
			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			GL11.glPushMatrix();
			if (entity.username.equals(mc.thePlayer.username))
				GL11.glTranslatef(x, y + 14, 50.0F);
			else
				GL11.glTranslatef(x, y + 28, 50.0F);
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

			renderPlayerBuffs(entity, (int) x + 14, (int) y + 22);
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		mc.getTextureManager()
				.bindTexture(new ResourceLocation(RPGThing.assetKey(), "textures/gui/player-crowns.png"));
		if (entity == null)
			emitQuad(x + 4, y + 24, 0f, 0.5f, 0.5f, 1f, 8, 8, 1.0f);
		else if (Player.getPlayer(entity.username, true).getParty() == null
				|| (Player.getPlayer(entity.username, true).getParty().getOwner() != null && Player
						.getPlayer(entity.username, true).getParty().getOwner().isThisPlayer(entity)))
			emitQuad(x + 4, y + 24, 0.5f, 0, 1.0f, 0.5f, 8, 8, 1.0f);
		else
			emitQuad(x + 4, y + 24, 0f, 0f, 0.5f, 0.5f, 8, 8, 1.0f);
		emitQuad(x + 4, y + 9, 0f, 0f, 0.5f, 0.5f, 8, 8, 1.0f);

		if (entity != null && entity.username.equals(mc.thePlayer.username)) {
			Player p = Player.getPlayer(entity.username, true);
			if (p.getUnspentSkillPoints() > 0) {
				GL11.glPushMatrix();
				double opacity = (1 + Math.sin(0.125 * frameGlowCount++)) / 2;
				GL11.glColor4d(1.0d, 1.0d, 1.0d, opacity);
				mc.getTextureManager().bindTexture(
						new ResourceLocation(RPGThing.assetKey(), "textures/gui/player-crown-glow.png"));
				emitQuad(x + 4, y + 9, 0f, 0f, 1f, 1f, 8, 8, 1.0f);
				GL11.glPopMatrix();
			}
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPushMatrix();
		String lvl = " 1";
		int glen = mc.fontRenderer.getStringWidth(lvl);
		GL11.glTranslatef(x + 3 + (glen / 4), y + 12, 50.0F);
		GL11.glScalef(0.4f, 0.4f, 50.0f);
		mc.fontRenderer.drawStringWithShadow(lvl, (int) 0, (int) 0, 0x00FFFFFF);
		GL11.glPopMatrix();
	}

	public void renderPlayerBuffs(EntityLivingBase entity, int x, int y) {
		if (!entity.getActivePotionEffects().isEmpty()) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			int i = 0;
			Collection effects = RPGThing.getProxy().getCachedPotionEffects(entity.entityId);
			if (effects == null)
				return;
			for (Iterator iterator = effects.iterator(); iterator.hasNext(); i++) {
				PotionEffect potioneffect = (PotionEffect) iterator.next();
				Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
				if (potion.hasStatusIcon()) {
					int index = potion.getStatusIconIndex();
					if (potioneffect.duration > 100 || (potioneffect.duration % 10 > 5)) {
						int dx = x + (i % buffIconsPerRow) * (buffIconSize + 2 * buffIconMargin);
						int dy = y + (i / buffIconsPerRow) * (buffIconSize + 2 * buffIconMargin);
						emitQuad(dx, dy, (index % 8) * 18, 198 + (index / 8) * 18, 18 + (index % 8) * 18,
								18 + 198 + (index / 8) * 18, buffIconSize, buffIconSize, 0.00390625F);
					}
				}
			}
		}
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
