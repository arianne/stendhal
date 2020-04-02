/***************************************************************************
 *                   Copyright (C) 2019 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.scripting.lua;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SilentNPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;


/**
 * A helper class for adding NPCs to game via Lua scripting engine.
 */
public class NPCHelper {

	// logger instance
	private static final Logger logger = Logger.getLogger(NPCHelper.class);

	private static final EntityManager eManager = SingletonRepository.getEntityManager();

	/**
	 * Creates a new SpeakerNPC instance.
	 *
	 * @param name
	 * 			String name of new NPC.
	 * @return
	 * 		New SpeakerNPC instance.
	 */
	public SpeakerNPC createSpeakerNPC(final String name) {
		return new SpeakerNPC(name);
	}

	/**
	 * Created a new SilentNPC instance.
	 *
	 * @return
	 * 		New SilentNPC instance.
	 */
	public SilentNPC createSilentNPC() {
		return new SilentNPC();
	}

	private FixedPath tableToPath(final LuaTable table, final boolean loop) {
		if (!table.istable()) {
			logger.error("Entity path must be a table");
			return null;
		}

		List<Node> nodes = new LinkedList<Node>();

		// Lua table indexing begins at 1
		int index;
		for (index = 1; index <= table.length(); index++) {
			LuaValue point = table.get(index);
			if (point.istable()) {
				LuaValue luaX = ((LuaTable) point).get(1);
				LuaValue luaY = ((LuaTable) point).get(2);

				if (luaX.isinttype() && luaY.isinttype()) {
					Integer X = luaX.toint();
					Integer Y = luaY.toint();

					nodes.add(new Node(X, Y));
				} else {
					logger.error("Path nodes must be integers");
					return null;
				}
			} else {
				logger.error("Invalid table data in entity path");
				return null;
			}
		}

		return new FixedPath(nodes, loop);
	}

	/**
	 * Helper function for setting an NPCs path.
	 *
	 * @param entity
	 * 		The NPC instance of which path is being set.
	 * @param table
	 * 		Lua table with list of coordinates representing nodes.
	 */
	public void setPath(final RPEntity entity, final LuaTable table, Boolean loop) {
		if (loop == null) {
			loop = false;
		}

		entity.setPath(tableToPath(table, loop));
	}

	/**
	 * Helper function for setting an NPCs path & starting position.
	 *
	 * @param entity
	 * 		The NPC instance of which path is being set.
	 * @param table
	 * 		Lua table with list of coordinates representing nodes.
	 */
	public void setPathAndPosition(final RPEntity entity, final LuaTable table, Boolean loop) {
		if (loop == null) {
			loop = false;
		}

		entity.setPathAndPosition(tableToPath(table, loop));
	}

	// FIXME:
	/**
	 * Creates an instance of a ChatAction from the class name string.
	 *
	 * @param className
	 * 		Class basename.
	 * @param params
	 * 		Parameters that should be passed to the constructor.
	 * @return
	 * 		New <code>ChatAction</code> instance or <code>null</code>.
	 */
	/*
	public ChatAction newAction(String className, final Object... params) {
		className = "games.stendhal.server.entity.npc.action." + className;

		try {
			if (params.length == 0) {
				try {
					final Constructor<?> constructor = Class.forName(className).getConstructor();

					return (ChatAction) constructor.newInstance();
				} catch (InvocationTargetException e2) {
				}
			} else {
				final Constructor<?>[] constructors = Class.forName(className).getConstructors();

				for (final Constructor<?> con: constructors) {
					try {
						return (ChatAction) con.newInstance(new Object[] { params });
					} catch (InvocationTargetException e2) {
					}
				}
			}
		} catch (ClassNotFoundException e1) {
			logger.error(e1, e1);
		} catch (InstantiationException e1) {
			logger.error(e1, e1);
		} catch (IllegalAccessException e1) {
			logger.error(e1, e1);
		} catch (IllegalArgumentException e1) {
			logger.error(e1, e1);
		} catch (NoSuchMethodException e1) {
			logger.error(e1, e1);
		} catch (SecurityException e1) {
			logger.error(e1, e1);
		}

		return null;
	}
	*/

	/* overloaded methods don't get called
	public ChatAction newAction(final String className) {
		return newAction(className, new Object[] {});
	}
	*/

	/**
	 * Creates an instance of a ChatCondition from the class name string.
	 *
	 * @param className
	 * 		Class basename.
	 * @param params
	 * 		Parameters that should be passed to the constructor.
	 * @return
	 * 		New <code>ChatCondition</code> instance or <code>null</code>.
	 */
	/*
	public ChatCondition newCondition(String className, final Object... params) {
		className = "games.stendhal.server.entity.npc.condition." + className;

		try {
			final Constructor<?>[] constructors = Class.forName(className).getConstructors();
			for (final Constructor<?> con: constructors) {
				try {
					return (ChatCondition) con.newInstance(new Object[] { params });
				} catch (InvocationTargetException e2) {
				}
			}
		} catch (ClassNotFoundException e1) {
			logger.error(e1, e1);
		} catch (InstantiationException e1) {
			logger.error(e1, e1);
		} catch (IllegalAccessException e1) {
			logger.error(e1, e1);
		} catch (IllegalArgumentException e1) {
			logger.error(e1, e1);
		}

		return null;
	}
	*/

	/**
	 * Adds merchant behavior to a SpeakerNPC.
	 *
	 * @param merchantType
	 * 		If set to "buyer", will add buyer behavior, otherwise will be "seller".
	 * @param npc
	 * 		The SpeakerNPC to add the behavior to.
	 * @param prices
	 * 		List of items & their prices (can be instance of either Map<String, Int> or LuaTable).
	 * @param addOffer
	 * 		If <code>true</code>, will add default replies for "offer" (default: <code>true</code>).
	 */
	@SuppressWarnings("unchecked")
	public void addMerchant(final String merchantType, final SpeakerNPC npc, final Object prices, Boolean addOffer) {
		// default is to add an "offer" response
		if (addOffer == null) {
			addOffer = true;
		}

		Map<String, Integer> priceList = null;
		if (prices instanceof LuaTable) {
			priceList = new LinkedHashMap<>();
			final LuaTable priceTable = (LuaTable) prices;
			for (final LuaValue key: priceTable.keys()) {
				String itemName = key.tojstring();
				final int itemPrice = priceTable.get(key).toint();

				// special handling of underscore characters in item names
				if (itemName.contains("_")) {
					// check if item is real item
					if (!eManager.isItem(itemName)) {
						itemName = itemName.replace("_", " ");
					}
				}

				priceList.put(itemName, itemPrice);
			}
		} else if (prices instanceof Map<?, ?>) {
			priceList = (LinkedHashMap<String, Integer>) prices;
		}

		if (priceList == null) {
			logger.error("Invalid price list type: must by LuaTable or Map<String, Integer>");
			return;
		}

		//final MerchantBehaviour behaviour;
		if (merchantType != null && merchantType.equals("buyer")) {
			new BuyerAdder().addBuyer(npc, new BuyerBehaviour(priceList), addOffer);
		} else {
			new SellerAdder().addSeller(npc, new SellerBehaviour(priceList), addOffer);
		}
	}

	/**
	 * Adds merchant seller behavior to a SpeakerNPC.
	 *
	 * @param npc
	 * 		The SpeakerNPC to add the behavior to.
	 * @param prices
	 * 		List of items & their prices (can be instance of either Map<String, Int> or LuaTable).
	 * @param addOffer
	 * 		If <code>true</code>, will add default replies for "offer" (default: <code>true</code>).
	 */
	public void addSeller(final SpeakerNPC npc, final Object prices, final boolean addOffer) {
		addMerchant("seller", npc, prices, addOffer);
	}

	/**
	 * Adds merchant buyer behavior to a SpeakerNPC.
	 *
	 * @param npc
	 * 		The SpeakerNPC to add the behavior to.
	 * @param prices
	 * 		List of items & their prices (can be instance of either Map<String, Int> or LuaTable).
	 * @param addOffer
	 * 		If <code>true</code>, will add default replies for "offer" (default: <code>true</code>).
	 */
	public void addBuyer(final SpeakerNPC npc, final Object prices, final boolean addOffer) {
		addMerchant("buyer", npc, prices, addOffer);
	}
}
