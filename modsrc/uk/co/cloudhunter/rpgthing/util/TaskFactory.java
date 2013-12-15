package uk.co.cloudhunter.rpgthing.util;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import uk.co.cloudhunter.rpgthing.RPGThing;
import uk.co.cloudhunter.rpgthing.core.ISyncTask;
import uk.co.cloudhunter.rpgthing.core.NetSyncQueue;
import uk.co.cloudhunter.rpgthing.core.Party;
import uk.co.cloudhunter.rpgthing.core.Player;
import uk.co.cloudhunter.rpgthing.network.StandardModPacket;

public class TaskFactory {

	private static ISyncTask syncParty = new ISyncTask() {
		@Override
		public String uid() {
			return "partysync";
		}

		@Override
		public void call() {
			for (Party party : Party.getAllParties(false)) {
				if (party.pollModified()) {
					StandardModPacket result = new StandardModPacket();
					result.setIsForServer(false);
					result.setType("partyline");
					party.writeToPacket(result, "party-data");
					result.setValue("payload", "party-data");
					result.setValue("party-target", party.getId());
					for (Player p : party.getPlayers())
						RPGThing.getProxy().sendToPlayer(p.getMinecraftPlayer(), result);
				}
			}
		}
	};

	private static ISyncTask syncPlayer = new ISyncTask() {
		@Override
		public String uid() {
			return "playersync";
		}

		@Override
		public void call() {
			for (Player player : Player.getAllPlayers(false)) {
				if (player.pollModified()) {
					StandardModPacket packet = new StandardModPacket();
					packet.setIsForServer(false);
					packet.setType("player");
					packet.setValue("payload", "player-data");
					packet.setValue("player-name", player.getName());
					player.writeToPacket(packet, "player-data");
					RPGThing.getProxy().sendToAllPlayers(packet);
				}
			}

		}
	};

	static {
		NetSyncQueue.getQueue().addRepeatingTask(syncParty);
		NetSyncQueue.getQueue().addRepeatingTask(syncPlayer);
	}

}
