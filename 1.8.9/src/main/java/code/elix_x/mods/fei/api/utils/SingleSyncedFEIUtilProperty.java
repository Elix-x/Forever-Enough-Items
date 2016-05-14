package code.elix_x.mods.fei.api.utils;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SingleSyncedFEIUtilProperty extends SyncedFEIUtilProperty implements IFEIUtil<SingleSyncedFEIUtilProperty> {

	protected String name;

	public SingleSyncedFEIUtilProperty(String name, String desc, ResourceLocation texture, String text){
		super(desc, texture, text);
		this.name = name;
	}

	public SingleSyncedFEIUtilProperty(String name, String desc, ResourceLocation texture){
		super(desc, texture);
		this.name = name;
	}

	public SingleSyncedFEIUtilProperty(String name, String desc, String text){
		super(desc, text);
		this.name = name;
	}

	@Override
	public String getName(){
		return name;
	}

	@Override
	public SingleSyncedFEIUtilProperty[] getAllProperties(){
		return new SingleSyncedFEIUtilProperty[]{this};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public SingleSyncedFEIUtilProperty getCurrentProperty(){
		return this;
	}

}
