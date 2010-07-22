package games.stendhal.server.entity.mapstuff.area;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.DelayedPlayerTextSender;
import games.stendhal.server.core.events.MovementListener;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.player.Player;

/**
 * Area that sends a private message to any player entering and/or leaving it.
 */
public class MessagingArea extends AreaEntity implements MovementListener {
	/** true if the area should cover the whole zone. */
	private final boolean coversZone;
	/** message sent to the players entering the area. */
	private final String enterMessage;
	/** message sent to the players leaving the area. */
	private final String leaveMessage;
	
	/**
	 * Create a MessagingArea.
	 * 
	 * @param coversZone true if the area should cover the whole zone
	 * @param width width of the area
	 * @param height height of the area
	 * @param enterMessage message to be sent to players entering the area 
	 * @param leaveMessage message to be sent to players leaving the area
	 */
	public MessagingArea(final boolean coversZone, final int width, final int height, final String enterMessage, 
			final String leaveMessage) {
		super(width, height);
		hide();
		
		this.coversZone = coversZone;
		this.enterMessage = enterMessage;
		this.leaveMessage = leaveMessage;
	}
	
	public void onEntered(final ActiveEntity entity, final StendhalRPZone zone, final int newX, final int newY) {
		if ((enterMessage != null) && (entity instanceof Player)) {
			// needs to be delayed to avoid the message appearing before server
			// welcome on login
			new DelayedPlayerTextSender((Player) entity, enterMessage, NotificationType.SCENE_SETTING, 1);
		}
	}
	
	public void onExited(final ActiveEntity entity, final StendhalRPZone zone, final int newX, final int newY) {
		if ((leaveMessage != null) && (entity instanceof Player)) {
			// needs to be delayed since normal messages get lost in case the player leaves zone
			new DelayedPlayerTextSender((Player) entity, leaveMessage, NotificationType.SCENE_SETTING, 1);
		}
	}
	
	public void onMoved(final ActiveEntity entity, final StendhalRPZone zone, final int oldX, final int oldY, final int newX, final int newY) {
		// required by interface
	}
	
	/**
	 * Called when this object is added to a zone.
	 * 
	 * @param zone
	 *            The zone this was added to.
	 */
	@Override
	public void onAdded(final StendhalRPZone zone) {
		super.onAdded(zone);
		
		if (coversZone) {
			setSize(zone.getWidth(), zone.getHeight());
		}
		zone.addMovementListener(this);
	}

	/**
	 * Called when this object is being removed from a zone.
	 * 
	 * @param zone
	 *            The zone this will be removed from.
	 */
	@Override
	public void onRemoved(final StendhalRPZone zone) {
		zone.removeMovementListener(this);
		super.onRemoved(zone);
	}
}
