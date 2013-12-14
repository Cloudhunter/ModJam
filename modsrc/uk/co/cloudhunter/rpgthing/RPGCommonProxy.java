package uk.co.cloudhunter.rpgthing;

import uk.co.cloudhunter.rpgthing.database.Database;
import uk.co.cloudhunter.rpgthing.util.TableFactory;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class RPGCommonProxy {

	private Database database;
	private TableFactory helperFactoryDatabase;

	public RPGCommonProxy() {
		this.database = new Database();
		this.helperFactoryDatabase = new TableFactory();
	}

	public Database getDatabase() {
		return database;
	}

	public void preInit(FMLPreInitializationEvent event) {
		// TODO Auto-generated method stub

	}

	public void init(FMLInitializationEvent event) {
		RPGThing.GUIs.registerGUI("PlayerTiles", uk.co.cloudhunter.rpgthing.gui.GUILayerPlayerTiles.class);
	}

	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(RPGThing.getInstance());
	}

	public void render(RenderGameOverlayEvent event) {
		// TODO Auto-generated method stub

	}

}
