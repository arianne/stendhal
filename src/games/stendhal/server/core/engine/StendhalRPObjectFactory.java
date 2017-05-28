/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.transformer.ArrestWarrantTransformer;
import games.stendhal.server.core.engine.transformer.EarningTransformer;
import games.stendhal.server.core.engine.transformer.ExpirationTrackerTransformer;
import games.stendhal.server.core.engine.transformer.FlowerGrowerTransFormer;
import games.stendhal.server.core.engine.transformer.HousePortalTransformer;
import games.stendhal.server.core.engine.transformer.MarketTransformer;
import games.stendhal.server.core.engine.transformer.OfferTransformer;
import games.stendhal.server.core.engine.transformer.PlayerTransformer;
import games.stendhal.server.core.engine.transformer.RentedSignTransformer;
import games.stendhal.server.core.engine.transformer.SpellTransformer;
import games.stendhal.server.core.engine.transformer.StoredChestTransformer;
import games.stendhal.server.core.engine.transformer.Transformer;
import games.stendhal.server.entity.mapstuff.ExpirationTracker;
import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import games.stendhal.server.entity.mapstuff.office.RentedSign;
import games.stendhal.server.entity.spell.Spell;
import games.stendhal.server.entity.trade.Earning;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.RPObjectFactory;

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
		transformerMap.put(Spell.RPCLASS_SPELL, new SpellTransformer());
		transformerMap.put(ExpirationTracker.RPCLASS_EXPIRATION_TRACKER, new ExpirationTrackerTransformer());
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
		}
		return trafo.transform(object);
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
