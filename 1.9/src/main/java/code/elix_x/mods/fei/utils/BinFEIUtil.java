package code.elix_x.mods.fei.utils;

import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.utils.SinglePermissionRequiredSyncedFEIUtilProperty;
import code.elix_x.mods.fei.config.FEIConfiguration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class BinFEIUtil extends SinglePermissionRequiredSyncedFEIUtilProperty {

	public BinFEIUtil(){
		super("Bin", "fei.gui.override.grid.utils.bin", new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "bin.png"), null);
	}

	@Override
	public void onServerSelect(EntityPlayer player, boolean permission){
		if(permission) player.inventory.setItemStack(null);
	}

}