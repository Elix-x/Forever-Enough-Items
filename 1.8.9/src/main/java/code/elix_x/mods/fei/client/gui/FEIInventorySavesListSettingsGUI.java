package code.elix_x.mods.fei.client.gui;

import org.lwjgl.util.Rectangle;

import code.elix_x.excore.utils.client.gui.elements.IntegralIncrementerGuiElement;
import code.elix_x.excore.utils.client.gui.elements.StringGuiElement;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.mods.fei.api.gui.ElementBasicSettingsGuiScreen;
import code.elix_x.mods.fei.api.gui.FEIGuiOverride;
import code.elix_x.mods.fei.client.gui.element.FEIInventorySavesList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

public class FEIInventorySavesListSettingsGUI extends ElementBasicSettingsGuiScreen {

	private FEIInventorySavesList list;

	public FEIInventorySavesListSettingsGUI(GuiScreen parent, FEIInventorySavesList list){
		super(parent, list.toRectangle(), true, true, list.getBorderX(), list.getBorderY(), list.backgroundColor, list.textColor, list.tooltipBackground);
		this.list = list;
	}

	@Override
	protected void addElements(){
		super.addElements();

		StringGuiElement s;
		add(s = new StringGuiElement("Element Y Size", xPos, nextY + 16 - 8, 2, 2, StatCollector.translateToLocal("fei.gui.settings.elementy") + " ", fontRendererObj, new RGBA(1f, 1f, 1f, 1f)));
		add(new IntegralIncrementerGuiElement("Element Y Size Incrementer", xPos + s.getWidth(), nextY, 16, 8, 16, 2, 2, 1, 0, 128, list.elementY){

			@Override
			public int getValue(){
				return list.elementY;
			}

			@Override
			public void setValue(int value){
				super.setValue(value);
				list.elementY = this.value;
			}

		});
	}

	@Override
	protected void setBorderX(int borderX){
		list.setBorderX(borderX);
		element.setWidth(list.getWidth());
		this.borderX = borderX;
	}

	@Override
	protected void setBorderY(int borderY){
		list.setBorderY(borderY);
		element.setHeight(list.getHeight());
		this.borderY = borderY;
	}

	@Override
	protected void drawElement(Rectangle element){
		list.setXPos(element.getX());
		list.setYPos(element.getY());
		list.setWidth(element.getWidth() - borderX * 2);
		list.setHeight(element.getHeight() - borderY * 2);
		list.setBorderX(borderX);
		list.setBorderY(borderY);
		list.drawGuiPost(FEIGuiOverride.instance(), this, -1, -1);
	}

	@Override
	protected void onClose(){
		list.setXPos(element.getX());
		list.setYPos(element.getY());
		list.setWidth(element.getWidth() - borderX * 2);
		list.setHeight(element.getHeight() - borderY * 2);
		list.setBorderX(borderX);
		list.setBorderY(borderY);
		list.tooltipBackground = tooltipBackground;
	}

}
