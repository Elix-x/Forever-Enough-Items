package code.elix_x.mods.fei.client.events;

import code.elix_x.mods.fei.api.gui.FEIGuiOverride;
import code.elix_x.mods.fei.api.profile.FEIChangeProfileEvent;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent.Post;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FEIGuiOverrideEvents {

	@SubscribeEvent
	public void changeProfile(FEIChangeProfileEvent event){
		FEIGuiOverride.changeProfile(event.currentProfile, event.newProfile);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void openGui(GuiOpenEvent event){
		if(event.getGui() instanceof GuiContainer || event.getGui() instanceof RecipesGui){
			FEIGuiOverride.instance().openGui(event.getGui());
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void initGuiPre(GuiScreenEvent.InitGuiEvent.Post event){
		if(event.getGui() instanceof GuiContainer || event.getGui() instanceof RecipesGui){
			FEIGuiOverride.instance().initGuiPre(event.getGui());
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void initGuiPost(GuiScreenEvent.InitGuiEvent.Post event){
		if(event.getGui() instanceof GuiContainer || event.getGui() instanceof RecipesGui){
			FEIGuiOverride.instance().initGuiPost(event.getGui());
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void drawBackground(GuiScreenEvent.BackgroundDrawnEvent event){
		if(event.getGui() instanceof GuiContainer || event.getGui() instanceof RecipesGui){
			FEIGuiOverride.instance().drawBackground(event.getGui(), event.getMouseX(), event.getMouseY());
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void drawScreen(GuiScreenEvent.DrawScreenEvent event){
		if(event.getGui() instanceof GuiContainer || event.getGui() instanceof RecipesGui){
			if(event instanceof Pre){
				FEIGuiOverride.instance().drawGuiPre(event.getGui(), event.getMouseX(), event.getMouseY());
			} else if(event instanceof Post){
				FEIGuiOverride.instance().drawGuiPost(event.getGui(), event.getMouseX(), event.getMouseY());
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void guiKeyboardEvent(GuiScreenEvent.KeyboardInputEvent.Pre event){
		if(event.getGui() instanceof GuiContainer || event.getGui() instanceof RecipesGui){
			if(FEIGuiOverride.instance().handleKeyboardEvent(event.getGui())){
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void guiMouseEvent(GuiScreenEvent.MouseInputEvent.Pre event){
		if(event.getGui() instanceof GuiContainer || event.getGui() instanceof RecipesGui){
			if(FEIGuiOverride.instance().handleMouseEvent(event.getGui())){
				event.setCanceled(true);
			}
		}
	}

}
