package uk.co.cloudhunter.rpgthing.util;

import java.util.ArrayList;
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

	private static ISyncTask taskCleanup = new ISyncTask() {
		private ArrayList<Player> playerDisposeQueue = new ArrayList<Player>();
		private ArrayList<Party> partyDisposeQueue = new ArrayList<Party>();

		@Override
		public String uid() {
			return "cleanup";
		}

		@Override
		public void call() {
			if (!Player.playerLock.isLocked() && !Party.partyLock.isLocked()) {
				try {
					Player.playerLock.lock();
					Party.partyLock.lock();
					playerDisposeQueue.clear();
					partyDisposeQueue.clear();
					for (Player player : Player.getAllPlayers(false))
						if (player.isDisconnected())
							playerDisposeQueue.add(player);
					for (Player player : playerDisposeQueue) {
						if (player.getParty() != null)
							player.getParty().removePlayer(player);
						Player.removePlayer(player);
					}
					for (Party party : Party.getAllParties(false))
						if (party.hasDisbanded())
							partyDisposeQueue.add(party);
					for (Party party : partyDisposeQueue)
						Party.removeParty(party);

					if (playerDisposeQueue.size() > 0)
						RPGThing.getLog().info("Cleaned " + playerDisposeQueue.size() + " players");
					if (partyDisposeQueue.size() > 0)
						RPGThing.getLog().info("Cleaned " + partyDisposeQueue.size() + " parties");

					playerDisposeQueue.clear();
					partyDisposeQueue.clear();
					if (Player.playerLock.isHeldByCurrentThread())
						Player.playerLock.unlock();
					if (Party.partyLock.isHeldByCurrentThread())
						Party.partyLock.unlock();
				} finally {
					if (Player.playerLock.isHeldByCurrentThread())
						Player.playerLock.unlock();
					if (Party.partyLock.isHeldByCurrentThread())
						Party.partyLock.unlock();
				}
			}
		}
	};

	static {
		NetSyncQueue.getQueue().addRepeatingTask(syncParty);
		NetSyncQueue.getQueue().addRepeatingTask(syncPlayer);
		NetSyncQueue.getQueue().addRepeatingTask(taskCleanup);
	}

}
