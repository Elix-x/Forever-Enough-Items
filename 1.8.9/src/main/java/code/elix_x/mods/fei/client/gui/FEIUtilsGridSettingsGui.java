package code.elix_x.mods.fei.client.gui;

import org.lwjgl.util.Rectangle;

import code.elix_x.excore.utils.client.gui.elements.ButtonGuiElement;
import code.elix_x.excore.utils.client.gui.elements.IntegralIncrementerGuiElement;
import code.elix_x.excore.utils.client.gui.elements.StringGuiElement;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.mods.fei.api.gui.ElementBasicSettingsGuiScreen;
import code.elix_x.mods.fei.api.gui.FEIGuiOverride;
import code.elix_x.mods.fei.client.gui.element.FEIUtilsGrid;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

public class FEIUtilsGridSettingsGui extends ElementBasicSettingsGuiScreen {

	private FEIUtilsGrid grid;

	public FEIUtilsGridSettingsGui(GuiScreen parent, FEIUtilsGrid grid){
		super(parent, grid.toRectangle(), true, true, grid.getBorderX(), grid.getBorderY(), grid.backgroundColor, grid.textColor, grid.tooltipBackground);
		this.grid = grid;
	}

	@Override
	protected void addElements(){
		add(new ButtonGuiElement("Utilities", xPos, nextY, guiWidth - 4, 20, 2, 2, StatCollector.translateToLocal("fei.gui.settings.grid.utilities")){

			@Override
			public void onButtonPressed(){
				super.onButtonPressed();
				mc.displayGuiScreen(new ConfigureGridUtilitiesGui(FEIUtilsGridSettingsGui.this, grid));
			}

		});
		nextY += 2 + 20 + 2;

		super.addElements();

		String ex;
		String ey;
		int sw = Math.max(fontRendererObj.getStringWidth(ex = StatCollector.translateToLocal("fei.gui.settings.grid.elementx") + " "), fontRendererObj.getStringWidth(ey = StatCollector.translateToLocal("fei.gui.settings.elementy") + " "));

		add(new StringGuiElement("Element X", xPos, nextY + 16 - 8, 2, 2, ex, fontRendererObj, new RGBA(1f, 1f, 1f, 1f)));
		add(new IntegralIncrementerGuiElement("Element X Incrementer", xPos + 2 + sw + 2, nextY, 24, 8, 16, 2, 2, 1, 2, 64, grid.elementX){

			@Override
			public int getValue(){
				return grid.elementX;
			}

			@Override
			public void setValue(int value){
				super.setValue(value);
				grid.elementX = this.value;
				element.setWidth(grid.getWidth());
			}

		});
		nextY += 2 + 16 + 2;

		add(new StringGuiElement("Element Y", xPos, nextY + 16 - 8, 2, 2, ey, fontRendererObj, new RGBA(1f, 1f, 1f, 1f)));
		add(new IntegralIncrementerGuiElement("Element Y Incrementer", xPos + 2 + sw + 2, nextY, 24, 8, 16, 2, 2, 1, 2, 64, grid.elementY){

			@Override
			public int getValue(){
				return grid.elementY;
			}

			@Override
			public void setValue(int value){
				super.setValue(value);
				grid.elementY = this.value;
				element.setHeight(grid.getHeight());
			}

		});
		nextY += 2 + 16 + 2;
	}

	@Override
	protected int checkSizeX(int prevSize){
		int x = borderX;
		while(x <= prevSize + grid.elementX + borderX) x += grid.elementX + borderX;
		x -= grid.elementX + borderX;
		return x;
	}

	@Override
	protected int checkSizeY(int prevSize){
		int y = borderY;
		while(y <= prevSize + grid.elementY + borderY) y += grid.elementY + borderY;
		y -= grid.elementY + borderY;
		return y;
	}

	@Override
	protected void setBorderX(int borderX){
		grid.setBorderX(borderX);
		element.setWidth(grid.getWidth());
		this.borderX = borderX;
	}

	@Override
	protected void setBorderY(int borderY){
		grid.setBorderY(borderY);
		element.setHeight(grid.getHeight());
		this.borderY = borderY;
	}

	@Override
	protected void drawElement(Rectangle element){
		grid.setXPos(element.getX());
		grid.setYPos(element.getY());
		grid.setBorderX(borderX);
		grid.setBorderY(borderY);
		grid.setElementsXY((element.getWidth() - borderX) / (grid.elementX + borderX), (element.getHeight() - borderY) / (grid.elementY + borderY));
		grid.drawGuiPost(FEIGuiOverride.instance(), this, -1, -1);
	}

	@Override
	protected void onClose(){
		grid.setXPos(element.getX());
		grid.setYPos(element.getY());
		grid.setBorderX(borderX);
		grid.setBorderY(borderY);
		grid.setElementsXY((element.getWidth() - borderX) / (grid.elementX + borderX), (element.getHeight() - borderY) / (grid.elementY + borderY));
		grid.tooltipBackground = tooltipBackground;
	}

}
