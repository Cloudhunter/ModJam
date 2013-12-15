package uk.co.cloudhunter.rpgthing.partyline;

import uk.co.cloudhunter.rpgthing.core.Party;
import uk.co.cloudhunter.rpgthing.core.Player;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

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
        {
            throw new WrongUsageException("commands.party.usage");
        }
        else if ("help".equals(args[0]))
        {
            throw new WrongUsageException("commands.party.usage");
        }
        
        if (icommandsender instanceof EntityPlayerMP)
        {
        	EntityPlayerMP player = (EntityPlayerMP) icommandsender;
            if ("create".equals(args[0]))
            {
            	Party party = Party.newParty(); // woo partay!
            	Player thePlayer = Player.getPlayer(player.username);
            	party.addPlayer(thePlayer);
            }
            else if ("invite".equals(args[0]))
            {
            	if (args.length <= 2)
            	{
            		throw new WrongUsageException("commands.party.usage");
            	}
            	Player thePlayer = Player.getPlayer(player.username);
            	Party party = thePlayer.getParty();
            	if (party == null)
            	{
            		party = Party.newParty(); // WE GOT A PARTY IN HERE
            		party.addPlayer(thePlayer);
            	}
            	party.addPlayer(Player.getPlayer(args[1]));
            }
        }
 
	}

}
