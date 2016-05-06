package code.elix_x.mods.fei.api.utils;

import code.elix_x.mods.fei.api.utils.IFEIUtil.IFEIUtilProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public abstract class SinglePropertyFEIUtil implements IFEIUtil<SinglePropertyFEIUtil>, IFEIUtilProperty {

	protected String name;

	protected String desc;
	protected ResourceLocation texture;
	protected String text;

	public SinglePropertyFEIUtil(String name, String desc, ResourceLocation texture, String text){
		this.name = name;
		this.desc = desc;
		this.texture = texture;
		this.text = text;
	}

	public SinglePropertyFEIUtil(String name, String desc, ResourceLocation texture){
		this(name, desc, texture, null);
	}

	public SinglePropertyFEIUtil(String name, String desc, String text){
		this(name, desc, null, text);
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
	public SinglePropertyFEIUtil getCurrentProperty(){
		return this;
	}

	@Override
	public String getDesc(){
		return StatCollector.translateToLocal(desc);
	}

	@Override
	public ResourceLocation getTexture(){
		return texture;
	}

	@Override
	public String getText(){
		return StatCollector.translateToLocal(text);
	}

}
