package mezz.jei.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class GuiProperties {
	@Nonnull
	private final Class guiClass;
	private final int guiLeft;
	private final int guiXSize;
	private final int screenWidth;
	private final int screenHeight;

	@Nullable
	public static GuiProperties create(@Nonnull GuiScreen guiScreen) {
		final int guiLeft;
		final int guiXSize;
		if (guiScreen instanceof RecipesGui) {
			RecipesGui recipesGui = (RecipesGui) guiScreen;
			guiLeft = recipesGui.getGuiLeft();
			guiXSize = recipesGui.getXSize();
		} else if (guiScreen instanceof GuiContainer) {
			GuiContainer guiContainer = (GuiContainer) guiScreen;
			Container inventorySlots = guiContainer.inventorySlots;
			if (inventorySlots == null) {
				return null;
			}
			List<ItemStack> inventory = inventorySlots.getInventory();
			if (inventory == null || inventory.isEmpty()) {
				return null;
			}
			guiLeft = guiContainer.guiLeft;
			guiXSize = guiContainer.xSize;
		} else {
			return null;
		}

		return new GuiProperties(guiScreen.getClass(), guiLeft, guiXSize, guiScreen.width, guiScreen.height);
	}

	private GuiProperties(@Nonnull Class guiClass, int guiLeft, int guiXSize, int screenWidth, int screenHeight) {
		this.guiClass = guiClass;
		this.guiLeft = guiLeft;
		this.guiXSize = guiXSize;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	@Nonnull
	public Class getGuiClass() {
		return guiClass;
	}

	public int getGuiLeft() {
		return guiLeft;
	}

	public int getGuiXSize() {
		return guiXSize;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GuiProperties)) {
			return false;
		}
		GuiProperties other = (GuiProperties) obj;
		return guiClass == other.getGuiClass() &&
				guiLeft == other.getGuiLeft() &&
				guiXSize == other.getGuiXSize() &&
				screenWidth == other.getScreenWidth() &&
				screenHeight == other.getScreenHeight();
	}

	@Override
	public int hashCode() {
		int result = guiClass.hashCode();
		result = 31 * result + guiLeft;
		result = 31 * result + guiXSize;
		result = 31 * result + screenWidth;
		result = 31 * result + screenHeight;
		return result;
	}
}
