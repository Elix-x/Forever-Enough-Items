package code.elix_x.mods.fei.api.utils;

import code.elix_x.mods.fei.api.client.IRenderable;
import code.elix_x.mods.fei.api.utils.IFEIUtil.IFEIUtilProperty;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SinglePropertyFEIUtil implements IFEIUtil<SinglePropertyFEIUtil>, IFEIUtilProperty {

	protected String name;

	protected String desc;
	protected IRenderable renderable;

	public SinglePropertyFEIUtil(String name, String desc, IRenderable renderable){
		this.name = name;
		this.desc = desc;
		this.renderable = renderable;
	}

	@Override
	public String getName(){
		return name;
	}

	@Override
	public SinglePropertyFEIUtil[] getAllProperties(){
		return new SinglePropertyFEIUtil[]{this};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public SinglePropertyFEIUtil getCurrentProperty(){
		return this;
	}

	@Override
	public String getDesc(){
		return I18n.translateToLocal(desc);
	}

	@Override
	public IRenderable getRenderable(){
		return renderable;
	}

}
