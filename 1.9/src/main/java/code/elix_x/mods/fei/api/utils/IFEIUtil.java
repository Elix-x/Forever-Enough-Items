package code.elix_x.mods.fei.api.utils;

import code.elix_x.mods.fei.api.utils.IFEIUtil.IFEIUtilProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IFEIUtil<U extends IFEIUtilProperty> {

	public String getName();

	public U[] getAllProperties();

	@SideOnly(Side.CLIENT)
	public U getCurrentProperty();

	public static interface IFEIUtilProperty {

		public String getDesc();

		@SideOnly(Side.CLIENT)
		public boolean isEnabled();

		@SideOnly(Side.CLIENT)
		public void onSelect();

		public ResourceLocation getTexture();

		public String getText();

	}

}
