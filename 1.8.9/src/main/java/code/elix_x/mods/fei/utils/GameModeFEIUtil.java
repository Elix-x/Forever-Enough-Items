package code.elix_x.mods.fei.utils;

import com.mojang.realmsclient.gui.ChatFormatting;

import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.api.utils.PermissionRequiredSyncedForFEIUtil;
import code.elix_x.mods.fei.config.FEIConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GameModeFEIUtil extends PermissionRequiredSyncedForFEIUtil<GameType> {

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
		return StatCollector.translateToLocal("fei.gui.override.grid.utils.gamemode." + type.name().toLowerCase());
	}

	@Override
	public void onSelect(GameType type, EntityPlayer player, boolean permission){
		if(permission) player.setGameType(type);
	}

	@Override
	public ResourceLocation getTexture(GameType type){
		return icons[type.getID()];
	}

	@Override
	public String getText(GameType type){
		return ChatFormatting.BOLD + (type == GameType.SPECTATOR ? "S" : "?");
	}

}