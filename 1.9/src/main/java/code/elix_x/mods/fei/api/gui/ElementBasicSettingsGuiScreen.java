package code.elix_x.mods.fei.api.gui;

import org.lwjgl.util.Rectangle;

import code.elix_x.excore.utils.client.gui.ColorSelectorGuiScreen;
import code.elix_x.excore.utils.client.gui.ElementalGuiScreen;
import code.elix_x.excore.utils.client.gui.elements.ButtonGuiElement;
import code.elix_x.excore.utils.client.gui.elements.CenteredStringGuiElement;
import code.elix_x.excore.utils.client.gui.elements.CheckBoxGuiElement;
import code.elix_x.excore.utils.client.gui.elements.IntegralIncrementerGuiElement;
import code.elix_x.excore.utils.client.gui.elements.StringGuiElement;
import code.elix_x.excore.utils.color.RGBA;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;

public class ElementBasicSettingsGuiScreen extends ElementalGuiScreen {

	protected Rectangle element;
	protected boolean resizeable;
	protected int borderX;
	protected int borderY;
	protected RGBA backgroundColor;
	protected RGBA textColor;
	protected boolean tooltipBackground;

	public ElementBasicSettingsGuiScreen(GuiScreen parent, Rectangle element, boolean resizeable, int borderX, int borderY, RGBA backgroundColor, RGBA textColor, boolean tooltipBackground){
		super(parent, 256, 192);
		this.element = element;
		this.resizeable = resizeable;
		this.borderX = borderX;
		this.borderY = borderY;
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
		this.tooltipBackground = tooltipBackground;
	}

	@Override
	public void addElements(){
		add(new ButtonGuiElement("Position And Size", xPos, nextY, guiWidth - 4, 20, 2, 2, I18n.translateToLocal("fei.gui.settings.possize")){

			@Override
			public void onButtonPressed(){
				super.onButtonPressed();
				mc.displayGuiScreen(new ElementPositionSizeSettingsGuiScreen(ElementBasicSettingsGuiScreen.this, element, resizeable){

					protected int checkSizeX(int prevSize){
						return ElementBasicSettingsGuiScreen.this.checkSizeX(prevSize);
					}

					protected int checkSizeY(int prevSize){
						return ElementBasicSettingsGuiScreen.this.checkSizeY(prevSize);
					}

					@Override
					protected void drawElement(){
						ElementBasicSettingsGuiScreen.this.drawElement(element);
					}

				});
			}

		});
		nextY += 2 + 20 + 2;

		String borderXS;
		String borderYS;

		int bsw = Math.max(mc.fontRendererObj.getStringWidth(borderXS = I18n.translateToLocal("fei.gui.settings.borderx") + " "), mc.fontRendererObj.getStringWidth(borderYS = I18n.translateToLocal("fei.gui.settings.bordery") + " "));

		add(new StringGuiElement("Border X", xPos, nextY + 16 - 8, 2, 2, borderXS, fontRendererObj, new RGBA(1f, 1f, 1f, 1f)));
		add(new IntegralIncrementerGuiElement("Border X Selector", xPos + bsw, nextY, 16, 8, 16, 2, 2, 1, 0, 32, borderX){

			@Override
			public int getValue(){
				return ElementBasicSettingsGuiScreen.this.borderX;
			}

			@Override
			public void setValue(int value){
				super.setValue(value);
				ElementBasicSettingsGuiScreen.this.setBorderX(this.value);
			}

		});
		nextY += 2 + 16 + 2;
		add(new StringGuiElement("Border Y", xPos, nextY + 16 - 8, 2, 2, borderYS, fontRendererObj, new RGBA(1f, 1f, 1f, 1f)));
		add(new IntegralIncrementerGuiElement("Border Y Selector", xPos + bsw, nextY, 16, 8, 16, 2, 2, 1, 0, 32, borderY){

			@Override
			public int getValue(){
				return ElementBasicSettingsGuiScreen.this.borderY;
			}

			@Override
			public void setValue(int value){
				super.setValue(value);
				ElementBasicSettingsGuiScreen.this.setBorderY(this.value);
			}

		});
		nextY += 2 + 16 + 2;

		add(new ButtonGuiElement("Background Color Button", xPos, nextY, guiWidth - borderX - borderX, 20, 2, 2, ""){

			@Override
			public void onButtonPressed(){
				super.onButtonPressed();
				mc.displayGuiScreen(new ColorSelectorGuiScreen(ElementBasicSettingsGuiScreen.this, backgroundColor));
			}

		});
		add(new CenteredStringGuiElement("Background Color", xPos + guiWidth / 2, nextY + (20 - 8) / 2, 2, 2, I18n.translateToLocal("fei.gui.settings.backgroundcolor"), fontRendererObj, backgroundColor));
		nextY += 2 + 20 + 2;

		add(new ButtonGuiElement("Text Color Button", xPos, nextY, guiWidth - borderX - borderX, 20, 2, 2, ""){

			@Override
			public void onButtonPressed(){
				super.onButtonPressed();
				mc.displayGuiScreen(new ColorSelectorGuiScreen(ElementBasicSettingsGuiScreen.this, textColor));
			}

		});
		add(new CenteredStringGuiElement("Text Color", xPos + guiWidth / 2, nextY + (20 - 8) / 2, 2, 2, I18n.translateToLocal("fei.gui.settings.textcolor"), fontRendererObj, textColor));
		nextY += 2 + 20 + 2;

		String tooltipBackground;
		bsw = fontRendererObj.getStringWidth(tooltipBackground = I18n.translateToLocal("fei.gui.settings.tooltipbackground") + " ");
		add(new StringGuiElement("Tooltip Background", xPos, nextY + 12 - 8, 2, 2, tooltipBackground, fontRendererObj, new RGBA(1f, 1f, 1f, 1f)));
		add(new CheckBoxGuiElement("Tooltip Background Checkbox", xPos + 2 + bsw, nextY, 12, 12, 2, 2, this.tooltipBackground){

			@Override
			public void setChecked(boolean checked){
				super.setChecked(checked);
				ElementBasicSettingsGuiScreen.this.tooltipBackground = checked;
			}

		});
		nextY += 2 + 12 + 2;
	}

	protected int checkSizeX(int prevSize){
		return prevSize;
	}

	protected int checkSizeY(int prevSize){
		return prevSize;
	}

	protected void setBorderX(int borderX){
		element.setWidth(element.getWidth() - this.borderX - this.borderX + borderX + borderX);
		this.borderX = borderX;
	}

	protected void setBorderY(int borderY){
		element.setHeight(element.getHeight() - this.borderY - this.borderY + borderY + borderY);
		this.borderY = borderY;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawBackground(0);
		drawElement(element);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	protected void drawElement(Rectangle element){
		if(backgroundColor.a > 0) drawRect(element.getX(), element.getY(), element.getX() + element.getWidth(), element.getY() + element.getHeight(), backgroundColor.argb());
	}

}
