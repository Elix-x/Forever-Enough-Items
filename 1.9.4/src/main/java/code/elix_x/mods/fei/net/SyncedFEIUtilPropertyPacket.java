package code.elix_x.mods.fei.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SyncedFEIUtilPropertyPacket implements IMessage {

	public int id;

	public SyncedFEIUtilPropertyPacket(){

	}

	public SyncedFEIUtilPropertyPacket(int id){
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(id);
	}

}
