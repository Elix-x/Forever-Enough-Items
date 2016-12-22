package code.elix_x.mods.fei.utils;

import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.client.IRenderable.ResourceLocationRenderable;
import code.elix_x.mods.fei.api.utils.SinglePermissionRequiredSyncedFEIUtilProperty;
import code.elix_x.mods.fei.api.utils.SingleSyncedFEIUtilProperty;
import code.elix_x.mods.fei.config.FEIConfiguration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class SaturateFEIUtil extends SinglePermissionRequiredSyncedFEIUtilProperty implements IFEIUtilInternal<SingleSyncedFEIUtilProperty> {

	public SaturateFEIUtil(){
		super("Saturate", "fei.gui.override.grid.utils.saturate", new ResourceLocationRenderable(new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "saturate.png")), null);
	}

	@Override
	public void onServerSelect(EntityPlayer player, boolean permission){
		if(permission){
			player.getFoodStats().setFoodLevel(20);
			player.getFoodStats().setFoodSaturationLevel(5);
		}
	}

}
