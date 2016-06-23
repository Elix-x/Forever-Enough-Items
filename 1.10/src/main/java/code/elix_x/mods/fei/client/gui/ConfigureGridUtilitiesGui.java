package code.elix_x.mods.fei.client.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import code.elix_x.excore.utils.client.gui.ElementalGuiScreen;
import code.elix_x.excore.utils.client.gui.elements.ButtonGuiElement;
import code.elix_x.excore.utils.client.gui.elements.ColoredRectangleGuiElement;
import code.elix_x.excore.utils.client.gui.elements.GridGuiElement;
import code.elix_x.excore.utils.client.gui.elements.IGuiElementsHandler;
import code.elix_x.mods.fei.client.gui.element.FEIUtilsGrid;
import code.elix_x.mods.fei.client.gui.element.FEIUtilsGrid.FEIUtilsGridElement;
import net.minecraft.client.gui.GuiScreen;

public class ConfigureGridUtilitiesGui extends ElementalGuiScreen {

	private FEIUtilsGrid grid;

	private Collection<FEIUtilsGridElement> all;
	private FEIUtilsGridElement[][] used;
	private List<FEIUtilsGridElement> unused = new ArrayList<FEIUtilsGridElement>();

	private FEIUtilsGridElement dragged;

	public ConfigureGridUtilitiesGui(GuiScreen parent, FEIUtilsGrid grid){
		super(parent, grid.getWidth(), 192);
		this.grid = grid;
	}

	@Override
	public void initGui(){
		guiWidth = grid.getWidth();

		all = grid.getAllElements();
		used = new FEIUtilsGridElement[grid.getElementsX()][grid.getElementsY()];
		unused.clear();
		unused.addAll(all);
		for(int x = 0; x < grid.getElementsX(); x++){
			for(int y = 0; y < grid.getElementsY(); y++){
				unused.remove(used[x][y] = grid.getElement(x, y));
			}
		}

		guiHeight = grid.getHeight() + 2 + (int) Math.ceil(unused.size() / (double) grid.getElementsX()) + 2;

		super.initGui();

		add(new ColoredRectangleGuiElement("Used Grid Background", xPos, yPos, guiWidth, grid.getHeight(), 0, 0, grid.backgroundColor));
		for(int x = 0; x < used.length; x++){
			FEIUtilsGridElement[] usedd = used[x];
			for(int y = 0; y < usedd.length; y++){
				final FEIUtilsGridElement e = usedd[y];
				final int xx = x;
				final int yy = y;
				add(new ButtonGuiElement("", xPos + x * (grid.elementX + grid.getBorderX()), yPos + y * (grid.elementY + grid.getBorderY()), grid.elementX, grid.elementY, grid.getBorderX(), grid.getBorderY(), ""){

					@Override
					public void drawGuiPost(IGuiElementsHandler handler, GuiScreen gui, int mouseX, int mouseY){
						button.enabled = inside(mouseX, mouseY);
						super.drawGuiPost(handler, gui, mouseX, mouseY);
						if(e != null){
							e.drawGuiPost(null, gui, xPos + grid.getBorderX(), yPos + grid.getBorderY(), mouseX, mouseY);
						}
					}

					@Override
					public void onButtonPressed(){
						if(e != null){
							grid.addElement(null, xx, yy);
							dragged = e;
							ConfigureGridUtilitiesGui.this.initGui();
						}
					}

				});
			}
		}

		UnusedGridGuiElement ugrid;
		add(ugrid = new UnusedGridGuiElement("Unused Grid", xPos, yPos + grid.getHeight(), grid.elementX, grid.elementY, grid.getElementsX(), (int) Math.ceil(unused.size() / (double) grid.getElementsX())));
		for(FEIUtilsGridElement element : unused) ugrid.addElement(ugrid.new UnusedGridElement(element));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawBackground(0);
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(dragged != null){
			dragged.drawGuiPost(null, this, mouseX - grid.elementX / 2, mouseY - grid.elementY / 2, mouseX, mouseY);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int key){
		if(key == 0 && dragged != null){
			if(xPos <= mouseX && mouseX <= xPos + guiWidth && yPos <= mouseY && mouseY <= yPos + grid.getHeight()){
				grid.addElement(dragged, (mouseX - xPos - grid.getBorderX()) / (grid.getBorderX() + grid.elementX), (mouseY - yPos - grid.getBorderY()) / (grid.getBorderY() + grid.elementY));
				dragged = null;
				initGui();
			} else {
				dragged = null;
			}
		}
		super.mouseReleased(mouseX, mouseY, key);
	}

	private class UnusedGridGuiElement extends GridGuiElement<ElementalGuiScreen> {

		public UnusedGridGuiElement(String name, int posX, int posY, int elementX, int elementY, int elementsX, int elementsY){
			super(name, posX, posY, elementX, elementY, elementsX, elementsY);
		}

		class UnusedGridElement extends GridElement {

			private FEIUtilsGridElement element;

			public UnusedGridElement(FEIUtilsGridElement element){
				this.element = element;
			}

			@Override
			public void drawGuiPost(ElementalGuiScreen handler, GuiScreen gui, int relX, int relY, int mouseX, int mouseY){
				element.drawGuiPost(null, gui, relX, relY, mouseX, mouseY);
			}

			@Override
			public boolean handleMouseEvent(ElementalGuiScreen handler, GuiScreen gui, int relX, int relY, int mouseX, int mouseY, boolean down, int key){
				if(key == 0 && down && inside(relX, relY, mouseX, mouseY)){
					dragged = element;
					return true;
				}
				return super.handleMouseEvent(handler, gui, relX, relY, mouseX, mouseY, down, key);
			}

		}

	}

}
