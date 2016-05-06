package code.elix_x.mods.fei.api.utils;

import code.elix_x.mods.fei.api.utils.IFEIUtil.IFEIUtilProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public abstract class FEIUtilProperty implements IFEIUtilProperty {

	protected String desc;

	protected ResourceLocation texture;

	protected String text;

	public FEIUtilProperty(String desc, ResourceLocation texture, String text){
		this.desc = desc;
		this.texture = texture;
		this.text = text;
	}

	public FEIUtilProperty(String desc, ResourceLocation texture){
		this(desc, texture, null);
	}

	public FEIUtilProperty(String desc, String text){
		this(desc, null, text);
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
