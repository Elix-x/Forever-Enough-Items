package code.elix_x.mods.fei.utils;

import org.apache.commons.lang3.RandomUtils;

import code.elix_x.excore.EXCore;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.client.IRenderable.ResourceLocationRenderable;
import code.elix_x.mods.fei.api.utils.SinglePermissionRequiredSyncedFEIUtilProperty;
import code.elix_x.mods.fei.api.utils.SingleSyncedFEIUtilProperty;
import code.elix_x.mods.fei.config.FEIConfiguration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class HealFEIUtil extends SinglePermissionRequiredSyncedFEIUtilProperty implements IFEIUtilInternal<SingleSyncedFEIUtilProperty> {

	public HealFEIUtil(){
		super("Heal", "fei.gui.override.grid.utils.heal", new ResourceLocationRenderable(new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "heal.png")), null);
	}

	@Override
	public void onServerSelect(EntityPlayer player, boolean permission){
		if(permission)
			player.setHealth(EXCore.foolsTime ? RandomUtils.nextFloat(0.5f, player.getMaxHealth()) : player.getMaxHealth());
	}

}
