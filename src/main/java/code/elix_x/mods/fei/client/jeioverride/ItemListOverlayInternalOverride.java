package code.elix_x.mods.fei.client.jeioverride;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.config.FEIConfiguration;
import code.elix_x.mods.fei.net.FEIGiveItemStackPacket;
import mezz.jei.Internal;
import mezz.jei.JeiRuntime;
import mezz.jei.JustEnoughItems;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.config.Config;
import mezz.jei.gui.GuiProperties;
import mezz.jei.gui.ItemListOverlay;
import mezz.jei.gui.ItemListOverlayInternal;
import mezz.jei.gui.TooltipRenderer;
import mezz.jei.gui.ingredients.GuiIngredientFast;
import mezz.jei.gui.ingredients.GuiIngredientFastList;
import mezz.jei.gui.ingredients.GuiItemStackGroup;
import mezz.jei.input.ClickedIngredient;
import mezz.jei.input.GuiTextFieldFilter;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.network.packets.PacketDeletePlayerItem;
import mezz.jei.util.Java6Helper;
import mezz.jei.util.MathUtil;
import mezz.jei.util.StackHelper;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemListOverlayInternalOverride extends ItemListOverlayInternal {

	private static final int borderPadding = 2;
	private static final int buttonSize = 20;
	private static final String nextLabel = ">";
	private static final String backLabel = "<";

	private static final int itemStackPadding = 1;
	private static final int itemStackWidth = GuiItemStackGroup.getWidth(itemStackPadding);
	private static final int itemStackHeight = GuiItemStackGroup.getHeight(itemStackPadding);
	private static int firstItemIndex = 0;

	private final ItemListOverlay parent;

	private final GuiButton nextButton;
	private final GuiButton backButton;
	private final GuiTextFieldFilter searchField;

	private String pageNumDisplayString = "1/1";
	private int pageNumDisplayX;
	private int pageNumDisplayY;

	private final GuiIngredientFastList guiIngredientList;
	@Nullable
	private GuiIngredientFast hovered = null;

	// properties of the gui we're beside
	private final GuiProperties guiProperties;
	private final List<Rectangle> guiAreas;
	private List<IAdvancedGuiHandler<?>> activeAdvancedGuiHandlers = Collections.emptyList();

	private boolean canGiveItems;
	private boolean canDeleteItems;

	private int searchFieldWidth;
	private int searchFieldHeight;

	public ItemListOverlayInternalOverride(ItemListOverlay parent, IIngredientRegistry ingredientRegistry, GuiScreen guiScreen, GuiProperties guiProperties, boolean canGiveItems, boolean canDeleteItems, int searchFieldWidth, int searchFieldHeight){
		super(parent, ingredientRegistry, guiScreen, guiProperties);

		this.canGiveItems = canGiveItems;
		this.canDeleteItems = canDeleteItems;
		this.searchFieldWidth = searchFieldWidth;
		this.searchFieldHeight = searchFieldHeight;

		this.parent = parent;

		this.guiIngredientList = new GuiIngredientFastList(ingredientRegistry);

		this.guiProperties = guiProperties;
		this.activeAdvancedGuiHandlers = getActiveAdvancedGuiHandlers(guiScreen);
		if(!activeAdvancedGuiHandlers.isEmpty() && guiScreen instanceof GuiContainer){
			GuiContainer guiContainer = (GuiContainer) guiScreen;
			guiAreas = getGuiAreas(guiContainer);
		} else{
			guiAreas = Collections.emptyList();
		}

		final int columns = getColumns_(guiProperties);
		final int rows = getRows_(guiProperties);
		final int xSize = columns * itemStackWidth;
		final int xEmptySpace = guiProperties.getScreenWidth() - guiProperties.getGuiLeft() - guiProperties.getGuiXSize() - xSize;

		final int leftEdge = guiProperties.getGuiLeft() + guiProperties.getGuiXSize() + (xEmptySpace / 2);
		final int rightEdge = leftEdge + xSize;

		final int yItemButtonSpace = getItemButtonYSpace(guiProperties);
		final int itemButtonsHeight = rows * itemStackHeight;

		final int buttonStartY = buttonSize + (2 * borderPadding) + (yItemButtonSpace - itemButtonsHeight) / 2;
		createItemButtons(guiIngredientList, guiAreas, leftEdge, buttonStartY, columns, rows);

		nextButton = new GuiButton(0, rightEdge - buttonSize, borderPadding, buttonSize, buttonSize, nextLabel);
		backButton = new GuiButton(1, leftEdge, borderPadding, buttonSize, buttonSize, backLabel);

		final int searchFieldX;
		final int searchFieldY = guiProperties.getScreenHeight() - searchFieldHeight - borderPadding - 2;

		if(isSearchBarCentered(guiProperties)){
			searchFieldX = guiProperties.getGuiLeft();
		} else{
			searchFieldX = leftEdge;
		}

		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		searchField = new GuiTextFieldFilter(0, fontRenderer, searchFieldX, searchFieldY, searchFieldWidth, searchFieldHeight, parent.getItemFilter());
		setKeyboardFocus(false);

		updateLayout();
	}

	private static boolean isSearchBarCentered(GuiProperties guiProperties){
		return Config.isCenterSearchBarEnabled();
	}

	private List<IAdvancedGuiHandler<?>> getActiveAdvancedGuiHandlers(GuiScreen guiScreen){
		List<IAdvancedGuiHandler<?>> activeAdvancedGuiHandler = new ArrayList<IAdvancedGuiHandler<?>>();
		if(guiScreen instanceof GuiContainer){
			for(IAdvancedGuiHandler<?> advancedGuiHandler : parent.getAdvancedGuiHandlers()){
				Class<?> guiContainerClass = advancedGuiHandler.getGuiContainerClass();
				if(guiContainerClass.isInstance(guiScreen)){
					activeAdvancedGuiHandler.add(advancedGuiHandler);
				}
			}
		}
		return activeAdvancedGuiHandler;
	}

	private List<Rectangle> getGuiAreas(GuiContainer guiContainer){
		List<Rectangle> guiAreas = new ArrayList<Rectangle>();
		for(IAdvancedGuiHandler<?> advancedGuiHandler : activeAdvancedGuiHandlers){
			List<Rectangle> guiExtraAreas = getGuiAreas(guiContainer, advancedGuiHandler);
			if(guiExtraAreas != null){
				guiAreas.addAll(guiExtraAreas);
			}
		}
		return guiAreas;
	}

	@Nullable
	private <T extends GuiContainer> List<Rectangle> getGuiAreas(GuiContainer gui, IAdvancedGuiHandler<T> advancedGuiHandler){
		Class<T> guiClass = advancedGuiHandler.getGuiContainerClass();
		if(guiClass.isInstance(gui)){
			T guiT = guiClass.cast(gui);
			return advancedGuiHandler.getGuiExtraAreas(guiT);
		}
		return null;
	}

	public boolean hasScreenChanged(GuiScreen guiScreen){
		if(!Config.isOverlayEnabled()){
			return true;
		}
		GuiProperties guiProperties = GuiProperties.create(guiScreen);
		if(guiProperties == null){
			return true;
		}
		if(!this.guiProperties.equals(guiProperties)){
			return true;
		} else if(!activeAdvancedGuiHandlers.isEmpty() && guiScreen instanceof GuiContainer){
			GuiContainer guiContainer = (GuiContainer) guiScreen;
			List<Rectangle> guiAreas = getGuiAreas(guiContainer);
			if(!Java6Helper.equals(this.guiAreas, guiAreas)){
				return true;
			}
		}

		return false;
	}

	private static void createItemButtons(GuiIngredientFastList guiItemStacks, @Nullable List<Rectangle> guiAreas, final int xStart, final int yStart, final int columnCount, final int rowCount){
		guiItemStacks.clear();

		for(int row = 0; row < rowCount; row++){
			int y = yStart + (row * itemStackHeight);
			for(int column = 0; column < columnCount; column++){
				int x = xStart + (column * itemStackWidth);
				GuiIngredientFast guiIngredientFast = new GuiIngredientFast(x, y, itemStackPadding);
				if(guiAreas != null){
					Rectangle stackArea = guiIngredientFast.getArea();
					if(intersects(guiAreas, stackArea)){
						continue;
					}
				}
				guiItemStacks.add(guiIngredientFast);
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

	public void updateLayout(){
		if(parent == null) return;
		ImmutableList<Object> ingredientList = parent.getItemFilter().getIngredientList();
		guiIngredientList.set(firstItemIndex, ingredientList);

		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

		pageNumDisplayString = (getPageNum() + 1) + "/" + getPageCount();
		int pageDisplayWidth = fontRenderer.getStringWidth(pageNumDisplayString);
		pageNumDisplayX = ((backButton.xPosition + backButton.width) + nextButton.xPosition) / 2 - (pageDisplayWidth / 2);
		pageNumDisplayY = backButton.yPosition + Math.round((backButton.height - fontRenderer.FONT_HEIGHT) / 2.0f);

		searchField.update();
	}

	private void nextPage(){
		final int itemsCount = parent.getItemFilter().size();
		if(itemsCount == 0){
			firstItemIndex = 0;
			return;
		}

		firstItemIndex += guiIngredientList.size();
		if(firstItemIndex >= itemsCount){
			firstItemIndex = 0;
		}
		updateLayout();
	}

	private void previousPage(){
		final int itemsPerPage = guiIngredientList.size();
		if(itemsPerPage == 0){
			firstItemIndex = 0;
			return;
		}
		final int itemsCount = parent.getItemFilter().size();

		int pageNum = firstItemIndex / itemsPerPage;
		if(pageNum == 0){
			pageNum = itemsCount / itemsPerPage;
		} else{
			pageNum--;
		}

		firstItemIndex = itemsPerPage * pageNum;
		if(firstItemIndex > 0 && firstItemIndex == itemsCount){
			pageNum--;
			firstItemIndex = itemsPerPage * pageNum;
		}
		updateLayout();
	}

	public void drawScreen(Minecraft minecraft, int mouseX, int mouseY){
		GlStateManager.disableLighting();

		minecraft.fontRenderer.drawString(pageNumDisplayString, pageNumDisplayX, pageNumDisplayY, Color.white.getRGB(), true);
		searchField.drawTextBox();

		nextButton.drawButton(minecraft, mouseX, mouseY);
		backButton.drawButton(minecraft, mouseX, mouseY);

		GlStateManager.disableBlend();

		if(shouldShowDeleteItemTooltip(minecraft)){
			hovered = guiIngredientList.render(minecraft, false, mouseX, mouseY);
		} else{
			boolean mouseOver = isMouseOver(mouseX, mouseY);
			hovered = guiIngredientList.render(minecraft, mouseOver, mouseX, mouseY);
		}

		Set<ItemStack> highlightedStacks = parent.getHighlightedStacks();
		if(!highlightedStacks.isEmpty()){
			StackHelper helper = Internal.getHelpers().getStackHelper();
			for(GuiIngredientFast guiItemStack : guiIngredientList.getAllGuiIngredients()){
				Object ingredient = guiItemStack.getIngredient();
				if(ingredient instanceof ItemStack){
					if(helper.containsStack(highlightedStacks, (ItemStack) ingredient) != null){
						guiItemStack.drawHighlight();
					}
				}
			}
		}

		if(hovered != null){
			hovered.drawHovered(minecraft);
		}

		GlStateManager.enableAlpha();
	}

	private boolean shouldShowDeleteItemTooltip(Minecraft minecraft){
		if(canDeleteItems && FEIConfiguration.canDeleteItems(Minecraft.getMinecraft().player)){
			EntityPlayer player = minecraft.player;
			if(!player.inventory.getItemStack().isEmpty()){
				JeiRuntime runtime = Internal.getRuntime();
				return runtime == null || !runtime.getRecipesGui().isOpen();
			}
		}
		return false;
	}

	public void drawTooltips(Minecraft minecraft, int mouseX, int mouseY){
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
		searchField.updateCursorCounter();
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY){
		if(mouseX < guiProperties.getGuiLeft() + guiProperties.getGuiXSize()){
			return isSearchBarCentered(guiProperties) && searchField.isMouseOver(mouseX, mouseY);
		}

		for(Rectangle guiArea : guiAreas){
			if(guiArea.contains(mouseX, mouseY)){
				return false;
			}
		}

		return true;
	}

	@Override
	@Nullable
	public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY){
		if(!isMouseOver(mouseX, mouseY)){
			return null;
		}

		ClickedIngredient<?> clicked = guiIngredientList.getIngredientUnderMouse(mouseX, mouseY);
		if(clicked != null){
			setKeyboardFocus(false);
			clicked.setAllowsCheating();
		}
		return clicked;
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

		JeiRuntime runtime = Internal.getRuntime();
		if(runtime == null || !runtime.getRecipesGui().isOpen()){
			if(Minecraft.getMinecraft().player.inventory.getItemStack().isEmpty()){
				ClickedIngredient<?> f = guiIngredientList.getIngredientUnderMouse(mouseX, mouseY);
				if(f != null && f.getValue() instanceof ItemStack && canGiveItems && FEIConfiguration.canGiveItems(Minecraft.getMinecraft().player)){
					ItemStack itemstack = ((ItemStack) f.getValue()).copy();
					if(mouseButton == 0) itemstack.setCount(itemstack.getMaxStackSize());
					else itemstack.setCount(1);
					ForeverEnoughItemsBase.net.sendToServer(new FEIGiveItemStackPacket(itemstack));
					return true;
				}
			} else{
				if(canDeleteItems && FEIConfiguration.canDeleteItems(Minecraft.getMinecraft().player)){
					Minecraft minecraft = Minecraft.getMinecraft();
					EntityPlayerSP player = minecraft.player;
					ItemStack itemStack = player.inventory.getItemStack();
					if(itemStack != null){
						player.inventory.setItemStack(ItemStack.EMPTY);
						PacketDeletePlayerItem packet = new PacketDeletePlayerItem(itemStack);
						JustEnoughItems.getProxy().sendPacketToServer(packet);
						return true;
					}
				} else if(Minecraft.getMinecraft().player.isCreative()){
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

	public boolean hasKeyboardFocus(){
		return searchField.isFocused();
	}

	public void setKeyboardFocus(boolean keyboardFocus){
		if(searchField != null) searchField.setFocused(keyboardFocus);
	}

	public boolean onKeyPressed(char typedChar, int keyCode){
		if(hasKeyboardFocus()){
			boolean handled = searchField.textboxKeyTyped(typedChar, keyCode);
			if(handled){
				boolean changed = Config.setFilterText(searchField.getText());
				if(changed){
					firstItemIndex = 0;
					updateLayout();
				}
			}
			return handled;
		}
		return false;
	}

	private int getItemButtonXSpace(GuiProperties guiProperties){
		return guiProperties.getScreenWidth() - (guiProperties.getGuiLeft() + guiProperties.getGuiXSize() + (2 * borderPadding));
	}

	private int getItemButtonYSpace(GuiProperties guiProperties){
		return guiProperties.getScreenHeight() - (buttonSize + (isSearchBarCentered(guiProperties) ? 0 : (searchFieldHeight + 2)) + (4 * borderPadding));
	}

	public int getColumns_(GuiProperties guiProperties){
		return getItemButtonXSpace(guiProperties) / itemStackWidth;
	}

	public int getRows_(GuiProperties guiProperties){
		return getItemButtonYSpace(guiProperties) / itemStackHeight;
	}

	private int getPageCount(){
		final int itemCount = parent.getItemFilter().size();
		final int stacksPerPage = guiIngredientList.size();
		if(stacksPerPage == 0){
			return 1;
		}
		int pageCount = MathUtil.divideCeil(itemCount, stacksPerPage);
		pageCount = Math.max(1, pageCount);
		return pageCount;
	}

	private int getPageNum(){
		final int stacksPerPage = guiIngredientList.size();
		if(stacksPerPage == 0){
			return 1;
		}
		return firstItemIndex / stacksPerPage;
	}

	public void close(){
		setKeyboardFocus(false);
		Config.saveFilterText();
	}

	@Nullable
	public ItemStack getStackUnderMouse(){
		if(hovered != null){
			Object ingredient = hovered.getIngredient();
			if(ingredient instanceof ItemStack){
				return (ItemStack) ingredient;
			}
		}
		return null;
	}

	public void setFilterText(String filterText){
		searchField.setText(filterText);
		setToFirstPage();
		updateLayout();
	}

	public static void setToFirstPage(){
		firstItemIndex = 0;
	}

	public ImmutableList<ItemStack> getVisibleStacks(){
		ImmutableList.Builder<ItemStack> visibleStacks = ImmutableList.builder();
		for(GuiIngredientFast guiItemStack : guiIngredientList.getAllGuiIngredients()){
			Object ingredient = guiItemStack.getIngredient();
			if(ingredient instanceof ItemStack){
				ItemStack itemStack = (ItemStack) ingredient;
				visibleStacks.add(itemStack);
			}
		}
		return visibleStacks.build();
	}

}
