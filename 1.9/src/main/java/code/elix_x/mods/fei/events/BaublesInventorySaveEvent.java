package code.elix_x.mods.fei.events;

import baubles.api.BaublesApi;
import code.elix_x.mods.fei.api.events.FEIInventorySaveEvent;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BaublesInventorySaveEvent {

	@SubscribeEvent
	public void save(FEIInventorySaveEvent event){
		NBTTagCompound nbt = new NBTTagCompound();
		IInventory baubles = BaublesApi.getBaubles(event.getEntityPlayer());
		for(int i = 0; i < baubles.getSizeInventory(); i++){
			NBTTagCompound tag = new NBTTagCompound();
			if(baubles.getStackInSlot(i) != null) baubles.getStackInSlot(i).writeToNBT(tag);
			nbt.setTag("Slot " + i, tag);
		}
		event.inventory.setTag("Baubles", nbt);
	}

	@SubscribeEvent
	public void load(FEIInventoryLoadEvent event){
		NBTTagCompound nbt = event.inventory.getCompoundTag("Baubles");
		if(nbt != null){
			IInventory baubles = BaublesApi.getBaubles(event.getEntityPlayer());
			for(int i = 0; i < baubles.getSizeInventory(); i++){
				NBTTagCompound tag = nbt.getCompoundTag("Slot " + i);
				if(tag != null){
					if(tag.getSize() > 0) baubles.setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(tag));
					else baubles.setInventorySlotContents(i, null);
				}
			}
		}
	}

}
