package code.elix_x.mods.fei.api.utils;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Iterables;

import code.elix_x.mods.fei.api.client.IRenderable;
import code.elix_x.mods.fei.api.utils.ForFEIUtil.CirculatingFEIUtilProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ForFEIUtil<T> extends FEIUtil<CirculatingFEIUtilProperty> {

	protected T[] ts;

	public ForFEIUtil(String name, T... ts){
		super(name);
		this.ts = ts;
		for(T t : this.ts){
			properties = (CirculatingFEIUtilProperty[]) ArrayUtils.add(properties, new CirculatingFEIUtilProperty(getDesc(t), getRenderable(t), t));
		}
	}

	public ForFEIUtil(String name, Class<T> claz, Iterable<T> ts){
		this(name, Iterables.toArray(ts, claz));
	}

	public ForFEIUtil(String name, Class<? extends Enum> claz){
		this(name, (T[]) claz.getEnumConstants());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public CirculatingFEIUtilProperty getCurrentProperty(){
		return properties[ArrayUtils.indexOf(ts, getCurrent())];
	}

	@SideOnly(Side.CLIENT)
	public abstract T getCurrent();

	public abstract String getDesc(T t);

	@SideOnly(Side.CLIENT)
	public abstract boolean isEnabled(T t);

	@SideOnly(Side.CLIENT)
	public abstract void onSelect(T t);

	public abstract IRenderable getRenderable(T t);

	public class CirculatingFEIUtilProperty extends FEIUtilProperty {

		private T t;

		public CirculatingFEIUtilProperty(String desc, IRenderable renderable, T t){
			super(desc, renderable);
			this.t = t;
		}

		@Override
		public String getDesc(){
			return ForFEIUtil.this.getDesc(t);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public boolean isEnabled(){
			return ForFEIUtil.this.isEnabled(t);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void onSelect(){
			ForFEIUtil.this.onSelect(t);
		}

		@Override
		public IRenderable getRenderable(){
			return ForFEIUtil.this.getRenderable(t);
		}

	}

}
