package code.elix_x.mods.fei.client.jeioverride;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import code.elix_x.excomms.reflection.ReflectionHelper.AClass;
import code.elix_x.excomms.reflection.ReflectionHelper.AField;
import code.elix_x.excore.utils.client.gui.elements.IGuiElement;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.client.gui.FEIGuiOverride;
import code.elix_x.mods.fei.api.client.gui.elements.IConfigurableFEIGuiElement;
import code.elix_x.mods.fei.api.client.gui.elements.INotDisableableFEIGuiElement;
import code.elix_x.mods.fei.api.client.gui.elements.ISaveableFEIGuiElement;
import code.elix_x.mods.fei.api.profile.Profile;
import code.elix_x.mods.fei.config.FEIConfiguration;
import code.elix_x.mods.fei.net.FEIGiveItemStackPacket;
import mezz.jei.JeiRuntime;
import mezz.jei.ModIngredientRegistration;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.config.Config;
import mezz.jei.config.ConfigValues;
import mezz.jei.config.Constants;
import mezz.jei.config.OverlayToggleEvent;
import mezz.jei.gui.ConfigButton;
import mezz.jei.gui.ItemListOverlayInternal;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@JEIPlugin
public class JeiReflector implements IModPlugin, IGuiElement<FEIGuiOverride>, INotDisableableFEIGuiElement, ISaveableFEIGuiElement, IConfigurableFEIGuiElement {

	public static final Gson gson = new Gson();

	public static final JeiReflector INSTANCE = null;

	private static final AClass<ModIngredientRegistration> modIngredientRegistrationClass = new AClass<>(ModIngredientRegistration.class);
	private static final AField<ModIngredientRegistration, Map<Class, Collection>> allIngredientsMap = modIngredientRegistrationClass.<Map<Class, Collection>>getDeclaredField("allIngredientsMap").setAccessible(true);
	private static final AField<ModIngredientRegistration, Map<Class, IIngredientHelper>> ingredientHelperMap = modIngredientRegistrationClass.<Map<Class, IIngredientHelper>>getDeclaredField("ingredientHelperMap").setAccessible(true);
	private static final AField<ModIngredientRegistration, Map<Class, IIngredientRenderer>> ingredientRendererMap = modIngredientRegistrationClass.<Map<Class, IIngredientRenderer>>getDeclaredField("ingredientRendererMap").setAccessible(true);

	private static final AField<Config, ConfigValues> values = new AClass<>(Config.class).<ConfigValues>getDeclaredField("values").setAccessible(true);

	private static final AField<ItemListOverlayInternal, ConfigButton> configButton = new AClass<>(ItemListOverlayInternal.class).<ConfigButton>getDeclaredField("configButton").setAccessible(true).setFinal(false);

	private JeiRuntime jeiRuntime;
	private ItemListOverlayInternal prevInternal;

	private boolean canGive;
	private boolean canDeleteAboveList;

	private int searchFieldWidth;
	private int searchFieldHeight;

	public JeiReflector(){
		if(INSTANCE != null) throw new IllegalArgumentException("An instance already exists!");
		FEIGuiOverride.addElement(this);
		MinecraftForge.EVENT_BUS.register(this);
		new AClass(JeiReflector.class).getDeclaredField("INSTANCE").setFinal(false).set(null, INSTANCE);
	}

	public boolean canGive(){
		return canGive;
	}

	public void setCanGive(boolean canGive){
		this.canGive = canGive;
		values.get(null).cheatItemsEnabled = (this.canGive || this.canDeleteAboveList) && (Minecraft.getMinecraft().player == null || FEIConfiguration.canGive(Minecraft.getMinecraft().player));
	}

	public boolean canDeleteAboveList(){
		return canDeleteAboveList;
	}

	public void setCanDeleteAboveList(boolean canDeleteAboveList){
		this.canDeleteAboveList = canDeleteAboveList;
		values.get(null).cheatItemsEnabled = (this.canGive || this.canDeleteAboveList) && (Minecraft.getMinecraft().player == null || FEIConfiguration.canGive(Minecraft.getMinecraft().player));
	}

	public int getSearchFieldWidth(){
		return searchFieldWidth;
	}

	public void setSearchFieldWidth(int searchFieldWidth){
		this.searchFieldWidth = searchFieldWidth;
	}

	public int getSearchFieldHeight(){
		return searchFieldHeight;
	}

	public void setSearchFieldHeight(int searchFieldHeight){
		this.searchFieldHeight = searchFieldHeight;
	}

	public void refreshJEIValues(){
		setCanGive(canGive);
		setCanDeleteAboveList(canDeleteAboveList);
		if(jeiRuntime != null && jeiRuntime.getItemListOverlay().getInternal() != null)
			configButton.set(jeiRuntime.getItemListOverlay().getInternal(), new ConfigButton(jeiRuntime.getItemListOverlay(), -100, -100, 0));
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinedWorld(EntityJoinWorldEvent event){
		if(event.getWorld().isRemote && event.getEntity() == Minecraft.getMinecraft().player) refreshJEIValues();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event){
		if(event.getModID().equals(Constants.MOD_ID)) refreshJEIValues();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onOverlayToggle(OverlayToggleEvent event){
		refreshJEIValues();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void openGui(GuiOpenEvent event){
		if(event.getGui() instanceof GuiContainer || event.getGui() instanceof RecipesGui)
			refreshJEIValues();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void initGui(GuiScreenEvent.InitGuiEvent.Post event){
		if(event.getGui() instanceof GuiContainer || event.getGui() instanceof RecipesGui) refreshJEIValues();
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onDrawBackgroundEventPost(GuiScreenEvent.BackgroundDrawnEvent event){
		if(jeiRuntime != null && jeiRuntime.getItemListOverlay().getInternal() != prevInternal){
			prevInternal = jeiRuntime.getItemListOverlay().getInternal();
			refreshJEIValues();
		}
	}

	@Override
	public void register(IModRegistry registry){

	}

	@Override
	public void onRuntimeAvailable(final IJeiRuntime jeiRuntime){
		this.jeiRuntime = (JeiRuntime) jeiRuntime;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry){

	}

	@Override
	public void registerIngredients(IModIngredientRegistration apiReg){
		ModIngredientRegistration registry = (ModIngredientRegistration) apiReg;
		Map<Class, Collection> allIngredientsMap = this.allIngredientsMap.get(registry);
		Map<Class, IIngredientHelper> ingredientHelperMap = this.ingredientHelperMap.get(registry);
		Map<Class, IIngredientRenderer> ingredientRendererMap = this.ingredientRendererMap.get(registry);
		for(Class clas : allIngredientsMap.keySet())
			registry.register(clas, allIngredientsMap.get(clas), new IngredientHelperDelegate(ingredientHelperMap.get(clas)), ingredientRendererMap.get(clas));
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

		canGive = data.canGive;
		canDeleteAboveList = data.canDeleteAboveList;
		searchFieldWidth = data.searchFieldWidth;
		searchFieldHeight = data.searchFieldHeight;

		/*
		 * File jeiDir = new File(profile.getSaveDir(), "JEI"); jeiDir.mkdir();
		 * 
		 * MinecraftForge.EVENT_BUS.post(new OnConfigChangedEvent(mezz.jei.config.Constants.MOD_ID, "", Minecraft.getMinecraft().world != null, false));
		 */
		refreshJEIValues();
	}

	@Override
	public JsonObject save(Profile profile){
		JsonData data = new JsonData();

		data.canGive = canGive;
		data.canDeleteAboveList = canDeleteAboveList;
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

	public class IngredientHelperDelegate<V> implements IIngredientHelper<V> {

		private final IIngredientHelper<V> delegate;

		public IngredientHelperDelegate(IIngredientHelper<V> delegate){
			this.delegate = delegate;
		}

		/*
		 * Wrapped delegation
		 */

		@Override
		public ItemStack cheatIngredient(V ingredient, boolean fullStack){
			if(canGive && FEIConfiguration.canGive(Minecraft.getMinecraft().player)){
				ItemStack res = delegate.cheatIngredient(ingredient, fullStack).copy();
				if(!res.isEmpty()){
					res.setCount(fullStack ? res.getMaxStackSize() : 1);
					ForeverEnoughItemsBase.net.sendToServer(new FEIGiveItemStackPacket(res));
				}
			}
			return ItemStack.EMPTY;
		}

		/*
		 * Direct delegation
		 */

		@Override
		public List<V> expandSubtypes(List<V> ingredients){
			return delegate.expandSubtypes(ingredients);
		}

		@Override
		public V getMatch(Iterable<V> ingredients, V ingredientToMatch){
			return delegate.getMatch(ingredients, ingredientToMatch);
		}

		@Override
		public String getDisplayName(V ingredient){
			return delegate.getDisplayName(ingredient);
		}

		@Override
		public String getUniqueId(V ingredient){
			return delegate.getUniqueId(ingredient);
		}

		@Override
		public String getWildcardId(V ingredient){
			return delegate.getWildcardId(ingredient);
		}

		@Override
		public String getModId(V ingredient){
			return delegate.getModId(ingredient);
		}

		@Override
		public Iterable<Color> getColors(V ingredient){
			return delegate.getColors(ingredient);
		}

		@Override
		public V copyIngredient(V ingredient){
			return delegate.copyIngredient(ingredient);
		}

		@Override
		public String getErrorInfo(V ingredient){
			return delegate.getErrorInfo(ingredient);
		}

	}

	public static class JsonData {

		private boolean canGive;
		private boolean canDeleteAboveList;

		private int searchFieldWidth;
		private int searchFieldHeight;

		private JsonData(){

		}

	}

}
