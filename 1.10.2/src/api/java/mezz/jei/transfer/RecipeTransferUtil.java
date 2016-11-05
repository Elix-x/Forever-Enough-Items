package mezz.jei.transfer;

import javax.annotation.Nullable;

import mezz.jei.Internal;
import mezz.jei.JeiRuntime;
import mezz.jei.RecipeRegistry;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.util.Log;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class RecipeTransferUtil {
	@Nullable
	public static IRecipeTransferError getTransferRecipeError(Container container, RecipeLayout recipeLayout, EntityPlayer player) {
		return transferRecipe(container, recipeLayout, player, false, false);
	}

	public static boolean transferRecipe(Container container, RecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer) {
		IRecipeTransferError error = transferRecipe(container, recipeLayout, player, maxTransfer, true);
		return error == null;
	}

	@Nullable
	private static IRecipeTransferError transferRecipe(Container container, RecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		final JeiRuntime runtime = Internal.getRuntime();
		if (runtime == null) {
			return RecipeTransferErrorInternal.INSTANCE;
		}

		final RecipeRegistry recipeRegistry = runtime.getRecipeRegistry();
		final IRecipeTransferHandler transferHandler = recipeRegistry.getRecipeTransferHandler(container, recipeLayout.getRecipeCategory());
		if (transferHandler == null) {
			if (doTransfer) {
				Log.error("No Recipe Transfer handler for container {}", container.getClass());
			}
			return RecipeTransferErrorInternal.INSTANCE;
		}

		//noinspection unchecked
		return transferHandler.transferRecipe(container, recipeLayout, player, maxTransfer, doTransfer);
	}
}
