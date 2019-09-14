package games.stendhal.server.core.rp.pvp;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;

import games.stendhal.server.entity.player.Player;

public class PlayerVsPlayerChallenge {

	private final long opened;
	private long accepted = -1l;
	//private long lastAction = -1l;
	private final Player challenger;
	private final Player challenged;

	public PlayerVsPlayerChallenge(long opened, Player challenger, Player challenged) {
		this.opened = opened;
		this.challenger = challenger;
		this.challenged = challenged;
	}

	/**
	 * Two Challenges are equal iff opened, challenger and challenged are equals.
	 * @see {@link java.lang.Object#equals(Object)}
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof PlayerVsPlayerChallenge)) {
			return false;
		}
		PlayerVsPlayerChallenge other = (PlayerVsPlayerChallenge) obj;

		if(!(this.opened == other.opened)) {
			return false;
		}

		return this.challenger.equals(other.challenger) && this.challenged.equals(other.challenged);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.challenger, this.challenged);
	}

	@Override
	public String toString() {
		ToStringHelper helper = MoreObjects.toStringHelper(this)
				.add("challenger", this.challenger.getName())
				.add("challenged", this.challenged.getName())
				.add("accepted", this.isAccepted());
		return helper.toString();
	}

	/**
	 * @return true iff the challenged player has accepted the challenge
	 */
	public boolean isAccepted() {
		return this.accepted > 0;
	}

	public void accept(long acceptanceTurn, Player challenged) {
		if(this.challenged != challenged) {
			throw new IllegalStateException("Illegal Challenged Player tries to accept challenge " + this.toString());
		}
		this.accepted = acceptanceTurn;
	}

	/**
	 * Checks if a player is involved into this challenge
	 *
	 * @param the player to check
	 * @return true if player equals challenged or challenger
	 */
	public boolean isInvolved(Player player) {
		return this.challenger.equals(player) || this.challenged.equals(player);
	}

	/**
	 * @return the turn number in which this challenge was opened
	 */
	public long getOpened() {
		return opened;
	}

	/**
	 * @return the player who started this challenge
	 */
	public Player getChallenger() {
		return challenger;
	}

	/**
	 * @return the player who was challenged
	 */
	public Player getChallenged() {
		return challenged;
	}

}
