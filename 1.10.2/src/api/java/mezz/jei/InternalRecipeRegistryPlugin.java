package mezz.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.IngredientUtil;
import mezz.jei.util.RecipeMap;

public class InternalRecipeRegistryPlugin implements IRecipeRegistryPlugin {
	private final RecipeRegistry recipeRegistry;
	private final ImmutableMultimap<String, String> categoriesForCraftItemKeys;
	private final IIngredientRegistry ingredientRegistry;
	private final ImmutableMap<String, IRecipeCategory> recipeCategoriesMap;
	private final RecipeMap recipeInputMap;
	private final RecipeMap recipeOutputMap;
	private final ListMultimap<IRecipeCategory, IRecipeWrapper> recipeWrappersForCategories;

	public InternalRecipeRegistryPlugin(RecipeRegistry recipeRegistry, ImmutableMultimap<String, String> categoriesForCraftItemKeys, IIngredientRegistry ingredientRegistry, ImmutableMap<String, IRecipeCategory> recipeCategoriesMap, RecipeMap recipeInputMap, RecipeMap recipeOutputMap, ListMultimap<IRecipeCategory, IRecipeWrapper> recipeWrappersForCategories) {
		this.recipeRegistry = recipeRegistry;
		this.categoriesForCraftItemKeys = categoriesForCraftItemKeys;
		this.ingredientRegistry = ingredientRegistry;
		this.recipeCategoriesMap = recipeCategoriesMap;
		this.recipeInputMap = recipeInputMap;
		this.recipeOutputMap = recipeOutputMap;
		this.recipeWrappersForCategories = recipeWrappersForCategories;
	}

	@Override
	public <V> List<String> getRecipeCategoryUids(IFocus<V> focus) {
		if (focus.getMode() == IFocus.Mode.NONE) {
			return getRecipeCategories();
		}

		V ingredient = focus.getValue();
		if (ingredient == null) {
			return getRecipeCategories();
		}

		switch (focus.getMode()) {
			case INPUT:
				return recipeInputMap.getRecipeCategories(ingredient);
			case OUTPUT:
				return recipeOutputMap.getRecipeCategories(ingredient);
			default:
				return getRecipeCategories();
		}
	}

	private ImmutableList<String> getRecipeCategories() {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		for (Map.Entry<String, IRecipeCategory> entry : recipeCategoriesMap.entrySet()) {
			IRecipeCategory recipeCategory = entry.getValue();
			if (!recipeRegistry.getRecipeWrappers(recipeCategory).isEmpty()) {
				builder.add(entry.getKey());
			}
		}
		return builder.build();
	}

	@Override
	public <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
		if (focus.getMode() == IFocus.Mode.NONE) {
			return getRecipeWrappers(recipeCategory);
		}

		V ingredient = focus.getValue();
		if (ingredient == null) {
			return getRecipeWrappers(recipeCategory);
		}

		IIngredientHelper<V> ingredientHelper = ingredientRegistry.getIngredientHelper(ingredient);

		if (focus.getMode() == IFocus.Mode.INPUT) {
			final ImmutableList<T> recipes = recipeInputMap.getRecipeWrappers(recipeCategory, ingredient);

			String recipeCategoryUid = recipeCategory.getUid();
			for (String inputKey : IngredientUtil.getUniqueIdsWithWildcard(ingredientHelper, ingredient)) {
				if (categoriesForCraftItemKeys.get(inputKey).contains(recipeCategoryUid)) {
					ImmutableSet<T> specificRecipes = ImmutableSet.copyOf(recipes);
					//noinspection unchecked
					List<T> recipesForCategory = (List<T>) recipeWrappersForCategories.get(recipeCategory);
					List<T> allRecipes = new ArrayList<T>(recipes);
					for (T recipe : recipesForCategory) {
						if (!specificRecipes.contains(recipe)) {
							allRecipes.add(recipe);
						}
					}
					return allRecipes;
				}
			}

			return recipes;
		} else if (focus.getMode() == IFocus.Mode.OUTPUT) {
			return recipeOutputMap.getRecipeWrappers(recipeCategory, ingredient);
		} else {
			return getRecipeWrappers(recipeCategory);
		}
	}

	private <T extends IRecipeWrapper> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory) {
		//noinspection unchecked
		List<T> recipeWrappers = (List<T>) recipeWrappersForCategories.get(recipeCategory);
		return Collections.unmodifiableList(recipeWrappers);
	}
}
