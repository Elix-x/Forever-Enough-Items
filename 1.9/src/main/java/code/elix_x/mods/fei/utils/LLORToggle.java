package code.elix_x.mods.fei.utils;

import com.mmyzd.llor.LightLevelOverlayReloaded;

import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.utils.ForFEIUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class LLORToggle extends ForFEIUtil<Boolean> {

	public static final ResourceLocation off = new ResourceLocation(ForeverEnoughItemsBase.MODID, "textures/icons/llor_off.png");
	public static final ResourceLocation on = new ResourceLocation(ForeverEnoughItemsBase.MODID, "textures/icons/llor_on.png");

	public LLORToggle(){
		super("LLOR Toggle", false, true);
	}

	@Override
	public Boolean getCurrent(){
		return LightLevelOverlayReloaded.instance.active;
	}

	@Override
	public String getDesc(Boolean b){
		return I18n.translateToLocal(b ? "fei.gui.override.grid.utils.llor.on" : "fei.gui.override.grid.utils.llor.off");
	}

	@Override
	public boolean isEnabled(Boolean b){
		return true;
	}

	@Override
	public void onSelect(Boolean b){
		LightLevelOverlayReloaded.instance.active = b;
	}

	@Override
	public ResourceLocation getTexture(Boolean b){
		return b ? on : off;
	}

	@Override
	public String getText(Boolean b){
		return null;
	}

}
