package code.elix_x.mods.fei.api.utils;

import code.elix_x.mods.fei.api.utils.IFEIUtil.IFEIUtilProperty;

public class FEIUtil<U extends IFEIUtilProperty> implements IFEIUtil<U> {

	protected String name;
	protected U[] properties;

	public FEIUtil(String name, U... properties){
		this.name = name;
		this.properties = properties;
	}

	@Override
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	@Override
	public U[] getAllProperties(){
		return properties;
	}

	public void setProperties(U... properties){
		this.properties = properties;
	}

	@Override
	public U getCurrentProperty(){
		return null;
	}

}
