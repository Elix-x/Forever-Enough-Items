package code.elix_x.mods.fei.api.utils;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Iterables;

import code.elix_x.mods.fei.api.utils.ForFEIUtil.CirculatingFEIUtilProperty;
import net.minecraft.util.ResourceLocation;

public abstract class ForFEIUtil<T> extends FEIUtil<CirculatingFEIUtilProperty> {

	protected T[] ts;

	public ForFEIUtil(String name, T... ts){
		super(name);
		this.ts = ts;
		for(T t : this.ts){
			properties = (CirculatingFEIUtilProperty[]) ArrayUtils.add(properties, new CirculatingFEIUtilProperty(getDesc(t), getTexture(t), getText(t), t));
		}
	}

	public ForFEIUtil(String name, Class<T> claz, Iterable<T> ts){
		this(name, Iterables.toArray(ts, claz));
	}

	public ForFEIUtil(String name, Class<? extends Enum> claz){
		this(name, (T[]) claz.getEnumConstants());
	}

	@Override
	public CirculatingFEIUtilProperty getCurrentProperty(){
		return properties[ArrayUtils.indexOf(ts, getCurrent())];
	}

	public abstract T getCurrent();

	public abstract String getDesc(T t);

	public abstract boolean isEnabled(T t);

	public abstract void onSelect(T t);

	public abstract ResourceLocation getTexture(T t);

	public abstract String getText(T t);

	public class CirculatingFEIUtilProperty extends FEIUtilProperty {

		private T t;

		public CirculatingFEIUtilProperty(String desc, ResourceLocation texture, String text, T t){
			super(desc, texture, text);
			this.t = t;
		}

		@Override
		public String getDesc(){
			return ForFEIUtil.this.getDesc(t);
		}

		@Override
		public boolean isEnabled(){
			return ForFEIUtil.this.isEnabled(t);
		}

		@Override
		public void onSelect(){
			ForFEIUtil.this.onSelect(t);
		}

		@Override
		public ResourceLocation getTexture(){
			return ForFEIUtil.this.getTexture(t);
		}

		@Override
		public String getText(){
			return ForFEIUtil.this.getText(t);
		}

	}

}
