package uk.co.cloudhunter.rpgthing.partyline;

import java.util.List;

import uk.co.cloudhunter.rpgthing.RPGThing;
import uk.co.cloudhunter.rpgthing.core.Party;
import uk.co.cloudhunter.rpgthing.core.Player;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public class PartylineChatCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "pc";
	}
	
	@Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "command.partychat.usage";
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] args) {
		if (!(icommandsender instanceof EntityPlayerMP))
			throw new WrongUsageException("command.partychat.playeronly");
		if (args.length == 0)
			throw new WrongUsageException("command.partychat.usage");
		
		Party party = Player.getPlayer(((EntityPlayer) icommandsender).username, false).getParty();
		if (party == null)
			throw new WrongUsageException("command.partychat.noparty");
		
		StringBuilder stringbuilder = new StringBuilder();
		for (String str : args)
		{
			stringbuilder.append(str);
			stringbuilder.append(" ");
		}
		
		String chatStr = stringbuilder.toString().trim();
		
		ChatMessageComponent message = ChatMessageComponent.createFromTranslationWithSubstitutions("chat.type.text", new Object[] {icommandsender.getCommandSenderName(), chatStr}).setColor(EnumChatFormatting.LIGHT_PURPLE).setItalic(true);

		List<EntityPlayer> gamePlayers = MinecraftServer.getServer().getServerConfigurationManager(
				MinecraftServer.getServer()).playerEntityList;
		for (EntityPlayer player : gamePlayers)
			for (Player p : party.getPlayers())
				if (p.getName().equals(player.username))
					((EntityPlayerMP) player).sendChatToPlayer(message);
	}

}
