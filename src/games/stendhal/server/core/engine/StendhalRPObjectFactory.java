package games.stendhal.server.core.engine;

import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import games.stendhal.server.entity.mapstuff.office.RentedSign;
import games.stendhal.server.entity.mapstuff.spawner.FlowerGrower;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.RPObjectFactory;

import org.apache.log4j.Logger;

/**
 * Creates concrete objects of Stendhal classes.
 *
 * @author hendrik
 */
public class StendhalRPObjectFactory extends RPObjectFactory {
	private static Logger logger = Logger.getLogger(StendhalRPObjectFactory.class);
	private static RPObjectFactory singleton;
	
	@Override
	public RPObject transform(final RPObject object) {
		final RPClass clazz = object.getRPClass();
		if (clazz == null) {
			logger.error("Cannot create concrete object for " + object + " because it does not have an RPClass.");
			return super.transform(object);
		}

		final String name = clazz.getName();

		if (name.equals(ArrestWarrant.RPCLASS_NAME)) {
			return new ArrestWarrant(object);
		} else if (name.equals(RentedSign.RPCLASS_NAME)) {
			return new RentedSign(object);
		} else if (name.equals("growing_entity_spawner")) {
			return createGrower(object);
		}

		// fallback
		return super.transform(object);
	}

	private RPObject createGrower(final RPObject object) {
		String itemname = object.get("class");
		itemname = itemname.substring(itemname.lastIndexOf('/') + 1, itemname.length() - "_grower".length());
		return new FlowerGrower(object, itemname);
	}

	/**
	 * returns the factory instance (this method is called
	 * by Marauroa using reflection).
	 * 
	 * @return RPObjectFactory
	 */
	public static RPObjectFactory getFactory() {
		if (singleton == null) {
			singleton = new StendhalRPObjectFactory();
		}
		return singleton;
	}
}
