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

import java.util.ArrayList;
import java.util.Arrays;
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
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SilentNPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;


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
		return new SpeakerNPC(name) {
			/**
			 * Additional method to support transitions using Lua tables.
			 *
			 * @param states
			 * 		The conversation state(s) the entity should be in to trigger response.
			 * 		Can be ConversationStates enum value or LuaTable of ConversationStates.
			 * @param triggers
			 * 		String or LuaTable of strings to trigger response.
			 * @param conditions
			 * 		ChatCondition instance or LuaTable of ChatCondition instances.
			 * @param nextState
			 * 		Conversation state to set entity to after response.
			 * @param reply
			 * 		The NPC's response or <code>null</code>
			 * @param actions
			 * 		ChatAction instance or LuaTable of ChatAction instances.
			 */
			@SuppressWarnings({ "unused", "unchecked" })
			public void add(final Object states, final Object triggers, final Object conditions,
					final ConversationStates nextState, final String reply, final Object actions) {

				ConversationStates[] listenStates = null;
				List<String> listenTriggers = null;
				ChatCondition listenConditions = null;
				ChatAction listenActions = null;

				if (states != null) {
					if (states instanceof ConversationStates) {
						listenStates = Arrays.asList((ConversationStates) states).toArray(new ConversationStates[] {});
					} else {
						final List<ConversationStates> tmp = new LinkedList<>();
						final LuaTable table = (LuaTable) states;
						for (final LuaValue idx: table.keys()) {
							final ConversationStates state = (ConversationStates) table.get(idx).touserdata(ConversationStates.class);

							if (state == null) {
								logger.error("Invalid ConversationStates data");
								continue;
							}

							tmp.add(state);
						}

						listenStates = tmp.toArray(new ConversationStates[] {});
					}
				}

				if (triggers != null) {
					listenTriggers = new ArrayList<>();
					if (triggers instanceof String) {
						listenTriggers.add((String) triggers);
					} else if (triggers instanceof List) {
						listenTriggers.addAll((List<String>) triggers);
					} else {
						final LuaTable table = (LuaTable) triggers;
						for (final LuaValue idx: table.keys()) {
							listenTriggers.add(table.get(idx).tojstring());
						}
					}
				}

				if (conditions != null) {
					if (conditions instanceof ChatCondition) {
						listenConditions = (ChatCondition) conditions;
					} else {
						listenConditions = newAndCondition((LuaTable) conditions);
					}
				}

				if (actions != null) {
					if (actions instanceof ChatAction) {
						listenActions = (ChatAction) actions;
					} else {
						listenActions = newMultipleActions((LuaTable) actions);
					}
				}

				add(listenStates, listenTriggers, listenConditions, nextState, reply, listenActions);
			}
		};
	}

	/**
	 * Creates a new SilentNPC instance.
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

	/**
	 * Helper method for creating a NotCondition instance.
	 *
	 * @param condition
	 * 		Condition to be checked.
	 * @return
	 * 		New NotCondition instance.
	 */
	public NotCondition newNotCondition(final ChatCondition condition) {
		return new NotCondition(condition);
	}

	/**
	 * Helper method for creating a NotCondition instance.
	 *
	 * @param lv
	 * 		Condition to be checked inside a LuaValue instance or list of
	 * 		conditions inside a LuaTable.
	 * @return
	 * 		New NotCondition instance.
	 */
	public NotCondition newNotCondition(final LuaValue lv) {
		if (lv.istable()) {
			return newNotCondition(newAndCondition(lv.checktable()));
		}

		return newNotCondition((ChatCondition) lv.touserdata(ChatCondition.class));
	}

	/**
	 * Helper method to create a AndCondition instance.
	 *
	 * @param conditionList
	 * 		LuaTable containing a list of ChatCondition instances.
	 * @return
	 * 		New AndCondition instance.
	 */
	public AndCondition newAndCondition(final LuaTable conditionList) {
		final List<ChatCondition> conditions = new LinkedList<>();
		for (final LuaValue idx: conditionList.keys()) {
			final LuaValue value = conditionList.get(idx);
			if (value.istable()) {
				conditions.add(newAndCondition(value.checktable()));
			} else if (value.isuserdata(ChatCondition.class)) {
				conditions.add((ChatCondition) value.touserdata(ChatCondition.class));
			} else {
				logger.warn("Invalid data type. Must be ChatCondition.");
			}
		}

		return new AndCondition(conditions.toArray(new ChatCondition[] {}));
	}

	/**
	 * Helper method for creating a MultipleActions instance.
	 *
	 * @param actionList
	 * 		LuaTable containing list of ChatAction instances.
	 * @return
	 * 		New MultipleActions instance.
	 */
	public MultipleActions newMultipleActions(final LuaTable actionList) {
		final List<ChatAction> actions = new LinkedList<>();
		for (final LuaValue idx: actionList.keys()) {
			final LuaValue value = actionList.get(idx);
			if (value.istable()) {
				actions.add(newMultipleActions(value.checktable()));
			} else if (value.isuserdata(ChatAction.class)) {
				actions.add((ChatAction) value.touserdata(ChatAction.class));
			} else {
				logger.warn("Invalid data type. Must be ChatAction or LuaTable.");
			}
		}

		return new MultipleActions(actions.toArray(new ChatAction[] {}));
	}
}
