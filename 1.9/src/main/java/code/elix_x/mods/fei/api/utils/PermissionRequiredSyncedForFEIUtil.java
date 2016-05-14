package code.elix_x.mods.fei.api.utils;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Iterables;

import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.api.utils.PermissionRequiredSyncedForFEIUtil.PermissionRequiredSyncedCirculatingFEIUtilProperty;
import code.elix_x.mods.fei.permission.FEIPermissionsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class PermissionRequiredSyncedForFEIUtil<T> extends FEIUtil<PermissionRequiredSyncedCirculatingFEIUtilProperty> {

	protected T[] ts;

	public PermissionRequiredSyncedForFEIUtil(String name, T... ts){
		super(name);
		this.ts = ts;
		for(T t : this.ts){
			properties = (PermissionRequiredSyncedCirculatingFEIUtilProperty[]) ArrayUtils.add(properties, new PermissionRequiredSyncedCirculatingFEIUtilProperty(getDesc(t), getTexture(t), getText(t), getPermissionLevel(t), t));
		}
	}

	public PermissionRequiredSyncedForFEIUtil(String name, Class<T> claz, Iterable<T> ts){
		this(name, Iterables.toArray(ts, claz));
	}

	public PermissionRequiredSyncedForFEIUtil(String name, Class<? extends Enum> claz){
		this(name, (T[]) claz.getEnumConstants());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PermissionRequiredSyncedCirculatingFEIUtilProperty getCurrentProperty(){
		return properties[ArrayUtils.indexOf(ts, getCurrent())];
	}

	@SideOnly(Side.CLIENT)
	public abstract T getCurrent();

	public abstract String getDesc(T t);

	@SideOnly(Side.CLIENT)
	public boolean isEnabled(T t){
		return FEIPermissionsManager.getPermissionLevels(Minecraft.getMinecraft().thePlayer).isHigherOrEqual(getPermissionLevel(t));
	}

	public abstract FEIPermissionLevel getPermissionLevel(T t);

	public abstract ResourceLocation getTexture(T t);

	public abstract String getText(T t);

	@SideOnly(Side.CLIENT)
	public abstract void onSelect(T t, EntityPlayer player, boolean permission);

	public class PermissionRequiredSyncedCirculatingFEIUtilProperty extends PermissionRequiredSyncedFEIUtilProperty {

		private T t;

		public PermissionRequiredSyncedCirculatingFEIUtilProperty(String desc, ResourceLocation texture, String text, FEIPermissionLevel level, T t){
			super(desc, texture, text, level);
			this.t = t;
		}

		@Override
		public String getDesc(){
			return PermissionRequiredSyncedForFEIUtil.this.getDesc(t);
		}

		@Override
		public FEIPermissionLevel getPermissionLevel(){
			return PermissionRequiredSyncedForFEIUtil.this.getPermissionLevel(t);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public boolean isEnabled(){
			return PermissionRequiredSyncedForFEIUtil.this.isEnabled(t);
		}

		@Override
		public void onServerSelect(EntityPlayer player, boolean permission){
			PermissionRequiredSyncedForFEIUtil.this.onSelect(t, player, permission);
		}

		@Override
		public ResourceLocation getTexture(){
			return PermissionRequiredSyncedForFEIUtil.this.getTexture(t);
		}

		@Override
		public String getText(){
			return PermissionRequiredSyncedForFEIUtil.this.getText(t);
		}

	}

}
