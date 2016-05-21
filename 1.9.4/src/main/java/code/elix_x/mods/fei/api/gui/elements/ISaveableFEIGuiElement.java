package code.elix_x.mods.fei.api.gui.elements;

import com.google.gson.JsonObject;

import code.elix_x.mods.fei.api.profile.Profile;

public interface ISaveableFEIGuiElement {

	public void load(Profile profile, JsonObject json);

	public JsonObject save(Profile profile);

}
