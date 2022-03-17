/***************************************************************************
 *                     Copyright © 2022 - Arianne                          *
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
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.scripting.ScriptInLua.LuaLogger;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.Entity;
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
import games.stendhal.server.entity.npc.PassiveNPC;
import games.stendhal.server.entity.npc.SilentNPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;


/**
 * Exposes some entity classes & functions to Lua.
 */
public class LuaEntityHelper {

	private static LuaLogger logger = LuaLogger.get();

	/** The singleton instance. */
	private static LuaEntityHelper instance;

	public static final EntityManager manager = SingletonRepository.getEntityManager();

	private static final LuaConditionHelper conditionHelper = LuaConditionHelper.get();
	private static final LuaActionHelper actionHelper = LuaActionHelper.get();


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
	 * Hidden singleton constructor.
	 */
	private LuaEntityHelper() {
		// singleton
	}

	/**
	 * Creates a new `Entity` instance.
	 *
	 * @param lt
	 *     Entity definition table.
	 * @return
	 *     New `Entity` instance.
	 */
	public Entity create(final LuaTable lt) {
		lt.checktable();

		String e_type = lt.get("type").tojstring();

		// default to SilentNPC
		if (e_type == null) {
			e_type = "silentnpc";
		}

		switch (e_type.toLowerCase()) {
			case "speakernpc":
				return buildSpeakerNPC(lt);
			case "silentnpc":
				return buildSilentNPC(lt);
			case "sign":
				return buildSign(lt);
			case "reader":
				return buildReader(lt);
			case "shopsign":
				return buildShopSign(lt);
			default:
				logger.error("unknown entity type: " + e_type);
		}

		return null;
	}

	/**
	 * Adds attributes defined in {@link games.stendhal.server.entity.Entity}.
	 *
	 * @param ent
	 *     The entity to whom attributes will be added.
	 * @param lt
	 *     Lua table of attributes.
	 */
	private void setEntityTraits(final Entity ent, final LuaTable lt) {
		final LuaValue l_pos = lt.get("pos");
		if (!l_pos.isnil()) {
			ent.setPosition(l_pos.get(1).checkint(), l_pos.get(2).checkint());
		}

		final LuaValue l_desc = lt.get("description");
		if (!l_desc.isnil()) {
			ent.setDescription(l_desc.checkjstring());
		}

		final LuaValue l_class = lt.get("class");
		if (!l_class.isnil()) {
			ent.setEntityClass(l_class.checkjstring());
		}

		final LuaValue l_subclass = lt.get("subclass");
		if (!l_subclass.isnil()) {
			ent.setEntitySubclass(l_subclass.checkjstring());
		}

		final LuaValue l_resistance = lt.get("resistance");
		if (!l_resistance.isnil()) {
			ent.setResistance(l_resistance.checkint());
		}

		final LuaValue l_size = lt.get("size");
		if (!l_size.isnil()) {
			ent.setSize(l_size.get(1).checkint(), l_size.get(2).checkint());
		}

		final LuaValue l_cursor = lt.get("cursor");
		if (!l_cursor.isnil()) {
			ent.setCursor(l_cursor.checkjstring());
		}

		final LuaValue l_visibility = lt.get("visibility");
		if (!l_visibility.isnil()) {
			ent.setVisibility(l_visibility.checkint());
		}

		final LuaValue l_menu = lt.get("menu");
		if (!l_menu.isnil()) {
			ent.setMenu(l_menu.checkjstring());
		}
	}

	/**
	 * Adds attributes defined in {@link games.stendhal.server.entity.npc.PassiveNPC}.
	 *
	 * @param npc
	 *     The entity to whom attributes will be added.
	 * @param lt
	 *     Lua table of attributes.
	 */
	private void setNPCTraits(final PassiveNPC npc, final LuaTable lt) {

		// *** Entity ***

		setEntityTraits(npc, lt);

		// *** ActiveEntity ***

		final LuaValue l_dir = lt.get("dir");
		if (!l_dir.isnil()) {
			npc.setDirection((Direction) l_dir.checkuserdata(Direction.class));
		}

		final LuaValue l_ignoresCollision = lt.get("ignoresCollision");
		if (!l_ignoresCollision.isnil()) {
			npc.setIgnoresCollision(l_ignoresCollision.checkboolean());
		}

		// *** GuidedEntity ***

		LuaValue l_pos = lt.get("pos"); // Entity.class
		final LuaValue l_path = lt.get("path");

		// entity starts at first node of path
		if (l_pos.isnil()) {
			l_pos = l_path.get(1);
		}

		if (!l_pos.isnil()) {
			l_pos.checktable();
			npc.setPosition(l_pos.get(1).checkint(), l_pos.get(2).checkint());
		}
		if (!l_path.isnil()) {
			l_path.checktable();

			boolean loop = true;
			final LuaValue l_loop = l_path.get("loop");
			if (!l_loop.isnil()) {
				loop = l_loop.checkboolean();
			}

			npc.setPath(tableToPath((LuaTable) l_path.get("nodes"), loop));

			final LuaValue l_retrace = l_path.get("retrace");
			if (!l_retrace.isnil() && l_retrace.checkboolean()) {
				npc.setRetracePath();
			}

			final LuaValue l_collisionAction = l_path.get("collisionAction");
			if (!l_collisionAction.isnil()) {
				npc.setCollisionAction((CollisionAction) l_collisionAction.checkuserdata(CollisionAction.class));
			}

			// TODO: GuidedEntity.addSuspend()
		}

		/*
		final LuaValue l_randMovement = lt.get("randomMovement");
		if (!l_randMovement.isnil()) {
			l_randMovement.checktable();

			Boolean ret;
			final LuaValue l_ret = l_randMovement.get("ret");
			if (!l_ret.isnil()) {
				ret = l_ret.checkboolean();
			}

			if (ret != null) {
				npc.setMovementRadius(l_randMovement.get("radius").checkint(), ret);
			} else {
				npc.setMovementRadius(l_randMovement.get("radius").checkint());
			}
		}
		*/

		final LuaValue l_speed = lt.get("speed");
		if (!l_speed.isnil()) {
			npc.setBaseSpeed(l_speed.checkdouble());
		}

		// *** RPEntity ***

		final LuaValue l_basehp = lt.get("basehp");
		if (!l_basehp.isnil()) {
			npc.setBaseHP(l_basehp.checkint());
		}

		final LuaValue l_hp = lt.get("hp");
		if (!l_hp.isnil()) {
			npc.setHP(l_hp.checkint());
		}

		final LuaValue l_title = lt.get("title");
		if (!l_title.isnil()) {
			npc.setTitle(l_title.checkjstring());
		}

		// *** DressedEntity ***

		final LuaValue l_outfit = lt.get("outfit");
		if (!l_outfit.isnil()) {
			npc.setOutfit(l_outfit.get("layers").checkjstring()); // FIXME: should change this to table

			final LuaTable l_outfitColors = (LuaTable) l_outfit.get("colors");
			if (!l_outfitColors.isnil()) {
				l_outfitColors.checktable();

				for (final LuaValue l_key: l_outfitColors.keys()) {
					npc.setOutfitColor(l_key.checkjstring(), l_outfitColors.get(l_key).checkint());
				}
			}
		}

		// *** NPC ***

		final LuaValue l_idea = lt.get("idea");
		if (!l_idea.isnil()) {
			npc.setIdea(l_idea.checkjstring());
		}

		final LuaValue l_sounds = lt.get("sounds");
		if (l_sounds.istable() && l_sounds.length() > 0) {
			final List<String> sounds = new ArrayList<>();
			for (int idx = 1; idx <= l_sounds.length(); idx++) {
				sounds.add(l_sounds.get(idx).checkjstring());
			}

			npc.setSounds(sounds);
		}

		// TODO: NPC: setMovement, setRandomPathFrom, setPerceptionRange, setMovementRange
		//            moveRandomly, setPathCompletedPause

		// *** PassiveNPC ***

		final LuaValue l_teleports = lt.get("teleports");
		if (!l_teleports.isnil()) {
			npc.setTeleportsFlag(l_teleports.checkboolean());
		}
	}

	/**
	 * Create a new interactive NPC.
	 *
	 * @param lt
	 *     Lua table of attributes.
	 * @see
	 *     games.stendhal.server.entity.npc.SpeakerNPC.
	 */
	private LuaSpeakerNPC buildSpeakerNPC(final LuaTable lt) {
		final LuaSpeakerNPC npc = new LuaSpeakerNPC(lt.get("name").checkjstring());
		setNPCTraits(npc, lt);

		LuaValue l_idleDir = lt.get("idleDir");
		if (l_idleDir.isnil()) {
			l_idleDir = lt.get("idleDirection");
		}
		if (!l_idleDir.isnil()) {
			npc.setIdleDirection((Direction) l_idleDir.checkuserdata(Direction.class));
		}

		final LuaValue l_chatTimeout = lt.get("chatTimeout");
		if (!l_chatTimeout.isnil()) {
			npc.setPlayerChatTimeout(l_chatTimeout.checklong());
		}

		final LuaValue l_perceptionRange = lt.get("perceptionRange");
		if (!l_perceptionRange.isnil()) {
			npc.setPerceptionRange(l_perceptionRange.checkint());
		}

		final LuaValue l_currentState = lt.get("currentState");
		if (!l_currentState.isnil()) {
			npc.setCurrentState((ConversationStates) l_currentState.checkuserdata(ConversationStates.class));
		}

		final LuaValue l_greeting = lt.get("greeting");
		if (!l_greeting.isnil()) {
			l_greeting.checktable();

			final String greetText = l_greeting.get("text").checkjstring();
			final LuaValue l_greetAction = l_greeting.get("action");
			if (!l_greetAction.isnil()) {
				npc.addGreeting(greetText, (ChatAction) l_greetAction.checkuserdata(ChatAction.class));
			} else {
				npc.addGreeting(greetText);
			}
		}

		final LuaValue l_replies = lt.get("replies");
		if (!l_replies.isnil()) {
			l_replies.checktable();

			final LuaValue l_replyQuest = l_replies.get("quest");
			if (!l_replyQuest.isnil()) {
				npc.addQuest(l_replyQuest.checkjstring());
			}

			final LuaValue l_replyJob = l_replies.get("job");
			if (!l_replyJob.isnil()) {
				npc.addJob(l_replyJob.checkjstring());
			}

			final LuaValue l_replyHelp = l_replies.get("help");
			if (!l_replyHelp.isnil()) {
				npc.addHelp(l_replyHelp.checkjstring());
			}

			final LuaValue l_replyOffer = l_replies.get("offer");
			if (!l_replyOffer.isnil()) {
				npc.addOffer(l_replyOffer.checkjstring());
			}

			final LuaValue l_replyBye = l_replies.get("bye");
			if (!l_replyBye.isnil()) {
				npc.addGoodbye(l_replyBye.checkjstring());
			}
		}

		// TODO: addReply

		final LuaValue l_altImage = lt.get("alternativeImage");
		if (!l_altImage.isnil()) {
			npc.setAlternativeImage(l_altImage.checkjstring());
		}

		return npc;
	}

	/**
	 * Creates a new non-interactive NPC.
	 *
	 * @param lt
	 *     Lua table of attributes.
	 * @see
	 *     games.stendhal.server.entity.npc.SilentNPC
	 */
	private LuaSilentNPC buildSilentNPC(final LuaTable lt) {
		final LuaSilentNPC npc = new LuaSilentNPC() {};
		setNPCTraits(npc, lt);

		return npc;
	}

	/**
	 * Creates a new sign.
	 *
	 * @param lt
	 *     Lua table of attributes.
	 * @param visible
	 *     Whether or not the sign should us a sprite visible to the player.
	 * @see
	 *     games.stendhal.server.entity.mapstuff.sign.Sign
	 * @see
	 *     games.stendhal.server.entity.mapstuff.sign.Reader
	 */
	private Sign buildSign(final LuaTable lt, final boolean visible) {
		if (!visible) {
			return buildReader(lt);
		}

		final Sign sign = new Sign();
		setEntityTraits(sign, lt);

		final LuaValue l_text = lt.get("text");
		if (!l_text.isnil()) {
			sign.setText(l_text.checkjstring());
		}

		return sign;
	}

	/**
	 * Creates a new sign.
	 *
	 * @param lt
	 *     Lua table of attributes.
	 * @see
	 *     {@link #buildSign(LuaTable, boolean)}
	 */
	private Sign buildSign(final LuaTable lt) {
		boolean visible = true;
		final LuaValue l_value = lt.get("visible");
		if (!l_value.isnil()) {
			visible = l_value.toboolean();
		}

		return buildSign(lt, visible);
	}

	/**
	 * Creates a new invisible sign.
	 *
	 * @param lt
	 *     Lua table of attributes.
	 * @see
	 *     games.stendhal.server.entity.mapstuff.sign.Sign
	 * @see
	 *     games.stendhal.server.entity.mapstuff.sign.Reader
	 */
	private Reader buildReader(final LuaTable lt) {
		final Reader reader = new Reader();
		setEntityTraits(reader, lt);

		final LuaValue l_text = lt.get("text");
		if (!l_text.isnil()) {
			reader.setText(l_text.checkjstring());
		}

		return reader;
	}

	/**
	 * Creates a new shop sign.
	 *
	 * @param lt
	 *     Lua table of attributes.
	 * @see
	 *     games.stendhal.server.entity.mapstuff.sign.ShopSign
	 */
	private ShopSign buildShopSign(final LuaTable lt) {
		final ShopSign sign = new ShopSign(
			lt.get("name").checkjstring(),
			lt.get("title").checkjstring(),
			lt.get("caption").checkjstring(),
			lt.get("seller").checkboolean());

		setEntityTraits(sign, lt);

		return sign;
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
	 * @deprecated
	 *     Use {@link #create(LuaTable)}.
	 */
	@Deprecated
	public LuaSpeakerNPC createSpeakerNPC(final String name) {
		return new LuaSpeakerNPC(name);
	}

	/**
	 * Creates a new SilentNPC instance.
	 *
	 * @return
	 * 		New SilentNPC instance.
	 * @deprecated
	 *     Use {@link #create(LuaTable)}.
	 */
	@Deprecated
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
	 * @deprecated
	 *     Use {@link games.stendhal.server.entity.GuidedEntity#setPath(FixedPath).
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
	 * @deprecated
	 *     Use {@link games.stendhal.server.entity.GuidedEntity#setPathAndPosition(FixedPath)}.
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
	 * @deprecated
	 *     Use {@link #create(LuaTable)}.
	 */
	@Deprecated
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
	 * @deprecated
	 *     Use {@link #create(LuaTable)}.
	 */
	@Deprecated
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
	 * @deprecated
	 *     Use {@link #create(LuaTable)}.
	 */
	@Deprecated
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
	 *
	 * @todo
	 *     Merge functionality into {@link games.stendhal.server.entity.GuidedEntity}
	 *     & delete this class.
	 */
	private interface LuaGuidedEntity {

		public void setPath(final FixedPath path);

		public void setPath(final LuaTable table, Boolean loop);

		public void setPathAndPosition(final FixedPath path);

		public void setPathAndPosition(final LuaTable table, Boolean loop);
	}

	/**
	 * @todo
	 *     Merge functionality into {@link games.stendhal.server.entity.npc.SpeakerNPC}
	 *     & delete this class.
	 */
	private class LuaSpeakerNPC extends SpeakerNPC implements LuaGuidedEntity {

		public LuaFunction idleAction;
		public LuaFunction attackRejectedAction;
		private boolean ignorePlayers = false;


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
					listenConditions = conditionHelper.andC((LuaTable) conditions);
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

		@Override
		public void setAttending(final RPEntity rpentity) {
			// workaround to prevent setIdea() being called
			if (!ignorePlayers) {
				super.setAttending(rpentity);

				if (getEngine().getCurrentState().equals(ConversationStates.IDLE) && idleAction instanceof LuaFunction) {
					final LuaSpeakerNPC thisNPC = this;

					SingletonRepository.getTurnNotifier().notifyInTurns(1, new TurnListener() {
						@Override
						public void onTurnReached(final int currentTurn) {
							idleAction.call(CoerceJavaToLua.coerce(thisNPC));
						}
					});
				}
			}
		}

		@SuppressWarnings("unused")
		public void setIgnorePlayers(final boolean ignore) {
			ignorePlayers = ignore;
		}

		@Override
		public void onRejectedAttackStart(final RPEntity attacker) {
			if (attackRejectedAction != null) {
				attackRejectedAction.call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(attacker));
			} else if (!ignorePlayers) {
				super.onRejectedAttackStart(attacker);
			}
		}
	}

	/**
	 * @todo
	 *     Merge functionality into {@link games.stendhal.server.entity.npc.SilentNPC}
	 *     & delete this class.
	 */
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
