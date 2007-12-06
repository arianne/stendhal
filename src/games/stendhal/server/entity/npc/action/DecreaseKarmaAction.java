package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * decreases the karma of the current player
 */
public class DecreaseKarmaAction extends SpeakerNPC.ChatAction {

	private double karmaDiff;

	/**
	 * Creates a new DecreaseKarmaAction
	 *
	 * @param karmaDiff amount of karma to subtract
	 */
	public DecreaseKarmaAction(double karmaDiff) {
		this.karmaDiff = karmaDiff;
	}

	@Override
	public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
		player.addKarma(-1 * karmaDiff);
	}

	@Override
	public String toString() {
		return "DecreaseKarma<" + karmaDiff +">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(karmaDiff);
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final DecreaseKarmaAction other = (DecreaseKarmaAction) obj;
		if (Double.doubleToLongBits(karmaDiff) != Double.doubleToLongBits(other.karmaDiff)) return false;
		return true;
	}
}