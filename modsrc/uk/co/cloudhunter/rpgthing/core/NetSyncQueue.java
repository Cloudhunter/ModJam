package uk.co.cloudhunter.rpgthing.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingDeque;

import uk.co.cloudhunter.rpgthing.RPGThing;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class NetSyncQueue implements ITickHandler {

	private static NetSyncQueue queueInstance;

	static {
		queueInstance = new NetSyncQueue();
		RPGThing.registerServerTickHandler(NetSyncQueue.queueInstance);
	}

	public static NetSyncQueue getQueue() {
		return NetSyncQueue.queueInstance;
	}

	private Stack<ISyncTask> queue = new Stack<ISyncTask>();
	private ArrayList<ISyncTask> repeatingTasks = new ArrayList<ISyncTask>();

	public void put(ISyncTask task) {
		synchronized (queue) {
			for (ISyncTask t : queue)
				if (t.uid().equals(task.uid()))
					return;
			queue.push(task);
		}
	}

	public void addRepeatingTask(ISyncTask task) {
		synchronized (repeatingTasks) {
			if (!repeatingTasks.contains(task))
				repeatingTasks.add(task);
		}
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		synchronized (queue) {
			if (queue.size() > 256)
				RPGThing.getLog().warning("Sync queue is very overburdened, is the server busy?");
			while (!queue.isEmpty()) {
				ISyncTask task = queue.pop();
				task.call();
			}
		}
		synchronized (repeatingTasks) {
			for (ISyncTask task : repeatingTasks)
				task.call();
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return "NetSyncQueue";
	}

}
