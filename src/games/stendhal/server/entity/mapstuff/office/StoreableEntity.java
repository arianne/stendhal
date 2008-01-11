package games.stendhal.server.entity.mapstuff.office;

/**
 * An entity that will be stored by the zone to the database.
 *
 * @author hendrik
 */
public interface StoreableEntity {

	/**
	 * Define this object as storable, but it doesn't in fact store the object.
	 * The object is stored on zone.finish
	 */
	void store();
}
