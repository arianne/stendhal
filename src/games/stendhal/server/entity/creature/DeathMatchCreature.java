package games.stendhal.server.entity.creature;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;

/**
 * An creature that will only reward the specified player 
 *
 * @author hendrik
 */
public class DeathMatchCreature extends Creature {

	private int points = 0;
	private Player player = null;

	/**
	 * DeathCreature
	 *
	 * @param copy creature to wrap
	 */
	public DeathMatchCreature(Creature copy) {
		super(copy);
	}

	@Override
	protected void addPlayersToReward(Entity player) {
		// don't reward the other attackers
	}

	@Override
	public Creature getInstance() {
		return new DeathMatchCreature(this);
	}

	/**
	 * Only this player gets an XP reward.
	 *
	 * @param player Player to reward
	 */
	public void setPlayerToReward(Player player) {
		this.player = player;
	}

	@Override
	protected void rewardKillers(int oldXP, int oldLevel) {
		Integer damageReceivedByPlayer = damageReceived.get(player);
		if (damageReceivedByPlayer != null) {
			int basePoints = player.getLevel();
			points = basePoints * (damageReceivedByPlayer / totalDamageReceived);
		} else {
			Logger.getLogger(DeathMatchCreature.class).error(damageReceived);
		}
	}

	/**
	 * Calculates the deathmatch points for this kill
	 *
	 * @return number of points to reward
	 */
	public int getDMPoints() {
		return points ;
	}

}
