package code.elix_x.mods.fei.client.gui;

import org.lwjgl.util.Rectangle;

import code.elix_x.excomms.color.RGBA;
import code.elix_x.excore.utils.client.gui.elements.IntegralIncrementerGuiElement;
import code.elix_x.excore.utils.client.gui.elements.StringGuiElement;
import code.elix_x.mods.fei.api.client.gui.ElementBasicSettingsGuiScreen;
import code.elix_x.mods.fei.api.client.gui.FEIGuiOverride;
import code.elix_x.mods.fei.client.gui.element.FEIModsItemsDropdown;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;

public class FEIModsItemsDropdownSettingsGui extends ElementBasicSettingsGuiScreen {

	private FEIModsItemsDropdown dropdown;

	public FEIModsItemsDropdownSettingsGui(GuiScreen parent, FEIModsItemsDropdown dropdown){
		super(parent, dropdown.toFoldedRectangle(), true, true, dropdown.getBorderX(), dropdown.getBorderY(), dropdown.backgroundColor, dropdown.textColor, dropdown.tooltipBackground);
		this.dropdown = dropdown;
	}

	@Override
	protected void addElements(){
		super.addElements();

		StringGuiElement s;
		add(s = new StringGuiElement("Folded Y Size", xPos, nextY + 16 - 8, 2, 2, I18n.translateToLocal("fei.gui.settings.dropdown.dropdownsize"), fontRenderer, new RGBA(1f, 1f, 1f, 1f)));
		add(new IntegralIncrementerGuiElement("Dropdown Size Incrementer", s.getRight(), nextY, 16, 8, 16, 2, 2, 1, 20, 1028, dropdown.dropdownSize){

			@Override
			public int getValue(){
				return dropdown.dropdownSize;
			}

			@Override
			public void setValue(int value){
				super.setValue(value);
				dropdown.dropdownSize = this.value;
			}

		});
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
		dropdown.focused = true;
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
		dropdown.reInitModsList();
	}

}
