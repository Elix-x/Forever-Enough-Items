package mezz.jei.gui;

import javax.annotation.Nullable;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mezz.jei.RecipeRegistry;
import mezz.jei.api.IRecipesGui;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.config.Constants;
import mezz.jei.config.KeyBindings;
import mezz.jei.gui.ingredients.GuiIngredient;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.IShowsRecipeFocuses;
import mezz.jei.input.InputHandler;
import mezz.jei.transfer.RecipeTransferUtil;
import mezz.jei.util.Log;
import mezz.jei.util.StringUtil;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.HoverChecker;
import org.lwjgl.input.Mouse;

public class RecipesGui extends GuiScreen implements IRecipesGui, IShowsRecipeFocuses {
	private static final int borderPadding = 6;
	private static final int innerPadding = 5;
	private static final int textPadding = 5;
	private static final int buttonWidth = 13;
	private static final int buttonHeight = 12;

	private int titleHeight;
	private int headerHeight;

	/* Internal logic for the gui, handles finding recipes */
	private final IRecipeGuiLogic logic;

	/* List of RecipeLayout to display */
	private final List<RecipeLayout> recipeLayouts = new ArrayList<RecipeLayout>();

	private String pageString;
	private String title;
	private ResourceLocation backgroundTexture;
	private final RecipeCategoryCraftingItemsArea recipeCategoryCraftingItemsArea;

	private HoverChecker titleHoverChecker;

	private GuiButton nextRecipeCategory;
	private GuiButton previousRecipeCategory;
	private GuiButton nextPage;
	private GuiButton previousPage;

	@Nullable
	private GuiScreen parentScreen;
	private int xSize;
	private int ySize;
	private int guiLeft;
	private int guiTop;

	public RecipesGui(RecipeRegistry recipeRegistry) {
		this.logic = new RecipeGuiLogic(recipeRegistry);
		this.recipeCategoryCraftingItemsArea = new RecipeCategoryCraftingItemsArea(recipeRegistry);
		this.mc = Minecraft.getMinecraft();
	}

	public int getGuiLeft() {
		return guiLeft;
	}

	public int getGuiTop() {
		return guiTop;
	}

	public int getXSize() {
		return xSize;
	}

	public int getYSize() {
		return ySize;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void initGui() {
		super.initGui();

		this.xSize = 176;

		ResourceLocation recipeBackgroundResource = new ResourceLocation(Constants.RESOURCE_DOMAIN, Constants.TEXTURE_RECIPE_BACKGROUND_PATH);
		if (this.height > 300) {
			this.ySize = 256;
			this.backgroundTexture = new ResourceLocation(Constants.RESOURCE_DOMAIN, Constants.TEXTURE_RECIPE_BACKGROUND_TALL_PATH);
		} else {
			this.ySize = 166;
			this.backgroundTexture = recipeBackgroundResource;
		}

		this.guiLeft = (width - this.xSize) / 2;
		this.guiTop = (height - this.ySize) / 2;

		this.titleHeight = fontRendererObj.FONT_HEIGHT + borderPadding;
		this.headerHeight = titleHeight + fontRendererObj.FONT_HEIGHT + textPadding;

		final int rightButtonX = guiLeft + xSize - borderPadding - buttonWidth;
		final int leftButtonX = guiLeft + borderPadding;

		int recipeClassButtonTop = guiTop + titleHeight - buttonHeight + 1;
		nextRecipeCategory = new GuiButtonExt(2, rightButtonX, recipeClassButtonTop, buttonWidth, buttonHeight, ">");
		previousRecipeCategory = new GuiButtonExt(3, leftButtonX, recipeClassButtonTop, buttonWidth, buttonHeight, "<");

		int pageButtonTop = guiTop + titleHeight + 3;
		nextPage = new GuiButtonExt(4, rightButtonX, pageButtonTop, buttonWidth, buttonHeight, ">");
		previousPage = new GuiButtonExt(5, leftButtonX, pageButtonTop, buttonWidth, buttonHeight, "<");

		addButtons();

		updateLayout();
	}

	private void addButtons() {
		this.buttonList.add(nextRecipeCategory);
		this.buttonList.add(previousRecipeCategory);
		this.buttonList.add(nextPage);
		this.buttonList.add(previousPage);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		GlStateManager.disableBlend();

		drawRect(guiLeft + borderPadding + buttonWidth,
				guiTop + borderPadding - 2,
				guiLeft + xSize - borderPadding - buttonWidth,
				guiTop + borderPadding + 10,
				0x30000000);
		drawRect(guiLeft + borderPadding + buttonWidth,
				guiTop + titleHeight + textPadding - 2,
				guiLeft + xSize - borderPadding - buttonWidth,
				guiTop + titleHeight + textPadding + 10,
				0x30000000);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		StringUtil.drawCenteredString(fontRendererObj, title, xSize, guiLeft, guiTop + borderPadding, Color.WHITE.getRGB(), true);
		StringUtil.drawCenteredString(fontRendererObj, pageString, xSize, guiLeft, guiTop + titleHeight + textPadding, Color.WHITE.getRGB(), true);

		nextRecipeCategory.drawButton(mc, mouseX, mouseY);
		previousRecipeCategory.drawButton(mc, mouseX, mouseY);
		nextPage.drawButton(mc, mouseX, mouseY);
		previousPage.drawButton(mc, mouseX, mouseY);

		RecipeLayout hovered = null;
		for (RecipeLayout recipeWidget : recipeLayouts) {
			if (recipeWidget.isMouseOver(mouseX, mouseY)) {
				hovered = recipeWidget;
			} else {
				recipeWidget.draw(mc, mouseX, mouseY);
			}
		}

		GuiIngredient hoveredItemStack = recipeCategoryCraftingItemsArea.draw(mc, mouseX, mouseY);

		if (hovered != null) {
			hovered.draw(mc, mouseX, mouseY);
		}
		if (hoveredItemStack != null) {
			hoveredItemStack.drawHovered(mc, 0, 0, mouseX, mouseY);
		}

		if (titleHoverChecker.checkHover(mouseX, mouseY)) {
			if (!logic.hasAllCategories()) {
				String showAllRecipesString = Translator.translateToLocal("jei.tooltip.show.all.recipes");
				TooltipRenderer.drawHoveringText(mc, showAllRecipesString, mouseX, mouseY);
			}
		}
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(backgroundTexture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.zLevel = 0;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		return mc.currentScreen == this && (mouseX >= guiLeft) && (mouseY >= guiTop) && (mouseX < guiLeft + xSize) && (mouseY < guiTop + ySize);
	}

	@Nullable
	@Override
	public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY) {
		if (isOpen()) {
			IClickedIngredient<?> clicked = recipeCategoryCraftingItemsArea.getIngredientUnderMouse(mouseX, mouseY);
			if (clicked != null) {
				return clicked;
			}

			if (isMouseOver(mouseX, mouseY)) {
				for (RecipeLayout recipeLayouts : this.recipeLayouts) {
					clicked = recipeLayouts.getIngredientUnderMouse(mouseX, mouseY);
					if (clicked != null) {
						return clicked;
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean canSetFocusWithMouse() {
		return true;
	}

	@Override
	public void handleMouseInput() throws IOException {
		final int x = Mouse.getEventX() * width / mc.displayWidth;
		final int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
		if (isMouseOver(x, y)) {
			int scrollDelta = Mouse.getEventDWheel();
			if (scrollDelta < 0) {
				logic.nextPage();
				updateLayout();
				return;
			} else if (scrollDelta > 0) {
				logic.previousPage();
				updateLayout();
				return;
			}
		}
		super.handleMouseInput();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (!isMouseOver(mouseX, mouseY)) {
			return;
		}

		if (titleHoverChecker.checkHover(mouseX, mouseY)) {
			if (logic.setCategoryFocus()) {
				updateLayout();
			}
		} else {
			for (RecipeLayout recipeLayout : recipeLayouts) {
				if (recipeLayout.handleClick(mc, mouseX, mouseY, mouseButton)) {
					return;
				}
			}
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (InputHandler.isInventoryCloseKey(keyCode) || InputHandler.isInventoryToggleKey(keyCode)) {
			close();
		} else if (KeyBindings.recipeBack.isActiveAndMatches(keyCode)) {
			back();
		}
	}

	public boolean isOpen() {
		return mc.currentScreen == this;
	}

	private void open() {
		if (!isOpen()) {
			parentScreen = mc.currentScreen;
		}
		mc.displayGuiScreen(this);
	}

	public void close() {
		if (isOpen()) {
			if (parentScreen != null) {
				mc.displayGuiScreen(parentScreen);
				parentScreen = null;
			} else {
				mc.thePlayer.closeScreen();
			}
			logic.clearHistory();
		}
	}

	@Override
	public <V> void show(@Nullable IFocus<V> focus) {
		if (focus == null) {
			Log.error("Null focus", new NullPointerException());
			return;
		}

		if (logic.setFocus(focus)) {
			open();
		}
	}

	@Override
	@Deprecated
	public void showRecipes(@Nullable ItemStack focus) {
		show(new Focus<ItemStack>(IFocus.Mode.OUTPUT, focus));
	}

	@Override
	@Deprecated
	public void showRecipes(@Nullable FluidStack focus) {
		show(new Focus<FluidStack>(IFocus.Mode.OUTPUT, focus));
	}

	@Override
	@Deprecated
	public void showUses(@Nullable ItemStack focus) {
		show(new Focus<ItemStack>(IFocus.Mode.INPUT, focus));
	}

	@Override
	@Deprecated
	public void showUses(@Nullable FluidStack focus) {
		show(new Focus<FluidStack>(IFocus.Mode.INPUT, focus));
	}

	@Override
	public void showCategories(@Nullable List<String> recipeCategoryUids) {
		if (recipeCategoryUids == null) {
			Log.error("Null recipeCategoryUids", new NullPointerException());
			return;
		}
		if (recipeCategoryUids.isEmpty()) {
			Log.error("Empty recipeCategoryUids", new IllegalArgumentException());
			return;
		}
		if (logic.setCategoryFocus(recipeCategoryUids)) {
			open();
		}
	}

	public void back() {
		if (logic.back()) {
			updateLayout();
		}
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		boolean updateLayout = true;

		if (guibutton.id == nextPage.id) {
			logic.nextPage();
		} else if (guibutton.id == previousPage.id) {
			logic.previousPage();
		} else if (guibutton.id == nextRecipeCategory.id) {
			logic.nextRecipeCategory();
		} else if (guibutton.id == previousRecipeCategory.id) {
			logic.previousRecipeCategory();
		} else if (guibutton.id >= RecipeLayout.recipeTransferButtonIndex) {
			int recipeIndex = guibutton.id - RecipeLayout.recipeTransferButtonIndex;
			RecipeLayout recipeLayout = recipeLayouts.get(recipeIndex);
			boolean maxTransfer = GuiScreen.isShiftKeyDown();
			Container container = getParentContainer();
			if (container != null && RecipeTransferUtil.transferRecipe(container, recipeLayout, mc.thePlayer, maxTransfer)) {
				close();
				updateLayout = false;
			}
		} else {
			updateLayout = false;
		}

		if (updateLayout) {
			updateLayout();
		}
	}

	private void updateLayout() {
		IRecipeCategory recipeCategory = logic.getRecipeCategory();
		if (recipeCategory == null) {
			return;
		}

		IDrawable recipeBackground = recipeCategory.getBackground();

		final int recipesPerPage = Math.max(1, (ySize - headerHeight) / (recipeBackground.getHeight() + innerPadding));
		final int recipeXOffset = guiLeft + (xSize - recipeBackground.getWidth()) / 2;
		final int recipeSpacing = (ySize - headerHeight - (recipesPerPage * recipeBackground.getHeight())) / (recipesPerPage + 1);

		logic.setRecipesPerPage(recipesPerPage);

		title = recipeCategory.getTitle();
		final int titleWidth = fontRendererObj.getStringWidth(title);
		final int titleX = guiLeft + (xSize - titleWidth) / 2;
		final int titleY = guiTop + borderPadding;
		titleHoverChecker = new HoverChecker(titleY, titleY + fontRendererObj.FONT_HEIGHT, titleX, titleX + titleWidth, 0);

		int spacingY = recipeBackground.getHeight() + recipeSpacing;

		recipeLayouts.clear();
		recipeLayouts.addAll(logic.getRecipeWidgets(recipeXOffset, guiTop + headerHeight + recipeSpacing, spacingY));
		addRecipeTransferButtons(recipeLayouts);

		nextPage.enabled = previousPage.enabled = logic.hasMultiplePages();
		nextRecipeCategory.enabled = previousRecipeCategory.enabled = logic.hasMultipleCategories();

		pageString = logic.getPageString();

		List<ItemStack> recipeCategoryCraftingItems = logic.getRecipeCategoryCraftingItems();
		recipeCategoryCraftingItemsArea.updateLayout(recipeCategoryCraftingItems, GuiProperties.create(this));
	}

	private void addRecipeTransferButtons(List<RecipeLayout> recipeLayouts) {
		buttonList.clear();
		addButtons();

		EntityPlayer player = mc.thePlayer;
		Container container = getParentContainer();

		for (RecipeLayout recipeLayout : recipeLayouts) {
			RecipeTransferButton button = recipeLayout.getRecipeTransferButton();
			button.init(container, recipeLayout, player);
			buttonList.add(button);
		}
	}

	@Nullable
	public GuiScreen getParentScreen() {
		return parentScreen;
	}

	@Nullable
	private Container getParentContainer() {
		if (parentScreen instanceof GuiContainer) {
			return ((GuiContainer) parentScreen).inventorySlots;
		}
		return null;
	}
}
