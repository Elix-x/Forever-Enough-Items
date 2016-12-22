package code.elix_x.mods.fei.proxy;

import code.elix_x.excore.utils.proxy.IProxy;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

public interface IFEIProxy extends IProxy<ForeverEnoughItemsBase> {

	public void loadComplete(FMLLoadCompleteEvent event);

}
