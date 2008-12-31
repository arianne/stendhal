package games.stendhal.client;

import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;

public abstract class PerceptionListenerImpl implements IPerceptionListener {

	public boolean onAdded(RPObject object) {
		return false;
	}

	public boolean onClear() {
		return false;
	}

	public boolean onDeleted(RPObject object) {
		return false;
	}

	public void onException(Exception exception, MessageS2CPerception perception) {

	}

	public boolean onModifiedAdded(RPObject object, RPObject changes) {
		return false;
	}

	public boolean onModifiedDeleted(RPObject object, RPObject changes) {
		return false;
	}

	public boolean onMyRPObject(RPObject added, RPObject deleted) {
		return false;
	}

	public void onPerceptionBegin(byte type, int timestamp) {

	}

	public void onPerceptionEnd(byte type, int timestamp) {
	}

	public void onSynced() {

	}

	public void onUnsynced() {
		

	}

}
