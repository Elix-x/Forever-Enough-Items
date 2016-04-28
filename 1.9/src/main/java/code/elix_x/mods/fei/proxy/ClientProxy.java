package code.elix_x.mods.fei.proxy;

import java.io.File;

import com.mmyzd.llor.LightLevelOverlayReloaded;

import at.feldim2425.moreoverlays.MoreOverlays;
import cei.ChunkEdgeIndicator;
import code.elix_x.excore.utils.proxy.IProxy;
import code.elix_x.excore.utils.reflection.AdvancedReflectionHelper.AField;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.FEIApi;
import code.elix_x.mods.fei.api.gui.FEIGuiOverride;
import code.elix_x.mods.fei.api.permission.IFEIPermissionsManager;
import code.elix_x.mods.fei.api.profile.Profile;
import code.elix_x.mods.fei.api.utils.ForFEIUtil;
import code.elix_x.mods.fei.api.utils.IFEIUtil;
import code.elix_x.mods.fei.client.events.FEIGuiOverrideEvents;
import code.elix_x.mods.fei.client.events.FEIProfileEvents;
import code.elix_x.mods.fei.client.gui.element.FEIInventorySavesList;
import code.elix_x.mods.fei.client.gui.element.FEIModsItemsDropdown;
import code.elix_x.mods.fei.client.gui.element.FEIProfilesSwitcher;
import code.elix_x.mods.fei.client.gui.element.FEIUtilsGrid;
import code.elix_x.mods.fei.config.FEIConfiguration;
import code.elix_x.mods.fei.net.SyncedFEIUtilPropertyPacket;
import code.elix_x.mods.fei.permission.FEIPermissionsManager;
import code.elix_x.mods.fei.utils.CEICycle;
import code.elix_x.mods.fei.utils.LightLevelOverlayToggle;
import code.elix_x.mods.fei.utils.MOCBCycle;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy {

	public FEIUtilsGrid grid;

	public LightLevelOverlayToggle llorToggle;
	public ForFEIUtil chunkEdgesCycle;

	public ClientProxy(){
		new AField<FEIApi>(FEIApi.class, "INSTANCE").setFinal(false).set(null, new FEIApi(){

			@Override
			public void onUtilPropertySelect(int id){
				ForeverEnoughItemsBase.net.sendToServer(new SyncedFEIUtilPropertyPacket(id));
			}

			@Override
			public IFEIPermissionsManager getPermissionsManager(World world){
				return FEIPermissionsManager.get(world);
			}

			@Override
			public File getFEIConfigDir(){
				return ForeverEnoughItemsBase.configDir;
			}

			@Override
			public void addGridUtil(IFEIUtil util){
				grid.addElement(util);
			}

		});
	}

	@Override
	public void preInit(FMLPreInitializationEvent event){
		grid = new FEIUtilsGrid();
		grid.addElement(FEIConfiguration.bin);
		grid.addElement(FEIConfiguration.gameMode);
		grid.addElement(FEIConfiguration.time);
		grid.addElement(FEIConfiguration.weather);
		grid.addElement(FEIConfiguration.magnet);
		grid.addElement(FEIConfiguration.heal);
		grid.addElement(FEIConfiguration.saturate);
		if(Loader.isModLoaded(LightLevelOverlayReloaded.MODID) || Loader.isModLoaded(MoreOverlays.MOD_ID)) grid.addElement(llorToggle = new LightLevelOverlayToggle(Loader.isModLoaded(LightLevelOverlayReloaded.MODID), Loader.isModLoaded(MoreOverlays.MOD_ID)));

		if(Loader.isModLoaded(ChunkEdgeIndicator.MODID)) chunkEdgesCycle = new CEICycle();
		else if(Loader.isModLoaded(MoreOverlays.MOD_ID)) chunkEdgesCycle = new MOCBCycle();
		if(Loader.isModLoaded(ChunkEdgeIndicator.MODID) || Loader.isModLoaded(MoreOverlays.MOD_ID)) grid.addElement(chunkEdgesCycle);
	}

	@Override
	public void init(FMLInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(new FEIGuiOverrideEvents());
		MinecraftForge.EVENT_BUS.register(new FEIProfileEvents());
	}

	@Override
	public void postInit(FMLPostInitializationEvent event){
		if(Loader.isModLoaded(ChunkEdgeIndicator.MODID)) CEICycle.initChunkRenderer();

		FEIGuiOverride.addElement(grid);

		FEIGuiOverride.addElement(new FEIInventorySavesList());

		FEIGuiOverride.addElement(new FEIModsItemsDropdown());

		FEIGuiOverride.addElement(new FEIProfilesSwitcher());

		Profile.load();

		FEIGuiOverride.loadFromCurrentProfile();

		Runtime.getRuntime().addShutdownHook(new Thread(){

			@Override
			public void run(){
				Profile.save();
			}

		});
	}

}
