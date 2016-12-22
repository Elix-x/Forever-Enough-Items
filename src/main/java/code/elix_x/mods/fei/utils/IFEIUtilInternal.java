package code.elix_x.mods.fei.utils;

import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.api.utils.IFEIUtil;
import code.elix_x.mods.fei.api.utils.IFEIUtil.IFEIUtilProperty;

public interface IFEIUtilInternal<U extends IFEIUtilProperty> extends IFEIUtil<U> {

	public void setPermissionLevel(FEIPermissionLevel permissionLevel);

}
