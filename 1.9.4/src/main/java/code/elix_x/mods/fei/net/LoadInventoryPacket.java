package code.elix_x.mods.fei.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class LoadInventoryPacket implements IMessage {

	public NBTTagCompound inventory;

	public LoadInventoryPacket(){

	}

	public LoadInventoryPacket(NBTTagCompound inventory){
		this.inventory = inventory;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		inventory = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf){
		ByteBufUtils.writeTag(buf, inventory);
	}

}
