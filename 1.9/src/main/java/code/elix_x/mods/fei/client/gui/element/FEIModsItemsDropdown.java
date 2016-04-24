package code.elix_x.mods.fei.client.gui.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import code.elix_x.excore.utils.client.gui.elements.ListGuiElement;
import code.elix_x.excore.utils.client.gui.elements.RectangularGuiElement;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.mods.fei.api.gui.FEIGuiOverride;
import code.elix_x.mods.fei.api.gui.elements.IConfigurableFEIGuiElement;
import code.elix_x.mods.fei.api.gui.elements.ISaveableFEIGuiElement;
import code.elix_x.mods.fei.api.profile.Profile;
import code.elix_x.mods.fei.client.gui.FEIModsItemsDropdownSettingsGui;
import mezz.jei.gui.ingredients.ItemStackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.common.registry.GameData;

public class FEIModsItemsDropdown extends RectangularGuiElement<FEIGuiOverride> implements ISaveableFEIGuiElement, IConfigurableFEIGuiElement {

	public static final Gson gson = new Gson();

	private Multimap<String, ItemStack> modItemsMap;

	private ModsListGuiElement modsList;

	private ItemsListGuiElement itemsList;

	protected int clickTimeThreshold = 250;
	protected int clickDistanceThreshold = 2;

	public RGBA backgroundColor = new RGBA(0, 0, 0, 0);

	public RGBA textColor = new RGBA(1f, 1f, 1f, 1f);

	public boolean tooltipBackground = false;

	private boolean focused;

	public FEIModsItemsDropdown(){
		super("FEI Mods Items Dropdown", 60, 0, 128, 100, 2, 2);

		modItemsMap = HashMultimap.create();

		for(Item item : GameData.getItemRegistry()){
			if(item != null){
				List<ItemStack> subItems = new ArrayList<>();
				try {
					item.getSubItems(item, null, subItems);
				} catch(NullPointerException e){
					subItems.add(new ItemStack(item));
				}
				modItemsMap.putAll(GameData.getItemRegistry().getNameForObject(item).getResourceDomain(), subItems);
			}
		}

		modsList = new ModsListGuiElement();
	}

	@Override
	public int getHeight(){
		return focused ? borderY + 128 + borderY : borderY + 20 + borderY;
	}

	@Override
	public void load(Profile profile, JsonObject json){
		JsonData data = gson.fromJson(json, JsonData.class);

		xPos = data.xPos;
		yPos = data.yPos;
		width = data.width;
		height = data.height;
		borderX = data.borderX;
		borderY = data.borderY;
		clickTimeThreshold = data.clickTimeThreshold;
		clickDistanceThreshold = data.clickDistanceThreshold;
		backgroundColor = data.backgroundColor;
		textColor = data.textColor;
		tooltipBackground = data.tooltipBackground;

		modsList = new ModsListGuiElement();
	}

	@Override
	public JsonObject save(Profile profile){
		JsonData data = new JsonData();

		data.xPos = xPos;
		data.yPos = yPos;
		data.width = width;
		data.height = height;
		data.borderX = borderX;
		data.borderY = borderY;
		data.clickTimeThreshold = clickTimeThreshold;
		data.clickDistanceThreshold = clickDistanceThreshold;
		data.backgroundColor = backgroundColor;
		data.textColor = textColor;
		data.tooltipBackground = tooltipBackground;

		return gson.toJsonTree(data).getAsJsonObject();
	}

	@Override
	public String getUnlocalizedName(){
		return "fei.gui.override.dropdown.modsitems";
	}

	@Override
	public void openConfigGui(GuiScreen parent, FEIGuiOverride fei){
		parent.mc.displayGuiScreen(new FEIModsItemsDropdownSettingsGui(parent, this));
	}

	@Override
	public void openGui(FEIGuiOverride fei, GuiScreen gui){

	}

	@Override
	public void initGui(FEIGuiOverride fei, GuiScreen gui){

	}

	@Override
	public void drawGuiPre(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY){
		if(focused){
			modsList.drawGuiPre(fei, gui, mouseX, mouseY);
			if(itemsList != null) itemsList.drawGuiPre(fei, gui, mouseX, mouseY);
		}
	}

	@Override
	public void drawBackground(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY){

	}

	@Override
	public void drawGuiPost(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY){
		Minecraft.getMinecraft().getRenderItem().zLevel = 100;
		GlStateManager.disableDepth();
		renderItemStackPre();
		if(backgroundColor.a > 0){
			fill(backgroundColor);
		}
		new GuiButtonExt(0, xPos + borderX, yPos + borderY, width, 20, I18n.translateToLocal("fei.gui.override.dropdown.modsitems.mods")).drawButton(gui.mc, mouseX, mouseY);
		if(focused){
			modsList.drawGuiPost(fei, gui, mouseX, mouseY);
			if(itemsList != null) itemsList.drawGuiPost(fei, gui, mouseX, mouseY);
		}
		renderItemStackPost();
		GlStateManager.enableDepth();
	}

	@Override
	public void drawGuiPostPost(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY) {
		if(focused){
			modsList.drawGuiPostPost(fei, gui, mouseX, mouseY);
			if(itemsList != null) itemsList.drawGuiPostPost(fei, gui, mouseX, mouseY);
		}
	}

	@Override
	public boolean handleKeyboardEvent(FEIGuiOverride fei, GuiScreen gui, boolean down, int key, char c){
		return false;
	}

	@Override
	public boolean handleMouseEvent(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY, boolean down, int key){
		if(down){
			if(key == 0){
				if(itemsList != null && !itemsList.inside(mouseX, mouseY)){
					itemsList = null;
					return true;
				} else if(itemsList == null && focused && !modsList.inside(mouseX, mouseY)){
					focused = false;
					fei.looseFocus();
					return true;
				}
			}
			if(itemsList == null && !focused && inside(mouseX, mouseY) && mouseY <= yPos + borderY + 20 + borderY){
				if(key == 0){
					focused = true;
					fei.setFocused(this);
					return true;
				}
			}
		}
		if(itemsList != null && itemsList.handleMouseEvent(fei, gui, mouseX, mouseY, down, key)) return true;
		if(focused && modsList.handleMouseEvent(fei, gui, mouseX, mouseY, down, key)) return true;
		return false;
	}

	@Override
	public boolean handleMouseEvent(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY, int dWheel){
		if(itemsList != null && itemsList.handleMouseEvent(fei, gui, mouseX, mouseY, dWheel)) return true;
		if(focused && modsList.handleMouseEvent(fei, gui, mouseX, mouseY, dWheel)) return true;
		return false;
	}

	public class ModsListGuiElement extends ListGuiElement<FEIGuiOverride> {

		public ModsListGuiElement(){
			super("Mods List", FEIModsItemsDropdown.this.xPos + FEIModsItemsDropdown.this.borderX, FEIModsItemsDropdown.this.borderY + 20 + FEIModsItemsDropdown.this.borderY, FEIModsItemsDropdown.this.width / 2, 128 - 20 - FEIModsItemsDropdown.this.borderY * 3, 20, FEIModsItemsDropdown.this.borderX, FEIModsItemsDropdown.this.borderY, new RGBA(0, 0, 0, 0));

			this.clickTimeThreshold = FEIModsItemsDropdown.this.clickTimeThreshold;
			this.clickDistanceThreshold = FEIModsItemsDropdown.this.clickDistanceThreshold;

			for(String s : modItemsMap.keySet()){
				add(new ModsListElement(s));
			}
		}

		public class ModsListElement extends ListElement {

			private String mod;

			public ModsListElement(String mod){
				this.mod = mod;
			}

			@Override
			public void drawGuiPost(FEIGuiOverride fei, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY){
				drawColoredRect(new Rectangle(x, relY, 10, 10), new RGBA(0, 255, 0), 100);
				new GuiButtonExt(0, x, relY, ModsListGuiElement.this.width, elementY, mod).drawButton(gui.mc, mouseX, mouseY);
			}

			@Override
			public boolean handleMouseEvent(FEIGuiOverride fei, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY, boolean down, int key){
				if(down && key == 0 && inside(relY, mouseX, mouseY)){
					itemsList = new ItemsListGuiElement(mod);
					return true;
				}
				return false;
			}

		}

	}

	public class ItemsListGuiElement extends ListGuiElement<FEIGuiOverride> {

		private ItemStackRenderer renderer = new ItemStackRenderer();

		public ItemsListGuiElement(String mod){
			super("Items List", FEIModsItemsDropdown.this.xPos + FEIModsItemsDropdown.this.getWidth() / 2 + FEIModsItemsDropdown.this.borderX, FEIModsItemsDropdown.this.borderY + 20 + FEIModsItemsDropdown.this.borderY, 16, 128 - 20 - FEIModsItemsDropdown.this.borderY * 3, 16, FEIModsItemsDropdown.this.borderX, FEIModsItemsDropdown.this.borderY, new RGBA(0, 0, 0, 0));

			this.clickTimeThreshold = FEIModsItemsDropdown.this.clickTimeThreshold;
			this.clickDistanceThreshold = FEIModsItemsDropdown.this.clickDistanceThreshold;

			for(ItemStack itemstack : modItemsMap.get(mod)){
				elements = ArrayUtils.add(elements, new ItemsListElement(itemstack));
			}
			Arrays.sort(elements, new Comparator<ListElement>(){

				@Override
				public int compare(ListElement o1, ListElement o2){
					ItemsListElement e1 = (ItemsListElement) o1;
					ItemsListElement e2 = (ItemsListElement) o2;
					int iid = GameData.getItemRegistry().getId(e2.itemstack.getItem()) - GameData.getItemRegistry().getId(e1.itemstack.getItem());
					return iid == 0 ? (e2.itemstack.getItemDamage() - e1.itemstack.getItemDamage() < 0 ? 1 : -1) : iid < 0 ? 1 : -1;
				}

			});
		}

		public class ItemsListElement extends ListElement {

			private ItemStack itemstack;

			public ItemsListElement(ItemStack itemstack){
				this.itemstack = itemstack;
			}

			@Override
			public void drawGuiPost(FEIGuiOverride fei, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY){
				GlStateManager.enableDepth();
				GL11.glPushMatrix();
				GL11.glTranslated(0, 0, 10);
				renderer.draw(gui.mc, x, relY, itemstack);
				GL11.glPopMatrix();
				GlStateManager.disableDepth();
			}

			@Override
			public void drawGuiPostPost(FEIGuiOverride fei, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY){
				if(inside(relY, mouseX, mouseY)){
					if(gui.isShiftKeyDown()){
						scissorsPost();
						drawTooltip(renderer.getFontRenderer(gui.mc, itemstack), mouseX, mouseY, false, true, 2, textColor, tooltipBackground, false, renderer.getTooltip(gui.mc, itemstack).toArray(new String[0]));
						scissorsPre();
					} else {
						scissorsPost();
						drawTooltip(renderer.getFontRenderer(gui.mc, itemstack), mouseX, mouseY, false, true, 0, textColor, tooltipBackground, false, itemstack.getDisplayName());
						scissorsPre();
					}
				}
			}

		}

	}

	public static class JsonData {

		private int xPos;
		private int yPos;

		private int width;
		private int height;

		private int borderX;
		private int borderY;

		private int clickTimeThreshold;
		private int clickDistanceThreshold;

		private RGBA backgroundColor;

		private RGBA textColor;

		private boolean tooltipBackground;

		private JsonData(){

		}

	}

}
