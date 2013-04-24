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

	@Override
	public void onTurnReached(final int currentTurn) {
		Player player = ref.get();
		
		if (player == null) {
			return;
		}
		player.removeImmunity();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof AntidoteEater) {
			final AntidoteEater other = (AntidoteEater) obj;
			Player player = ref.get();
			if (player == null) {
				return other.ref.get() == null;
			}
			
			return player.equals(other.ref.get());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return refName.hashCode();
	}
}
