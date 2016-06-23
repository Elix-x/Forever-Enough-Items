package at.feldim2425.moreoverlays;

import at.feldim2425.moreoverlays.config.ConfigHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = MoreOverlays.MOD_ID, version = MoreOverlays.VERSION, name = MoreOverlays.NAME, clientSideOnly = true, dependencies = "required-after:Forge@[12.17.0.1957,);", guiFactory = "at.feldim2425.moreoverlays.config.GuiFactory")
public class MoreOverlays {

    public static final String MOD_ID = "moreoverlays";
    public static final String NAME = "MoreOverlays";
    public static final String VERSION = "1.4";

    @SidedProxy(clientSide = "at.feldim2425.moreoverlays.Proxy")
    public static Proxy proxy;

    public static Logger logger = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(event);
        if (proxy != null)
            proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (proxy != null)
            proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (proxy != null)
            proxy.postInit();
    }
}
