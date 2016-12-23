package code.elix_x.mods.fei.client.gui;

import code.elix_x.excomms.color.RGBA;
import code.elix_x.excore.utils.client.gui.ElementalGuiScreen;
import code.elix_x.excore.utils.client.gui.elements.IGuiElement;
import code.elix_x.excore.utils.client.gui.elements.IGuiElementsHandler;
import code.elix_x.excore.utils.client.gui.elements.ItemStackButtonGuiElement;
import code.elix_x.excore.utils.client.gui.elements.ListGuiElement;
import code.elix_x.excore.utils.client.gui.elements.StringGuiElement;
import code.elix_x.excore.utils.client.gui.elements.TextFieldGuiElement;
import code.elix_x.mods.fei.api.client.gui.FEIGuiOverride;
import code.elix_x.mods.fei.api.client.gui.elements.IConfigurableFEIGuiElement;
import code.elix_x.mods.fei.api.profile.Profile;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class ProfileSettingsGuiScreen extends ElementalGuiScreen {

	private FEIGuiOverride fei;

	private Profile profile;

	private RGBA textColor = new RGBA(1f, 1f, 1f, 1f);

	private TextFieldGuiElement<ElementalGuiScreen> name;

	private ItemStackButtonGuiElement<ElementalGuiScreen> icon;

	private ListGuiElement<ElementalGuiScreen> elementsList;

	public ProfileSettingsGuiScreen(ProfilesGuiScreen parent, FEIGuiOverride fei, Profile profile){
		super(parent, 256, 180);
		this.fei = fei;
		this.profile = profile;
	}

	public void setProfileItemStack(ItemStack itemstack){
		icon.setItemStack(itemstack);
	}

	@Override
	protected void addElements(){
		int w;

		String nameSetting = I18n.translateToLocal("fei.gui.profile.settings.name") + " ";
		w = mc.fontRendererObj.getStringWidth(nameSetting);
		add(new StringGuiElement("Profile Name", xPos, nextY + 12 - 8, 2, 2, nameSetting, fontRendererObj, textColor));
		add(name = new TextFieldGuiElement("Profile Name Text Box", xPos + 2 + w, nextY, guiWidth - w - 6, 12, 2, 2, mc.fontRendererObj, name != null ? name.getCurrentText() : profile.getName()));
		nextY += 2 + 12 + 2;

		String iconSetting = I18n.translateToLocal("fei.gui.profile.settings.icon") + " ";
		w = mc.fontRendererObj.getStringWidth(iconSetting);
		add(new StringGuiElement("Profile Icon", xPos, nextY + 20 - 8, 2, 2, iconSetting, fontRendererObj, textColor));
		add(icon = new ItemStackButtonGuiElement("Profile Icon Text Box", xPos + 2 + w, yPos + 2 + 12 + 2, 20, 20, 2, 2, icon != null ? icon.getItemStack() : profile.getIcon()){

			@Override
			public void onButtonPressed(){
				super.onButtonPressed();
				mc.displayGuiScreen(new SelectProfileIconGuiScreen(ProfileSettingsGuiScreen.this, itemstack));
			}

		});
		nextY += 2 + 20 + 2;

		add(new StringGuiElement("Elements", xPos, nextY, 2, 2, I18n.translateToLocal("fei.gui.profile.settings.elements"), fontRendererObj, textColor));
		nextY += 2 + 8 + 2;

		add(elementsList = new ListGuiElement("Elements List", xPos, nextY, guiWidth, 128, 20, 2, 2, new RGBA(0, 0, 0, 0)){

			@Override
			public void initGui(IGuiElementsHandler handler, GuiScreen gui){
				reInitElements();
				for(IGuiElement<FEIGuiOverride> element : fei.getElements()){
					add(new GuiElementListElement(element));
				}
				super.initGui(handler, gui);
			}

			@Override
			public void drawGuiPost(IGuiElementsHandler handler, GuiScreen gui, int mouseX, int mouseY){
				super.drawGuiPost(handler, gui, mouseX, mouseY);
				if(inside(mouseX, mouseY) && isShiftKeyDown()){
					drawTooltipWithBackgroundTranslate(fontRendererObj, mouseX, mouseY, false, true, "fei.gui.profile.settings.elements.leftlick", "fei.gui.profile.settings.elements.rightclick");
				}
			}

			class GuiElementListElement extends ListElement {

				private IGuiElement<FEIGuiOverride> element;

				public GuiElementListElement(IGuiElement<FEIGuiOverride> element){
					this.element = element;
				}

				@Override
				public void drawGuiPost(IGuiElementsHandler handler, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY){
					GuiButtonExt button = new GuiButtonExt(0, x, relY, guiWidth - borderX - borderX, elementY, element instanceof IConfigurableFEIGuiElement ? I18n.translateToLocal(((IConfigurableFEIGuiElement) element).getUnlocalizedName()) : element.getName());
					button.enabled = fei.isEnabled(element);
					button.drawButton(mc, mouseX, mouseY);
				}

				@Override
				public boolean handleMouseEvent(IGuiElementsHandler handler, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY, boolean down, int key){
					if(down && inside(relY, mouseX, mouseY)){
						if(key == 0){
							fei.setEnabled(element, !fei.isEnabled(element));
							return true;
						}
						if(key == 1){
							if(element instanceof IConfigurableFEIGuiElement){
								((IConfigurableFEIGuiElement) element).openConfigGui(ProfileSettingsGuiScreen.this, fei);
								return true;
							}
						}
					}
					return false;
				}

			}

		});
		nextY += 2 + 128 + 2;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawBackground(0);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void onClose(){
		profile.setName(name.getCurrentText());
		profile.setIcon(icon.getItemStack());
		fei.saveToProfile(profile);
		((ProfilesGuiScreen) parent).reInitCurrentProfile();
	}

}
