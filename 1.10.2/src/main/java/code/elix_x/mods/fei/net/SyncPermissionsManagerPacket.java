package code.elix_x.mods.fei.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SyncPermissionsManagerPacket implements IMessage {

	public NBTTagCompound nbt;

	public SyncPermissionsManagerPacket(){

	}

	public SyncPermissionsManagerPacket(NBTTagCompound nbt){
		this.nbt = nbt;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf){
		ByteBufUtils.writeTag(buf, nbt);
	}

}
