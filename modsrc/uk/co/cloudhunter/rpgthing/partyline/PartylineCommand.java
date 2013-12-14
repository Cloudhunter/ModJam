package uk.co.cloudhunter.rpgthing.partyline;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

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
	}

}
