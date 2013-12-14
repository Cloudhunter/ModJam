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
            throw new WrongUsageException("commands.forge.usage");
        }
        
        if (icommandsender instanceof EntityPlayerMP)
        {
        	EntityPlayerMP player = (EntityPlayerMP) icommandsender;
            if ("create".equals(args[0]))
            {
            	Party party = new Party(); // woo partay!
            	party.addPlayer(new Player(player.username));
            	
            }
        }
 
	}

}
