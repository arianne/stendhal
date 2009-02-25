package games.stendhal.client.entity;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * translates the visual representation into server side commands.
 * 
 * @author astridemma
 */
public enum ActionType {
	LOOK("look", "Look"),
	READ("look", "Read"),
	INSPECT("inspect", "Inspect"),
	ATTACK("attack", "Attack"),
	STOP_ATTACK("stop", "Stop attack"),
	PUSH("push", "Push"),
	CLOSE("use", "Close"),
	OPEN("use", "Open"),
	OWN("own", "Own"),
	USE("use", "Use"),
	HARVEST("use", "Harvest"),
	PICK("use", "Pick"),
	PROSPECT("use", "Prospect"),
	FISH("use", "Fish"),
	WISH("use", "Make a Wish"),
	LEAVE_SHEEP("forsake", "Leave sheep"),
	LEAVE_PET("forsake", "Leave pet"),
	ADD_BUDDY("addbuddy", "Add to Buddies"),
	ADMIN_INSPECT("inspect", "(*)Inspect"),
	ADMIN_DESTROY("destroy", "(*)Destroy"),
	ADMIN_ALTER("alter", "(*)Alter"),
	SET_OUTFIT("outfit", "Set outfit"),
	ADMIN_VIEW_NPC_TRANSITIONS("npctransitions", "(*)View Transitions");
	// JOIN_GUILD("guild", "Manage Guilds");

	/**
	 * the String send to the server, if so.
	 */
	private final String actionCode;

	/**
	 * the String which is shown to the user.
	 */
	private final String actionRepresentation;

	/**
	 * Constructor.
	 * 
	 * @param actCode
	 *            the code to be sent to the server
	 * @param actionRep
	 *            the String to be shown to the user
	 */
	private ActionType(final String actCode, final String actionRep) {
		actionCode = actCode;
		actionRepresentation = actionRep;
	}

	/**
	 * finds the ActionType that belongs to a visual String representation.
	 * 
	 * @param representation
	 *            the menu String
	 * @return the Action Element or null if not found
	 */
	public static ActionType getbyRep(final String representation) {
		for (final ActionType at : ActionType.values()) {
			if (at.actionRepresentation.equals(representation)) {
				return at;
			}

		}
		Logger.getLogger(ActionType.class).error(
				representation + " =code: not found");
		return null;
	}

	/**
	 * @return the command code for usage on server side
	 */
	@Override
	public String toString() {
		return actionCode;
	}

	/**
	 * @return the String the user should see on the menu
	 */
	public String getRepresentation() {
		return actionRepresentation;
	}

	/**
	 * sends the requested action to the server.
	 * 
	 * @param rpaction
	 *            action to be sent
	 */
	public void send(final RPAction rpaction) {
		StendhalClient.get().send(rpaction);
	}
}
