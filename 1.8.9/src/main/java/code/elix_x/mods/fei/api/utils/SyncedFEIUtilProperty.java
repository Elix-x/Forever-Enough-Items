package code.elix_x.mods.fei.api.utils;

import java.util.HashMap;
import java.util.Map;

import code.elix_x.mods.fei.api.FEIApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public abstract class SyncedFEIUtilProperty extends FEIUtilProperty {

	private static Map<Integer, SyncedFEIUtilProperty> idProperty = new HashMap<Integer, SyncedFEIUtilProperty>();

	private static int nextId = 0;

	public static void onClientSelectPacket(int id, EntityPlayer player){
		idProperty.get(id).onServerSelect(player);
	}

	private int id;

	public SyncedFEIUtilProperty(String desc, ResourceLocation texture, String text){
		super(desc, texture, text);
		idProperty.put(id = nextId++, this);
	}

	public SyncedFEIUtilProperty(String desc, ResourceLocation texture){
		super(desc, texture);
		idProperty.put(id = nextId++, this);
	}

	public SyncedFEIUtilProperty(String desc, String text){
		super(desc, text);
		idProperty.put(id = nextId++, this);
	}

	@Override
	public void onSelect(){
		FEIApi.INSTANCE.onUtilPropertySelect(id);
	}

	public abstract void onServerSelect(EntityPlayer player);

}
