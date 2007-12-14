package games.stendhal.server.entity.mapstuff.office;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

/**
 * are persitent arrest warrant
 *
 * @author hendrik
 */
public class ArrestWarrant extends Entity {
	private static final String CRIMINAL = "criminal";
	private static final String POLICE_OFFICER = "police_officer";
	private static final String MINUTES = "minutes";
	private static final String REASON = "reason";
	private static final String TIMESTAMP = "timestamp";
	private static final String STARTED = "started";

	public static void generateRPClass() {
		RPClass clazz = new RPClass("arrest_warrant");
		clazz.isA("entity");
		clazz.addAttribute(CRIMINAL, Type.STRING, Definition.HIDDEN);
		clazz.addAttribute(POLICE_OFFICER, Type.STRING, Definition.HIDDEN);
		clazz.addAttribute(MINUTES, Type.INT, Definition.HIDDEN);
		clazz.addAttribute(REASON, Type.LONG_STRING, Definition.HIDDEN);
		clazz.addAttribute(TIMESTAMP, Type.FLOAT, Definition.HIDDEN);
		clazz.addAttribute(STARTED, Type.FLAG, Definition.HIDDEN);
	}

	/**
	 * Creates an ArrestWarrant
	 *
	 * @param criminalName  name of criminal to be jailed
	 * @param policeOfficer name of police officer who issued the /jail command
	 * @param minutes time of sentence
	 * @param reason reason
	 */
	public ArrestWarrant(String criminalName, Player policeOfficer, int minutes, String reason) {
		store();
		put(CRIMINAL, criminalName);
		put(POLICE_OFFICER, policeOfficer.getName());
		put(MINUTES, minutes);
		put(REASON, reason);
		put(TIMESTAMP, System.currentTimeMillis());
	}

	/**
	 * creates an ArrestWarrant based on a deserialized RPObject; 
	 * use the other constructor.
	 *
	 * @param rpobject RPObject
	 */
	public ArrestWarrant(RPObject rpobject) {
		super(rpobject);
		store();
	}

	/**
	 * gets the name of the criminal
	 *
	 * @return name of criminal
	 */
	public String getCriminal() {
		return get(CRIMINAL);
	}
}
