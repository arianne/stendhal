package games.stendhal.server.entity.creature;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;

/**
 * An creature that will only reward the specified player 
 *
 * @author hendrik
 */
public class DeathMatchCreature extends Creature {

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

	public void setPlayerToReward(Player player) {
		playersToReward.clear();
		playersToReward.add(player);
	}

}
