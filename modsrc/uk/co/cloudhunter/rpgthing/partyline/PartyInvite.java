package uk.co.cloudhunter.rpgthing.partyline;

import net.minecraft.util.ChatMessageComponent;
import uk.co.cloudhunter.rpgthing.core.Party;
import uk.co.cloudhunter.rpgthing.core.Player;

public class PartyInvite 
{
	private Party theParty;
	private Player thePlayer;
	private String ownerName;
	
	public PartyInvite(Party party, Player player)
	{
		ownerName = party.getOwner().getName();
		theParty = party;
		thePlayer = player;
	}

	public void accept()
	{
		theParty.sendMessageToPlayers(ChatMessageComponent.createFromTranslationWithSubstitutions("rpgthing.party.joined", thePlayer.getName()));
		theParty.addPlayer(thePlayer);
		thePlayer.getMinecraftPlayer().sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("rpgthing.party.selfjoin"));
		thePlayer.clearInvites();
	}
	
	public void decline()
	{
		
	}
	
	public void getOwnerName()
	{
		
	}
}
