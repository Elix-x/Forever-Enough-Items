package code.elix_x.mods.fei.api.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import code.elix_x.excore.utils.client.gui.elements.IGuiElement;
import code.elix_x.excore.utils.client.gui.elements.IGuiElementsHandler;
import code.elix_x.mods.fei.api.gui.elements.INotDisableableFEIGuiElement;
import code.elix_x.mods.fei.api.gui.elements.ISaveableFEIGuiElement;
import code.elix_x.mods.fei.api.profile.Profile;
import mezz.jei.Internal;
import mezz.jei.gui.ItemListOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class FEIGuiOverride implements IGuiElementsHandler<IGuiElement<FEIGuiOverride>> {

	private static FEIGuiOverride instance = new FEIGuiOverride();

	public static FEIGuiOverride instance(){
		return instance;
	}

	public static void addElement(IGuiElement<FEIGuiOverride> element){
		instance.add(element);
	}

	public static void changeProfile(Profile current, Profile neww){
		if(current != null) instance.saveToProfile(current);
		instance.loadFromProfile(neww);
	}

	public static void loadFromCurrentProfile(){
		instance.loadFromProfile(Profile.getCurrentProfile());
	}

	public static void saveToCurrentProfile(){
		instance.saveToProfile(Profile.getCurrentProfile());
	}

	private Map<String, IGuiElement<FEIGuiOverride>> elementsMap = new HashMap<String, IGuiElement<FEIGuiOverride>>();
	private Map<IGuiElement<FEIGuiOverride>, Boolean> elementsEnabledMap = new HashMap<IGuiElement<FEIGuiOverride>, Boolean>();
	private List<IGuiElement<FEIGuiOverride>> elements = new ArrayList<IGuiElement<FEIGuiOverride>>();

	private IGuiElement<FEIGuiOverride> focused;

	private FEIGuiOverride(){

	}

	public ItemListOverlay getItemListOverlay(){
		return Internal.getRuntime().getItemListOverlay();
	}

	public List<IGuiElement<FEIGuiOverride>> getElements(){
		return elements;
	}

	@Override
	public void add(IGuiElement<FEIGuiOverride> element){
		instance.elementsMap.put(element.getName(), element);
		instance.elements.add(element);
	}

	public IGuiElement<FEIGuiOverride> getFocused(){
		return focused;
	}

	public void setFocused(IGuiElement<FEIGuiOverride> focused){
		this.focused = focused;
	}

	public void looseFocus(){
		setFocused(null);
	}

	public boolean isEnabled(IGuiElement element){
		return element instanceof INotDisableableFEIGuiElement || (elementsEnabledMap.containsKey(element) && elementsEnabledMap.get(element));
	}

	public void setEnabled(IGuiElement element, boolean enabled){
		if(!(element instanceof INotDisableableFEIGuiElement)) elementsEnabledMap.put(element, enabled);
	}

	public void enable(IGuiElement element){
		setEnabled(element, true);
	}

	public void disable(IGuiElement element){
		setEnabled(element, false);
	}

	public void loadFromProfile(Profile profile){
		JsonObject json = profile.getData();
		JsonArray arr = json.get("elements").getAsJsonArray();
		for(JsonElement el : arr){
			JsonObject o = el.getAsJsonObject();
			IGuiElement element = elementsMap.get(o.get("name").getAsString());
			if(element != null){
				if(!(element instanceof INotDisableableFEIGuiElement)) setEnabled(element, o.get("enabled").getAsBoolean());
				if(element instanceof ISaveableFEIGuiElement) ((ISaveableFEIGuiElement) element).load(profile, o.get("data").getAsJsonObject());
			}
		}
	}

	public void saveToProfile(Profile profile){
		JsonObject json = profile.getData();
		JsonArray arr = new JsonArray();
		for(IGuiElement element : elements){
			JsonObject o = new JsonObject();
			o.addProperty("name", element.getName());
			if(!(element instanceof INotDisableableFEIGuiElement)) o.addProperty("enabled", isEnabled(element));
			if(element instanceof ISaveableFEIGuiElement) o.add("data", ((ISaveableFEIGuiElement) element).save(profile));
			arr.add(o);
		}
		json.add("elements", arr);
	}

	public void openGui(GuiScreen gui){
		for(IGuiElement element : elements){
			element.openGui(this, gui);
		}
	}

	public void initGuiPre(GuiScreen gui){
		for(IGuiElement element : elements){
			element.initGui(this, gui);
		}
	}

	public void initGuiPost(GuiScreen gui){

	}

	public void drawGuiPre(GuiScreen gui, int mouseX, int mouseY){
		for(IGuiElement<FEIGuiOverride> element : elements){
			if(element != focused && isEnabled(element)) element.drawGuiPre(this, gui, mouseX, mouseY);
		}
		if(focused != null) focused.drawGuiPre(this, gui, mouseX, mouseY);
	}

	public void drawBackground(GuiScreen gui, int mouseX, int mouseY){
		for(IGuiElement<FEIGuiOverride> element : elements){
			if(element != focused && isEnabled(element)) element.drawBackground(this, gui, mouseX, mouseY);
		}
		if(focused != null) focused.drawBackground(this, gui, mouseX, mouseY);
	}

	public void drawGuiPost(GuiScreen gui, int mouseX, int mouseY){
		for(IGuiElement<FEIGuiOverride> element : elements){
			if(element != focused && isEnabled(element)) element.drawGuiPost(this, gui, mouseX, mouseY);
		}
		if(focused != null) focused.drawGuiPost(this, gui, mouseX, mouseY);

		for(IGuiElement<FEIGuiOverride> element : elements){
			if(element != focused && isEnabled(element)) element.drawGuiPostPost(this, gui, mouseX, mouseY);
		}
		if(focused != null) focused.drawGuiPostPost(this, gui, mouseX, mouseY);
	}

	public boolean handleKeyboardEvent(GuiScreen gui){
		if(focused != null && focused.handleKeyboardEvent(this, gui, Keyboard.getEventKeyState(), Keyboard.getEventKey(), Keyboard.getEventCharacter())) return true;
		for(IGuiElement<FEIGuiOverride> element : elements){
			if(element != focused && isEnabled(element) && element.handleKeyboardEvent(this, gui, Keyboard.getEventKeyState(), Keyboard.getEventKey(), Keyboard.getEventCharacter())){
				return true;
			}
		}
		return false;
	}

	public boolean handleMouseEvent(GuiScreen gui){
		if(Mouse.getEventButton() > -1){
			ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
			int mouseX = Mouse.getX() * res.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
			int mouseY = res.getScaledHeight() - Mouse.getY() * res.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;

			if(focused != null && focused.handleMouseEvent(this, gui, mouseX, mouseY, Mouse.getEventButtonState(), Mouse.getEventButton())) return true;
			for(IGuiElement<FEIGuiOverride> element : elements){
				if(element != focused && isEnabled(element) && element.handleMouseEvent(this, gui, mouseX, mouseY, Mouse.getEventButtonState(), Mouse.getEventButton())){
					return true;
				}
			}
		} else if(Mouse.getEventDWheel() != 0){
			ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
			int mouseX = Mouse.getX() * res.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
			int mouseY = res.getScaledHeight() - Mouse.getY() * res.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;

			if(focused != null && focused.handleMouseEvent(this, gui, mouseX, mouseY, Mouse.getEventDWheel())) return true;
			for(IGuiElement<FEIGuiOverride> element : elements){
				if(element != focused && isEnabled(element) && element.handleMouseEvent(this, gui, mouseX, mouseY, Mouse.getEventDWheel())){
					return true;
				}
			}
		}
		return false;	
	}

}
