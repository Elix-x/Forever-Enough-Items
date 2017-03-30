package code.elix_x.mods.fei.utils;

import code.elix_x.excore.EXCore;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.client.IRenderable;
import code.elix_x.mods.fei.api.client.IRenderable.ResourceLocationRenderable;
import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.api.utils.PermissionRequiredSyncedForFEIUtil;
import code.elix_x.mods.fei.config.FEIConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TimeFEIUtil extends PermissionRequiredSyncedForFEIUtil<Integer> implements IFEIUtilInternal<PermissionRequiredSyncedForFEIUtil.PermissionRequiredSyncedCirculatingFEIUtilProperty> {

	public static final String[] descs = {"sunrise", "noon", "sunset", "midnight"};
	public static final ResourceLocation[] icons = {new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "sunrise.png"), new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "noon.png"), new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "sunset.png"), new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "midnight.png")};

	private FEIPermissionLevel permissionLevel;

	public TimeFEIUtil(){
		super("Time", 0, 1, 2, 3);
	}

	public void setPermissionLevel(FEIPermissionLevel permissionLevel){
		this.permissionLevel = permissionLevel;
	}

	@Override
	public void onSelect(Integer i, EntityPlayer player, boolean permission){
		if(permission) player.world.setWorldTime(EXCore.foolsTime ? player.world.rand.nextInt(24000) : i * 6000);
	}

	@Override
	public IRenderable getRenderable(Integer i){
		return new ResourceLocationRenderable(icons[i]);
	}

	@Override
	public FEIPermissionLevel getPermissionLevel(Integer i){
		return permissionLevel;
	}

	@Override
	public String getDesc(Integer i){
		return I18n.translateToLocal("fei.gui.override.grid.utils.time." + descs[i]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Integer getCurrent(){
		return Math.min((int) Minecraft.getMinecraft().world.getWorldTime() / 6000, 3);
	}

}