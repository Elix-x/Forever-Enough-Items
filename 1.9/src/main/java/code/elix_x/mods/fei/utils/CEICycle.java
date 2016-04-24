package code.elix_x.mods.fei.utils;

import cei.ChunkEdgeRenderer;
import code.elix_x.excore.utils.reflection.AdvancedReflectionHelper.AField;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.utils.ForFEIUtil;
import code.elix_x.mods.fei.config.FEIConfiguration;
import code.elix_x.mods.fei.proxy.ClientProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class CEICycle extends ForFEIUtil<Integer> {

	public static final String[] descs = {"off", "corners", "all"};
	public static final ResourceLocation[] icons = {new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "cei_off.png"), new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "cei_corners.png"), new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "cei_all.png")};

	public static final AField<Integer> chunkEdgeState = new AField<>(ChunkEdgeRenderer.class, "chunkEdgeState").setAccessible(true);

	public CEICycle(){
		super("CEI Cycle", 0, 1, 2);
	}

	@Override
	public Integer getCurrent(){
		return chunkEdgeState.get(ClientProxy.cei);
	}

	@Override
	public String getDesc(Integer i){
		return I18n.translateToLocal("fei.gui.override.grid.utils.cei." + descs[i]);
	}

	@Override
	public boolean isEnabled(Integer i){
		return true;
	}

	@Override
	public void onSelect(Integer i){
		chunkEdgeState.set(ClientProxy.cei, i);
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
