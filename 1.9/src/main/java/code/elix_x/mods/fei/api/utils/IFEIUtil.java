package code.elix_x.mods.fei.api.utils;

import code.elix_x.mods.fei.api.utils.IFEIUtil.IFEIUtilProperty;
import net.minecraft.util.ResourceLocation;

public interface IFEIUtil<U extends IFEIUtilProperty> {

	public String getName();

	public U[] getAllProperties();

	public U getCurrentProperty();

	public static interface IFEIUtilProperty {

		public String getDesc();

		public boolean isEnabled();

		public void onSelect();

		public ResourceLocation getTexture();

		public String getText();

	}

}
