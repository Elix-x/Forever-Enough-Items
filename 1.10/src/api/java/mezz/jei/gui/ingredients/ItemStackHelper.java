package mezz.jei.gui.ingredients;

import javax.annotation.Nonnull;
import java.util.Collection;

import net.minecraft.item.ItemStack;

import mezz.jei.Internal;
import mezz.jei.gui.Focus;

public class ItemStackHelper implements IIngredientHelper<ItemStack> {
	@Override
	public Collection<ItemStack> expandSubtypes(Collection<ItemStack> contained) {
		return Internal.getStackHelper().getAllSubtypes(contained);
	}

	@Override
	public ItemStack getMatch(Iterable<ItemStack> ingredients, @Nonnull Focus toMatch) {
		return Internal.getStackHelper().containsStack(ingredients, toMatch.getStack());
	}

	@Nonnull
	@Override
	public Focus createFocus(@Nonnull ItemStack ingredient) {
		return new Focus(ingredient);
	}
}
