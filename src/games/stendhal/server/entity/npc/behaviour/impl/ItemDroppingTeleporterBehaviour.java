// $Id$
package games.stendhal.server.entity.npc.behaviour.impl;

import java.util.List;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import marauroa.common.game.IRPZone;

/**
 * teleports the SpeakerNPC to a random location on the outside world and causes
 * it to walk a random path while also randomly dropping items
 *
 * @author kymara
 */
public final class ItemDroppingTeleporterBehaviour extends TeleporterBehaviour {
	final SpeakerNPC speakerNPC;
    final String itemName;


	/**
	 * Creates a new ItemDroppingTeleporterBehaviour.
	 *
	 * @param speakerNPC
	 *            SpeakerNPC
	 * @param repeatedText
	 *            text to repeat
	 * @param itemName
	 *            name of item to drop
	 */
	public ItemDroppingTeleporterBehaviour(final SpeakerNPC speakerNPC, final List<String> setZones, final String zoneStartsWithLimiter,
			final String repeatedText, final String itemName) {
		super(speakerNPC, setZones, zoneStartsWithLimiter, repeatedText);
		this.speakerNPC = speakerNPC;
		this.itemName = itemName;
	}

	/**
	 * Creates a new ItemDroppingTeleporterBehaviour.
	 *
	 * @param speakerNPC
	 *            SpeakerNPC
	 * @param repeatedText
	 *            text to repeat
	 * @param useHighProbabilityZones
	 *            true to make teleportation to a hand
	 *            selected list of zones more likely
	 * @param itemName
	 *            name of item to drop
	 */
	public ItemDroppingTeleporterBehaviour(final SpeakerNPC speakerNPC, final List<String> setZones, final String zoneStartsWithLimiter, final String repeatedText, final boolean useHighProbabilityZones, final String itemName) {
		super(speakerNPC, setZones, zoneStartsWithLimiter, repeatedText, useHighProbabilityZones);
		this.speakerNPC = speakerNPC;
		this.itemName = itemName;
	}

	@Override
	protected void doRegularBehaviour() {
		if(Rand.throwCoin()==1) {
			final Item item = SingletonRepository.getEntityManager().getItem(itemName);
			final IRPZone zone = speakerNPC.getZone();
			// place under NPC
			item.setPosition(speakerNPC.getX(), speakerNPC.getY());
			zone.add(item);
		}
		super.doRegularBehaviour();
	}

}
