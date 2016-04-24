package mezz.jei.gui;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import mezz.jei.Internal;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.IRecipeCategory;

public class Focus {
	public enum Mode {
		INPUT, OUTPUT, NONE
	}

	private final ItemStack stack;
	private final Fluid fluid;
	@Nonnull
	private Mode mode = Mode.NONE;
	private boolean allowsCheating = false;

	public Focus() {
		this.stack = null;
		this.fluid = null;
	}

	public Focus(ItemStack stack) {
		this.stack = stack;
		this.fluid = getFluidFromItemStack(stack);
	}

	public Focus(Fluid fluid) {
		this.stack = null;
		this.fluid = fluid;
	}

	@Nullable
	private static Fluid getFluidFromItemStack(@Nonnull ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof IFluidContainerItem) {
			IFluidContainerItem fluidContainerItem = (IFluidContainerItem) item;
			FluidStack fluidStack = fluidContainerItem.getFluid(stack);
			if (fluidStack == null) {
				return null;
			}
			return fluidStack.getFluid();
		} else if (FluidContainerRegistry.isFilledContainer(stack)) {
			FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
			if (fluidStack == null) {
				return null;
			}
			return fluidStack.getFluid();
		} else if (item instanceof ItemBlock) {
			ItemBlock itemBlock = (ItemBlock) item;
			Block block = itemBlock.getBlock();
			return FluidRegistry.lookupFluidForBlock(block);
		}

		return null;
	}

	public Fluid getFluid() {
		return fluid;
	}

	public ItemStack getStack() {
		return stack;
	}

	public boolean isBlank() {
		return stack == null && fluid == null;
	}

	public void setMode(@Nonnull Mode mode) {
		this.mode = mode;
	}

	@Nonnull
	public Mode getMode() {
		return mode;
	}

	public void setAllowsCheating() {
		allowsCheating = true;
	}

	public boolean allowsCheating() {
		return allowsCheating;
	}

	public boolean equalsFocus(@Nonnull Focus other) {
		return ItemStack.areItemStacksEqual(this.stack, other.getStack()) && fluid == other.getFluid() && mode == other.getMode();
	}

	@Nonnull
	public List<IRecipeCategory> getCategories() {
		IRecipeRegistry recipeRegistry = Internal.getRuntime().getRecipeRegistry();
		if (mode == Mode.INPUT) {
			return getInputCategories(recipeRegistry);
		} else if (mode == Mode.OUTPUT) {
			return getOutputCategories(recipeRegistry);
		} else {
			return recipeRegistry.getRecipeCategories();
		}
	}

	@Nonnull
	private List<IRecipeCategory> getInputCategories(@Nonnull IRecipeRegistry recipeRegistry) {
		if (stack != null && fluid != null) {
			List<IRecipeCategory> categories = new ArrayList<>(recipeRegistry.getRecipeCategoriesWithInput(stack));
			categories.addAll(recipeRegistry.getRecipeCategoriesWithInput(fluid));
			return ImmutableSet.copyOf(categories).asList();
		}
		if (stack != null) {
			return recipeRegistry.getRecipeCategoriesWithInput(stack);
		} else {
			return recipeRegistry.getRecipeCategoriesWithInput(fluid);
		}
	}

	@Nonnull
	private List<IRecipeCategory> getOutputCategories(@Nonnull IRecipeRegistry recipeRegistry) {
		if (stack != null && fluid != null) {
			List<IRecipeCategory> categories = new ArrayList<>(recipeRegistry.getRecipeCategoriesWithOutput(stack));
			categories.addAll(recipeRegistry.getRecipeCategoriesWithOutput(fluid));
			return ImmutableSet.copyOf(categories).asList();
		}
		if (stack != null) {
			return recipeRegistry.getRecipeCategoriesWithOutput(stack);
		} else {
			return recipeRegistry.getRecipeCategoriesWithOutput(fluid);
		}
	}

	@Nonnull
	public List<Object> getRecipes(@Nonnull IRecipeCategory recipeCategory) {
		IRecipeRegistry recipeRegistry = Internal.getRuntime().getRecipeRegistry();
		if (mode == Mode.INPUT) {
			return getInputRecipes(recipeRegistry, recipeCategory);
		} else if (mode == Mode.OUTPUT) {
			return getOutputRecipes(recipeRegistry, recipeCategory);
		} else {
			return recipeRegistry.getRecipes(recipeCategory);
		}
	}

	@Nonnull
	private List<Object> getInputRecipes(@Nonnull IRecipeRegistry recipeRegistry, @Nonnull IRecipeCategory recipeCategory) {
		if (stack != null && fluid != null) {
			List<Object> recipes = new ArrayList<>(recipeRegistry.getRecipesWithInput(recipeCategory, stack));
			recipes.addAll(recipeRegistry.getRecipesWithInput(recipeCategory, fluid));
			return ImmutableSet.copyOf(recipes).asList();
		}
		if (stack != null) {
			return recipeRegistry.getRecipesWithInput(recipeCategory, stack);
		} else {
			return recipeRegistry.getRecipesWithInput(recipeCategory, fluid);
		}
	}

	@Nonnull
	private List<Object> getOutputRecipes(@Nonnull IRecipeRegistry recipeRegistry, @Nonnull IRecipeCategory recipeCategory) {
		if (stack != null && fluid != null) {
			List<Object> recipes = new ArrayList<>(recipeRegistry.getRecipesWithOutput(recipeCategory, stack));
			recipes.addAll(recipeRegistry.getRecipesWithOutput(recipeCategory, fluid));
			return ImmutableSet.copyOf(recipes).asList();
		}
		if (stack != null) {
			return recipeRegistry.getRecipesWithOutput(recipeCategory, stack);
		} else {
			return recipeRegistry.getRecipesWithOutput(recipeCategory, fluid);
		}
	}
}
