/*
 * @(#) src/games/stendhal/server/entity/SpeakerNPCFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.npc;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;

/**
 * A base factory for <code>SpeakerNPC</code> objects.
 */
public class SpeakerNPCFactory implements ConfigurableFactory {

	/**
	 * Creates a new SpeakerNPC. Override this if you want to use a subclass of
	 * SpeakerNPC.
	 *
	 * @param name
	 *            The NPC name.
	 * @return An object of class SpeakerNPC or a subclass.
	 */
	protected SpeakerNPC instantiate(final String name) {
		return new SpeakerNPC(name);
	}

	/**
	 * Extract the NPC name from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The name
	 * @throws IllegalArgumentException
	 *             If the attribute is missing.
	 */
	protected String getName(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("name");
	}

	/**
	 * Extract the NPC class (i.e. its visual appearance) from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The class.
	 * @throws IllegalArgumentException
	 *             If the attribute is missing.
	 */
	protected String getClass(final ConfigurableFactoryContext ctx) {
		return ctx.getString("class", null);
	}

	/**
	 * Extract the NPC hitpoints from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 *
	 * @return The hitpoints.
	 */
	protected int getHP(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("hp", 100);
	}

	/**
	 * Extract the NPC level from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 *
	 * @return The level of experience.
	 */
	protected int getLevel(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("level", 0);
	}

	/**
	 * Extract the NPC description from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 *
	 * @return The text that will be shown when a player looks at the NPC, or
	 *         null if the default description should be used.
	 */
	protected String getDescription(final ConfigurableFactoryContext ctx) {
		return ctx.getString("description", null);
	}

	/**
	 * Extract the direction in which the NPC faces from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 *
	 * @return The hitpoints.
	 */
	protected Direction getDirection(final ConfigurableFactoryContext ctx) {
		final String s = ctx.getString("direction", "down");
		return Direction.valueOf(s.toUpperCase());
	}

	public void createDialog(final SpeakerNPC npc) {
		// sub classes can implement this method
	}

	protected List<Node> getPath(final ConfigurableFactoryContext ctx) {
		final List<Node> result = new ArrayList<Node>();
		int i = 0;
		boolean lastNode = false;

		do {
			final String s = ctx.getString("node" + i, null);
			if (s != null) {
				final String[] coords = s.split(",");
				final int x = Integer.parseInt(coords[0]);
				final int y = Integer.parseInt(coords[1]);
				final Node node = new Node(x, y);
				result.add(node);
				i++;
			} else {
				lastNode = true;
			}
		} while (!lastNode);

		return result;
	}

	//
	// ConfigurableFactory
	//

	/**
	 * Create a damaging area.
	 *
	 * @param ctx
	 *            Configuration context.
	 *
	 * @return A SpeakerNPC.
	 *
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 *
	 * @see SpeakerNPC
	 */
	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		final SpeakerNPC npc = instantiate(getName(ctx));

		npc.setBaseHP(100);
		npc.setHP(getHP(ctx));
		npc.setLevel(getLevel(ctx));

		final String clazz = getClass(ctx);

		if (clazz != null) {
			npc.setEntityClass(clazz);
		}

		final String description = getDescription(ctx);

		if (description != null) {
			npc.setDescription(description);
		}

		final List<Node> path = getPath(ctx);
		npc.setPath(new FixedPath(path, path.size() > 0));
		npc.setDirection(getDirection(ctx));

		createDialog(npc);

		return npc;
	}
}
