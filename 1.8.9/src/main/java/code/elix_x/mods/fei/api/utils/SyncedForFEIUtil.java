package code.elix_x.mods.fei.api.utils;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Iterables;

import code.elix_x.mods.fei.api.utils.SyncedForFEIUtil.SyncedCirculatingFEIUtilProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public abstract class SyncedForFEIUtil<T> extends FEIUtil<SyncedCirculatingFEIUtilProperty> {

	protected T[] ts;

	public SyncedForFEIUtil(String name, T... ts){
		super(name);
		this.ts = ts;
		for(T t : this.ts){
			properties = (SyncedCirculatingFEIUtilProperty[]) ArrayUtils.add(properties, new SyncedCirculatingFEIUtilProperty(getDesc(t), getTexture(t), getText(t), t));
		}
	}

	public SyncedForFEIUtil(String name, Class<T> claz, Iterable<T> ts){
		this(name, Iterables.toArray(ts, claz));
	}

	public SyncedForFEIUtil(String name, Class<? extends Enum> claz){
		this(name, (T[]) claz.getEnumConstants());
	}

	@Override
	public SyncedCirculatingFEIUtilProperty getCurrentProperty(){
		return properties[ArrayUtils.indexOf(ts, getCurrent())];
	}

	public abstract T getCurrent();

	public abstract String getDesc(T t);

	public abstract boolean isEnabled(T t);

	public abstract ResourceLocation getTexture(T t);

	public abstract String getText(T t);

	public abstract void onSelect(T t, EntityPlayer player);

	public class SyncedCirculatingFEIUtilProperty extends SyncedFEIUtilProperty {

		private T t;

		public SyncedCirculatingFEIUtilProperty(String desc, ResourceLocation texture, String text, T t){
			super(desc, texture, text);
			this.t = t;
		}

		@Override
		public String getDesc(){
			return SyncedForFEIUtil.this.getDesc(t);
		}

		@Override
		public boolean isEnabled(){
			return SyncedForFEIUtil.this.isEnabled(t);
		}

		@Override
		public void onServerSelect(EntityPlayer player){
			SyncedForFEIUtil.this.onSelect(t, player);
		}

		@Override
		public ResourceLocation getTexture(){
			return SyncedForFEIUtil.this.getTexture(t);
		}

		@Override
		public String getText(){
			return SyncedForFEIUtil.this.getText(t);
		}

	}

}
