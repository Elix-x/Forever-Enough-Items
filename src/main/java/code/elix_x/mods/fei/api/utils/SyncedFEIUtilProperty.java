package code.elix_x.mods.fei.api.utils;

import java.util.HashMap;
import java.util.Map;

import code.elix_x.mods.fei.api.FEIApi;
import code.elix_x.mods.fei.api.client.IRenderable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SyncedFEIUtilProperty extends FEIUtilProperty {

	private static Map<Integer, SyncedFEIUtilProperty> idProperty = new HashMap<Integer, SyncedFEIUtilProperty>();

	private static int nextId = 0;

	public static void onClientSelectPacket(int id, EntityPlayer player){
		idProperty.get(id).onServerSelect(player);
	}

	private int id;

	public SyncedFEIUtilProperty(String desc, IRenderable renderable){
		super(desc, renderable);
		idProperty.put(id = nextId++, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSelect(){
		FEIApi.INSTANCE.onUtilPropertySelect(id);
	}

	public abstract void onServerSelect(EntityPlayer player);

}
