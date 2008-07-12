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
	public ArrestWarrantList(final StendhalRPZone zone) {
		super(zone, ArrestWarrant.class);
	}

	@Override
	public String getName(final ArrestWarrant arrestWarrant) {
		return arrestWarrant.getCriminal();
	}

	@Override
	public String toString() {
		final StringBuilder who = new StringBuilder();
		for (final ArrestWarrant aw : getList()) {
			who.append(aw.getCriminal());
			who.append(": ");
			who.append(aw.getMinutes());
			who.append(" Minutes because: ");
			who.append(aw.getReason());
			who.append("\n");
		}
		return who.toString();
	}
}
