/*
 * @(#) src/games/stendhal/server/entity/SpeakerNPCFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.npc;

import games.stendhal.common.Direction;
import games.stendhal.server.config.factory.ConfigurableFactory;
import games.stendhal.server.config.factory.ConfigurableFactoryContext;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * A base factory for <code>SpeakerNPC</code> objects.
 */
public class SpeakerNPCFactory implements ConfigurableFactory {

	/**
	 * Creates a new SpeakerNPC. Override this if you want to use a
	 * subclass of SpeakerNPC.
	 *
	 * @param name The NPC name.
	 * @return An object of class SpeakerNPC or a subclass.
	 */
	protected SpeakerNPC instantiate(String name) {
		return new SpeakerNPC(name);
	}

	/**
	 * Extract the NPC name from a context.
	 *
	 * @param	ctx		The configuration context.
	 * @return	The name
	 * @throws	IllegalArgumentException If the attribute is missing.
	 */
	protected String getName(ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("name");
	}

	/**
	 * Extract the NPC class (i.e. its visual appearance) from a context.
	 *
	 * @param	ctx		The configuration context.
	 * @return	The class.
	 * @throws	IllegalArgumentException If the attribute is missing.
	 */
	protected String getClass(ConfigurableFactoryContext ctx) {
		return ctx.getString("class", null);
	}

	/**
	 * Extract the NPC hitpoints from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The hitpoints.
	 */
	protected int getHP(ConfigurableFactoryContext ctx) {
		return ctx.getInt("hp", 100);
	}

	/**
	 * Extract the NPC level from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The level of experience.
	 */
	protected int getLevel(ConfigurableFactoryContext ctx) {
		return ctx.getInt("level", 0);
	}

	/**
	 * Extract the NPC description from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The text that will be shown when a player looks at
	 *          the NPC, or null if the default description should
	 *          be used.
	 */
	protected String getDescription(ConfigurableFactoryContext ctx) {
		return ctx.getString("description", null);
	}

	/**
	 * Extract the direction in which the NPC faces from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The hitpoints.
	 */
	protected Direction getDirection(ConfigurableFactoryContext ctx) {
		// TODO: improve error handling make use of valueOf
		String s = ctx.getString("direction", "down");
		if (s.equals("down")) {
			return Direction.DOWN;
		} else if (s.equals("left")) {
			return Direction.LEFT;
		} else if (s.equals("up")) {
			return Direction.UP;
		} else {
			return Direction.RIGHT;
		}
	}

	public void createDialog(SpeakerNPC npc) {
		// sub classes can implement this method
	}

	protected List<Node> getPath(ConfigurableFactoryContext ctx) {
		List<Node> result = new ArrayList<Node>();
		int i = 0;
		boolean lastNode = false;

		do {
			String s = ctx.getString("node" + i, null);
			if (s != null) {
				String[] coords = s.split(",");
				int x = Integer.parseInt(coords[0]);
				int y = Integer.parseInt(coords[1]);
				Node node = new Node(x, y);
				result.add(node);
				i++;
			} else {
				lastNode = true;
			}
		}
		while (!lastNode);

		return result;
	}

	//
	// ConfigurableFactory
	//

	/**
	 * Create a damaging area.
	 *
	 * @param	ctx		Configuration context.
	 *
	 * @return	A SpeakerNPC.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value suitable for
	 *				meaningful user interpretation.
	 *
	 * @see		SpeakerNPC
	 */
	public Object create(ConfigurableFactoryContext ctx) {
		SpeakerNPC npc = instantiate(getName(ctx));

		npc.setBaseHP(100);
		npc.setHP(getHP(ctx));
		npc.setLevel(getLevel(ctx));

		String clazz = getClass(ctx);

		if (clazz != null) {
			npc.setEntityClass(clazz);
		}

		String description = getDescription(ctx);

		if (description != null) {
			npc.setDescription(description);
		}

		List<Node> path = getPath(ctx);
		npc.setPath(new FixedPath(path, path.size() > 0));
		npc.setDirection(getDirection(ctx));

		createDialog(npc);

		return npc;
	}
}
