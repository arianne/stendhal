package games.stendhal.server.entity.status;

/**
 * A status effect that causes the player to move more slowly
 */
public class ZombieStatus extends Status {
	
	/** The original speed of the entity */
	private double originalSpeed;
	
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
	
	/**
	 * @param speed
	 * 		The default speed of the entity
	 */
	public void setOriginalSpeed(double speed) {
		originalSpeed = speed;
	}
	
	/**
	 * @return
	 * 		The default speed of the entity
	 */
	public double getOriginalSpeed() {
		return originalSpeed;
	}

}
