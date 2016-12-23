package code.elix_x.mods.fei.events;

import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.config.FEIConfiguration;
import code.elix_x.mods.fei.permission.FEIPermissionsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class OnPlayerJoinEvent {

	@SubscribeEvent
	public void join(PlayerLoggedInEvent event){
		if(!event.player.world.isRemote){
			final EntityPlayer player = event.player;
			FEIPermissionsManager.syncWith(player);
			if(FEIConfiguration.developerMode){
				FEIPermissionsManager.setPermissionLevels(player, FEIPermissionLevel.OWNER);
			} else if(!player.world.getMinecraftServer().isDedicatedServer()){
				new Thread(new Runnable(){

					@Override
					public void run(){
						while(Minecraft.getMinecraft().player == null)
							sleep();
						if(EntityPlayer.getUUID(Minecraft.getMinecraft().player.getGameProfile()).equals(EntityPlayer.getUUID(player.getGameProfile()))){
							if(player.canUseCommand(4, "feiop")){
								FEIPermissionsManager.setPermissionLevels(player, FEIPermissionLevel.OWNER);
							}
						}
					}

					private void sleep(){
						try{
							Thread.sleep(5);
						} catch(InterruptedException e){

						}
					}

				}).run();
			}
		}
	}

}
