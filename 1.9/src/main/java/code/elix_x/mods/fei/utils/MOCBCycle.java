package code.elix_x.mods.fei.utils;

import at.feldim2425.moreoverlays.chunkbounds.ChunkBoundsHandler;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.utils.ForFEIUtil;
import code.elix_x.mods.fei.config.FEIConfiguration;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class MOCBCycle extends ForFEIUtil<Integer> {

	public static final String[] descs = {"off", "corners", "all"};
	public static final ResourceLocation[] icons = {new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "chunkedge_off.png"), new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "chunkedge_corners.png"), new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "chunkedge_all.png")};

	public MOCBCycle(){
		super("CEI Cycle", 0, 1, 2);
	}

	@Override
	public Integer getCurrent(){
		return (int) ChunkBoundsHandler.mode;
	}

	@Override
	public String getDesc(Integer i){
		return I18n.translateToLocal("fei.gui.override.grid.utils.chunkedge." + descs[i]);
	}

	@Override
	public boolean isEnabled(Integer i){
		return true;
	}

	@Override
	public void onSelect(Integer i){
		ChunkBoundsHandler.mode = i.byteValue();
	}

	@Override
	public ResourceLocation getTexture(Integer i){
		return icons[i];
	}

	@Override
	public String getText(Integer i){
		return null;
	}

}
