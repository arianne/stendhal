package games.stendhal.server.entity.creature;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

import java.awt.Shape;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
	protected void addPlayersToReward(RPEntity player) {
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