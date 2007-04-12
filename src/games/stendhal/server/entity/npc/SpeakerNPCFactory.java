/*
 * @(#) src/games/stendhal/server/entity/SignFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.npc;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContext;
import games.stendhal.common.Direction;
import games.stendhal.server.pathfinder.Path;

/**
 * A base factory for <code>Sign</code> objects.
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
	 *
	 * @return	The message text.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is missing.
	 */
	protected String getName(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("name")) == null) {
			throw new IllegalArgumentException("Required attribute 'name' missing");
		}

		return s;
	}
	
	/**
	 * Extract the NPC class (i.e. its visual appearance) from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The message text.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is missing.
	 */
	protected String getClass(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("class")) == null) {
			throw new IllegalArgumentException("Required attribute 'class' missing");
		}

		return s;
	}

	/**
	 * Extract the NPC hitpoints from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The hitpoints.
	 */
	protected int getHP(ConfigurableFactoryContext ctx) {
		String s;

		s = ctx.getAttribute("hp");
		if (s == null) {
			return 100;
		} else {
			return Integer.parseInt(s);
		}
	}

	/**
	 * Extract the NPC level from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The level of experience.
	 */
	protected int getLevel(ConfigurableFactoryContext ctx) {
		String s;

		s = ctx.getAttribute("level");
		if (s == null) {
			return 0;
		} else {
			return Integer.parseInt(s);
		}
	}

	/**
	 * Extract the direction in which the NPC faces from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The hitpoints.
	 */
	protected Direction getDirection(ConfigurableFactoryContext ctx) {
		String s;

		s = ctx.getAttribute("direction");
		if (s == null || s.equals("down")) {
			return Direction.DOWN;
		} else if (s.equals("left")){
			return Direction.LEFT;
		} else if (s.equals("up")){
			return Direction.UP;
		} else {
			return Direction.RIGHT;
		}
	}

	protected void createDialog(SpeakerNPC npc) {
		
	}
	
	protected List<Path.Node> getPath(ConfigurableFactoryContext ctx) {
		List<Path.Node> result = new ArrayList<Path.Node>();
		int i = 0;
		boolean lastNode = false;
		
		do {
			String s;
			s = ctx.getAttribute("node" + i);
			if (s != null) {
				String[] coords = s.split(",");
				int x = Integer.parseInt(coords[0]);
				int y = Integer.parseInt(coords[1]);
				Path.Node node = new Path.Node(x, y);
				result.add(node);
				i++;
			} else {
				lastNode = true;
			}
		}
		while (! lastNode);
		
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
	 * @return	A Sign.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		games.stendhal.server.entity.npc.SpeakerNPC
	 */
	public Object create(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		SpeakerNPC npc = instantiate(getName(ctx));

		npc.put("class", getClass(ctx));
		npc.initHP(getHP(ctx));
		npc.setLevel(getLevel(ctx));
		
		List<Path.Node> path = getPath(ctx);
		npc.setPath(path, path.size() > 0);
		npc.setDirection(getDirection(ctx));

		createDialog(npc);

		return npc;
	}
}
