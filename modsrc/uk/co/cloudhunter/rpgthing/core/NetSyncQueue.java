package uk.co.cloudhunter.rpgthing.core;

import java.util.EnumSet;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingDeque;

import uk.co.cloudhunter.rpgthing.RPGThing;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class NetSyncQueue extends Thread implements ITickHandler {

	private static NetSyncQueue queueInstance;

	public static NetSyncQueue getQueue() {
		return NetSyncQueue.queueInstance;
	}

	private Stack<ISyncTask> queue = new Stack<ISyncTask>();
	private Object notifier = new Object();
	private boolean isRunning = true;

	public void put(ISyncTask task) {
		synchronized (queue) {
			for (ISyncTask t : queue)
				if (t.uid().equals(task.uid()))
					return;
			queue.push(task);
		}
	}

	public void shutdown() {
		isRunning = false;
		interrupt();
	}

	@Override
	public void run() {
		while (true) {
			try {
				synchronized (notifier) {
					notifier.wait();
				}
				synchronized (queue) {
					while (!queue.isEmpty()) {
						ISyncTask task = queue.pop();
						task.call();
					}
				}
			} catch (InterruptedException interrupt) {
				RPGThing.getLog().warning(interrupt, "NetSyncQueue interrupted!");
			}
		}
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		synchronized (notifier) {
			notifier.notifyAll();
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
