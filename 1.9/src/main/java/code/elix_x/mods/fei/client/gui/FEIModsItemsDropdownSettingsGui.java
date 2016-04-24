package code.elix_x.mods.fei.client.gui;

import org.lwjgl.util.Rectangle;

import code.elix_x.mods.fei.api.gui.ElementBasicSettingsGuiScreen;
import code.elix_x.mods.fei.api.gui.FEIGuiOverride;
import code.elix_x.mods.fei.client.gui.element.FEIModsItemsDropdown;
import net.minecraft.client.gui.GuiScreen;

public class FEIModsItemsDropdownSettingsGui extends ElementBasicSettingsGuiScreen {

	private FEIModsItemsDropdown dropdown;

	public FEIModsItemsDropdownSettingsGui(GuiScreen parent, FEIModsItemsDropdown dropdown){
		super(parent, dropdown.toRectangle(), true, dropdown.getBorderX(), dropdown.getBorderY(), dropdown.backgroundColor, dropdown.textColor, dropdown.tooltipBackground);
		this.dropdown = dropdown;
	}

	@Override
	protected void setBorderX(int borderX){
		dropdown.setBorderX(borderX);
		element.setWidth(dropdown.getWidth());
		this.borderX = borderX;
	}

	@Override
	protected void setBorderY(int borderY){
		dropdown.setBorderY(borderY);
		element.setHeight(dropdown.getHeight());
		this.borderY = borderY;
	}

	@Override
	protected void drawElement(Rectangle element){
		dropdown.setXPos(element.getX());
		dropdown.setYPos(element.getY());
		dropdown.setWidth(element.getWidth() - borderX * 2);
		dropdown.setHeight(element.getHeight() - borderY * 2);
		dropdown.setBorderX(borderX);
		dropdown.setBorderY(borderY);
		dropdown.drawGuiPost(FEIGuiOverride.instance(), this, -1, -1);
	}

	@Override
	protected void onClose(){
		dropdown.setXPos(element.getX());
		dropdown.setYPos(element.getY());
		dropdown.setWidth(element.getWidth() - borderX * 2);
		dropdown.setHeight(element.getHeight() - borderY * 2);
		dropdown.setBorderX(borderX);
		dropdown.setBorderY(borderY);
		dropdown.tooltipBackground = tooltipBackground;
	}

}
