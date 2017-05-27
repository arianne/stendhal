/*
 * @(#) src/games/stendhal/server/entity/NPCOwnedChestFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.chest;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * A factory for <code>NPCOwnedChest</code> objects.
 */
public class NPCOwnedChestFactory implements ConfigurableFactory {

	//
	// NPCOwnedChestFactory
	//

	/**
	 * Extract the NPC from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The NPC.
	 * @throws IllegalArgumentException
	 *             If attribute 'npc' is invalid or missing.
	 */
	protected SpeakerNPC getNPC(final ConfigurableFactoryContext ctx) {
		final String npcName = ctx.getRequiredString("npc");
		final SpeakerNPC npc = SingletonRepository.getNPCList().get(npcName);
		if (npc == null) {
			throw new IllegalArgumentException("Unknown NPC: " + npcName);
		}

		return npc;
	}

	//
	// ConfigurableFactory
	//

	/**
	 * Create an NPC owned chest.
	 *
	 * @param ctx
	 *            Configuration context.
	 *
	 * @return An NPCOwnedChest.
	 *
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 *
	 * @see NPCOwnedChest
	 */
	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		return new NPCOwnedChest(getNPC(ctx));
	}
}
