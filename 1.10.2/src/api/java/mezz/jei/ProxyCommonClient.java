package mezz.jei;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.config.Config;
import mezz.jei.config.Constants;
import mezz.jei.config.KeyBindings;
import mezz.jei.config.SessionData;
import mezz.jei.gui.ItemListOverlay;
import mezz.jei.network.packets.PacketJEI;
import mezz.jei.plugins.jei.JEIInternalPlugin;
import mezz.jei.plugins.vanilla.VanillaPlugin;
import mezz.jei.util.AnnotatedInstanceUtil;
import mezz.jei.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
public class ProxyCommonClient extends ProxyCommon {
	private List<IModPlugin> plugins = new ArrayList<IModPlugin>();
	@Nullable
	private JeiStarter starter;

	private static void initVersionChecker() {
		final NBTTagCompound compound = new NBTTagCompound();
		compound.setString("curseProjectName", "just-enough-items-jei");
		compound.setString("curseFilenameParser", "jei_" + ForgeVersion.mcVersion + "-[].jar");
		FMLInterModComms.sendRuntimeMessage(Constants.MOD_ID, "VersionChecker", "addCurseCheck", compound);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		Config.preInit(event);
		initVersionChecker();

		ASMDataTable asmDataTable = event.getAsmData();
		this.plugins = AnnotatedInstanceUtil.getModPlugins(asmDataTable);

		IModPlugin vanillaPlugin = getVanillaPlugin(this.plugins);
		if (vanillaPlugin != null) {
			this.plugins.remove(vanillaPlugin);
			this.plugins.add(0, vanillaPlugin);
		}

		IModPlugin jeiInternalPlugin = getJeiInternalPlugin(this.plugins);
		if (jeiInternalPlugin != null) {
			this.plugins.remove(jeiInternalPlugin);
			this.plugins.add(jeiInternalPlugin);
		}
	}

	@Nullable
	private IModPlugin getVanillaPlugin(List<IModPlugin> modPlugins) {
		for (IModPlugin modPlugin : modPlugins) {
			if (modPlugin instanceof VanillaPlugin) {
				return modPlugin;
			}
		}
		return null;
	}

	@Nullable
	private IModPlugin getJeiInternalPlugin(List<IModPlugin> modPlugins) {
		for (IModPlugin modPlugin : modPlugins) {
			if (modPlugin instanceof JEIInternalPlugin) {
				return modPlugin;
			}
		}
		return null;
	}

	@Override
	public void init(FMLInitializationEvent event) {
		KeyBindings.init();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// Reload when resources change
		Minecraft minecraft = Minecraft.getMinecraft();
		IReloadableResourceManager reloadableResourceManager = (IReloadableResourceManager) minecraft.getResourceManager();
		reloadableResourceManager.registerReloadListener(new IResourceManagerReloadListener() {
			@Override
			public void onResourceManagerReload(IResourceManager resourceManager) {
				if (SessionData.hasJoinedWorld()) {
					restartJEI();
				}
			}
		});

		long jeiStartTime = System.currentTimeMillis();
		Log.info("Beginning postInit...");
		this.starter = new JeiStarter(this.plugins);
		Log.info("Finished postInit in {} ms", System.currentTimeMillis() - jeiStartTime);

		this.starter.start(this.plugins);
	}

	@SubscribeEvent
	public void onEntityJoinedWorld(EntityJoinWorldEvent event) {
		if (event.getWorld().isRemote && !SessionData.hasJoinedWorld() && Minecraft.getMinecraft().thePlayer != null) {
			SessionData.setJoinedWorld();
			Config.syncWorldConfig();
		}
	}

	@Override
	public void restartJEI() {
		// check that JEI has been started before. if not, do nothing
		if (this.starter != null && this.starter.hasStarted()) {
			this.starter.start(this.plugins);
		}
	}

	private static void reloadItemList() {
		JeiRuntime runtime = Internal.getRuntime();
		if (runtime != null) {
			ItemListOverlay itemListOverlay = runtime.getItemListOverlay();
			ItemFilter itemFilter = itemListOverlay.getItemFilter();
			itemFilter.build();
		}
	}

	@Override
	public void sendPacketToServer(PacketJEI packet) {
		NetHandlerPlayClient netHandler = FMLClientHandler.instance().getClient().getConnection();
		if (netHandler != null) {
			netHandler.sendPacket(packet.getPacket());
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if (!Constants.MOD_ID.equals(eventArgs.getModID())) {
			return;
		}

		if (Config.syncAllConfig()) {
			reloadItemList();
		}
	}
}
