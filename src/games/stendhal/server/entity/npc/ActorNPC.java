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
	 * constructor
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
