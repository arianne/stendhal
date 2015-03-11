package games.stendhal.server.entity.status;

/**
 * A status effect that causes the player to move more slowly
 */
public class ZombieStatus extends Status {
	
	/**
	 * Create the status
	 */
	public ZombieStatus() {
		super("zombie");
	}
	
	/**
	 * @return
	 * 		StatusType
	 */
	@Override
	public StatusType getStatusType() {
		return StatusType.ZOMBIE;
	}
}
