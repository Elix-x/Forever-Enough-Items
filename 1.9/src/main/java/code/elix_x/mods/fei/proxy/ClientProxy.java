package code.elix_x.mods.fei.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import cei.ChunkEdgeRenderer;
import code.elix_x.excore.utils.proxy.IProxy;
import code.elix_x.excore.utils.reflection.AdvancedReflectionHelper.AField;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.FEIApi;
import code.elix_x.mods.fei.api.gui.FEIGuiOverride;
import code.elix_x.mods.fei.api.permission.IFEIPermissionsManager;
import code.elix_x.mods.fei.api.profile.Profile;
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
import code.elix_x.mods.fei.utils.LLORToggle;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.IEventListener;

public class ClientProxy implements IProxy {

	public static ChunkEdgeRenderer cei;

	public FEIUtilsGrid grid;

	public LLORToggle llorToggle;
	public CEICycle ceiCycle;

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
		grid.addElement(llorToggle = new LLORToggle());
		grid.addElement(ceiCycle = new CEICycle());
	}

	@Override
	public void init(FMLInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(new FEIGuiOverrideEvents());
		MinecraftForge.EVENT_BUS.register(new FEIProfileEvents());
	}

	@Override
	public void postInit(FMLPostInitializationEvent event){
		for(Object o : ((ConcurrentHashMap<Object, ArrayList<IEventListener>>) new AField<ConcurrentHashMap<Object, ArrayList<IEventListener>>>(EventBus.class, "listeners").setAccessible(true).get(MinecraftForge.EVENT_BUS)).keySet()){
			if(o instanceof ChunkEdgeRenderer){
				cei = (ChunkEdgeRenderer) o;
				break;
			}
		}

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
