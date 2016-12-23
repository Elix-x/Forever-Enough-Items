package code.elix_x.mods.fei.utils;

import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.client.IRenderable;
import code.elix_x.mods.fei.api.client.IRenderable.ResourceLocationRenderable;
import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.api.utils.PermissionRequiredSyncedForFEIUtil;
import code.elix_x.mods.fei.capabilities.MagnetCapability;
import code.elix_x.mods.fei.net.MagnetStatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagnetFEIUtil extends PermissionRequiredSyncedForFEIUtil<Boolean> implements IFEIUtilInternal<PermissionRequiredSyncedForFEIUtil.PermissionRequiredSyncedCirculatingFEIUtilProperty> {

	public static final ResourceLocation off = new ResourceLocation(ForeverEnoughItemsBase.MODID, "textures/icons/magnet_off.png");
	public static final ResourceLocation on = new ResourceLocation(ForeverEnoughItemsBase.MODID, "textures/icons/magnet_on.png");

	private FEIPermissionLevel permissionLevel;

	public MagnetFEIUtil(){
		super("Magnet", false, true);
	}

	public void setPermissionLevel(FEIPermissionLevel permissionLevel){
		this.permissionLevel = permissionLevel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Boolean getCurrent(){
		return Minecraft.getMinecraft().player.getCapability(MagnetCapability.CAPABILITY, null).active;
	}

	@Override
	public String getDesc(Boolean t){
		return I18n.translateToLocal(t ? "fei.gui.override.grid.utils.magnet.on" : "fei.gui.override.grid.utils.magnet.off");
	}

	@Override
	public FEIPermissionLevel getPermissionLevel(Boolean t){
		return permissionLevel;
	}

	@Override
	public IRenderable getRenderable(Boolean t){
		return new ResourceLocationRenderable(t ? on : off);
	}

	@Override
	public void onSelect(Boolean t, EntityPlayer player, boolean permission){
		if(permission) player.getCapability(MagnetCapability.CAPABILITY, null).active = t;
		ForeverEnoughItemsBase.net.sendTo(new MagnetStatePacket(player.getCapability(MagnetCapability.CAPABILITY, null).active), (EntityPlayerMP) player);
	}

}
