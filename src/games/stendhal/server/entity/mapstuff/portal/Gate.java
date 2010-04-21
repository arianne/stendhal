package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.Type;

public class Gate extends Entity implements UseListener {
	private static final String HORIZONTAL = "h";
	private static final String VERTICAL = "v";
	private static final String ORIENTATION = "orientation";
	private static final String IMAGE = "image";
	
	private static final String DEFAULT_IMAGE = "fence_gate";

	public static void generateGateRPClass() {
		if (!RPClass.hasRPClass("gate")) {
			final RPClass gate = new RPClass("gate");
			gate.isA("entity");
			gate.addAttribute(ORIENTATION, Type.STRING);
			gate.addAttribute(IMAGE, Type.STRING);
		}
	}
	
	private boolean isOpen;

	/**
	 * Create a new gate.
	 * 
	 * @param orientation gate orientation. Either "v" or "h".
	 */
	public Gate(final String orientation, String image) {
		setRPClass("gate");
		put("type", "gate");
		setOrientation(orientation);
		setOpen(false);
		if (image != null) {
			put(IMAGE, image);
		} else {
			put(IMAGE, DEFAULT_IMAGE);
		}
	}
	
	/**
	 * Create a new vertical gate.
	 */
	public Gate() {
		this(VERTICAL, null);
	}

	/**
	 * Set the orientation of the gate.
	 * 
	 * @param orientation "h" for horizontal, "v" for vertical
	 */
	private void setOrientation(final String orientation) {
		if (HORIZONTAL.equals(orientation)) {
			put(ORIENTATION, HORIZONTAL);
		} else {
			put(ORIENTATION, VERTICAL);
		}
	}

	/**
	 * Open the gate.
	 */
	public void open() {
		setOpen(true);
	}

	/**
	 * Check if the gate is open.
	 * 
	 * @return true iff the gate is open
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * Close the gate.
	 */
	public void close() {
		setOpen(false);
	}

	public boolean onUsed(final RPEntity user) {
		if (this.nextTo(user)) {
			setOpen(!isOpen());
			return true;
		}
		return false;
	}

	/**
	 * Set the door open or closed.
	 * 
	 * @param open true if the door is opened, false otherwise
	 */
	private void setOpen(final boolean open) {
		if (open) {
			setResistance(0);
		} else {
			// Closing the gate - check there's nobody on the way
			if (getZone() != null)  {
				for (Entity entity : getZone().getEntitiesAt(getX(), getY())) {
					if (entity.getResistance() > 0) {
						return;
					}
				}
			}
			setResistance(100);
		}
		isOpen = open;
		notifyWorldAboutChanges();
	}
}
