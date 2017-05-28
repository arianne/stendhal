package games.stendhal.server.core.rp.pvp;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

import games.stendhal.server.entity.player.Player;

public class PlayerVsPlayerChallenge {

	private final long opened;
	private long accepted = -1l;
	//private long lastAction = -1l;
	private final String challenger;
	private final String challenged;

	public PlayerVsPlayerChallenge(long opened, Player challenger, Player challenged) {
		this.opened = opened;
		this.challenger = challenger.getName();
		this.challenged = challenged.getName();
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
		ToStringHelper helper = Objects.toStringHelper(this)
				.add("challenger", this.challenger)
				.add("challenged", this.challenged)
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
		if(this.challenged != challenged.getName()) {
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
		return this.challenger.equals(player.getName()) || this.challenged.equals(player.getName());
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
	public String getChallenger() {
		return challenger;
	}

	/**
	 * @return the player who was challenged
	 */
	public String getChallenged() {
		return challenged;
	}

}
