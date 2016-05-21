package code.elix_x.mods.fei.utils;

import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.utils.SinglePermissionRequiredSyncedFEIUtilProperty;
import code.elix_x.mods.fei.config.FEIConfiguration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class HealFEIUtil extends SinglePermissionRequiredSyncedFEIUtilProperty {

	public HealFEIUtil(){
		super("Heal", "fei.gui.override.grid.utils.heal", new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "heal.png"), null);
	}

	@Override
	public void onServerSelect(EntityPlayer player, boolean permission){
		if(permission) player.setHealth(player.getMaxHealth());
	}

}
