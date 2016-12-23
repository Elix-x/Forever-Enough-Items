package code.elix_x.mods.fei.utils;

import code.elix_x.excomms.color.RGBA;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.client.IRenderable;
import code.elix_x.mods.fei.api.client.IRenderable.ResourceLocationRenderable;
import code.elix_x.mods.fei.api.client.IRenderable.StringRenderable;
import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.api.utils.PermissionRequiredSyncedForFEIUtil;
import code.elix_x.mods.fei.config.FEIConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GameModeFEIUtil extends PermissionRequiredSyncedForFEIUtil<GameType> implements IFEIUtilInternal<PermissionRequiredSyncedForFEIUtil.PermissionRequiredSyncedCirculatingFEIUtilProperty> {

	public static final ResourceLocation[] icons = {new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "survival.png"), new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "creative.png"), new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "adventure.png"), null};

	private FEIPermissionLevel permissionLevel;

	public GameModeFEIUtil(){
		super("Game Mode", GameType.SURVIVAL, GameType.CREATIVE, GameType.ADVENTURE, GameType.SPECTATOR);
	}

	public void setPermissionLevel(FEIPermissionLevel permissionLevel){
		this.permissionLevel = permissionLevel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GameType getCurrent(){
		return Minecraft.getMinecraft().playerController.getCurrentGameType();
	}
	
	@Override
	public FEIPermissionLevel getPermissionLevel(GameType t){
		return permissionLevel;
	}

	@Override
	public String getDesc(GameType type){
		return I18n.translateToLocal("fei.gui.override.grid.utils.gamemode." + type.name().toLowerCase());
	}

	@Override
	public void onSelect(GameType type, EntityPlayer player, boolean permission){
		if(permission) player.setGameType(type);
	}

	@Override
	public IRenderable getRenderable(GameType type){
		return type == GameType.SPECTATOR ? new StringRenderable(TextFormatting.BOLD + "S", new RGBA(1f, 1f, 1f)) : new ResourceLocationRenderable(icons[type.getID()]);
	}

}