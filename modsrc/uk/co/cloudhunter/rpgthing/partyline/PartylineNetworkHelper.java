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
				HashMap<String, Object> values = new HashMap<String, Object>();
				HashMap<Integer, String> players = new HashMap<Integer, String>();
				for (uk.co.cloudhunter.rpgthing.core.Player p : party.getPlayers())
					players.put(players.size(), p.getName());
				values.put("party-target", target);
				values.put("players", players);
				values.put("owner", party.getOwner().getName());
				result.setValue("party-data", values);
				result.setValue("payload", "party-data");
				RPGThing.getProxy().sendToPlayer((EntityPlayer) player, result);
			}
		}

		if (!isServer) {
			String req = (String) sp.getValue("payload");
			if (req.equals("party-data")) {
				int target = (Integer) sp.getValue("party-target");
				Party party = Party.getPartyById(target, true);

				HashMap<String, Object> payload = (HashMap<String, Object>) sp.getValue("party-data");
				HashMap<Integer, String> persons = (HashMap<Integer, String>) payload.get("players");
				String owner = (String) payload.get("owner");

				for (Entry<Integer, String> person : persons.entrySet())
					party.addPlayer(uk.co.cloudhunter.rpgthing.core.Player.getPlayer(person.getValue(), true));
				party.setOwner(uk.co.cloudhunter.rpgthing.core.Player.getPlayer(owner, true));
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
