package uk.co.cloudhunter.rpgthing.partyline;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.Player;
import uk.co.cloudhunter.rpgthing.RPGThing;
import uk.co.cloudhunter.rpgthing.core.Party;
import uk.co.cloudhunter.rpgthing.network.ModPacket;
import uk.co.cloudhunter.rpgthing.network.StandardModPacket;

public class PartylineNetworkHelper {

	public void accept(ModPacket packet, Player player, boolean isServer) {
		StandardModPacket sp = (StandardModPacket) packet;

		if (isServer) {
			String req = (String) sp.getValue("request");
			if (req.equals("party-data")) {
				int target = (Integer) sp.getValue("party-target");
				Party party = Party.getPartyById(target, false);
				StandardModPacket result = new StandardModPacket();
				result.setIsForServer(false);
				result.setType("partyline");
				party.writeToPacket(result, "party-data");
				result.setValue("payload", "party-data");
				RPGThing.getProxy().sendToPlayer((EntityPlayer) player, result);
			}
		}

		if (!isServer) {
			String req = (String) sp.getValue("payload");
			if (req.equals("party-data")) {
				int target = (Integer) sp.getValue("party-target");
				Party party = Party.getPartyById(target, true);
				party.readFromPacket(sp, "party-data");
			}

			if (req.equals("join-party")) {
				int target = (Integer) sp.getValue("party-target");
				Party party = Party.getPartyById(target, true);
				uk.co.cloudhunter.rpgthing.core.Player.getPlayer(((EntityPlayer) player).username, true).setParty(party);

				StandardModPacket updateReq = new StandardModPacket();
				updateReq.setIsForServer(true);
				updateReq.setType("partyline");
				updateReq.setValue("request", "party-data");
				updateReq.setValue("party-target", target);
				RPGThing.getProxy().sendToServer(updateReq);
			}
		}

	}
}
