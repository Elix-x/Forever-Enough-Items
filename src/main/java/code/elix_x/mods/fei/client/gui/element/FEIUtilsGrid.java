package code.elix_x.mods.fei.client.gui.element;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.util.Rectangle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import code.elix_x.excore.utils.client.gui.elements.GridGuiElement;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.mods.fei.api.client.gui.FEIGuiOverride;
import code.elix_x.mods.fei.api.client.gui.elements.IConfigurableFEIGuiElement;
import code.elix_x.mods.fei.api.client.gui.elements.ISaveableFEIGuiElement;
import code.elix_x.mods.fei.api.profile.Profile;
import code.elix_x.mods.fei.api.utils.IFEIUtil;
import code.elix_x.mods.fei.api.utils.IFEIUtil.IFEIUtilProperty;
import code.elix_x.mods.fei.client.gui.FEIUtilsGridSettingsGui;
import code.elix_x.mods.fei.client.gui.element.FEIUtilsGrid.JsonData.JsonGridElementData;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class FEIUtilsGrid extends GridGuiElement<FEIGuiOverride> implements ISaveableFEIGuiElement, IConfigurableFEIGuiElement {

	public static final Gson gson = new Gson();

	private Map<String, FEIUtilsGridElement> allElements = new HashMap<String, FEIUtilsGridElement>();

	private FEIUtilsGridElement currentSelected;

	public RGBA textColor = new RGBA(255, 255, 255, 255);

	public boolean tooltipBackground = false;

	public FEIUtilsGrid(){
		super("FEI Utils Grid", 0, 0, 20, 20, 5, 2, 2, 2, new RGBA(0, 0, 0, 0));
	}

	protected void addElement(FEIUtilsGridElement element){
		allElements.put(element.getName(), element);
	}

	public void addElement(IFEIUtil e){
		addElement(new FEIUtilsGridElement(e));
	}

	@Override
	public FEIUtilsGridElement getElement(int x, int y){
		return (FEIUtilsGridElement) super.getElement(x, y);
	}

	public Collection<FEIUtilsGridElement> getAllElements(){
		return allElements.values();
	}

	@Override
	public void load(Profile profile, JsonObject json){
		JsonData data = gson.fromJson(json, JsonData.class);

		xPos = data.xPos;
		yPos = data.yPos;
		elementsX = data.elementsX;
		elementsY = data.elementsY;
		borderX = data.borderX;
		borderY = data.borderY;
		backgroundColor = data.backgroundColor;
		textColor = data.textColor;
		tooltipBackground = data.tooltipBackground;

		reInitElements();

		for(Entry<String, JsonGridElementData> e : data.utils.entrySet()){
			FEIUtilsGridElement element = allElements.get(e.getKey());
			if(element != null){
				element.currentPropertyIndex = e.getValue().currentPropertyIndex;
				element.updateCurrentProperty();
				addElement(element, e.getValue().x, e.getValue().y);
			}
		}
	}

	@Override
	public JsonObject save(Profile profile){
		JsonData data = new JsonData();

		data.xPos = xPos;
		data.yPos = yPos;
		data.elementsX = elementsX;
		data.elementsY = elementsY;
		data.borderX = borderX;
		data.borderY = borderY;
		data.backgroundColor = backgroundColor;
		data.textColor = textColor;
		data.tooltipBackground = tooltipBackground;

		for(int x = 0; x < getElementsX(); x++){
			for(int y = 0; y < elementsY; y++){
				FEIUtilsGridElement element = getElement(x, y);
				if(element != null){
					data.utils.put(element.getName(), new JsonGridElementData(x, y, element.currentPropertyIndex));
				}
			}
		}

		return gson.toJsonTree(data).getAsJsonObject();
	}

	@Override
	public String getUnlocalizedName(){
		return "fei.gui.override.grid.utils";
	}

	@Override
	public void openConfigGui(GuiScreen parent, FEIGuiOverride fei){
		parent.mc.displayGuiScreen(new FEIUtilsGridSettingsGui(parent, this));
	}

	@Override
	public void drawGuiPost(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY){
		super.drawGuiPost(fei, gui, mouseX, mouseY);
		if(gui.isShiftKeyDown() && (inside(mouseX, mouseY) || (currentSelected != null && currentSelected.isInsideExtended(calcAbsX(getX(currentSelected)), calcAbsY(getY(currentSelected)), mouseX, mouseY)))){
			drawTooltip(gui.mc.fontRendererObj, mouseX, mouseY, true, true, 4, textColor, tooltipBackground, true, "fei.gui.override.grid.utils.leftclick", "fei.gui.override.grid.utils.rightclick");
		}
	}

	@Override
	public boolean handleMouseEvent(FEIGuiOverride fei, GuiScreen gui, int mouseX, int mouseY, boolean down, int key){
		if(currentSelected != null){
			if(currentSelected.handleExtendedMouseEvent(fei, gui, calcAbsX(getX(currentSelected)), calcAbsY(getY(currentSelected)), mouseX, mouseY, down, key)){
				fei.looseFocus();
				return true;
			} else if(!currentSelected.isInsideExtended(calcAbsX(getX(currentSelected)), calcAbsY(getY(currentSelected)), mouseX, mouseY)){
				currentSelected = null;
				fei.looseFocus();
				return true;
			}
		} else if(down && key == 1){
			if(inside(mouseX, mouseY)){
				currentSelected = getElement(calcLocalX(mouseX), calcLocalY(mouseY));
				if(currentSelected != null && currentSelected.canBeExtended()){
					fei.setFocused(this);
				} else{
					currentSelected = null;
				}
				return true;
			}
		}
		return super.handleMouseEvent(fei, gui, mouseX, mouseY, down, key);
	}

	public class FEIUtilsGridElement extends GridElement {

		public IFEIUtil util;

		public int currentPropertyIndex;

		public IFEIUtilProperty currentProperty;

		public FEIUtilsGridElement(IFEIUtil util){
			this.util = util;
		}

		public String getName(){
			return util.getName();
		}

		public IFEIUtilProperty[] getAllProperties(){
			return util.getAllProperties();
		}

		public int getPropertiesCount(){
			return getAllProperties().length;
		}

		public boolean canBeExtended(){
			return getPropertiesCount() > 1;
		}

		public void updateCurrentProperty(){
			currentProperty = getAllProperties()[currentPropertyIndex];
		}

		public void updateCurrentPropertyIndex(){
			currentPropertyIndex = ArrayUtils.indexOf(getAllProperties(), currentProperty);
		}

		public int getNextPropertyIndex(){
			return (currentPropertyIndex + 1) % getPropertiesCount();
		}

		public IFEIUtilProperty getNextProperty(){
			return getAllProperties()[getNextPropertyIndex()];
		}

		public boolean isInsideExtended(int relX, int relY, int mouseX, int mouseY){
			return relX <= mouseX && mouseX <= relX + elementX && relY <= mouseY && mouseY <= relY + getPropertiesCount() * (elementY + borderY) - borderY;
		}

		@Override
		public void initGui(FEIGuiOverride fei, GuiScreen gui, int relX, int relY){
			currentProperty = util.getCurrentProperty();
			if(currentProperty != null){
				updateCurrentPropertyIndex();
			} else{
				updateCurrentProperty();
			}
		}

		@Override
		public void drawGuiPost(FEIGuiOverride fei, GuiScreen gui, int relX, int relY, int mouseX, int mouseY){
			if(this != currentSelected){
				IFEIUtilProperty prop = getNextProperty();
				GuiButtonExt button = new GuiButtonExt(0, relX, relY, elementX, elementY, "");
				button.enabled = prop.isEnabled();
				button.drawButton(gui.mc, mouseX, mouseY);
				prop.getRenderable().render(new Rectangle(relX + 2, relY + 2, elementX - 4, elementY - 4), gui.mc);
			}
		}

		@Override
		public void drawGuiPostPost(FEIGuiOverride fei, GuiScreen gui, int relX, int relY, int mouseX, int mouseY){
			if(this == currentSelected){
				if(backgroundColor.a > 0)
					gui.drawRect(relX - borderX, relY - borderY, relX + elementX + borderX, relY + getPropertiesCount() * (elementY + borderY), backgroundColor.argb());
				for(int i = 0; i < getPropertiesCount(); i++){
					IFEIUtilProperty prop = getAllProperties()[i];
					int y = relY + i * (elementY + borderY);
					GuiButtonExt button = new GuiButtonExt(0, relX, y, elementX, elementY, "");
					button.enabled = prop.isEnabled();
					button.drawButton(gui.mc, mouseX, mouseY);
					prop.getRenderable().render(new Rectangle(relX + 2, y + 2, elementX - 4, elementY - 4), gui.mc);
					if(button.isMouseOver()){
						drawTooltip(gui.mc.fontRendererObj, mouseX, mouseY, false, true, 0, textColor, tooltipBackground, false, prop.getDesc());
					}
				}
			} else{
				if(inside(relX, relY, mouseX, mouseY)){
					drawTooltip(gui.mc.fontRendererObj, mouseX, mouseY, false, true, 0, textColor, tooltipBackground, false, getNextProperty().getDesc());
				}
			}
		}

		public boolean handleExtendedMouseEvent(FEIGuiOverride fei, GuiScreen gui, int relX, int relY, int mouseX, int mouseY, boolean down, int key){
			if(isInsideExtended(relX, relY, mouseX, mouseY)){
				if(down && key == 0){
					int i = (int) ((mouseY - relY) / (float) (borderY + getPropertiesCount() * (elementY + borderY)) * getPropertiesCount());
					if(getAllProperties()[i].isEnabled()){
						currentPropertyIndex = i;
						updateCurrentProperty();
						currentProperty.onSelect();
						currentSelected = null;
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public boolean handleMouseEvent(FEIGuiOverride fei, GuiScreen gui, int relX, int relY, int mouseX, int mouseY, boolean down, int key){
			if(inside(relX, relY, mouseX, mouseY)){
				if(down){
					if(key == 0){
						if(getNextProperty().isEnabled()){
							currentPropertyIndex = getNextPropertyIndex();
							updateCurrentProperty();
							currentProperty.onSelect();
							return true;
						}
					} else if(key == 1){
						currentSelected = this;
						return true;
					}
				}
			}
			return false;
		}

	}

	public static class JsonData {

		private int xPos;
		private int yPos;

		private int elementsX;
		private int elementsY;

		private int borderX;
		private int borderY;

		private RGBA backgroundColor;

		private RGBA textColor;

		private boolean tooltipBackground;

		private Map<String, JsonGridElementData> utils = new HashMap<String, JsonGridElementData>();;

		private JsonData(){

		}

		public static class JsonGridElementData {

			private int x;
			private int y;

			private int currentPropertyIndex;

			private JsonGridElementData(){

			}

			private JsonGridElementData(int x, int y, int currentPropertyIndex){
				this.x = x;
				this.y = y;
				this.currentPropertyIndex = currentPropertyIndex;
			}

		}

	}

}
