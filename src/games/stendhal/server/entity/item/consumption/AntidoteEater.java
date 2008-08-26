/**
 *
 */
package games.stendhal.server.entity.item.consumption;

import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.player.Player;

import java.lang.ref.WeakReference;

class AntidoteEater implements TurnListener {

	WeakReference<Player> ref;
	private String refName;

	public AntidoteEater(final Player player) {
		ref = new WeakReference<Player>(player);
		refName = player.getName();
	}

	public void onTurnReached(final int currentTurn) {
		if (ref.get() == null) {
			return;
		}
		ref.get().removeImmunity();

	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof AntidoteEater) {
			final AntidoteEater other = (AntidoteEater) obj;
			if (ref.get() == null) {
				return other.ref.get() == null;
			}
			return ref.get().equals(other.ref.get());

		} else {
			return false;
		}

	}

	@Override
	public int hashCode() {

		return refName.hashCode();
	}
}
