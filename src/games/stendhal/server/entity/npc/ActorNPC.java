package games.stendhal.server.entity.npc;

/**
 * Simple entity used in spectacles. Can walk around and say monologues, but cannot speak with players.
 *
 * @author yoriy
 *
 */
public class ActorNPC extends NPC {

	private final boolean attackable;

	/**
	 * Create a new ActorNPC.
	 *
	 * @param attackable <code>true</code> if the entity can be attacked,
	 * 	otherwise <code>false</code>
	 */
	public ActorNPC(boolean attackable) {
		this.attackable=attackable;
	}

	/**
	 *
	 */
	@Override
	public boolean isAttackable() {
		return attackable;
	}

}
