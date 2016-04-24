package code.elix_x.mods.fei.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class FEIGiveItemStackPacket implements IMessage {

	public ItemStack itemstack;

	public FEIGiveItemStackPacket(){

	}

	public FEIGiveItemStackPacket(ItemStack itemstack){
		this.itemstack = itemstack;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		if(buf.isReadable()) itemstack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf){
		if(itemstack != null) ByteBufUtils.writeItemStack(buf, itemstack);
	}

}
