package games.stendhal.server.core.engine;

import games.stendhal.server.core.engine.transformer.ArrestWarrantTransformer;
import games.stendhal.server.core.engine.transformer.EarningTransformer;
import games.stendhal.server.core.engine.transformer.FlowerGrowerTransFormer;
import games.stendhal.server.core.engine.transformer.HousePortalTransformer;
import games.stendhal.server.core.engine.transformer.MarketTransformer;
import games.stendhal.server.core.engine.transformer.OfferTransformer;
import games.stendhal.server.core.engine.transformer.PlayerTransformer;
import games.stendhal.server.core.engine.transformer.RentedSignTransformer;
import games.stendhal.server.core.engine.transformer.StoredChestTransformer;
import games.stendhal.server.core.engine.transformer.Transformer;
import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import games.stendhal.server.entity.mapstuff.office.RentedSign;
import games.stendhal.server.trade.Earning;
import games.stendhal.server.trade.Market;
import games.stendhal.server.trade.Offer;

import java.util.HashMap;
import java.util.Map;

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
	private Map<String, Transformer> transformerMap;
	
	
	public StendhalRPObjectFactory() {
		super();
		transformerMap = new HashMap<String, Transformer>();
		transformerMap.put("growing_entity_spawner", new FlowerGrowerTransFormer());
		transformerMap.put(ArrestWarrant.RPCLASS_NAME, new ArrestWarrantTransformer());
		transformerMap.put(RentedSign.RPCLASS_NAME, new RentedSignTransformer());
		transformerMap.put("chest", new StoredChestTransformer());
		transformerMap.put("house_portal", new HousePortalTransformer());
		transformerMap.put(Offer.OFFER_RPCLASS_NAME, new OfferTransformer());
		transformerMap.put(Earning.EARNING_RPCLASS_NAME, new EarningTransformer());
		transformerMap.put(Market.MARKET_RPCLASS_NAME, new MarketTransformer());
		transformerMap.put("player", new PlayerTransformer());
	}
	
	private void fixRPClass(final RPObject object) {
		final RPClass clazz = object.getRPClass();
		if ((clazz == null) || (clazz.getName() == null) || (clazz.getName().trim().equals(""))) {
			if (object.has("type")) {
				logger.warn("Fixing empty class, setting it to type=" + object.get("type") + " on object: " + object);
				object.setRPClass(object.get("type"));
			}
		}
	}
	
	@Override
	public RPObject transform(final RPObject object) {

		fixRPClass(object);

		final RPClass clazz = object.getRPClass();
		if (clazz == null) {
			logger.error("Cannot create concrete object for " + object
					+ " because it does not have an RPClass.");
			return super.transform(object);
		}

		final String name = clazz.getName();
		Transformer trafo = transformerMap.get(name);
		if (trafo == null) {

			return super.transform(object);
		} else {
			return trafo.transform(object);
		}

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
