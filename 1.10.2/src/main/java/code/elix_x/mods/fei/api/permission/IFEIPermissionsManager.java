package code.elix_x.mods.fei.api.permission;

import net.minecraft.entity.player.EntityPlayer;

public interface IFEIPermissionsManager {

	public FEIPermissionLevel getPermissionLevel(EntityPlayer player);

}
