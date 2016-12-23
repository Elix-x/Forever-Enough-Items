package code.elix_x.mods.fei.events;

import code.elix_x.excore.utils.shape3d.Sphere;
import code.elix_x.mods.fei.capabilities.MagnetCapability;
import code.elix_x.mods.fei.config.FEIConfiguration;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class OnPlayerTickEvent {

	@SubscribeEvent
	public void tick(PlayerTickEvent event){
		if(event.phase == Phase.START){
			if(!event.player.isDead && event.player.getHealth() != 0 && event.player.getCapability(MagnetCapability.CAPABILITY, null).active){
				if(FEIConfiguration.magnetRadius > 0){
					EntityPlayer player = event.player;

					double maxspeedxz = 0.5D;
					double maxspeedy = 0.5D;
					double speedxz = 0.05D;
					double speedy = 0.07000000000000001D;

					for(EntityItem item : new Sphere(player.posX, player.posY, player.posZ, FEIConfiguration.magnetRadius).getAffectedEntities(player.world, EntityItem.class)){
						if(!item.cannotPickup()){
							double dx = player.posX - item.posX;
							double dy = player.posY + player.getEyeHeight() - item.posY;
							double dz = player.posZ - item.posZ;
							double absxz = Math.sqrt(dx * dx + dz * dz);
							double absy = Math.abs(dy);

							if(absxz < 1.0D){
								item.onCollideWithPlayer(player);
							} else{
								dx /= absxz;
								dz /= absxz;
							}
							if(absy > 1.0D){
								dy /= absy;
							}

							double vx = item.motionX + speedxz * dx;
							double vy = item.motionY + speedy * dy;
							double vz = item.motionZ + speedxz * dz;

							double absvxz = Math.sqrt(vx * vx + vz * vz);
							double absvy = Math.abs(vy);

							double rationspeedxz = absvxz / maxspeedxz;
							if(rationspeedxz > 1.0D){
								vx /= rationspeedxz;
								vz /= rationspeedxz;
							}
							double rationspeedy = absvy / maxspeedy;
							if(rationspeedy > 1.0D){
								vy /= rationspeedy;
							}
							item.motionX = vx;
							item.motionY = vy;
							item.motionZ = vz;
						}
					}
				}
			}
		}
	}

}
