package code.elix_x.mods.fei.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class FEIInventoryLoadEvent extends PlayerEvent {

	public final NBTTagCompound inventory;

	public FEIInventoryLoadEvent(EntityPlayer player, NBTTagCompound inventory){
		super(player);
		this.inventory = inventory;
	}

}
