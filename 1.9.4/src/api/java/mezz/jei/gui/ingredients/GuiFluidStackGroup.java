package mezz.jei.gui.ingredients;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;

public class GuiFluidStackGroup extends GuiIngredientGroup<FluidStack, GuiIngredient<FluidStack>> implements IGuiFluidStackGroup {
	private static final FluidStackHelper helper = new FluidStackHelper();

	@Override
	public void init(int slotIndex, boolean input, int xPosition, int yPosition, int width, int height, int capacityMb, boolean showCapacity, @Nullable IDrawable overlay) {
		FluidStackRenderer renderer = new FluidStackRenderer(capacityMb, showCapacity, width, height, overlay);
		GuiIngredient<FluidStack> guiIngredient = new GuiIngredient<>(renderer, helper, slotIndex, input, xPosition, yPosition, width, height, 0, itemCycleOffset);
		guiIngredients.put(slotIndex, guiIngredient);
	}
}
