package uk.co.cloudhunter.rpgthing.partyline;

import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;

import uk.co.cloudhunter.rpgthing.core.Party;
import uk.co.cloudhunter.rpgthing.core.Player;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public class PartylineCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "party";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "commands.party.usage";
	}
	
	@Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
	
	@Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
		if (par2ArrayOfStr.length == 1)
		{
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"help", "create", "invite", "eject", "disband", "leave", "accept", "decline"});
		}
		
		if (par2ArrayOfStr.length == 2)
			if (par2ArrayOfStr[0].equals("invite") || par2ArrayOfStr[0].equals("eject"))
				return getListOfStringsMatchingLastWord(par2ArrayOfStr, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
			else if (par2ArrayOfStr[0].equals("accept") || par2ArrayOfStr[0].equals("decline"))
				return getListOfStringsMatchingLastWord(par2ArrayOfStr, Player.getPlayer(par1ICommandSender.getCommandSenderName(), false).partyInvites.keySet().toArray(new String[0]));
		
		return null;
    }

	@Override
	public void processCommand(ICommandSender icommandsender, String[] args) {
		if (args.length == 0)
			throw new WrongUsageException("commands.party.usage");
		else if ("help".equals(args[0]))
			throw new WrongUsageException("commands.party.usage");

		if (icommandsender instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) icommandsender;
			if ("create".equals(args[0])) {
				Party party = Party.newParty(false); // woo partay!
				Player thePlayer = Player.getPlayer(player.username, false);
				party.addPlayer(thePlayer);
				party.setOwner(thePlayer);
				icommandsender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.party.create"));
			} else if ("invite".equals(args[0])) {
				if (args.length <= 1)
					throw new WrongUsageException("commands.party.usage");
				if (FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(args[1]) == null)
					throw new CommandException("commands.party.notExist");
				Player thePlayer = Player.getPlayer(player.username, false);
				Party party = thePlayer.getParty();
				if (party == null) {
					party = Party.newParty(false); // WE GOT A PARTY IN HERE
					party.addPlayer(thePlayer);
					party.setOwner(thePlayer);
				}
				if (!thePlayer.equals(party.getOwner()))
					throw new CommandException("commands.party.notOwner");
				Player.getPlayer(args[1], false).inviteToParty(party);
				icommandsender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.party.invite", new Object[] {args[1]}));
			} else if ("eject".equals(args[0])) {
				if (args.length <= 1)
					throw new WrongUsageException("commands.party.usage");
				Player thePlayer = Player.getPlayer(player.username, false);
				Party party = thePlayer.getParty();
				if (party == null)
					throw new CommandException("commands.party.none");
				if (!thePlayer.equals(party.getOwner()))
					throw new CommandException("commands.party.notOwner");
				party.removePlayer(Player.getPlayer(args[1], false));
				icommandsender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.party.eject", new Object[] {args[1]}));
			} else if ("disband".equals(args[0])) {
				Player thePlayer = Player.getPlayer(player.username, false);
				
				Party party = thePlayer.getParty();
				if (party == null)
					throw new CommandException("commands.party.none");
				if (!thePlayer.equals(party.getOwner()))
					throw new CommandException("commands.party.notOwner");
				party.disband();
				icommandsender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.party.disband"));
			} else if ("leave".equals(args[0])) {
				Player thePlayer = Player.getPlayer(player.username, false);
				Party party = thePlayer.getParty();
				if (party == null)
					throw new CommandException("commands.party.none");
				party.removePlayer(thePlayer);
				
				icommandsender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.party.leave"));
			} else if ("accept".equals(args[0])) {
				if (args.length <= 1)
					throw new WrongUsageException("commands.party.usage");
				Player thePlayer = Player.getPlayer(player.username, false);
				PartyInvite invite = thePlayer.partyInvites.get(args[1]);
				if (invite == null)
					throw new CommandException("commands.party.noinvite");
				invite.accept();
			} else if ("decline".equals(args[0])) {
				if (args.length <= 1)
					throw new WrongUsageException("commands.party.usage");
				Player thePlayer = Player.getPlayer(player.username, false);
				PartyInvite invite = thePlayer.partyInvites.get(args[1]);
				if (invite == null)
					throw new CommandException("commands.party.noinvite");
				invite.decline();
			} else
				throw new WrongUsageException("commands.party.usage");
		}

	}

}
