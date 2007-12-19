/**
 *
 */
package games.stendhal.server.entity.item.consumption;

import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.player.Player;

import java.lang.ref.WeakReference;

class AntidoteEater implements TurnListener {

	WeakReference<Player> ref;

	public AntidoteEater(Player player) {
		ref = new WeakReference<Player>(player);
	}

	public void onTurnReached(int currentTurn) {
		if (ref.get() == null) {
			return;
		}
		ref.get().removeImmunity();

	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof AntidoteEater) {
			AntidoteEater other = (AntidoteEater) obj;
			return ref.get() == other.ref.get();

		} else {
			return false;
		}

	}

	@Override
	public int hashCode() {

		return ref.hashCode();
	}
}
