package code.elix_x.mods.fei.client.jeioverride;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import code.elix_x.excore.utils.client.gui.elements.IGuiElement;
import code.elix_x.excore.utils.reflection.AdvancedReflectionHelper.AField;
import code.elix_x.mods.fei.api.gui.FEIGuiOverride;
import code.elix_x.mods.fei.api.gui.elements.IConfigurableFEIGuiElement;
import code.elix_x.mods.fei.api.gui.elements.INotDisableableFEIGuiElement;
import code.elix_x.mods.fei.api.gui.elements.ISaveableFEIGuiElement;
import code.elix_x.mods.fei.api.profile.FEIChangeProfileEvent;
import code.elix_x.mods.fei.api.profile.Profile;
import code.elix_x.mods.fei.config.FEIConfiguration;
import mezz.jei.Internal;
import mezz.jei.JeiRuntime;
import mezz.jei.JustEnoughItems;
import mezz.jei.ProxyCommonClient;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.config.Config;
import mezz.jei.config.LocalizedConfiguration;
import mezz.jei.gui.ItemListOverlay;
import mezz.jei.util.Log;
import mezz.jei.util.StackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@JEIPlugin
public class JeiReflector implements IModPlugin, IGuiElement<FEIGuiOverride>, INotDisableableFEIGuiElement, ISaveableFEIGuiElement, IConfigurableFEIGuiElement {

	public static final Gson gson = new Gson();

	private static final AField<JeiRuntime, ItemListOverlay> itemListOverlay = new AField(JeiRuntime.class, "itemListOverlay").setAccessible(true).setFinal(false);
	private static final AField<StackHelper, Boolean> uidCacheEnabled = new AField(StackHelper.class, "uidCacheEnabled").setAccessible(true);
	private static final AField<ItemListOverlay, List<IAdvancedGuiHandler<?>>> advancedGuiHandlers = new AField(ItemListOverlay.class, "advancedGuiHandlers").setAccessible(true);
	private static final AField<ProxyCommonClient, List<IModPlugin>> plugins = new AField(ProxyCommonClient.class, "plugins").setAccessible(true);
	private static final AField<Config, LocalizedConfiguration> itemBlacklistConfig = new AField(Config.class, "itemBlacklistConfig").setAccessible(true);
	private static final AField<Config, LocalizedConfiguration> searchColorsConfig = new AField(Config.class, "searchColorsConfig").setAccessible(true);
	private static final AField<Config, Boolean> centerSearchBarEnabled = new AField(Config.class, "centerSearchBarEnabled").setAccessible(true);
	private static final AField<Configuration, File> file = new AField(Configuration.class, "file").setAccessible(true);
	private static final AField<Configuration, Boolean> changed = new AField(Configuration.class, "changed").setAccessible(true);

	public static final JeiReflector INSTANCE = null;

	public boolean canGiveItems;
	public boolean canDeleteItemsAboveItemsList;

	public int searchFieldWidth;
	public int searchFieldHeight;

	public JeiReflector(){
		if(INSTANCE != null) throw new IllegalArgumentException("An instance already exists!");
		FEIGuiOverride.addElement(this);
		MinecraftForge.EVENT_BUS.register(this);
		new AField(JeiReflector.class, "INSTANCE").setFinal(false).set(null, INSTANCE);
	}

	public void reloadItemListOverlay(final IJeiRuntime jeiRuntime){
		ItemListOverlayOverride overlay = new ItemListOverlayOverride(((ItemListOverlay) jeiRuntime.getItemListOverlay()).getItemFilter(), advancedGuiHandlers.get((ItemListOverlay) jeiRuntime.getItemListOverlay()), Internal.getIngredientRegistry(), canGiveItems, canDeleteItemsAboveItemsList, searchFieldWidth, searchFieldHeight);
		itemListOverlay.set((JeiRuntime) jeiRuntime, overlay);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void provileReload(FEIChangeProfileEvent event){
		reloadItemListOverlay(Internal.getRuntime());
	}

	@Override
	public void register(IModRegistry registry){

	}

	@Override
	public void onRuntimeAvailable(final IJeiRuntime jeiRuntime){
		new Thread(){

			public void run(){
				while(uidCacheEnabled.get(Internal.getStackHelper())){
					try{
						Thread.sleep(1);
					} catch(InterruptedException e){

					}
				}

				reloadItemListOverlay(jeiRuntime);

				Iterator<IModPlugin> iterator = plugins.get((ProxyCommonClient) JustEnoughItems.getProxy()).iterator();
				while(iterator.hasNext()){
					IModPlugin plugin = iterator.next();
					if(plugin != JeiReflector.this){
						try{
							plugin.onRuntimeAvailable(jeiRuntime);
						} catch(RuntimeException e){
							Log.error("Mod plugin failed: {}", plugin.getClass(), e);
							iterator.remove();
						}
					}
				}
			}

		}.start();
	}

	@Override
	public String getUnlocalizedName(){
		return "fei.gui.override.jei.override";
	}

	@Override
	public void openConfigGui(GuiScreen parent, FEIGuiOverride fei){
		parent.mc.displayGuiScreen(new JEIOverrideConfigurationGui(parent, this));
	}

	@Override
	public void load(Profile profile, JsonObject json){
		JsonData data = gson.fromJson(json, JsonData.class);

		canGiveItems = data.canGiveItems;
		canDeleteItemsAboveItemsList = data.canDeleteItemsAboveItemsList;
		searchFieldWidth = data.searchFieldWidth;
		searchFieldHeight = data.searchFieldHeight;

		if(data.moveSearchFieldToCenter != null) centerSearchBarEnabled.set(null, data.moveSearchFieldToCenter);

		File jeiDir = new File(profile.getSaveDir(), "JEI");
		jeiDir.mkdir();

		if(FEIConfiguration.loadJeiFromProfileConfig){
			LocalizedConfiguration config = Config.getConfig();
			file.set(config, new File(jeiDir, config.getConfigFile().getName()));
			changed.set(config, true);
			config.load();
		}

		if(FEIConfiguration.loadJeiFromProfileWorld){
			Configuration world = Config.getWorldConfig();
			if(world != null){
				file.set(world, new File(jeiDir, world.getConfigFile().getName()));
				changed.set(world, true);
				world.load();
			}
		}

		if(FEIConfiguration.loadJeiFromProfileBlacklist){
			LocalizedConfiguration blacklist = itemBlacklistConfig.get(null);
			file.set(blacklist, new File(jeiDir, blacklist.getConfigFile().getName()));
			changed.set(blacklist, true);
			blacklist.load();
		}

		if(FEIConfiguration.loadJeiFromProfileColors){
			LocalizedConfiguration colors = searchColorsConfig.get(null);
			file.set(colors, new File(jeiDir, colors.getConfigFile().getName()));
			changed.set(colors, true);
			colors.load();
		}

		MinecraftForge.EVENT_BUS.post(new OnConfigChangedEvent(mezz.jei.config.Constants.MOD_ID, "", Minecraft.getMinecraft().theWorld != null, false));
	}

	@Override
	public JsonObject save(Profile profile){
		JsonData data = new JsonData();

		data.canGiveItems = canGiveItems;
		data.canDeleteItemsAboveItemsList = canDeleteItemsAboveItemsList;
		data.searchFieldWidth = searchFieldWidth;
		data.searchFieldHeight = searchFieldHeight;

		return gson.toJsonTree(data).getAsJsonObject();
	}

	@Override
	public String getName(){
		return "JEI Override";
	}

	@Override
	public void openGui(FEIGuiOverride handler, GuiScreen gui){

	}

	@Override
	public void initGui(FEIGuiOverride handler, GuiScreen gui){

	}

	@Override
	public void drawGuiPre(FEIGuiOverride handler, GuiScreen gui, int mouseX, int mouseY){

	}

	@Override
	public void drawBackground(FEIGuiOverride handler, GuiScreen gui, int mouseX, int mouseY){

	}

	@Override
	public void drawGuiPost(FEIGuiOverride handler, GuiScreen gui, int mouseX, int mouseY){

	}

	@Override
	public void drawGuiPostPost(FEIGuiOverride handler, GuiScreen gui, int mouseX, int mouseY){

	}

	@Override
	public boolean handleKeyboardEvent(FEIGuiOverride handler, GuiScreen gui, boolean down, int key, char c){
		return false;
	}

	@Override
	public boolean handleMouseEvent(FEIGuiOverride handler, GuiScreen gui, int mouseX, int mouseY, boolean down, int key){
		return false;
	}

	@Override
	public boolean handleMouseEvent(FEIGuiOverride handler, GuiScreen gui, int mouseX, int mouseY, int dWheel){
		return false;
	}

	public static class JsonData {

		private boolean canGiveItems;
		private boolean canDeleteItemsAboveItemsList;

		private Boolean moveSearchFieldToCenter;
		private int searchFieldWidth;
		private int searchFieldHeight;

		private JsonData(){

		}

	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry){

	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry){

	}

}
