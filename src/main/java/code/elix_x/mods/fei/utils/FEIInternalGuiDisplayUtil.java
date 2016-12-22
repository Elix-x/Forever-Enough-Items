package code.elix_x.mods.fei.utils;

import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.client.IRenderable;
import code.elix_x.mods.fei.api.utils.SinglePermissionRequiredSyncedFEIUtilProperty;
import code.elix_x.mods.fei.api.utils.SingleSyncedFEIUtilProperty;
import code.elix_x.mods.fei.net.FEIGuiType;
import net.minecraft.entity.player.EntityPlayer;

public class FEIInternalGuiDisplayUtil extends SinglePermissionRequiredSyncedFEIUtilProperty implements IFEIUtilInternal<SingleSyncedFEIUtilProperty> {

	protected FEIGuiType gui;

	public FEIInternalGuiDisplayUtil(String name, String desc, IRenderable renderable, FEIGuiType gui){
		super(name, desc, renderable, null);
		this.gui = gui;
	}

	@Override
	public void onServerSelect(EntityPlayer player, boolean permission){
		if(permission){
			ForeverEnoughItemsBase.guiHandler.display(player, gui, 0, 0, 0);
		}
	}

}
