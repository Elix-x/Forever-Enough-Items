package code.elix_x.mods.fei.client.jeioverride;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.config.FEIConfiguration;
import code.elix_x.mods.fei.net.FEIGiveItemStackPacket;
import mezz.jei.ItemFilter;
import mezz.jei.JustEnoughItems;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.config.Config;
import mezz.jei.gui.Focus;
import mezz.jei.gui.GuiProperties;
import mezz.jei.gui.ItemListOverlay;
import mezz.jei.gui.TooltipRenderer;
import mezz.jei.gui.ingredients.GuiItemStackFast;
import mezz.jei.gui.ingredients.GuiItemStackFastList;
import mezz.jei.gui.ingredients.GuiItemStackGroup;
import mezz.jei.input.GuiTextFieldFilter;
import mezz.jei.network.packets.PacketDeletePlayerItem;
import mezz.jei.network.packets.PacketJEI;
import mezz.jei.util.ItemStackElement;
import mezz.jei.util.Log;
import mezz.jei.util.MathUtil;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class ItemListOverlayOverride extends ItemListOverlay {

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
	@Nonnull
	private final List<IAdvancedGuiHandler<?>> advancedGuiHandlers;

	private final GuiItemStackFastList guiItemStacks = new GuiItemStackFastList();
	private GuiButton nextButton;
	private GuiButton backButton;
	private GuiTextFieldFilter searchField;

	private String pageNumDisplayString;
	private int pageNumDisplayX;
	private int pageNumDisplayY;

	private GuiItemStackFast hovered = null;

	// properties of the gui we're beside
	@Nullable
	private GuiProperties guiProperties;
	@Nullable
	private List<Rectangle> guiAreas;
	@Nonnull
	private List<IAdvancedGuiHandler<?>> activeAdvancedGuiHandlers = Collections.emptyList();

	private boolean open = false;

	private boolean canGiveItems;
	private boolean canDeleteItems;

	private boolean moveSearchFieldToCenter;
	private int searchFieldWidth;
	private int searchFieldHeight;

	public ItemListOverlayOverride(@Nonnull ItemFilter itemFilter, @Nonnull List<IAdvancedGuiHandler<?>> advancedGuiHandlers, boolean canGiveItems, boolean canDeleteItems, boolean moveSearchFieldToCenter, int searchFieldWidth, int searchFieldHeight){
		super(itemFilter, advancedGuiHandlers);
		this.itemFilter = itemFilter;
		this.advancedGuiHandlers = advancedGuiHandlers;

		this.canGiveItems = canGiveItems;
		this.canDeleteItems = canDeleteItems;
		this.moveSearchFieldToCenter = moveSearchFieldToCenter;
		this.searchFieldWidth = searchFieldWidth;
		this.searchFieldHeight = searchFieldHeight;
	}

	@Override
	public void initGui(@Nonnull GuiScreen guiScreen){
		GuiProperties guiProperties = GuiProperties.create(guiScreen);
		if(guiProperties == null){
			return;
		}

		this.guiProperties = guiProperties;
		this.activeAdvancedGuiHandlers = getActiveAdvancedGuiHandlers(guiScreen);
		if (!activeAdvancedGuiHandlers.isEmpty() && guiScreen instanceof GuiContainer) {
			GuiContainer guiContainer = (GuiContainer) guiScreen;
			guiAreas = getGuiAreas(guiContainer);
		} else {
			guiAreas = null;
		}

		final int columns = getColumns();
		if(columns < 4){
			close();
			return;
		}

		final int rows = getRows();
		final int xSize = columns * itemStackWidth;
		final int xEmptySpace = guiProperties.getScreenWidth() - guiProperties.getGuiLeft() - guiProperties.getGuiXSize() - xSize;

		final int leftEdge = guiProperties.getGuiLeft() + guiProperties.getGuiXSize() + (xEmptySpace / 2);
		final int rightEdge = leftEdge + xSize;

		final int yItemButtonSpace = getItemButtonYSpace();
		final int itemButtonsHeight = rows * itemStackHeight;

		final int buttonStartY = buttonSize + (2 * borderPadding) + (yItemButtonSpace - itemButtonsHeight) / 2;
		createItemButtons(guiItemStacks, guiAreas, leftEdge, buttonStartY, columns, rows);

		nextButton = new GuiButtonExt(0, rightEdge - buttonSize, borderPadding, buttonSize, buttonSize, nextLabel);
		backButton = new GuiButtonExt(1, leftEdge, borderPadding, buttonSize, buttonSize, backLabel);

		if(moveSearchFieldToCenter){
			int searchFieldY = guiProperties.getScreenHeight() - searchFieldHeight - borderPadding - 2;
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
			searchField = new GuiTextFieldFilter(0, fontRenderer, (guiProperties.getScreenWidth() - searchFieldWidth) / 2, searchFieldY, searchFieldWidth, searchFieldHeight);
			setKeyboardFocus(false);
			searchField.setItemFilter(itemFilter);
		} else {
			int searchFieldY = guiProperties.getScreenHeight() - searchFieldHeight - borderPadding - 2;
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
			searchField = new GuiTextFieldFilter(0, fontRenderer, rightEdge - searchFieldWidth - borderPadding, searchFieldY, searchFieldWidth, searchFieldHeight);
			setKeyboardFocus(false);
			searchField.setItemFilter(itemFilter);
		}

		updateLayout();

		open();
	}

	@Nonnull
	private List<IAdvancedGuiHandler<?>> getActiveAdvancedGuiHandlers(@Nonnull GuiScreen guiScreen) {
		List<IAdvancedGuiHandler<?>> activeAdvancedGuiHandler = new ArrayList<>();
		if (guiScreen instanceof GuiContainer) {
			GuiContainer guiContainer = (GuiContainer) guiScreen;
			for (IAdvancedGuiHandler<?> advancedGuiHandler : advancedGuiHandlers){
				if(advancedGuiHandler.getGuiContainerClass().isAssignableFrom(guiContainer.getClass())){
					activeAdvancedGuiHandler.add(advancedGuiHandler);
				}
			}
		}
		return activeAdvancedGuiHandler;
	}

	private List<Rectangle> getGuiAreas(GuiContainer guiContainer) {
		List<Rectangle> guiAreas = new ArrayList<>();
		for (IAdvancedGuiHandler<?> advancedGuiHandler : activeAdvancedGuiHandlers) {
			List<Rectangle> guiExtraAreas = getGuiAreas(guiContainer, advancedGuiHandler);
			if (guiExtraAreas != null) {
				guiAreas.addAll(guiExtraAreas);
			}
		}
		return guiAreas;
	}

	private <T extends GuiContainer> List<Rectangle> getGuiAreas(GuiContainer guiContainer, IAdvancedGuiHandler<T> advancedGuiHandler) {
		if (advancedGuiHandler.getGuiContainerClass().isAssignableFrom(guiContainer.getClass())) {
			T guiT = advancedGuiHandler.getGuiContainerClass().cast(guiContainer);
			return advancedGuiHandler.getGuiExtraAreas(guiT);
		}
		return null;
	}

	@Override
	public void updateGui(@Nonnull GuiScreen guiScreen){
		if(this.guiProperties == null){
			initGui(guiScreen);
		} else {
			GuiProperties guiProperties = GuiProperties.create(guiScreen);
			if(guiProperties == null){
				return;
			}
			if(!this.guiProperties.equals(guiProperties)){
				initGui(guiScreen);
			} else if (!activeAdvancedGuiHandlers.isEmpty() && guiScreen instanceof GuiContainer) {
				GuiContainer guiContainer = (GuiContainer) guiScreen;
				List<Rectangle> guiAreas = getGuiAreas(guiContainer);
				if(!Objects.equals(this.guiAreas, guiAreas)){
					initGui(guiContainer);
				}
			}
		}
	}

	private static void createItemButtons(@Nonnull GuiItemStackFastList guiItemStacks, @Nullable List<Rectangle> guiAreas, final int xStart, final int yStart, final int columnCount, final int rowCount){
		guiItemStacks.clear();

		for (int row = 0; row < rowCount; row++){
			int y = yStart + (row * itemStackHeight);
			for (int column = 0; column < columnCount; column++){
				int x = xStart + (column * itemStackWidth);
				GuiItemStackFast guiItemStackFast = new GuiItemStackFast(x, y, itemStackPadding);
				if(guiAreas != null){
					Rectangle stackArea = guiItemStackFast.getArea();
					if(intersects(guiAreas, stackArea)){
						continue;
					}
				}
				guiItemStacks.add(guiItemStackFast);
			}
		}
	}

	private static boolean intersects(List<Rectangle> areas, Rectangle comparisonArea){
		for (Rectangle area : areas){
			if(area.intersects(comparisonArea)){
				return true;
			}
		}
		return false;
	}

	private void updateLayout(){
		ImmutableList<ItemStackElement> itemList = itemFilter.getItemList();
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
		} else {
			pageNum--;
		}

		firstItemIndex = itemsPerPage * pageNum;
		if (firstItemIndex > 0 && firstItemIndex == itemsCount) {
			pageNum--;
			firstItemIndex = itemsPerPage * pageNum;
		}
		updateLayout();
	}

	@Override
	public void drawScreen(@Nonnull Minecraft minecraft, int mouseX, int mouseY){
		if(!isOpen()){
			return;
		}

		GlStateManager.disableLighting();

		minecraft.fontRendererObj.drawString(pageNumDisplayString, pageNumDisplayX, pageNumDisplayY, Color.white.getRGB(), true);
		searchField.drawTextBox();

		nextButton.drawButton(minecraft, mouseX, mouseY);
		backButton.drawButton(minecraft, mouseX, mouseY);

		GlStateManager.disableBlend();

		if(shouldShowDeleteItemTooltip(minecraft)){
			hovered = guiItemStacks.render(minecraft, false, mouseX, mouseY);
		} else {
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
		if(canDeleteItems && FEIConfiguration.canDeleteItems(Minecraft.getMinecraft().thePlayer)){
			EntityPlayer player = minecraft.thePlayer;
			if(player.inventory.getItemStack() != null){
				return true;
			}
		}
		return false;
	}

	@Override
	public void drawTooltips(@Nonnull Minecraft minecraft, int mouseX, int mouseY){
		if(!isOpen()){
			return;
		}

		boolean mouseOver = isMouseOver(mouseX, mouseY);
		if(mouseOver && shouldShowDeleteItemTooltip(minecraft)){
			String deleteItem = Translator.translateToLocal("jei.tooltip.delete.item");
			TooltipRenderer.drawHoveringText(minecraft, deleteItem, mouseX, mouseY);
		}

		if(hovered != null){
			hovered.drawTooltip(minecraft, mouseX, mouseY);
		}
	}

	@Override
	public void handleTick(){
		if(searchField != null){
			searchField.updateCursorCounter();
		}
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY){
		if(guiProperties == null || !isOpen()){
			return false;
		} else {
			if(!(moveSearchFieldToCenter && searchField.isMouseOver(mouseX, mouseY))){
				if(mouseX < guiProperties.getGuiLeft() + guiProperties.getGuiXSize()){
					return false;
				}
			}
		}

		if(guiAreas != null){
			for (Rectangle guiArea : guiAreas){
				if(guiArea.contains(mouseX, mouseY)){
					return false;
				}
			}
		}

		return true;
	}

	@Override
	@Nullable
	public Focus getFocusUnderMouse(int mouseX, int mouseY){
		if(!isMouseOver(mouseX, mouseY)){
			return null;
		}

		Focus focus = guiItemStacks.getFocusUnderMouse(mouseX, mouseY);
		boolean key = Thread.currentThread().getStackTrace()[2].getMethodName().equals("getFocusUnderMouseForKey");
		if(focus != null && (key || Config.isEditModeEnabled() || !canGiveItems || !FEIConfiguration.canGiveItems(Minecraft.getMinecraft().thePlayer))){
			setKeyboardFocus(false);
			return focus;
		}
		return null;
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

		if(Minecraft.getMinecraft().thePlayer.inventory.getItemStack() == null){
			Focus f = guiItemStacks.getFocusUnderMouse(mouseX, mouseY);
			if(f != null && canGiveItems &&  FEIConfiguration.canGiveItems(Minecraft.getMinecraft().thePlayer)){
				ItemStack itemstack = f.getStack().copy();
				if(mouseButton == 0) itemstack.stackSize = itemstack.getMaxStackSize();
				else itemstack.stackSize = 1;
				ForeverEnoughItemsBase.net.sendToServer(new FEIGiveItemStackPacket(itemstack));
				return true;
			}
		} else {
			if(canDeleteItems && FEIConfiguration.canDeleteItems(Minecraft.getMinecraft().thePlayer)){
				Minecraft minecraft = Minecraft.getMinecraft();
				EntityPlayerSP player = minecraft.thePlayer;
				ItemStack itemStack = player.inventory.getItemStack();
				if(itemStack != null){
					player.inventory.setItemStack(null);
					PacketJEI packet = new PacketDeletePlayerItem(itemStack);
					JustEnoughItems.getProxy().sendPacketToServer(packet);
					return true;
				}
			}
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
	public boolean onKeyPressed(char typedChar, int keyCode) {
		if (hasKeyboardFocus()) {
			boolean changed = searchField.textboxKeyTyped(typedChar, keyCode);
			if (changed) {
				firstItemIndex = 0;
				updateLayout();
			}
			return changed || ChatAllowedCharacters.isAllowedCharacter(typedChar);
		}
		return false;
	}

	private int getItemButtonXSpace(){
		if(guiProperties == null){
			return 0;
		}
		return guiProperties.getScreenWidth() - (guiProperties.getGuiLeft() + guiProperties.getGuiXSize() + (2 * borderPadding));
	}

	private int getItemButtonYSpace(){
		if(guiProperties == null){
			return 0;
		}
		return guiProperties.getScreenHeight() - (buttonSize + (moveSearchFieldToCenter ? 0 : (searchFieldHeight + 2)) + (4 * borderPadding));
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

	@Override
	public void open(){
		open = true;
		setKeyboardFocus(false);
	}

	@Override
	public void close(){
		open = false;
		setKeyboardFocus(false);
		Config.saveFilterText();
	}

	@Override
	public boolean isOpen(){
		return open && Config.isOverlayEnabled();
	}

	@Nullable
	@Override
	public ItemStack getStackUnderMouse(){
		if(hovered == null){
			return null;
		} else {
			return hovered.getItemStack();
		}
	}

	@Override
	public void setFilterText(@Nullable String filterText){
		if(filterText == null){
			Log.error("null filterText", new NullPointerException());
			return;
		}
		searchField.setText(filterText);
		Config.setFilterText(filterText);
	}

	@Nonnull
	@Override
	public String getFilterText(){
		return itemFilter.getFilterText();
	}

}
