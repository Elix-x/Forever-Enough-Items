package code.elix_x.mods.fei.api.utils;

import code.elix_x.mods.fei.api.client.IRenderable;
import code.elix_x.mods.fei.api.utils.IFEIUtil.IFEIUtilProperty;
import net.minecraft.util.text.translation.I18n;

public abstract class FEIUtilProperty implements IFEIUtilProperty {

	protected String desc;

	protected IRenderable renderable;

	public FEIUtilProperty(String desc, IRenderable renderable){
		this.desc = desc;
		this.renderable = renderable;
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
