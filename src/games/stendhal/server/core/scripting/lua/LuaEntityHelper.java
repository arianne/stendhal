/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
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

import static games.stendhal.common.constants.Actions.SUMMON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.scripting.ScriptInLua.LuaLogger;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.mapstuff.sign.Reader;
import games.stendhal.server.entity.mapstuff.sign.ShopSign;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SilentNPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;


/**
 * Exposes some entity classes & functions to Lua.
 */
public class LuaEntityHelper {

	private static LuaLogger logger = LuaLogger.get();

	public static final EntityManager manager = SingletonRepository.getEntityManager();

	private static final LuaConditionHelper conditionHelper = LuaConditionHelper.get();
	private static final LuaActionHelper actionHelper = LuaActionHelper.get();

	private static LuaEntityHelper instance;


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 * 		Static EntityHelper instance.
	 */
	public static LuaEntityHelper get() {
		if (instance == null) {
			instance = new LuaEntityHelper();
		}

		return instance;
	}

	/**
	 * Converts a table of coordinates to a FixedPath instance.
	 *
	 * @param table
	 * 		Table containing coordinates.
	 * @param loop
	 * 		If <code>true</code>, the path should loop.
	 * @return
	 * 		New FixedPath instance.
	 */
	private static FixedPath tableToPath(final LuaTable table, final boolean loop) {
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
	 * Retrieves a logged in Player.
	 *
	 * @param name
	 * 		Name of player.
	 * @return
	 * 		Logged in player or <code>null</code>.
	 */
	public Player getPlayer(final String name) {
		return SingletonRepository.getRuleProcessor().getPlayer(name);
	}

	/**
	 * Retrieves an existing SpeakerNPC.
	 *
	 * FIXME: cannot cast to LuaSpeakerNPC, so specialized methods will not work
	 * 			with entities retrieved from this method that are not instances
	 * 			of LuaSpeakerNPC.
	 *
	 * @param name
	 * 		Name of NPC.
	 * @return
	 * 		SpeakerNPC instance or <code>null</code>.
	 */
	public SpeakerNPC getNPC(final String name) {
		final SpeakerNPC npc = SingletonRepository.getNPCList().get(name);

		if (npc == null) {
			logger.warn("NPC \"" + name + "\" not found");
			return null;
		}
		if (!(npc instanceof LuaSpeakerNPC)) {
			logger.warn("Lua call to entities:getNPC did not return LuaSpeakerNPC instance, specialized methods will fail with NPC \"" + npc.getName() + "\"");
		}

		return npc;
	}

	/**
	 * Retrieves a registered Item.
	 *
	 * @param name
	 * 		Name of the item.
	 * @return
	 * 		Item instance or <code>null</code> if not a registered item.
	 */
	public Item getItem(final String name) {
		return manager.getItem(name);
	}

	/**
	 * Retrieves a registered StackableItem.
	 *
	 * @param name
	 * 		Name of the item.
	 * @return
	 * 		StackableItem instance or <code>null</code> if not a registered stackable item.
	 */
	public StackableItem getStackableItem(final String name) {
		final Item item = getItem(name);
		if (item instanceof StackableItem) {
			return (StackableItem) item;
		}

		return null;
	}

	/**
	 * Creates a new SpeakerNPC instance.
	 *
	 * @param name
	 * 		Name of new NPC.
	 * @return
	 * 		New SpeakerNPC instance.
	 */
	public LuaSpeakerNPC createSpeakerNPC(final String name) {
		return new LuaSpeakerNPC(name);
	}

	/**
	 * Creates a new SilentNPC instance.
	 *
	 * @return
	 * 		New SilentNPC instance.
	 */
	public LuaSilentNPC createSilentNPC() {
		return new LuaSilentNPC();
	}

	/**
	 * Helper function for setting an NPCs path.
	 *
	 * @param entity
	 * 		The NPC instance of which path is being set.
	 * @param table
	 * 		Lua table with list of coordinates representing nodes.
	 */
	@Deprecated
	public void setPath(final RPEntity entity, final LuaTable table, Boolean loop) {
		logger.warn("entities:setPath is deprecated. Call \"setPath\" directly from the entity instance.");

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
	@Deprecated
	public void setPathAndPosition(final RPEntity entity, final LuaTable table, Boolean loop) {
		logger.warn("entities:setPathAndPosition is deprecated. Call \"setPathAndPosition\" directly from the entity instance.");

		if (loop == null) {
			loop = false;
		}

		entity.setPathAndPosition(tableToPath(table, loop));
	}

	/**
	 * Sets a LuaGuidedEntity's path using a table.
	 *
	 * @param entity
	 * 		The NPC instance of which path is being set.
	 * @param table
	 * 		Lua table with list of coordinates representing nodes.
	 */
	private static void setEntityPath(final LuaGuidedEntity entity, final LuaTable table, Boolean loop) {
		if (loop == null) {
			loop = false;
		}

		entity.setPath(tableToPath(table, loop));
	}

	/**
	 * Sets a LuaGuidedEntity's path & starting position using a table.
	 *
	 * @param entity
	 * 		The NPC instance of which path is being set.
	 * @param table
	 * 		Lua table with list of coordinates representing nodes.
	 */
	private static void setEntityPathAndPosition(final LuaGuidedEntity entity, final LuaTable table, Boolean loop) {
		if (loop == null) {
			loop = false;
		}

		entity.setPathAndPosition(tableToPath(table, loop));
	}

	/**
	 * Creates a new Sign entity.
	 *
	 * @return
	 * 		New Sign instance.
	 */
	public Sign createSign() {
		return createSign(true);
	}

	/**
	 * Creates a new Sign entity.
	 *
	 * @param visible
	 * 		If <code>false</code>, sign does not have a visual representation.
	 * @return
	 * 		New Sign instance.
	 */
	public Sign createSign(final boolean visible) {
		if (visible) {
			return new Sign();
		}

		return new Reader();
	}

	/**
	 * Creates a new ShopSign entity.
	 *
	 * @param name
	 * 		The shop name.
	 * @param title
	 * 		The sign title.
	 * @param caption
	 * 		The caption above the table.
	 * @param seller
	 * 		<code>true</code>, if this sign is for items sold by an NPC (defaults to <code>true</code> if <code>null</code>).
	 * @return
	 * 		New ShopSign instance.
	 */
	public ShopSign createShopSign(final String name, final String title, final String caption, Boolean seller) {
		// default to seller
		if (seller == null) {
			seller = true;
		}

		return new ShopSign(name, title, caption, seller);
	}

	/**
	 * Summons a creature into the world.
	 *
	 * FIXME: "coercion error java.lang.IllegalArgumentException: argument type mismatch" occurs if "raid" is LuaBoolean or LuaValue type
	 *
	 * @param name
	 * 		Name of creature to be summoned.
	 * @param zone
	 * 		Name of zone where creature should be summoned.
	 * @param x
	 * 		Horizontal position of summon location.
	 * @param y
	 * 		Vertical position of summon location.
	 * @param summoner
	 * 		Name of entity doing the summoning.
	 * @param raid
	 * 		(boolean) Whether or not the creature should be a RaidCreature instance (default: true)
	 * @return
	 * 		0 = success
	 * 		1 = creature not found
	 * 		2 = zone not found
	 */
	private int summonCreature(final String name, final String zoneName, final int x, final int y, String summoner, final boolean raid) {
		if (summoner == null) {
			logger.warn("Unknown summoner");
			summoner = getClass().getName();
		}

		if (!manager.isCreature(name)) {
			return 1;
		}

		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(zoneName);
		if (zone == null) {
			return 2;
		}

		if (raid) {
			final RaidCreature creature = new RaidCreature(manager.getCreature(name));
			StendhalRPAction.placeat(zone, creature, x, y);
		} else {
			final Creature creature = manager.getCreature(name); // use standard creatures
			StendhalRPAction.placeat(zone, creature, x, y);
		}

		new GameEvent(summoner, SUMMON, name).raise();

		return 0;
	}

	public int summonCreature(final LuaTable table) {
		final String name = table.get("name").tojstring();
		final String zoneName = table.get("zone").tojstring();
		final Integer x = table.get("x").toint();
		final Integer y = table.get("y").toint();
		String summoner = null;
		boolean raid = true;

		final LuaValue checksummoner = table.get("summoner");
		if (checksummoner != null && !checksummoner.isnil() && checksummoner.isstring()) {
			summoner = checksummoner.tojstring();
		}

		final LuaValue checkraid = table.get("raid");
		if (checkraid != null && !checkraid.isnil() && checkraid.isboolean()) {
			raid = checkraid.toboolean();
		}

		return summonCreature(name, zoneName, x, y, summoner, raid);
	}


	/**
	 * A special interface that overloads setPath & setPathAndPosition
	 * methods to accept a Lua table as parameter argument.
	 */
	private interface LuaGuidedEntity {

		public void setPath(final FixedPath path);

		public void setPath(final LuaTable table, Boolean loop);

		public void setPathAndPosition(final FixedPath path);

		public void setPathAndPosition(final LuaTable table, Boolean loop);
	}

	private class LuaSpeakerNPC extends SpeakerNPC implements LuaGuidedEntity {

		public LuaSpeakerNPC(final String name) {
			super(name);
		}

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
				} else if (conditions instanceof LuaFunction) {
					listenConditions = conditionHelper.create((LuaFunction) conditions);
				} else {
					listenConditions = conditionHelper.andCondition((LuaTable) conditions);
				}
			}

			if (actions != null) {
				if (actions instanceof ChatAction) {
					listenActions = (ChatAction) actions;
				} else if (actions instanceof LuaFunction) {
					listenActions = actionHelper.create((LuaFunction) actions);
				} else {
					listenActions = actionHelper.multiple((LuaTable) actions);
				}
			}

			add(listenStates, listenTriggers, listenConditions, nextState, reply, listenActions);
		}

		@Override
		public void setPath(LuaTable table, Boolean loop) {
			LuaEntityHelper.setEntityPath(this, table, loop);
		}

		@Override
		public void setPathAndPosition(LuaTable table, Boolean loop) {
			LuaEntityHelper.setEntityPathAndPosition(this, table, loop);
		}
	}

	private class LuaSilentNPC extends SilentNPC implements LuaGuidedEntity {

		@Override
		public void setPath(LuaTable table, Boolean loop) {
			LuaEntityHelper.setEntityPath(this, table, loop);
		}

		@Override
		public void setPathAndPosition(LuaTable table, Boolean loop) {
			LuaEntityHelper.setEntityPathAndPosition(this, table, loop);
		}

	}
}
