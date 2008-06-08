package games.stendhal.server.entity.mapstuff.office;

import games.stendhal.server.core.engine.StendhalRPZone;

/**
 * A list of ArrestWarrants as frontend for the zone storage.
 * 
 * @author hendrik
 */
public class ArrestWarrantList extends StoreableEntityList<ArrestWarrant> {

	private static final long serialVersionUID = 9038872708537070249L;

	/**
	 * Creates a new ArrestWarrantList.
	 * 
	 * @param zone
	 *            zone to store the ArrestWarrants in
	 */
	public ArrestWarrantList(StendhalRPZone zone) {
		super(zone, ArrestWarrant.class);
	}

	@Override
	public String getName(ArrestWarrant arrestWarrant) {
		return arrestWarrant.getCriminal();
	}

	@Override
	public String toString() {
		StringBuilder who = new StringBuilder();
		for (ArrestWarrant aw : getList()) {
			who.append(aw.getCriminal());
			who.append("\n");
		}
		return who.toString();
	}
}
