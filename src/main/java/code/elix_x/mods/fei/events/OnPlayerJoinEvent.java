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
<<<<<<< Updated upstream
	public void join(EntityJoinWorldEvent event){
		if(event.getEntity() instanceof EntityPlayer && !event.getEntity().worldObj.isRemote){
			final EntityPlayer player = (EntityPlayer) event.getEntity();
			if(FEIConfiguration.developerMode){
				FEIPermissionsManager.setPermissionLevels(player, FEIPermissionLevel.OWNER);
			} else if(!player.worldObj.getMinecraftServer().isDedicatedServer()){
				Minecraft.getMinecraft().addScheduledTask(new Runnable(){

					@Override
					public void run(){
=======
	public void join(PlayerLoggedInEvent event){
		if(!event.player.worldObj.isRemote){
			final EntityPlayer player = event.player;
			FEIPermissionsManager.syncWith(player);
			if(FEIConfiguration.developerMode){
				FEIPermissionsManager.setPermissionLevels(player, FEIPermissionLevel.OWNER);
			} else if(!player.worldObj.getMinecraftServer().isDedicatedServer()){
				new Thread(new Runnable(){

					@Override
					public void run(){
						while(Minecraft.getMinecraft().thePlayer == null) sleep();
>>>>>>> Stashed changes
						if(EntityPlayer.getUUID(Minecraft.getMinecraft().thePlayer.getGameProfile()).equals(EntityPlayer.getUUID(player.getGameProfile()))){
							if(player.canCommandSenderUseCommand(4, "feiop")){
								FEIPermissionsManager.setPermissionLevels(player, FEIPermissionLevel.OWNER);
							}
<<<<<<< Updated upstream
						}
					}

				});
			}
			FEIPermissionsManager.syncWith(player);
=======
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
>>>>>>> Stashed changes
		}
	}

}
