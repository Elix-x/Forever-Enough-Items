package code.elix_x.mods.fei.utils;

import code.elix_x.excore.utils.reflection.AdvancedReflectionHelper.AField;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.client.IRenderable;
import code.elix_x.mods.fei.api.client.IRenderable.ResourceLocationRenderable;
import code.elix_x.mods.fei.api.utils.ForFEIUtil;
import code.elix_x.mods.fei.config.FEIConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ChunkBordersFEIUtil extends ForFEIUtil<Boolean> {

	public static final AField<DebugRenderer, Boolean> field_190079_e = new AField<DebugRenderer, Boolean>(DebugRenderer.class, "field_190079_e").setAccessible(true);

	public static final String[] descs = {"off", "all"};
	public static final ResourceLocation[] icons = {new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "chunkborders_off.png"), new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "chunkborders_all.png")};

	public ChunkBordersFEIUtil(){
		super("CEI Cycle", false, true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Boolean getCurrent(){
		return field_190079_e.get(Minecraft.getMinecraft().debugRenderer);
	}

	@Override
	public String getDesc(Boolean b){
		return I18n.translateToLocal("fei.gui.override.grid.utils.chunkborders." + (b ? "all" : "off"));
	}

	@Override
	public boolean isEnabled(Boolean b){
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSelect(Boolean b){
		field_190079_e.set(Minecraft.getMinecraft().debugRenderer, b);
	}

	@Override
	public IRenderable getRenderable(Boolean b){
		return new ResourceLocationRenderable(icons[b ? 1 : 0]);
	}

}
