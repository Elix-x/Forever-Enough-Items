package code.elix_x.mods.fei.client.gui;

import code.elix_x.excore.utils.client.gui.ElementalGuiScreen;
import code.elix_x.excore.utils.client.gui.elements.ListGuiElement;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.mods.fei.api.gui.FEIGuiOverride;
import code.elix_x.mods.fei.api.profile.Profile;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class ProfilesGuiScreen extends ElementalGuiScreen {

	private FEIGuiOverride fei;

	private ListGuiElement<ElementalGuiScreen> list;

	private Profile prevCurrentProfile;

	public ProfilesGuiScreen(GuiScreen parent, FEIGuiOverride fei){
		super(parent, 256, 192);
		this.fei = fei;
	}

	@Override
	public void initGui(){
		super.initGui();
		list.center();
		prevCurrentProfile = Profile.getCurrentProfile();
	}

	@Override
	protected void addElements(){
		add(list = new ListGuiElement<ElementalGuiScreen>("Profiles List", 0, 0, 256, 192, 20, 2, 2, new RGBA(0, 0, 0, 0)){

			@Override
			public void initGui(ElementalGuiScreen handler, GuiScreen gui){
				reInitElements();
				for(Profile profile : Profile.getProfiles()){
					add(new ProfileListElement(profile));
				}
				super.initGui(handler, gui);
			}

			@Override
			public void drawGuiPost(ElementalGuiScreen handler, GuiScreen gui, int mouseX, int mouseY){
				super.drawGuiPost(handler, gui, mouseX, mouseY);
				if(isShiftKeyDown() && inside(mouseX, mouseY)){
					drawTooltipWithBackgroundTranslate(fontRendererObj, mouseX, mouseY, false, true, "fei.gui.profiles.leftlick", "fei.gui.profiles.rightclick", "fei.gui.profiles.shiftleftclick", "fei.gui.profiles.shiftrightclick");
				}
			}

			class ProfileListElement extends ListElement {

				private Profile profile;

				public ProfileListElement(Profile profile){
					this.profile = profile;
				}

				@Override
				public void drawGuiPost(ElementalGuiScreen handler, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY){
					GuiButtonExt button = new GuiButtonExt(0, x, relY, width, elementY, profile.getName());
					button.enabled = profile != Profile.getCurrentProfile();
					button.drawButton(mc, mouseX, mouseY);
				}

				@Override
				public boolean handleMouseEvent(ElementalGuiScreen handler, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY, boolean down, int key){
					if(down && inside(relY, mouseX, mouseY)){
						if(key == 0){
							if(!isShiftKeyDown()){
								Profile.setCurrentProfile(profile);
								ProfilesGuiScreen.this.initGui();
								return true;
							} else {
								profile.copy();
								ProfilesGuiScreen.this.initGui();
								return true;
							}
						}
						if(key == 1){
							if(!isShiftKeyDown()){
								Profile.setCurrentProfile(profile);
								mc.displayGuiScreen(new ProfileSettingsGuiScreen(ProfilesGuiScreen.this, ProfilesGuiScreen.this.fei, profile));
								return true;
							} else {
								profile.delete();
								ProfilesGuiScreen.this.initGui();
								return true;
							}
						}
					}
					return false;
				}

			}

		});
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawBackground(0);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void reInitCurrentProfile(){
		Profile.setCurrentProfile(prevCurrentProfile);
	}
}
