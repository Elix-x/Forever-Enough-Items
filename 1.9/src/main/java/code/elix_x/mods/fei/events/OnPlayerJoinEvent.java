package code.elix_x.mods.fei.events;

import code.elix_x.mods.fei.permission.FEIPermissionsManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class OnPlayerJoinEvent {

	@SubscribeEvent
	public void join(PlayerLoggedInEvent event){
		if(!event.player.worldObj.isRemote) FEIPermissionsManager.syncWith(event.player);
	}

}
