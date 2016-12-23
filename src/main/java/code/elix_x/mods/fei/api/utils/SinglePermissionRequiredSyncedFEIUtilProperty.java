package code.elix_x.mods.fei.api.utils;

import code.elix_x.mods.fei.api.client.IRenderable;
import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.permission.FEIPermissionsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SinglePermissionRequiredSyncedFEIUtilProperty extends SingleSyncedFEIUtilProperty {

	protected FEIPermissionLevel permissionLevel;

	public SinglePermissionRequiredSyncedFEIUtilProperty(String name, String desc, IRenderable renderable, FEIPermissionLevel permissionLevel){
		super(name, desc, renderable);
		this.permissionLevel = permissionLevel;
	}

	public void setPermissionLevel(FEIPermissionLevel permissionLevel){
		this.permissionLevel = permissionLevel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isEnabled(){
		return FEIPermissionsManager.getPermissionLevels(Minecraft.getMinecraft().player).isHigherOrEqual(permissionLevel);
	}

	@Override
	public void onServerSelect(EntityPlayer player){
		onServerSelect(player, FEIPermissionsManager.getPermissionLevels(player).isHigherOrEqual(permissionLevel));
	}

	public abstract void onServerSelect(EntityPlayer player, boolean permission);

}
