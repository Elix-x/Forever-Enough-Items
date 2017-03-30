package code.elix_x.mods.fei.client.gui.element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import code.elix_x.excomms.color.RGBA;
import code.elix_x.excore.utils.client.gui.elements.ListGuiElement;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.client.gui.FEIGuiOverride;
import code.elix_x.mods.fei.api.client.gui.elements.IConfigurableFEIGuiElement;
import code.elix_x.mods.fei.api.client.gui.elements.ISaveableFEIGuiElement;
import code.elix_x.mods.fei.api.events.FEIInventorySaveEvent;
import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.api.profile.Profile;
import code.elix_x.mods.fei.client.gui.FEIInventorySavesListSettingsGUI;
import code.elix_x.mods.fei.net.LoadInventoryPacket;
import code.elix_x.mods.fei.permission.FEIPermissionsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class FEIInventorySavesList extends ListGuiElement<FEIGuiOverride> implements ISaveableFEIGuiElement, IConfigurableFEIGuiElement {

	public static final Logger logger = LogManager.getLogger("Inventory Saves List");

	public static final Gson gson = new Gson();

	public RGBA textColor = new RGBA(255, 255, 255, 255);

	public boolean tooltipBackground = false;

	private FEIInventorySavesListLoadElement edit = null;

	public FEIInventorySavesList(){
		super("FEI Invetory Saves List", 0, 54, 64, 128, 20, 2, 2, new RGBA(0, 0, 0, 0));
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
		scrollDistance = data.scrollDistance;
		clickTimeThreshold = data.clickTimeThreshold;
		clickDistanceThreshold = data.clickDistanceThreshold;
		backgroundColor = data.backgroundColor;
		textColor = data.textColor;
		tooltipBackground = data.tooltipBackground;

		File nbtd = new File(profile.getSaveDir(), "Inventory Saves.nbt");
		if(nbtd.exists()){
			try{
				NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(nbtd));
				NBTTagList list = (NBTTagList) nbt.getTag("saves");
				reInitElements(list.tagCount() + 1);
				for(int i = 0; i < list.tagCount(); i++){
					NBTTagCompound tag = list.getCompoundTagAt(i);
					elements[i] = new FEIInventorySavesListLoadElement(tag.getString("name"), tag.getCompoundTag("inventory"));
				}
				elements[elements.length - 1] = new FEIInventorySavesListNewElement();
			} catch(IOException e){
				logger.error("Caught exception while reading saves from nbt:", e);
			}
		}
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
		data.scrollDistance = scrollDistance;
		data.clickTimeThreshold = clickTimeThreshold;
		data.clickDistanceThreshold = clickDistanceThreshold;
		data.backgroundColor = backgroundColor;
		data.textColor = textColor;
		data.tooltipBackground = tooltipBackground;

		File nbtd = new File(profile.getSaveDir(), "Inventory Saves.nbt");
		try{
			nbtd.createNewFile();
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagList list = new NBTTagList();
			for(ListElement element : elements){
				if(element instanceof FEIInventorySavesListLoadElement){
					NBTTagCompound tag = new NBTTagCompound();
					tag.setString("name", ((FEIInventorySavesListLoadElement) element).name);
					tag.setTag("inventory", ((FEIInventorySavesListLoadElement) element).inventory);
					list.appendTag(tag);
				}
			}
			nbt.setTag("saves", list);
			CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(nbtd));
		} catch(IOException e){
			logger.error("Caught exception while writing saves to nbt:", e);
		}

		return gson.toJsonTree(data).getAsJsonObject();
	}

	@Override
	public String getUnlocalizedName(){
		return "fei.gui.override.list.inventorysaves";
	}

	@Override
	public void openConfigGui(GuiScreen parent, FEIGuiOverride fei){
		parent.mc.displayGuiScreen(new FEIInventorySavesListSettingsGUI(parent, this));
	}

	@Override
	public void drawGuiPost(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY){
		super.drawGuiPost(fei, gui, mouseX, mouseY);
		if(gui.isShiftKeyDown() && inside(mouseX, mouseY)){
			drawTooltip(gui.mc.fontRenderer, mouseX, mouseY, true, true, 4, textColor, tooltipBackground, true, "fei.gui.override.list.inventorysaves.leftlick", "fei.gui.override.list.inventorysaves.rightclick", "fei.gui.override.list.inventorysaves.shiftleftclick", "fei.gui.override.list.inventorysaves.shiftrightclick");
		}
	}

	@Override
	public boolean handleMouseEventPost(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY, boolean down, int key){
		if(edit != null){
			if(down){
				edit.editf.mouseClicked(mouseX, mouseY, key);
				if(!edit.editf.isFocused()){
					edit.name = edit.editf.getText();
					edit.editf = null;
					edit = null;
					FEIGuiOverride.saveToCurrentProfile();
					fei.looseFocus();
				}
			}
			return true;
		}
		return super.handleMouseEventPost(fei, gui, mouseX, mouseY, down, key);
	}

	@Override
	public boolean handleKeyboardEvent(FEIGuiOverride fei, GuiScreen gui, boolean down, int key, char c){
		if(edit != null){
			if(down) edit.editf.textboxKeyTyped(c, key);
			return true;
		} else{
			return false;
		}
	}

	public class FEIInventorySavesListLoadElement extends ListElement {

		private String name;

		private NBTTagCompound inventory;

		private GuiTextField editf;

		public FEIInventorySavesListLoadElement(String name, NBTTagCompound inventory){
			this.name = name;
			this.inventory = inventory;
		}

		@Override
		public void drawGuiPost(FEIGuiOverride fei, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY){
			GuiButtonExt button;
			if(this == edit){
				button = new GuiButtonExt(0, x, relY, width, elementY, "");
			} else{
				button = new GuiButtonExt(0, x, relY, width, elementY, name);
			}
			button.enabled = FEIPermissionsManager.getPermissionLevels(Minecraft.getMinecraft().player).isHigherOrEqual(FEIPermissionLevel.MODERATOR);
			button.drawButton(gui.mc, mouseX, mouseY);
			if(this == edit) editf.drawTextBox();
		}

		@Override
		public boolean handleMouseEvent(FEIGuiOverride fei, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY, boolean down, int key){
			if(down && inside(relY, mouseX, mouseY)){
				if(key == 0){
					if(!gui.isShiftKeyDown()){
						ForeverEnoughItemsBase.net.sendToServer(new LoadInventoryPacket(inventory));
					} else if(FEIPermissionsManager.getPermissionLevels(Minecraft.getMinecraft().player).isHigherOrEqual(FEIPermissionLevel.MODERATOR)){
						load();
					}
					return true;
				} else if(key == 1){
					if(!gui.isShiftKeyDown()){
						editf = new GuiTextField(0, gui.mc.fontRenderer, x + borderX * 2, relY + borderY * 2, width - borderX * 4, elementY - borderY * 4);
						editf.setText(name);
						editf.setFocused(true);
						edit = this;
						fei.setFocused(FEIInventorySavesList.this);
					} else{
						remove(index);
						checkScrollDistance();
					}
					FEIGuiOverride.saveToCurrentProfile();
					return true;
				}
			}
			return false;
		}

		public void load(){
			MinecraftForge.EVENT_BUS.post(new FEIInventorySaveEvent(Minecraft.getMinecraft().player, inventory));
			FEIGuiOverride.saveToCurrentProfile();
		}

	}

	public class FEIInventorySavesListNewElement extends ListElement {

		public FEIInventorySavesListNewElement(){

		}

		@Override
		public void drawGuiPost(FEIGuiOverride fei, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY){
			new GuiButtonExt(0, x, relY, width, elementY, I18n.translateToLocal("fei.gui.override.list.inventorysaves.new")).drawButton(gui.mc, mouseX, mouseY);
		}

		@Override
		public boolean handleMouseEvent(FEIGuiOverride fei, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY, boolean down, int key){
			if(down && key == 0 && inside(relY, mouseX, mouseY)){
				edit = new FEIInventorySavesListLoadElement("", new NBTTagCompound());
				edit.editf = new GuiTextField(0, gui.mc.fontRenderer, x + borderX * 2, relY + borderY * 2, width - borderX * 4, elementY - borderY * 4);
				edit.editf.setFocused(true);
				edit.load();
				add(edit, index);
				fei.setFocused(FEIInventorySavesList.this);
				return true;
			} else{
				return false;
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

		private int scrollDistance;

		private int clickTimeThreshold;
		private int clickDistanceThreshold;

		private RGBA backgroundColor;

		private RGBA textColor;

		private boolean tooltipBackground;

		private JsonData(){

		}

	}

}
