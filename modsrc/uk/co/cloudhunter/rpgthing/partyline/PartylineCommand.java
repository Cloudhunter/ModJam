package uk.co.cloudhunter.rpgthing.partyline;

import uk.co.cloudhunter.rpgthing.core.Party;
import uk.co.cloudhunter.rpgthing.core.Player;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
				Player thePlayer = Player.getPlayer(player.username, false);
				Party party = thePlayer.getParty();
				if (party == null) {
					party = Party.newParty(false); // WE GOT A PARTY IN HERE
					party.addPlayer(thePlayer);
					party.setOwner(thePlayer);
				}
				if (!thePlayer.equals(party.getOwner()))
					throw new CommandException("commands.party.notOwner");
				party.addPlayer(Player.getPlayer(args[1], false));
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
				
			}
		}

	}

}
