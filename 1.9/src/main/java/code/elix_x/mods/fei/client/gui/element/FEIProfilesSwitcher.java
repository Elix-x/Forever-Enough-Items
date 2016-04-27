package code.elix_x.mods.fei.client.gui.element;

import org.lwjgl.input.Keyboard;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import code.elix_x.excore.utils.client.gui.elements.ListGuiElement;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.mods.fei.api.gui.FEIGuiOverride;
import code.elix_x.mods.fei.api.gui.elements.IConfigurableFEIGuiElement;
import code.elix_x.mods.fei.api.gui.elements.INotDisableableFEIGuiElement;
import code.elix_x.mods.fei.api.gui.elements.ISaveableFEIGuiElement;
import code.elix_x.mods.fei.api.profile.Profile;
import code.elix_x.mods.fei.client.gui.FEIProfilesSwitcherSettingsGui;
import code.elix_x.mods.fei.client.gui.ProfilesGuiScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class FEIProfilesSwitcher extends ListGuiElement<FEIGuiOverride> implements ISaveableFEIGuiElement, IConfigurableFEIGuiElement, INotDisableableFEIGuiElement {

	public static final Gson gson = new Gson();

	public RGBA textColor = new RGBA(1f, 1f, 1f, 1f);

	public boolean tooltipBackground = false;

	private boolean focused;

	public FEIProfilesSwitcher(){
		super("FEI Profiles Switcher", 0, 150, 20, 80, 20, 2, 2, new RGBA(0, 0, 0, 0));
		inverted = true;
	}

	@Override
	public int getHeight(){
		return focused ? super.getHeight() : borderY + 20 + borderY;
	}

	@Override
	public void load(Profile profile, JsonObject json){
		JsonData data = gson.fromJson(json, JsonData.class);

		xPos = data.xPos;
		yPos = data.yPos;
		height = data.height;
		borderX = data.borderX;
		borderY = data.borderY;
		scrollDistance = data.scrollDistance;
		clickTimeThreshold = data.clickTimeThreshold;
		clickDistanceThreshold = data.clickDistanceThreshold;
		backgroundColor = data.backgroundColor;
		textColor = data.textColor;
		tooltipBackground = data.tooltipBackground;
	}

	@Override
	public JsonObject save(Profile profile){
		JsonData data = new JsonData();

		data.xPos = xPos;
		data.yPos = yPos;
		data.height = height;
		data.borderX = borderX;
		data.borderY = borderY;
		data.scrollDistance = scrollDistance;
		data.clickTimeThreshold = clickTimeThreshold;
		data.clickDistanceThreshold = clickDistanceThreshold;
		data.backgroundColor = backgroundColor;
		data.textColor = textColor;
		data.tooltipBackground = tooltipBackground;

		return gson.toJsonTree(data).getAsJsonObject();
	}

	@Override
	public String getUnlocalizedName(){
		return "fei.gui.override.selector.profile";
	}

	@Override
	public void openConfigGui(GuiScreen parent, FEIGuiOverride fei){
		parent.mc.displayGuiScreen(new FEIProfilesSwitcherSettingsGui(parent, this));
	}

	@Override
	public void initGui(FEIGuiOverride fei, GuiScreen gui){
		reInitElements();
		for(Profile profile : Profile.getProfiles()){
			add(new FEIProfilesSwitchListElement(profile));
		}
		super.initGui(fei, gui);
	}

	@Override
	public void drawGuiPre(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY){
		if(focused) super.drawGuiPre(fei, gui, mouseX, mouseY);
	}

	@Override
	public void drawBackground(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY){
		if(focused) super.drawBackground(fei, gui, mouseX, mouseY);
	}

	@Override
	public void drawGuiPost(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY){
		if(focused){
			super.drawGuiPost(fei, gui, mouseX, mouseY);
		} else {
			new GuiButtonExt(0, getXPos() + borderX, getYPos() + borderY, 20, 20, "").drawButton(gui.mc, mouseX, mouseY);
			renderItemStackFull(Profile.getCurrentProfile().getIcon(), getXPos() + borderX + 2, getYPos() + borderY + 2);
			if(inside(mouseX, mouseY)){
				drawTooltip(gui.mc.fontRendererObj, mouseX, mouseY, false, true, 0, textColor, tooltipBackground, false, Profile.getCurrentProfile().getName());
			}
		}
	}

	@Override
	public boolean handleKeyboardEvent(FEIGuiOverride fei, GuiScreen gui, boolean down, int key, char c){
		if(down && key == Keyboard.KEY_P){
			gui.mc.displayGuiScreen(new ProfilesGuiScreen(gui, fei));
			return true;
		}
		if(focused){
			return super.handleKeyboardEvent(fei, gui, down, key, c);
		} else {
			return false;
		}
	}

	@Override
	public boolean handleMouseEvent(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY, boolean down, int key){
		if(focused){
			if(!inside(mouseX, mouseY)){
				focused = false;
				return true;
			}
			return super.handleMouseEvent(fei, gui, mouseX, mouseY, down, key);
		} else {
			if(down && inside(mouseX, mouseY)){
				if(key == 0){
					focused = true;
					fei.setFocused(this);
					initGui(fei, gui);
					return true;
				}
				if(key == 1){
					gui.mc.displayGuiScreen(new ProfilesGuiScreen(gui, fei));
					return true;
				}
			}
		}
		return false;
	}

	public class FEIProfilesSwitchListElement extends ListElement {

		private Profile profile;

		public FEIProfilesSwitchListElement(Profile profile){
			this.profile = profile;
		}

		@Override
		public void drawGuiPost(FEIGuiOverride fei, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY){
			new GuiButtonExt(0, x, relY, 20, 20, "").drawButton(gui.mc, mouseX, mouseY);
			renderItemStackFull(profile.getIcon(), x + 2, relY + 2);
		}

		@Override
		public void drawGuiPostPost(FEIGuiOverride fei, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY){
			if(inside(relY, mouseX, mouseY)){
				scissorsPost();
				drawTooltip(gui.mc.fontRendererObj, mouseX, mouseY, false, true, 0, textColor, tooltipBackground, false, profile.getName());
				scissorsPre();
			}
		}

		@Override
		public boolean handleMouseEvent(FEIGuiOverride fei, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY, boolean down, int key){
			if(down && key == 0 && inside(relY, mouseX, mouseY)){
				Profile.setCurrentProfile(profile);
				focused = false;
				fei.looseFocus();
				return true;
			}
			return false;
		}

	}

	public static class JsonData {

		private int xPos;
		private int yPos;

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
