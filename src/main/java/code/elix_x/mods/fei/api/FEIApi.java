package code.elix_x.mods.fei.api;

import java.io.File;

import code.elix_x.mods.fei.api.permission.IFEIPermissionsManager;
import code.elix_x.mods.fei.api.utils.IFEIUtil;
import net.minecraft.world.World;

public interface FEIApi {

	public static final FEIApi INSTANCE = null;

	public File getFEIConfigDir();

	public void addGridUtil(IFEIUtil util);

	public IFEIPermissionsManager getPermissionsManager(World world);

	public void onUtilPropertySelect(int id);

}
