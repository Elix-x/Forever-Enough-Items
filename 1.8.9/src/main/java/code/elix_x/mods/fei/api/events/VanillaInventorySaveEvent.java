package code.elix_x.mods.fei.api.events;

import code.elix_x.mods.fei.events.FEIInventoryLoadEvent;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VanillaInventorySaveEvent {

	@SubscribeEvent
	public void save(FEIInventorySaveEvent event){
		event.inventory.setTag("vanilla", event.entityPlayer.inventory.writeToNBT(new NBTTagList()));
	}

	@SubscribeEvent
	public void load(FEIInventoryLoadEvent event){
		event.entityPlayer.inventory.readFromNBT((NBTTagList) event.inventory.getTag("vanilla"));
	}

}
