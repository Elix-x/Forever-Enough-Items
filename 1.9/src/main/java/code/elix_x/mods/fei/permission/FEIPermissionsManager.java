package code.elix_x.mods.fei.permission;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import code.elix_x.excore.utils.nbt.mbt.MBT;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.api.permission.IFEIPermissionsManager;
import code.elix_x.mods.fei.net.SyncPermissionsManagerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FEIPermissionsManager extends WorldSavedData implements IFEIPermissionsManager {

	public static final String NAME = "FEIPermissions";

	public static final MBT mbt = new MBT();

	public static FEIPermissionLevel getPermissionLevels(EntityPlayer player){
		return get(player.worldObj).getPermissionLevel(player);
	}

	public static void setPermissionLevels(EntityPlayer player, FEIPermissionLevel level){
		get(player.worldObj).setPermissionLevel(player, level);
		syncWithAll();
	}

	public static void syncWith(EntityPlayer player){
		NBTTagCompound nbt = new NBTTagCompound();
		get(player.worldObj).writeToNBT(nbt);
		ForeverEnoughItemsBase.net.sendTo(new SyncPermissionsManagerPacket(nbt), (EntityPlayerMP) player);
	}

	public static void syncWithAll(){
		NBTTagCompound nbt = new NBTTagCompound();
		get(FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld()).writeToNBT(nbt);
		ForeverEnoughItemsBase.net.sendToAll(new SyncPermissionsManagerPacket(nbt));
	}

	@SideOnly(Side.CLIENT)
	public static void onSync(SyncPermissionsManagerPacket packet){
		get(Minecraft.getMinecraft().theWorld).readFromNBT(packet.nbt);
	}

	public static FEIPermissionsManager get(World world){
		FEIPermissionsManager manager = (FEIPermissionsManager) world.getMapStorage().loadData(FEIPermissionsManager.class, NAME);
		if(manager == null){
			manager = new FEIPermissionsManager(NAME);
			world.getMapStorage().setData(NAME, manager);
		}
		return manager;
	}

	private Map<UUID, FEIPermissionLevel> permissions = new HashMap<UUID, FEIPermissionLevel>();

	public FEIPermissionsManager(String name){
		super(name);
	}

	public FEIPermissionLevel getPermissionLevel(UUID player){
		return permissions.get(player) == null ? FEIPermissionLevel.USER : permissions.get(player);
	}

	public FEIPermissionLevel getPermissionLevel(EntityPlayer player){
		return getPermissionLevel(player.getUUID(player.getGameProfile()));
	}

	public void setPermissionLevel(UUID player, FEIPermissionLevel level){
		permissions.put(player, level);
		markDirty();
	}

	public void setPermissionLevel(EntityPlayer player, FEIPermissionLevel level){
		setPermissionLevel(player.getUUID(player.getGameProfile()), level);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		permissions = mbt.fromNBT(nbt.getTag("data"), HashMap.class, UUID.class, FEIPermissionLevel.class);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt){
		nbt.setTag("data", mbt.toNBT(permissions));
	}

}
