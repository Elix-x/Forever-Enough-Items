package code.elix_x.mods.fei.api.utils;

import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.permission.FEIPermissionsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SinglePermissionRequiredSyncedFEIUtilProperty extends SingleSyncedFEIUtilProperty {

	protected FEIPermissionLevel permissionLevel;

	public SinglePermissionRequiredSyncedFEIUtilProperty(String name, String desc, ResourceLocation texture, String text, FEIPermissionLevel permissionLevel){
		super(name, desc, texture, text);
		this.permissionLevel = permissionLevel;
	}

	public SinglePermissionRequiredSyncedFEIUtilProperty(String name, String desc, ResourceLocation texture, FEIPermissionLevel permissionLevel){
		super(name, desc, texture);
		this.permissionLevel = permissionLevel;
	}

	public SinglePermissionRequiredSyncedFEIUtilProperty(String name, String desc, String text, FEIPermissionLevel permissionLevel){
		super(name, desc, text);
		this.permissionLevel = permissionLevel;
	}

	public FEIPermissionLevel getPermissionLevel(){
		return permissionLevel;
	}

	public void setPermissionLevel(FEIPermissionLevel permissionLevel){
		this.permissionLevel = permissionLevel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isEnabled(){
		return FEIPermissionsManager.getPermissionLevels(Minecraft.getMinecraft().thePlayer).isHigherOrEqual(permissionLevel);
	}

	@Override
	public void onServerSelect(EntityPlayer player){
		onServerSelect(player, FEIPermissionsManager.getPermissionLevels(player).isHigherOrEqual(permissionLevel));
	}

	public abstract void onServerSelect(EntityPlayer player, boolean permission);

}
