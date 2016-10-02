package mezz.jei.gui;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.ingredients.GuiFluidStackGroup;
import mezz.jei.gui.ingredients.GuiIngredient;
import mezz.jei.gui.ingredients.GuiIngredientGroup;
import mezz.jei.gui.ingredients.GuiItemStackGroup;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.util.Ingredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeLayout implements IRecipeLayout {
	private static final int RECIPE_BUTTON_SIZE = 12;
	public static final int recipeTransferButtonIndex = 100;

	private final IRecipeCategory recipeCategory;
	private final GuiItemStackGroup guiItemStackGroup;
	private final GuiFluidStackGroup guiFluidStackGroup;
	private final Map<Class, GuiIngredientGroup> guiIngredientGroups;
	private final RecipeTransferButton recipeTransferButton;
	private final IRecipeWrapper recipeWrapper;
	private final IFocus<?> focus;

	private final int posX;
	private final int posY;

	public <T extends IRecipeWrapper> RecipeLayout(int index, int posX, int posY, IRecipeCategory<T> recipeCategory, T recipeWrapper, IFocus focus) {
		this.recipeCategory = recipeCategory;
		this.focus = focus;

		ItemStack itemStackFocus = null;
		FluidStack fluidStackFocus = null;
		Object focusValue = focus.getValue();
		if (focusValue instanceof ItemStack) {
			itemStackFocus = (ItemStack) focusValue;
		} else if (focusValue instanceof FluidStack) {
			fluidStackFocus = (FluidStack) focusValue;
		}
		this.guiItemStackGroup = new GuiItemStackGroup(new Focus<ItemStack>(focus.getMode(), itemStackFocus));
		this.guiFluidStackGroup = new GuiFluidStackGroup(new Focus<FluidStack>(focus.getMode(), fluidStackFocus));

		this.guiIngredientGroups = new HashMap<Class, GuiIngredientGroup>();
		this.guiIngredientGroups.put(ItemStack.class, this.guiItemStackGroup);
		this.guiIngredientGroups.put(FluidStack.class, this.guiFluidStackGroup);

		int width = recipeCategory.getBackground().getWidth();
		int height = recipeCategory.getBackground().getHeight();
		this.recipeTransferButton = new RecipeTransferButton(recipeTransferButtonIndex + index, posX + width + 2, posY + height - RECIPE_BUTTON_SIZE, RECIPE_BUTTON_SIZE, RECIPE_BUTTON_SIZE, "+");
		this.posX = posX;
		this.posY = posY;

		this.recipeWrapper = recipeWrapper;

		try {
			IIngredients ingredients = new Ingredients();
			recipeWrapper.getIngredients(ingredients);
			recipeCategory.setRecipe(this, recipeWrapper, ingredients);
		} catch (LinkageError ignored) { // legacy
			recipeCategory.setRecipe(this, recipeWrapper);
		}
	}

	public void draw(Minecraft minecraft, int mouseX, int mouseY) {
		IDrawable background = recipeCategory.getBackground();

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.enableAlpha();

		GlStateManager.pushMatrix();
		GlStateManager.translate(posX, posY, 0.0F);
		{
			background.draw(minecraft);
			recipeCategory.drawExtras(minecraft);
			recipeCategory.drawAnimations(minecraft);
			recipeWrapper.drawAnimations(minecraft, background.getWidth(), background.getHeight());
		}
		GlStateManager.popMatrix();

		final int recipeMouseX = mouseX - posX;
		final int recipeMouseY = mouseY - posY;

		GlStateManager.pushMatrix();
		GlStateManager.translate(posX, posY, 0.0F);
		{
			recipeWrapper.drawInfo(minecraft, background.getWidth(), background.getHeight(), recipeMouseX, recipeMouseY);
		}
		GlStateManager.popMatrix();

		GuiIngredient hoveredIngredient = null;
		for (GuiIngredientGroup guiIngredientGroup : guiIngredientGroups.values()) {
			GuiIngredient hovered = guiIngredientGroup.draw(minecraft, posX, posY, mouseX, mouseY);
			if (hovered != null) {
				hoveredIngredient = hovered;
			}
		}

		recipeTransferButton.drawButton(minecraft, mouseX, mouseY);
		GlStateManager.disableBlend();
		GlStateManager.disableLighting();

		if (hoveredIngredient != null) {
			hoveredIngredient.drawHovered(minecraft, posX, posY, recipeMouseX, recipeMouseY);
		} else if (isMouseOver(mouseX, mouseY)) {
			List<String> tooltipStrings = recipeWrapper.getTooltipStrings(recipeMouseX, recipeMouseY);
			if (tooltipStrings != null && !tooltipStrings.isEmpty()) {
				TooltipRenderer.drawHoveringText(minecraft, tooltipStrings, mouseX, mouseY);
			}
		}

		GlStateManager.disableAlpha();
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		final int recipeMouseX = mouseX - posX;
		final int recipeMouseY = mouseY - posY;
		final IDrawable background = recipeCategory.getBackground();
		return recipeMouseX >= 0 && recipeMouseX < background.getWidth() && recipeMouseY >= 0 && recipeMouseY < background.getHeight();
	}

	@Nullable
	public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY) {
		IClickedIngredient<?> clicked = guiItemStackGroup.getIngredientUnderMouse(posX, posY, mouseX, mouseY);
		if (clicked == null) {
			clicked = guiFluidStackGroup.getIngredientUnderMouse(posX, posY, mouseX, mouseY);
		}
		return clicked;
	}

	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return recipeWrapper.handleClick(minecraft, mouseX - posX, mouseY - posY, mouseButton);
	}

	@Override
	public GuiItemStackGroup getItemStacks() {
		return guiItemStackGroup;
	}

	@Override
	public IGuiFluidStackGroup getFluidStacks() {
		return guiFluidStackGroup;
	}

	@Override
	public <T> IGuiIngredientGroup<T> getIngredientsGroup(Class<T> ingredientClass) {
		//noinspection unchecked
		GuiIngredientGroup<T> guiIngredientGroup = guiIngredientGroups.get(ingredientClass);
		if (guiIngredientGroup == null) {
			T value = null;
			Object focusValue = this.focus.getValue();
			if (ingredientClass.isInstance(focusValue)) {
				//noinspection unchecked
				value = (T) focusValue;
			}
			IFocus<T> focus = new Focus<T>(this.focus.getMode(), value);
			guiIngredientGroup = new GuiIngredientGroup<T>(ingredientClass, focus);
			guiIngredientGroups.put(ingredientClass, guiIngredientGroup);
		}
		return guiIngredientGroup;
	}

	@Override
	public void setRecipeTransferButton(int posX, int posY) {
		recipeTransferButton.xPosition = posX + this.posX;
		recipeTransferButton.yPosition = posY + this.posY;
	}

	@Override
	public IFocus<?> getFocus() {
		return focus;
	}

	public RecipeTransferButton getRecipeTransferButton() {
		return recipeTransferButton;
	}

	public IRecipeWrapper getRecipeWrapper() {
		return recipeWrapper;
	}

	public IRecipeCategory getRecipeCategory() {
		return recipeCategory;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}
}
