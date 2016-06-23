package code.elix_x.mods.fei.api.gui.elements;

import code.elix_x.mods.fei.api.gui.FEIGuiOverride;
import net.minecraft.client.gui.GuiScreen;

public interface IConfigurableFEIGuiElement {

	public String getUnlocalizedName();

	public void openConfigGui(GuiScreen parent, FEIGuiOverride fei);

}
