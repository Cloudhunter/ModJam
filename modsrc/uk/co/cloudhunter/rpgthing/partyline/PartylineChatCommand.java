package uk.co.cloudhunter.rpgthing.partyline;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;

public class PartylineChatCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "pc";
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
		
		StringBuilder stringbuilder = new StringBuilder();
		for (String str : args)
		{
			stringbuilder.append(str);
			stringbuilder.append(" ");
		}
		
		String chatStr = stringbuilder.toString().trim();
		System.out.println(chatStr);
	}

}
