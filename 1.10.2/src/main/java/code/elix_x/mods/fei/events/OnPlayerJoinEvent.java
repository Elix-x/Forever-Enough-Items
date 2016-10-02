package code.elix_x.mods.fei.events;

import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.config.FEIConfiguration;
import code.elix_x.mods.fei.permission.FEIPermissionsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OnPlayerJoinEvent {

	@SubscribeEvent
	public void join(EntityJoinWorldEvent event){
		if(event.getEntity() instanceof EntityPlayer && !event.getEntity().worldObj.isRemote){
			final EntityPlayer player = (EntityPlayer) event.getEntity();
			if(FEIConfiguration.developerMode){
				FEIPermissionsManager.setPermissionLevels(player, FEIPermissionLevel.OWNER);
			} else if(!player.worldObj.getMinecraftServer().isDedicatedServer()){
				Minecraft.getMinecraft().addScheduledTask(new Runnable(){

					@Override
					public void run(){
						if(EntityPlayer.getUUID(Minecraft.getMinecraft().thePlayer.getGameProfile()).equals(EntityPlayer.getUUID(player.getGameProfile()))){
							if(player.canCommandSenderUseCommand(4, "feiop")){
								FEIPermissionsManager.setPermissionLevels(player, FEIPermissionLevel.OWNER);
							}
						}
					}

				});
			}
			FEIPermissionsManager.syncWith(player);
		}
	}

}
