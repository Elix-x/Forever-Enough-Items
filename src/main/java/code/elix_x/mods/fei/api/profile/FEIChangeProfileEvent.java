package code.elix_x.mods.fei.api.profile;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class FEIChangeProfileEvent extends Event {

	public final Profile currentProfile;

	public final Profile newProfile;

	public FEIChangeProfileEvent(Profile currentProfile, Profile newProfile){
		this.currentProfile = currentProfile;
		this.newProfile = newProfile;
	}

}
