package code.elix_x.mods.fei.client.events;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import code.elix_x.mods.fei.api.profile.Profile;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FEIProfileEvents {

	@SubscribeEvent
	public void load(WorldEvent.Load event){
		File dir = event.getWorld().getSaveHandler().getWorldDirectory();
		if(dir != null){
			File profile = new File(dir, "current.profile");
			if(profile.exists()){
				try {
					Profile.setCurrentProfile(FileUtils.readFileToString(profile));
				} catch (IOException e) {
					Profile.logger.error("Caught exception while reading current profile file: ", e);
				}
			}
		}
	}

	@SubscribeEvent
	public void save(WorldEvent.Save event){
		File dir = event.getWorld().getSaveHandler().getWorldDirectory();
		if(dir != null){
			File profile = new File(dir, "current.profile");
			try {
				profile.createNewFile();
				FileUtils.write(profile, Profile.getCurrentProfile().getName());
			} catch(IOException e){
				Profile.logger.error("Caught exception while writing current profile file: ", e);
			}
		}
	}

}
