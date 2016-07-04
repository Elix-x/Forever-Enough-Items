package code.elix_x.mods.fei.api.utils;

import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.permission.FEIPermissionsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class PermissionRequiredSyncedFEIUtilProperty extends SyncedFEIUtilProperty {

	protected FEIPermissionLevel permissionLevel;

	public PermissionRequiredSyncedFEIUtilProperty(String desc, ResourceLocation texture, String text, FEIPermissionLevel permissionLevel){
		super(desc, texture, text);
		this.permissionLevel = permissionLevel;
	}

	public PermissionRequiredSyncedFEIUtilProperty(String desc, ResourceLocation texture, FEIPermissionLevel permissionLevel){
		super(desc, texture);
		this.permissionLevel = permissionLevel;
	}

	public PermissionRequiredSyncedFEIUtilProperty(String desc, String text, FEIPermissionLevel permissionLevel){
		super(desc, text);
		this.permissionLevel = permissionLevel;
	}

	public FEIPermissionLevel getPermissionLevel(){
		return permissionLevel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isEnabled(){
		return FEIPermissionsManager.getPermissionLevels(Minecraft.getMinecraft().thePlayer).isHigherOrEqual(getPermissionLevel());
	}

	@Override
	public void onServerSelect(EntityPlayer player){
		onServerSelect(player, FEIPermissionsManager.getPermissionLevels(player).isHigherOrEqual(getPermissionLevel()));
	}

	public abstract void onServerSelect(EntityPlayer player, boolean permission);

}
