package code.elix_x.mods.fei.command;

import java.util.Collections;
import java.util.List;

import code.elix_x.mods.fei.api.permission.FEIPermissionLevel;
import code.elix_x.mods.fei.permission.FEIPermissionsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class FEIOpCommand extends CommandBase {

	public FEIOpCommand(){

	}

	@Override
	public String getCommandName(){
		return "feiop";
	}

	@Override
	public String getCommandUsage(ICommandSender sender){
		return "/feiop <player> <level>";
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index){
		return index == 0;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender){
		return sender == MinecraftServer.getServer() || (sender instanceof EntityPlayer && (FEIPermissionsManager.getPermissionLevels((EntityPlayer) sender).isAdmindistrator() || (MinecraftServer.getServer() instanceof IntegratedServer && EntityPlayer.getUUID(Minecraft.getMinecraft().thePlayer.getGameProfile()).equals(EntityPlayer.getUUID(((EntityPlayer) sender).getGameProfile())) && sender.canCommandSenderUseCommand(4, getCommandName()))));
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos){
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : args.length == 2 ? getListOfStringsMatchingLastWord(args, FEIPermissionLevel.names()) : Collections.<String>emptyList();
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		World world = MinecraftServer.getServer().getEntityWorld();
		if(args.length == 0){
			if(sender instanceof EntityPlayer){
				if(MinecraftServer.getServer() instanceof IntegratedServer){
					if(EntityPlayer.getUUID(Minecraft.getMinecraft().thePlayer.getGameProfile()).equals(EntityPlayer.getUUID(((EntityPlayer) sender).getGameProfile()))){
						FEIPermissionsManager.setPermissionLevels((EntityPlayer) sender, FEIPermissionLevel.OWNER);
						return;
					}
				}
			}
		} else if(args.length == 1){
			if(sender == MinecraftServer.getServer()){
				FEIPermissionsManager.setPermissionLevels(getPlayer(sender, args[0]), FEIPermissionLevel.OWNER);
			}
		} else if(args.length == 2){
			EntityPlayerMP send = getCommandSenderAsPlayer(sender);
			EntityPlayerMP recieve = getPlayer(sender, args[0]);
			FEIPermissionLevel level = FEIPermissionLevel.valueOf(args[1]);
			if(level == null){
				throw new CommandException(StatCollector.translateToLocal("fei.command.feiop.error.wronglevel"), level);
			} else if(level.isLower(FEIPermissionsManager.getPermissionLevels(send))){
				FEIPermissionsManager.setPermissionLevels(recieve, level);
			} else {
				throw new CommandException(StatCollector.translateToLocal("fei.command.feiop.error.toohighlevel"), level);
			}
		}
		throw new WrongUsageException("fei.command.feiop.error.generic");
	}

}
