package code.elix_x.mods.fei.client.gui;

import org.lwjgl.util.Rectangle;

import code.elix_x.excore.utils.client.gui.elements.IntegralIncrementerGuiElement;
import code.elix_x.excore.utils.client.gui.elements.StringGuiElement;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.mods.fei.api.gui.ElementBasicSettingsGuiScreen;
import code.elix_x.mods.fei.api.gui.FEIGuiOverride;
import code.elix_x.mods.fei.client.gui.element.FEIProfilesSwitcher;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;

public class FEIProfilesSwitcherSettingsGui extends ElementBasicSettingsGuiScreen {

	private FEIProfilesSwitcher switcher;

	public FEIProfilesSwitcherSettingsGui(GuiScreen parent, FEIProfilesSwitcher switcher){
		super(parent, switcher.toRectangle(), false, switcher.getBorderX(), switcher.getBorderY(), switcher.backgroundColor, switcher.textColor, switcher.tooltipBackground);
		this.switcher = switcher;
	}

	@Override
	protected void initElements(){
		super.initElements();

		StringGuiElement s;
		add(s = new StringGuiElement("Element Y Size", xPos, nextY + 16 - 8, 2, 2, I18n.translateToLocal("fei.gui.settings.elementy") + " ", fontRendererObj, new RGBA(1f, 1f, 1f, 1f)));
		add(new IntegralIncrementerGuiElement("Element Y Size Incrementer", xPos + s.getWidth(), nextY, 16, 8, 16, 2, 2, 1, 0, 128, switcher.elementY){

			@Override
			public int getValue(){
				return switcher.elementY;
			}

			@Override
			public void setValue(int value){
				super.setValue(value);
				switcher.elementY = this.value;
			}

		});
	}

	@Override
	protected void setBorderX(int borderX){
		switcher.setBorderX(borderX);
		element.setWidth(switcher.getWidth());
		this.borderX = borderX;
	}

	@Override
	protected void setBorderY(int borderY){
		switcher.setBorderY(borderY);
		element.setHeight(switcher.getHeight());
		this.borderY = borderY;
	}

	@Override
	protected void drawElement(Rectangle element){
		switcher.setXPos(element.getX());
		switcher.setYPos(element.getY());
		switcher.setBorderX(borderX);
		switcher.setBorderY(borderY);
		switcher.drawGuiPost(FEIGuiOverride.instance(), this, -1, -1);
	}

	@Override
	protected void onClose(){
		switcher.setXPos(element.getX());
		switcher.setYPos(element.getY());
		switcher.setBorderX(borderX);
		switcher.setBorderY(borderY);
		switcher.tooltipBackground = tooltipBackground;
	}

}
