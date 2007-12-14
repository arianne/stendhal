package games.stendhal.server.entity.mapstuff.office;

import games.stendhal.server.StendhalRPZone;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPObject;

/**
 * a list of ArrestWarrants as frontend for the the zoen storage
 * 
 * @author hendrik
 */
public class ArrestWarrantList {

	private static final long serialVersionUID = 9038872708537070249L;
	private StendhalRPZone zone;

	/**
	 * creates a new ArrestWarrantList
	 * 
	 * @param zone
	 *            zone to store the ArrestWarrants in
	 */
	public ArrestWarrantList(StendhalRPZone zone) {
		this.zone = zone;
	}

	/**
	 * Adds an ArrestWarrant
	 * 
	 * @param warrant
	 *            ArrestWarrant
	 */
	public void add(ArrestWarrant warrant) {
		zone.add(warrant);
		zone.storeToDatabase();
	}

	/**
	 * returns the ArrestWarrant for the specified player name
	 * 
	 * @param criminal
	 *            name of player to be arrested
	 * @return ArrestWarrant or <code>null</code> in case there is none
	 */
	public ArrestWarrant getByName(String criminal) {
		List<ArrestWarrant> arrestWarrants = getList();
		for (ArrestWarrant arrestWarrant : arrestWarrants) {
			if (arrestWarrant.getCriminal().equals(criminal)) {
				return arrestWarrant;
			}
		}
		return null;
	}

	/**
	 * removes all ArrestWarrants for this player
	 * 
	 * @param criminal
	 *            name of player
	 */
	public void removeByName(String criminal) {
		List<ArrestWarrant> arrestWarrants = getList();
		for (ArrestWarrant arrestWarrant : arrestWarrants) {
			if (arrestWarrant.getCriminal().equals(criminal)) {
				zone.remove(arrestWarrant);
			}
		}
		zone.storeToDatabase();
	}

	/**
	 * gets a list of ArrestWarrant from the zone storage. Note: This is only a
	 * temporary snapshot, do not save it outside the scope of a method.
	 * 
	 * @return List of ArrestWarrants.
	 */
	private List<ArrestWarrant> getList() {
		List<ArrestWarrant> res = new LinkedList<ArrestWarrant>();
		for (RPObject object : zone) {
			if (object instanceof ArrestWarrant) {
				ArrestWarrant arrestWarrant = (ArrestWarrant) object;
				res.add(arrestWarrant);
			}
		}
		return res;
	}
}
