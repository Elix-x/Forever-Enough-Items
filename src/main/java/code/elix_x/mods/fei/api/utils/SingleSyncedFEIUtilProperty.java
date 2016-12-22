package code.elix_x.mods.fei.api.utils;

import code.elix_x.mods.fei.api.client.IRenderable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SingleSyncedFEIUtilProperty extends SyncedFEIUtilProperty implements IFEIUtil<SingleSyncedFEIUtilProperty> {

	protected String name;

	public SingleSyncedFEIUtilProperty(String name, String desc, IRenderable renderable){
		super(desc, renderable);
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
