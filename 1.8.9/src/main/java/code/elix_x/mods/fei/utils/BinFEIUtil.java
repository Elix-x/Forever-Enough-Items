package code.elix_x.mods.fei.utils;

import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.utils.SinglePermissionRequiredSyncedFEIUtilProperty;
import code.elix_x.mods.fei.config.FEIConfiguration;
import code.elix_x.mods.fei.permission.FEIPermissionsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BinFEIUtil extends SinglePermissionRequiredSyncedFEIUtilProperty {

	public BinFEIUtil(){
		super("Bin", "fei.gui.override.grid.utils.bin", new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "bin.png"), null);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSelect(){
		super.onSelect();
		if(FEIPermissionsManager.getPermissionLevels(Minecraft.getMinecraft().thePlayer).isHigherOrEqual(permissionLevel)) Minecraft.getMinecraft().thePlayer.inventory.setItemStack(null);
	}

	@Override
	public void onServerSelect(EntityPlayer player, boolean permission){
		if(permission) player.inventory.setItemStack(null);
	}

}