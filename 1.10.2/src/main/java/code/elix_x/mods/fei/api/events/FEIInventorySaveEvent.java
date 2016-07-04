package code.elix_x.mods.fei.api.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class FEIInventorySaveEvent extends PlayerEvent {

	public final NBTTagCompound inventory;

	public FEIInventorySaveEvent(EntityPlayer player, NBTTagCompound inventory){
		super(player);
		this.inventory = inventory;
	}

}
