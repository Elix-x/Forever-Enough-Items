package mezz.jei.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import com.google.common.collect.ImmutableList;
import mezz.jei.Internal;
import mezz.jei.RecipeRegistry;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Log;
import mezz.jei.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class RecipeGuiLogic implements IRecipeGuiLogic {
	private static class State {
		/** The focus of this GUI */
		@Nonnull
		public final Focus focus;
		/** List of Recipe Categories that involve the focus */
		@Nonnull
		public ImmutableList<IRecipeCategory> recipeCategories;
		public int recipeCategoryIndex;
		public int pageIndex;
		public int recipesPerPage;

		public State(@Nonnull Focus focus, @Nonnull List<IRecipeCategory> recipeCategories, int recipeCategoryIndex, int pageIndex) {
			this.focus = focus;
			this.recipeCategories = ImmutableList.copyOf(recipeCategories);
			this.recipeCategoryIndex = recipeCategoryIndex;
			this.pageIndex = pageIndex;
		}
	}

	/** The current state of this GUI */
	@Nullable
	private State state = null;

	/** The previous states of this GUI */
	@Nonnull
	private final Stack<State> history = new Stack<>();

	/** List of recipes for the currently selected recipeClass */
	@Nonnull
	private List<Object> recipes = Collections.emptyList();

	/**
	 * List of items that can craft recipes from the current recipe category
	 *
	 * @see IModRegistry#addRecipeCategoryCraftingItem(ItemStack, String...)
	 */
	@Nonnull
	private Collection<ItemStack> recipeCategoryCraftingItems = Collections.emptyList();

	@Override
	public boolean setFocus(@Nonnull Focus focus) {
		return setFocus(focus, true);
	}

	@Override
	public boolean back() {
		if (history.empty()) {
			return false;
		}
		final State state = history.pop();
		setState(state);
		return true;
	}

	@Override
	public void clearHistory() {
		while (!history.empty()) {
			history.pop();
		}
	}

	private boolean setFocus(@Nonnull Focus focus, boolean saveHistory) {
		if (this.state != null && this.state.focus.equalsFocus(focus)) {
			return true;
		}

		final List<IRecipeCategory> recipeCategories = focus.getCategories();
		if (recipeCategories.isEmpty()) {
			return false;
		}

		final int recipeCategoryIndex = getRecipeCategoryIndex(recipeCategories);

		if (this.state != null && saveHistory) {
			history.push(this.state);
		}

		final State state = new State(focus, recipeCategories, recipeCategoryIndex, 0);
		setState(state);

		return true;
	}

	private void setState(@Nonnull State state) {
		this.state = state;
		updateRecipes();
	}

	private static int getRecipeCategoryIndex(@Nonnull List<IRecipeCategory> recipeCategories) {
		final Container container = Minecraft.getMinecraft().thePlayer.openContainer;
		if (container == null) {
			return 0;
		}

		final RecipeRegistry recipeRegistry = Internal.getRuntime().getRecipeRegistry();
		for (int i = 0; i < recipeCategories.size(); i++) {
			IRecipeCategory recipeCategory = recipeCategories.get(i);
			if (recipeRegistry.getRecipeTransferHandler(container, recipeCategory) != null) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public boolean setCategoryFocus() {
		IRecipeCategory recipeCategory = getRecipeCategory();
		if (recipeCategory == null) {
			return false;
		}

		if (this.state != null) {
			history.push(this.state);
		}

		final List<IRecipeCategory> recipeCategories = Internal.getRuntime().getRecipeRegistry().getRecipeCategories();
		final int recipeCategoryIndex = recipeCategories.indexOf(recipeCategory);
		final State state = new State(new Focus(), recipeCategories, recipeCategoryIndex, 0);
		setState(state);

		return true;
	}

	@Override
	public boolean setCategoryFocus(List<String> recipeCategoryUids) {
		List<IRecipeCategory> recipeCategories = Internal.getRuntime().getRecipeRegistry().getRecipeCategories(recipeCategoryUids);
		if (recipeCategories.isEmpty()) {
			return false;
		}

		if (this.state != null) {
			history.push(this.state);
		}

		final State state = new State(new Focus(), recipeCategories, 0, 0);
		setState(state);

		return true;
	}

	@Override
	public Focus getFocus() {
		if (state == null) {
			return null;
		}
		return state.focus;
	}

	@Override
	@Nonnull
	public Collection<ItemStack> getRecipeCategoryCraftingItems() {
		return recipeCategoryCraftingItems;
	}

	@Override
	public void setRecipesPerPage(int recipesPerPage) {
		if (state == null) {
			return;
		}
		if (state.recipesPerPage != recipesPerPage) {
			int recipeIndex = state.pageIndex * state.recipesPerPage;
			state.pageIndex = recipeIndex / recipesPerPage;

			state.recipesPerPage = recipesPerPage;
			updateRecipes();
		}
	}
	
	private void updateRecipes() {
		if (state == null) {
			return;
		}

		final IRecipeCategory recipeCategory = getRecipeCategory();
		if (recipeCategory == null) {
			recipes = Collections.emptyList();
			recipeCategoryCraftingItems = Collections.emptyList();
		} else {
			Focus focus = state.focus;
			recipes = focus.getRecipes(recipeCategory);
			recipeCategoryCraftingItems = focus.getRecipeCategoryCraftingItems(recipeCategory);
		}
	}

	@Override
	@Nullable
	public IRecipeCategory getRecipeCategory() {
		if (state == null || state.recipeCategories.size() == 0) {
			return null;
		}
		return state.recipeCategories.get(state.recipeCategoryIndex);
	}

	@Override
	@Nonnull
	public List<RecipeLayout> getRecipeWidgets(int posX, int posY, int spacingY) {
		if (state == null) {
			return Collections.emptyList();
		}

		List<RecipeLayout> recipeWidgets = new ArrayList<>();

		IRecipeCategory recipeCategory = getRecipeCategory();
		if (recipeCategory == null) {
			return recipeWidgets;
		}

		IRecipeRegistry recipeRegistry = Internal.getRuntime().getRecipeRegistry();

		int recipeWidgetIndex = 0;
		for (int recipeIndex = state.pageIndex * state.recipesPerPage; recipeIndex < recipes.size() && recipeWidgets.size() < state.recipesPerPage; recipeIndex++) {
			Object recipe = recipes.get(recipeIndex);
			IRecipeWrapper recipeWrapper = getRecipeWrapper(recipeRegistry, recipe, recipe.getClass());
			if (recipeWrapper == null) {
				continue;
			}

			RecipeLayout recipeWidget = new RecipeLayout(recipeWidgetIndex++, posX, posY, recipeCategory, recipeWrapper, state.focus);
			recipeWidgets.add(recipeWidget);

			posY += spacingY;
		}

		return recipeWidgets;
	}

	@Nullable
	private <T> IRecipeWrapper getRecipeWrapper(IRecipeRegistry recipeRegistry, T recipe, Class<? extends T> recipeClass) {
		IRecipeHandler<T> recipeHandler = recipeRegistry.getRecipeHandler(recipeClass);
		if (recipeHandler == null) {
			Log.error("Couldn't find recipe handler for recipe: {}", recipe);
			return null;
		}

		return recipeHandler.getRecipeWrapper(recipe);
	}

	@Override
	public void nextRecipeCategory() {
		if (state == null) {
			return;
		}
		final int recipesTypesCount = state.recipeCategories.size();
		state.recipeCategoryIndex = (state.recipeCategoryIndex + 1) % recipesTypesCount;
		state.pageIndex = 0;
		updateRecipes();
	}

	@Override
	public boolean hasMultiplePages() {
		return state != null && recipes.size() > state.recipesPerPage;
	}

	@Override
	public void previousRecipeCategory() {
		if (state == null) {
			return;
		}
		final int recipesTypesCount = state.recipeCategories.size();
		state.recipeCategoryIndex = (recipesTypesCount + state.recipeCategoryIndex - 1) % recipesTypesCount;
		state.pageIndex = 0;
		updateRecipes();
	}

	@Override
	public void nextPage() {
		if (state == null) {
			return;
		}
		int pageCount = pageCount(state.recipesPerPage);
		state.pageIndex = (state.pageIndex + 1) % pageCount;
		updateRecipes();
	}

	@Override
	public void previousPage() {
		if (state == null) {
			return;
		}
		int pageCount = pageCount(state.recipesPerPage);
		state.pageIndex = (pageCount + state.pageIndex - 1) % pageCount;
		updateRecipes();
	}

	private int pageCount(int recipesPerPage) {
		if (recipes.size() <= 1) {
			return 1;
		}

		return MathUtil.divideCeil(recipes.size(), recipesPerPage);
	}

	@Override
	@Nonnull
	public String getPageString() {
		if (state == null) {
			return "1/1";
		}
		return (state.pageIndex + 1) + "/" + pageCount(state.recipesPerPage);
	}

	@Override
	public boolean hasMultipleCategories() {
		return state != null && state.recipeCategories.size() > 1;
	}

	@Override
	public boolean hasAllCategories() {
		return state != null && state.recipeCategories.size() == Internal.getRuntime().getRecipeRegistry().getRecipeCategories().size();
	}
}
