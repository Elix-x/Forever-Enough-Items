package code.elix_x.mods.fei;

import java.io.File;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.mmyzd.llor.LightLevelOverlayReloaded;
import com.mojang.realmsclient.gui.ChatFormatting;

import at.feldim2425.moreoverlays.MoreOverlays;
import cei.ChunkEdgeIndicator;
import code.elix_x.excore.EXCore;
import code.elix_x.excore.utils.packets.SmartNetworkWrapper;
import code.elix_x.excore.utils.proxy.IProxy;
import code.elix_x.mods.fei.api.events.VanillaInventorySaveEvent;
import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.api.utils.SyncedFEIUtilProperty;
import code.elix_x.mods.fei.capabilities.MagnetCapability;
import code.elix_x.mods.fei.command.FEIOpCommand;
import code.elix_x.mods.fei.config.FEIConfiguration;
import code.elix_x.mods.fei.events.AttachMagnetCapabilityEvent;
import code.elix_x.mods.fei.events.BaublesInventorySaveEvent;
import code.elix_x.mods.fei.events.FEIInventoryLoadEvent;
import code.elix_x.mods.fei.events.OnPlayerJoinEvent;
import code.elix_x.mods.fei.events.OnPlayerTickEvent;
import code.elix_x.mods.fei.net.FEIGiveItemStackPacket;
import code.elix_x.mods.fei.net.LoadInventoryPacket;
import code.elix_x.mods.fei.net.SyncPermissionsManagerPacket;
import code.elix_x.mods.fei.net.SyncedFEIUtilPropertyPacket;
import code.elix_x.mods.fei.permission.FEIPermissionsManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ForeverEnoughItemsBase.MODID, name = ForeverEnoughItemsBase.NAME, version = ForeverEnoughItemsBase.VERSION, dependencies = "required-after:" + EXCore.DEPENDENCY + ";required-after:" + ForeverEnoughItemsBase.JEIDEPENDENCY + ";after:" + ForeverEnoughItemsBase.BAUBLESDEPENDENCY + ";after:" + ForeverEnoughItemsBase.LLORDEPENDENCY + ";after:" + ForeverEnoughItemsBase.CEIDEPENDENCY + ";after:" + ForeverEnoughItemsBase.MOREOVERLAYSDEPENDENCY)
public class ForeverEnoughItemsBase {

	public static final String MODID = "FEI";
	public static final String NAME = "Forever Enough Items";
	public static final String VERSION = "1.0.9";

	public static final String JEIDEPENDENCY = mezz.jei.config.Constants.MOD_ID;
	public static final String BAUBLESDEPENDENCY = "Baubles";
	public static final String CEIDEPENDENCY = ChunkEdgeIndicator.MODID + "@[" + ChunkEdgeIndicator.VERSION + ",)";
	public static final String LLORDEPENDENCY = LightLevelOverlayReloaded.MODID;
	public static final String MOREOVERLAYSDEPENDENCY = MoreOverlays.MOD_ID + "@[" + MoreOverlays.VERSION + ",)";

	@SidedProxy(modId = MODID, serverSide = "code.elix_x.mods.fei.proxy.ServerProxy", clientSide = "code.elix_x.mods.fei.proxy.ClientProxy")
	public static IProxy proxy;

	public static File configDir;

	public static SmartNetworkWrapper net;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		configDir = new File(event.getModConfigurationDirectory(), NAME);
		configDir.mkdir();

		net = new SmartNetworkWrapper(NAME);
		net.registerMessage3(new Function<SyncPermissionsManagerPacket, Runnable>(){

			@Override
			public Runnable apply(final SyncPermissionsManagerPacket packet){
				return new Runnable(){

					@Override
					public void run(){
						FEIPermissionsManager.onSync(packet);
					}

				};
			}

		}, SyncPermissionsManagerPacket.class, Side.CLIENT);
		net.registerMessage1(new Function<Pair<SyncedFEIUtilPropertyPacket, MessageContext>, Runnable>(){

			@Override
			public Runnable apply(final Pair<SyncedFEIUtilPropertyPacket, MessageContext> pair){
				return new Runnable(){

					@Override
					public void run(){
						SyncedFEIUtilProperty.onClientSelectPacket(pair.getLeft().id, pair.getRight().getServerHandler().playerEntity);
					}

				};
			}

		}, SyncedFEIUtilPropertyPacket.class, Side.SERVER);
		net.registerMessage1(new Function<Pair<LoadInventoryPacket, MessageContext>, Runnable>(){

			@Override
			public Runnable apply(final Pair<LoadInventoryPacket, MessageContext> pair){
				return new Runnable(){

					@Override
					public void run(){
						NBTTagCompound inventory = pair.getLeft().inventory;
						EntityPlayer player = pair.getRight().getServerHandler().playerEntity;
						if(FEIConfiguration.canLoadInventory(player)){
							MinecraftForge.EVENT_BUS.post(new FEIInventoryLoadEvent(player, inventory));
						} else {
							player.addChatComponentMessage(new TextComponentString(String.format(ChatFormatting.RED + I18n.translateToLocal("fei.lowlevel.inventorysaves"), FEIPermissionLevel.MODERATOR)));
						}
					}

				};
			}

		}, LoadInventoryPacket.class, Side.SERVER);
		net.registerMessage1(new Function<Pair<FEIGiveItemStackPacket, MessageContext>, Runnable>(){

			@Override
			public Runnable apply(final Pair<FEIGiveItemStackPacket, MessageContext> pair){
				return new Runnable(){

					@Override
					public void run(){
						FEIGiveItemStackPacket packet = pair.getLeft();
						EntityPlayer player = pair.getRight().getServerHandler().playerEntity;
						if(FEIConfiguration.canGiveItems(player)) player.inventory.addItemStackToInventory(packet.itemstack);
					}

				};
			}

		}, FEIGiveItemStackPacket.class, Side.SERVER);

		FEIConfiguration.preInit(event);

		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		CapabilityManager.INSTANCE.register(MagnetCapability.class, new IStorage(){

			@Override
			public NBTBase writeNBT(Capability capability, Object instance, EnumFacing side){
				return null;
			}

			@Override
			public void readNBT(Capability capability, Object instance, EnumFacing side, NBTBase nbt){

			}

		}, new Callable<MagnetCapability>(){

			@Override
			public MagnetCapability call() throws Exception {
				return null;
			}

		});

		MinecraftForge.EVENT_BUS.register(new OnPlayerJoinEvent());
		MinecraftForge.EVENT_BUS.register(new AttachMagnetCapabilityEvent());
		MinecraftForge.EVENT_BUS.register(new VanillaInventorySaveEvent());
		MinecraftForge.EVENT_BUS.register(new OnPlayerTickEvent());

		if(Loader.isModLoaded("Baubles")) MinecraftForge.EVENT_BUS.register(new BaublesInventorySaveEvent());

		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInit(event);
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event){
		event.registerServerCommand(new FEIOpCommand());
	}

}
