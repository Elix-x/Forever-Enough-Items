package code.elix_x.mods.fei.client.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.ImmutableList;

import mezz.jei.Internal;
import mezz.jei.ItemFilter;
import mezz.jei.api.IItemListOverlay;
import mezz.jei.config.Config;
import mezz.jei.gui.TooltipRenderer;
import mezz.jei.gui.ingredients.GuiIngredientFast;
import mezz.jei.gui.ingredients.GuiIngredientFastList;
import mezz.jei.gui.ingredients.GuiItemStackGroup;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.input.ClickedIngredient;
import mezz.jei.input.GuiTextFieldFilter;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.IKeyable;
import mezz.jei.input.IMouseHandler;
import mezz.jei.input.IShowsRecipeFocuses;
import mezz.jei.util.Log;
import mezz.jei.util.MathUtil;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class SelectProfileIconGuiScreen extends GuiScreen {

	public final ProfileSettingsGuiScreen parent;

	private ItemListCustom list;

	private ItemStack itemstack;

	public SelectProfileIconGuiScreen(ProfileSettingsGuiScreen parent, ItemStack itemstack){
		this.parent = parent;
		this.itemstack = itemstack;

		list = new ItemListCustom(Internal.getRuntime().getItemListOverlay().getItemFilter());
	}

	@Override
	public void initGui(){
		list.initGui(width, height);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawBackground(0);
		list.drawScreen(mc, mouseX, mouseY);
		list.drawTooltips(mc, mouseX, mouseY);
	}

	@Override
	protected void keyTyped(char c, int key) throws IOException{
		if(key == 1){
			parent.setProfileItemStack(itemstack);
			this.mc.displayGuiScreen(parent);
			return;
		}
		list.onKeyPressed(c, key);
	}

	@Override
	public void handleMouseInput() throws IOException{
		super.handleMouseInput();
		list.handleMouseScrolled(Mouse.getEventX() * this.width / this.mc.displayWidth, this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1, Mouse.getEventDWheel());
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
		if(mouseButton == 0 && list.getStackUnderMouse() != null){
			parent.setProfileItemStack(list.getStackUnderMouse());
			this.mc.displayGuiScreen(parent);
			return;
		}
		list.handleMouseClicked(mouseX, mouseY, mouseButton);
	}

	private static class ItemListCustom implements IItemListOverlay, IShowsRecipeFocuses, IMouseHandler, IKeyable {

		private static final int borderPadding = 2;
		private static final int searchHeight = 16;
		private static final int buttonSize = 20;
		private static final String nextLabel = ">";
		private static final String backLabel = "<";

		private static final int itemStackPadding = 1;
		private static final int itemStackWidth = GuiItemStackGroup.getWidth(itemStackPadding);
		private static final int itemStackHeight = GuiItemStackGroup.getHeight(itemStackPadding);
		private static int firstItemIndex = 0;

		@Nonnull
		private final ItemFilter itemFilter;

		private int screenWidth;
		private int screenHeight;

		private final GuiIngredientFastList guiItemStacks = new GuiIngredientFastList(Internal.getIngredientRegistry());
		private GuiButton nextButton;
		private GuiButton backButton;
		private GuiTextFieldFilter searchField;

		private String pageNumDisplayString;
		private int pageNumDisplayX;
		private int pageNumDisplayY;

		private GuiIngredientFast hovered = null;

		public ItemListCustom(@Nonnull ItemFilter itemFilter){
			this.itemFilter = itemFilter;
		}

		public void initGui(int screenWidth, int screenHeight){
			this.screenWidth = screenWidth;
			this.screenHeight = screenHeight;

			final int columns = getColumns();
			if(columns < 4){
				return;
			}

			final int rows = getRows();
			final int xSize = columns * itemStackWidth;
			final int xEmptySpace = this.screenWidth - xSize;

			final int leftEdge = xEmptySpace / 2;
			final int rightEdge = leftEdge + xSize;

			final int yItemButtonSpace = getItemButtonYSpace();
			final int itemButtonsHeight = rows * itemStackHeight;

			final int buttonStartY = buttonSize + (2 * borderPadding) + (yItemButtonSpace - itemButtonsHeight) / 2;
			createItemButtons(guiItemStacks, leftEdge, buttonStartY, columns, rows);

			nextButton = new GuiButtonExt(0, rightEdge - buttonSize, borderPadding, buttonSize, buttonSize, nextLabel);
			backButton = new GuiButtonExt(1, leftEdge, borderPadding, buttonSize, buttonSize, backLabel);

			int configButtonX = rightEdge - buttonSize + 1;
			int configButtonY = this.screenHeight - buttonSize - borderPadding;

			int searchFieldY = this.screenHeight - searchHeight - borderPadding - 2;
			int searchFieldWidth = rightEdge - leftEdge;
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
			searchField = new GuiTextFieldFilter(0, fontRenderer, leftEdge, searchFieldY, searchFieldWidth, searchHeight, itemFilter);
			setKeyboardFocus(false);

			updateLayout();
		}

		private static void createItemButtons(@Nonnull GuiIngredientFastList guiItemStacks, final int xStart, final int yStart, final int columnCount, final int rowCount){
			guiItemStacks.clear();

			for(int row = 0; row < rowCount; row++){
				int y = yStart + (row * itemStackHeight);
				for(int column = 0; column < columnCount; column++){
					int x = xStart + (column * itemStackWidth);
					guiItemStacks.add(new GuiIngredientFast(x, y, itemStackPadding));
				}
			}
		}

		private static boolean intersects(List<Rectangle> areas, Rectangle comparisonArea){
			for(Rectangle area : areas){
				if(area.intersects(comparisonArea)){
					return true;
				}
			}
			return false;
		}

		private void updateLayout(){
			ImmutableList<IIngredientListElement> itemList = itemFilter.getIngredientList();
			guiItemStacks.set(firstItemIndex, itemList);

			FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;

			pageNumDisplayString = (getPageNum() + 1) + "/" + getPageCount();
			int pageDisplayWidth = fontRendererObj.getStringWidth(pageNumDisplayString);
			pageNumDisplayX = ((backButton.xPosition + backButton.width) + nextButton.xPosition) / 2 - (pageDisplayWidth / 2);
			pageNumDisplayY = backButton.yPosition + Math.round((backButton.height - fontRendererObj.FONT_HEIGHT) / 2.0f);

			searchField.update();
		}

		private void nextPage(){
			final int itemsCount = itemFilter.size();
			if(itemsCount == 0){
				firstItemIndex = 0;
				return;
			}

			firstItemIndex += guiItemStacks.size();
			if(firstItemIndex >= itemsCount){
				firstItemIndex = 0;
			}
			updateLayout();
		}

		private void previousPage(){
			final int itemsPerPage = guiItemStacks.size();
			if(itemsPerPage == 0){
				firstItemIndex = 0;
				return;
			}
			final int itemsCount = itemFilter.size();

			int pageNum = firstItemIndex / itemsPerPage;
			if(pageNum == 0){
				pageNum = itemsCount / itemsPerPage;
			} else{
				pageNum--;
			}

			firstItemIndex = itemsPerPage * pageNum;
			updateLayout();
		}

		public void drawScreen(@Nonnull Minecraft minecraft, int mouseX, int mouseY){
			GlStateManager.disableLighting();

			minecraft.fontRendererObj.drawString(pageNumDisplayString, pageNumDisplayX, pageNumDisplayY, Color.white.getRGB(), true);
			searchField.drawTextBox();

			nextButton.drawButton(minecraft, mouseX, mouseY);
			backButton.drawButton(minecraft, mouseX, mouseY);

			GlStateManager.disableBlend();

			if(shouldShowDeleteItemTooltip(minecraft)){
				hovered = guiItemStacks.render(minecraft, false, mouseX, mouseY);
			} else{
				boolean mouseOver = isMouseOver(mouseX, mouseY);
				hovered = guiItemStacks.render(minecraft, mouseOver, mouseX, mouseY);
			}

			if(hovered != null){
				RenderHelper.enableGUIStandardItemLighting();
				hovered.drawHovered(minecraft);
				RenderHelper.disableStandardItemLighting();
			}

			GlStateManager.enableAlpha();
		}

		private boolean shouldShowDeleteItemTooltip(Minecraft minecraft){
			if(Config.isDeleteItemsInCheatModeActive()){
				EntityPlayer player = minecraft.thePlayer;
				if(player.inventory.getItemStack() != null){
					return true;
				}
			}
			return false;
		}

		public void drawTooltips(@Nonnull Minecraft minecraft, int mouseX, int mouseY){
			boolean mouseOver = isMouseOver(mouseX, mouseY);
			if(mouseOver && shouldShowDeleteItemTooltip(minecraft)){
				String deleteItem = Translator.translateToLocal("jei.tooltip.delete.item");
				TooltipRenderer.drawHoveringText(minecraft, deleteItem, mouseX, mouseY);
			}

			if(hovered != null){
				hovered.drawTooltip(minecraft, mouseX, mouseY);
			}
		}

		public void handleTick(){
			if(searchField != null){
				searchField.updateCursorCounter();
			}
		}

		@Override
		public boolean isMouseOver(int mouseX, int mouseY){
			return true;
		}

		@Override
		@Nullable
		public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY){
			if(!isMouseOver(mouseX, mouseY)){
				return null;
			}

			ClickedIngredient<?> focus = guiItemStacks.getIngredientUnderMouse(mouseX, mouseY);
			if(focus != null){
				setKeyboardFocus(false);
				focus.setAllowsCheating();
			}
			return focus;
		}

		@Override
		public boolean canSetFocusWithMouse(){
			return true;
		}

		@Override
		public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton){
			if(!isMouseOver(mouseX, mouseY)){
				setKeyboardFocus(false);
				return false;
			}

			boolean buttonClicked = handleMouseClickedButtons(mouseX, mouseY);
			if(buttonClicked){
				setKeyboardFocus(false);
				return true;
			}

			return handleMouseClickedSearch(mouseX, mouseY, mouseButton);
		}

		@Override
		public boolean handleMouseScrolled(int mouseX, int mouseY, int scrollDelta){
			if(!isMouseOver(mouseX, mouseY)){
				return false;
			}
			if(scrollDelta < 0){
				nextPage();
				return true;
			} else if(scrollDelta > 0){
				previousPage();
				return true;
			}
			return false;
		}

		private boolean handleMouseClickedButtons(int mouseX, int mouseY){
			Minecraft minecraft = Minecraft.getMinecraft();
			if(nextButton.mousePressed(minecraft, mouseX, mouseY)){
				nextPage();
				nextButton.playPressSound(minecraft.getSoundHandler());
				return true;
			} else if(backButton.mousePressed(minecraft, mouseX, mouseY)){
				previousPage();
				backButton.playPressSound(minecraft.getSoundHandler());
				return true;
			}
			return false;
		}

		private boolean handleMouseClickedSearch(int mouseX, int mouseY, int mouseButton){
			boolean searchClicked = searchField.isMouseOver(mouseX, mouseY);
			setKeyboardFocus(searchClicked);
			if(searchClicked && searchField.handleMouseClicked(mouseX, mouseY, mouseButton)){
				updateLayout();
			}
			return searchClicked;
		}

		@Override
		public boolean hasKeyboardFocus(){
			return searchField != null && searchField.isFocused();
		}

		@Override
		public void setKeyboardFocus(boolean keyboardFocus){
			if(searchField != null){
				searchField.setFocused(keyboardFocus);
			}
		}

		@Override
		public boolean onKeyPressed(char typedChar, int keyCode){
			if(hasKeyboardFocus()){
				char character = Keyboard.getEventCharacter();
				boolean changed = searchField.textboxKeyTyped(character, Keyboard.getEventKey());
				if(changed){
					while(firstItemIndex >= itemFilter.size() && firstItemIndex > 0){
						previousPage();
					}
					updateLayout();
				}
				return changed || ChatAllowedCharacters.isAllowedCharacter(character);
			}
			return false;
		}

		private int getItemButtonXSpace(){
			return this.screenWidth - (2 * borderPadding);
		}

		private int getItemButtonYSpace(){
			return this.screenHeight - (buttonSize + searchHeight + 2 + (4 * borderPadding));
		}

		private int getColumns(){
			return getItemButtonXSpace() / itemStackWidth;
		}

		private int getRows(){
			return getItemButtonYSpace() / itemStackHeight;
		}

		private int getPageCount(){
			final int itemCount = itemFilter.size();
			final int stacksPerPage = guiItemStacks.size();
			if(stacksPerPage == 0){
				return 1;
			}
			int pageCount = MathUtil.divideCeil(itemCount, stacksPerPage);
			pageCount = Math.max(1, pageCount);
			return pageCount;
		}

		private int getPageNum(){
			final int stacksPerPage = guiItemStacks.size();
			if(stacksPerPage == 0){
				return 1;
			}
			return firstItemIndex / stacksPerPage;
		}

		@Nullable
		@Override
		public ItemStack getStackUnderMouse(){
			if(hovered != null){
				Object ingredient = hovered.getIngredient();
				if(ingredient instanceof ItemStack){
					return (ItemStack) ingredient;
				}
			}
			return null;
		}

		@Override
		public void setFilterText(@Nullable String filterText){
			if(filterText == null){
				Log.error("null filterText", new NullPointerException());
				return;
			}
			searchField.setText(filterText);
		}

		@Nonnull
		@Override
		public String getFilterText(){
			return searchField.getText();
		}

		@Override
		public ImmutableList<ItemStack> getFilteredStacks(){
			return null;
		}

		@Override
		public ImmutableList<ItemStack> getVisibleStacks(){
			return null;
		}

		@Override
		public void highlightStacks(Collection<ItemStack> stacks){

		}

	}

}
