package code.elix_x.mods.fei.events;

import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.capabilities.MagnetCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AttachMagnetCapabilityEvent {

	@SubscribeEvent
	public void attach(AttachCapabilitiesEvent.Entity event){
		if(event.getEntity() instanceof EntityPlayer)
			event.addCapability(new ResourceLocation(ForeverEnoughItemsBase.MODID, "magnet"), new ICapabilitySerializable<NBTTagByte>(){

				MagnetCapability instance = new MagnetCapability();

				@Override
				public boolean hasCapability(Capability<?> capability, EnumFacing facing){
					return capability == MagnetCapability.CAPABILITY;
				}

				@Override
				public <T> T getCapability(Capability<T> capability, EnumFacing facing){
					return capability == MagnetCapability.CAPABILITY ? (T) MagnetCapability.CAPABILITY.cast(instance) : null;
				}

				@Override
				public NBTTagByte serializeNBT(){
					return new NBTTagByte((byte) (instance.active ? 1 : 0));
				}

				@Override
				public void deserializeNBT(NBTTagByte nbt){
					instance.active = nbt.getByte() == 1;
				}

			});
	}

}
