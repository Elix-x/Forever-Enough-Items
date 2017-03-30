package code.elix_x.mods.fei.client.jeioverride;

import code.elix_x.excomms.color.RGBA;
import code.elix_x.excore.utils.client.gui.ElementalGuiScreen;
import code.elix_x.excore.utils.client.gui.elements.ButtonGuiElement;
import code.elix_x.excore.utils.client.gui.elements.CheckBoxGuiElement;
import code.elix_x.excore.utils.client.gui.elements.StringGuiElement;
import mezz.jei.config.Config;
import mezz.jei.config.JEIModConfigGui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.translation.I18n;

public class JEIOverrideConfigurationGui extends ElementalGuiScreen {

	private JeiReflector jeiRelfector;

	public JEIOverrideConfigurationGui(GuiScreen parent, JeiReflector jeiRelfector){
		super(parent, 256, 5 * 24);
		this.jeiRelfector = jeiRelfector;
	}

	@Override
	public void addElements(){
		add(new ButtonGuiElement("JEI", xPos, nextY, guiWidth - 4, 20, 2, 2, I18n.translateToLocal("fei.gui.override.jei.override.jei")){

			@Override
			public void onButtonPressed(){
				super.onButtonPressed();
				mc.displayGuiScreen(new JEIModConfigGui(JEIOverrideConfigurationGui.this));
			}

		});

		nextY += 2 + 20 + 2;

		StringGuiElement se;
		add(se = new StringGuiElement("Can Give Items", xPos, nextY + 12 - 8, 2, 2, I18n.translateToLocal("fei.gui.override.jei.override.cangiveitems") + " ", fontRenderer, new RGBA(1f, 1f, 1f, 1f)));
		add(new CheckBoxGuiElement("Can Give Items Check Box", xPos + se.getWidth(), nextY, 12, 12, 2, 2, jeiRelfector.canGive()){

			@Override
			public void setChecked(boolean checked){
				super.setChecked(checked);
				jeiRelfector.setCanGive(checked);
			}

		});
		nextY += 2 + 12 + 2;

		add(se = new StringGuiElement("Can Delete Items Above Items List", xPos, nextY + 12 - 8, 2, 2, I18n.translateToLocal("fei.gui.override.jei.override.candeleteitemsaboveitemslist") + " ", fontRenderer, new RGBA(1f, 1f, 1f, 1f)));
		add(new CheckBoxGuiElement("Can Delete Items Above Items List Check Box", xPos + se.getWidth(), nextY, 12, 12, 2, 2, jeiRelfector.canDeleteAboveList()){

			@Override
			public void setChecked(boolean checked){
				super.setChecked(checked);
				jeiRelfector.setCanDeleteAboveList(checked);
			}

		});
		nextY += 2 + 12 + 2;

		//TODO - Return of search field size configuration
		/*add(se = new StringGuiElement("Search Field Width", xPos, nextY + 16 - 8, 2, 2, I18n.translateToLocal("fei.gui.override.jei.override.searchfieldwidth") + " ", fontRenderer, new RGBA(1f, 1f, 1f, 1f)));
		add(new IntegralIncrementerGuiElement("Search Field Width Incrementer", xPos + se.getWidth(), nextY, 24, 8, 16, 2, 2, 1, 0, 1024, jeiRelfector.getSearchFieldWidth()){

			@Override
			public int getValue(){
				return jeiRelfector.getSearchFieldWidth();
			}

			@Override
			public void setValue(int value){
				super.setValue(value);
				jeiRelfector.setSearchFieldWidth(this.value);
			}

		});
		nextY += 2 + 16 + 2;

		add(se = new StringGuiElement("Search Field Height", xPos, nextY + 16 - 8, 2, 2, I18n.translateToLocal("fei.gui.override.jei.override.searchfieldheight") + " ", fontRenderer, new RGBA(1f, 1f, 1f, 1f)));
		add(new IntegralIncrementerGuiElement("Search Field Height Incrementer", xPos + se.getWidth(), nextY, 24, 8, 16, 2, 2, 1, 0, 1024, jeiRelfector.getSearchFieldHeight()){

			@Override
			public int getValue(){
				return jeiRelfector.getSearchFieldHeight();
			}

			@Override
			public void setValue(int value){
				super.setValue(value);
				jeiRelfector.setSearchFieldHeight(this.value);
			}

		});*/
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawBackground(0);
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(Config.isCenterSearchBarEnabled())
			new GuiTextField(0, fontRenderer, (width - jeiRelfector.getSearchFieldWidth()) / 2, height - jeiRelfector.getSearchFieldHeight() - 4, jeiRelfector.getSearchFieldWidth(), jeiRelfector.getSearchFieldHeight()).drawTextBox();
		else new GuiTextField(0, fontRenderer, width - jeiRelfector.getSearchFieldWidth() - 2, height - jeiRelfector.getSearchFieldHeight() - 4, jeiRelfector.getSearchFieldWidth(), jeiRelfector.getSearchFieldHeight()).drawTextBox();
	}

	@Override
	protected void onClose(){

	}

}
