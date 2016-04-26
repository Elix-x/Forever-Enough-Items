package code.elix_x.mods.fei.events;

import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.permission.FEIPermissionsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class OnPlayerJoinEvent {

	@SubscribeEvent
	public void join(PlayerLoggedInEvent event){
		if(!event.player.worldObj.isRemote){
			if(event.player.worldObj.getMinecraftServer() instanceof IntegratedServer){
				if(EntityPlayer.getUUID(Minecraft.getMinecraft().thePlayer.getGameProfile()).equals(EntityPlayer.getUUID(event.player.getGameProfile()))){
					if(event.player.canCommandSenderUseCommand(4, "feiop")){
						FEIPermissionsManager.setPermissionLevels(event.player, FEIPermissionLevel.OWNER);
					}
				}
			}
			FEIPermissionsManager.syncWith(event.player);
		}
	}

}
