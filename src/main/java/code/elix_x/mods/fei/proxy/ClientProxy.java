package code.elix_x.mods.fei.proxy;

import java.io.File;

import com.mmyzd.llor.LightLevelOverlayReloaded;

import at.feldim2425.moreoverlays.MoreOverlays;
import code.elix_x.excomms.color.RGBA;
import code.elix_x.excomms.reflection.ReflectionHelper.AClass;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.FEIApi;
import code.elix_x.mods.fei.api.client.gui.FEIGuiOverride;
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
import code.elix_x.mods.fei.utils.ChunkBordersFEIUtil;
import code.elix_x.mods.fei.utils.IFEIUtilInternal;
import code.elix_x.mods.fei.utils.LightLevelOverlayToggleFEIUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IFEIProxy {

	public FEIUtilsGrid grid;

	public ChunkBordersFEIUtil chunkBordersCycle;
	public LightLevelOverlayToggleFEIUtil llorToggle;

	public ClientProxy(){
		new AClass<>(FEIApi.class).getDeclaredField("INSTANCE").setFinal(false).set(null, new FEIApi(){

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
		try{
			Class.forName("code.elix_x.mods.fei.client.jeioverride.JeiReflector").getName();
			Class.forName("code.elix_x.mods.fei.client.jeioverride.ItemListOverlayOverride").getName();
		} catch(Exception e){
			throw new CustomModLoadingErrorDisplayException("Incompatible JEI version installed", e){

				@Override
				public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer){

				}

				@Override
				public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime){
					int nextY = (errorScreen.height - 24) / 2;
					errorScreen.drawCenteredString(fontRenderer, "FEI Detected Incompatible JEI version", errorScreen.width / 2, nextY, new RGBA(255, 0, 0, 255).argb());
					nextY += 8;
					errorScreen.drawCenteredString(fontRenderer, "Something changed in internal JEI classes between updates", errorScreen.width / 2, nextY, new RGBA(255, 0, 0, 255).argb());
					nextY += 8;
					errorScreen.drawCenteredString(fontRenderer, "For now, please report this to github (https://github.com/elix-x/Forever-Enough-Items/issues) and downgrade to JEI " + mezz.jei.config.Constants.VERSION + " if possible.", errorScreen.width / 2, nextY, new RGBA(255, 0, 0, 255).argb());
				}

			};
		}

		grid = new FEIUtilsGrid();
		for(IFEIUtilInternal util : FEIConfiguration.utils) grid.addElement(util);
		grid.addElement(chunkBordersCycle = new ChunkBordersFEIUtil());
		if(Loader.isModLoaded(LightLevelOverlayReloaded.MODID) || Loader.isModLoaded(MoreOverlays.MOD_ID))
			grid.addElement(llorToggle = new LightLevelOverlayToggleFEIUtil(Loader.isModLoaded(LightLevelOverlayReloaded.MODID), Loader.isModLoaded(MoreOverlays.MOD_ID)));
	}

	@Override
	public void init(FMLInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(new FEIGuiOverrideEvents());
		MinecraftForge.EVENT_BUS.register(new FEIProfileEvents());
	}

	@Override
	public void postInit(FMLPostInitializationEvent event){
		FEIGuiOverride.addElement(grid);

		FEIGuiOverride.addElement(new FEIInventorySavesList());

		FEIGuiOverride.addElement(new FEIModsItemsDropdown());

		FEIGuiOverride.addElement(new FEIProfilesSwitcher());
	}

	@Override
	public void loadComplete(FMLLoadCompleteEvent event){
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
