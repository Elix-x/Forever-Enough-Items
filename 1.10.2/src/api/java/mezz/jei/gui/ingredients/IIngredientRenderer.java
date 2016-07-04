package mezz.jei.gui.ingredients;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public interface IIngredientRenderer<T> {
	void setIngredients(@Nonnull Collection<T> ingredients);

	void draw(@Nonnull Minecraft minecraft, int xPosition, int yPosition, @Nullable T value);

	@Nonnull
	List<String> getTooltip(@Nonnull Minecraft minecraft, @Nonnull T value);

	FontRenderer getFontRenderer(@Nonnull Minecraft minecraft, @Nonnull T value);
}
