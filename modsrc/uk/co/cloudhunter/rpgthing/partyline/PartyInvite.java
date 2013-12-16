package uk.co.cloudhunter.rpgthing.partyline;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import uk.co.cloudhunter.rpgthing.core.Party;
import uk.co.cloudhunter.rpgthing.core.Player;

public class PartyInvite 
{
	private Party theParty;
	private Player thePlayer;
	private String ownerName;
	
	public PartyInvite(Party party, Player player) {
		ownerName = party.getOwner().getName();
		theParty = party;
		thePlayer = player;
	}

	public void accept() {
		theParty.sendMessageToPlayers(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.party.joined", thePlayer.getName()));
		theParty.addPlayer(thePlayer);
		thePlayer.getMinecraftPlayer().sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.party.selfjoin"));
		thePlayer.removeInvite(ownerName);
		thePlayer.clearInvites();
	}
	
	public void decline() {
		thePlayer.removeInvite(ownerName);
		theParty.getOwner().getMinecraftPlayer().sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.party.declined", thePlayer.getName()));
		declineNoRemove();
	}
	
	public String getOwnerName() {
		return ownerName;
	}

	public void declineNoRemove() {
		EntityPlayer player = theParty.getOwner().getMinecraftPlayer();
		player.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("command.party.declineOwner",  thePlayer.getName()));
	}
}
