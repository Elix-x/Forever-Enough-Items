package code.elix_x.mods.fei.api.client.gui.elements;

import code.elix_x.mods.fei.api.client.gui.FEIGuiOverride;
import net.minecraft.client.gui.GuiScreen;

public interface IConfigurableFEIGuiElement {

	public String getUnlocalizedName();

	public void openConfigGui(GuiScreen parent, FEIGuiOverride fei);

}
