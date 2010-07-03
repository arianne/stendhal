package games.stendhal.client.entity;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * translates the visual representation into server side commands.
 * 
 * @author astridemma
 */
public enum ActionType {
	LOOK("look", "Look"),
	READ("look", "Read"),
	LOOK_CLOSELY("use", "Look closely"),
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
	LEAVE_SHEEP("forsake", "Leave sheep") {
		@Override
		public RPAction fillTargetInfo(final RPObject arg0) {
			RPAction rpaction = super.fillTargetInfo(arg0);
			rpaction.put("species", "sheep");
			return rpaction;
		}
	},
	LEAVE_PET("forsake", "Leave pet") {
		@Override
		public RPAction fillTargetInfo(final RPObject arg0) {
			RPAction rpaction = super.fillTargetInfo(arg0);
			rpaction.put("species", "pet");
			return rpaction;
		}
	},
	ADD_BUDDY("addbuddy", "Add to Buddies") {
		@Override
		public RPAction fillTargetInfo(final RPObject object) {
			RPAction rpaction = super.fillTargetInfo(object);
			rpaction.put("target", object.get("name"));
			return rpaction;
		}
	},
	IGNORE("ignore", "Ignore") {
		@Override
		public RPAction fillTargetInfo(final RPObject object) {
			RPAction rpaction = super.fillTargetInfo(object);
			rpaction.put("target", object.get("name"));
			return rpaction;
		}
	},
	UNIGNORE("unignore", "Remove Ignore") {
		@Override
		public RPAction fillTargetInfo(final RPObject object) {
			RPAction rpaction = super.fillTargetInfo(object);
			rpaction.put("target", object.get("name"));
			return rpaction;
		}
	},
	ADMIN_INSPECT("inspect", "(*)Inspect"),
	ADMIN_DESTROY("destroy", "(*)Destroy"),
	ADMIN_ALTER("alter", "(*)Alter"),
	SET_OUTFIT("outfit", "Set outfit"),
	WHERE("where", "Where") {
		@Override
		public RPAction fillTargetInfo(final RPObject object) {
			RPAction rpaction = super.fillTargetInfo(object);
			rpaction.put("target", object.get("name"));
			return rpaction;
		}
	},
	ADMIN_VIEW_NPC_TRANSITIONS("npctransitions", "(*)View Transitions"),
	KNOCK("knock", "Knock");
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
	
	public RPAction fillTargetInfo(final RPObject rpObject) {
		
		RPAction rpaction = new RPAction();
		
		rpaction.put("type", toString());
		final int id = rpObject.getID().getObjectID();

		if (rpObject.isContained()) {
			rpaction.put("baseobject",
					rpObject.getContainer().getID().getObjectID());
			rpaction.put("baseslot", rpObject.getContainerSlot().getName());
			rpaction.put("baseitem", id);
		} else {
			StringBuilder target;
			target = new StringBuilder("#");
			target.append(Integer.toString(id));
			rpaction.put("target", target.toString());
		}
		
		return rpaction;
	}
	
	
}
