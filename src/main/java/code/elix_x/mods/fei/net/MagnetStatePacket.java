package code.elix_x.mods.fei.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MagnetStatePacket implements IMessage {

	public boolean on;

	public MagnetStatePacket(){
	}

	public MagnetStatePacket(boolean on){
		this.on = on;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		on = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf){
		buf.writeBoolean(on);
	}

}
