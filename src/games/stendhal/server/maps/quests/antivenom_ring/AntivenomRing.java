/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.antivenom_ring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.AbstractQuest;
import games.stendhal.server.util.ItemCollection;

/**
 * QUEST: Antivenom Ring
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Jameson (the retired apothecary)</li>
 * <li>Zoey (zoologist at animal sanctuary)</li>
 * <li>Other NPCs to give hints at location of apothecary's lab: Valo, Haizen, & Ortiv Milquetoast</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Complete Traps for Klaas quest to gain entrance into apothecary's lab.</li>
 * <li>Bring note to apothecary to Jameson.</li>
 * <li>As a favor to Klaas, Jameson will help you to strengthen your medicinal ring.</li>
 * <li>Bring Jameson a medicinal ring, cobra venom, 2 mandragora and 5 fairycakes.</li>
 * <li>Jameson infuses the ring.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>2000 XP</li>
 * <li>antivenom ring</li>
 * <li>Karma: 25???</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 *
 *
 * @author AntumDeluge
 */
public class AntivenomRing extends AbstractQuest {

	private static final String QUEST_SLOT = "antivenom_ring";

	// NPCs involved in quest
	private final static String apothecary = "Jameson";
	private final static String zoologist = "Zoey";

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (player.hasQuest(QUEST_SLOT)) {
			res.add("I have found the hermit apothecary's lab in Semos Mountain.");

			final String questState = player.getQuest(QUEST_SLOT);
			if (questState.equals("done")) {
				res.add("I brought all the items that " + apothecary + " requested.");
				res.add("He applied a special mixture to my ring which made it more resistant to poison. I also got some XP and karma.");
			} else if (questState.equals("rejected")) {
				res.add("Poison is too dangerous. I do not want to get hurt.");
			} else {
				if (questState.startsWith("enhancing;")) {
					res.add("I brought all the items that " + apothecary + " requested.");
					res.add(apothecary + " is enhancing my ring.");
				} else {
					ItemCollection itemList = new ItemCollection();
					itemList.addFromString(questState.replace(";", ","));

					res.add(apothecary + " has asked me to gather some items. I still need to bring " + apothecary + " " + Grammar.enumerateCollection(itemList.toStringList()) + ".");
				}
			}
		}

		return res;
	}

	private void prepareHintNPCs() {
		final SpeakerNPC hintNPC1 = npcs.get("Valo");
		final SpeakerNPC hintNPC2 = npcs.get("Haizen");
		final SpeakerNPC hintNPC3 = npcs.get("Ortiv Milquetoast");

		// Valo is asked about an apothecary
		hintNPC1.add(ConversationStates.ATTENDING,
				"apothecary",
				null,
				ConversationStates.ATTENDING,
				"Hmmm, yes, I knew a man long ago who was studying medicines and antipoisons. The last I heard he was #retreating into the mountains.",
				null);

		hintNPC1.add(ConversationStates.ATTENDING,
				Arrays.asList("retreat", "retreats", "retreating"),
				null,
				ConversationStates.ATTENDING,
				"He's probably hiding. Keep an eye out for hidden entrances.",
				null);

		// Haizen is asked about an apothecary
		hintNPC2.add(ConversationStates.ATTENDING,
				"apothecary",
				null,
				ConversationStates.ATTENDING,
				"Yes, there was once an estudious man in Kalavan. But, due to complications with leadership there he was forced to leave. I heard that he was #hiding somewhere in the Semos region.",
				null);

		hintNPC2.add(ConversationStates.ATTENDING,
				Arrays.asList("hide", "hides", "hiding", "hidden"),
				null,
				ConversationStates.ATTENDING,
				"If I were hiding I would surely do it in a secret room with a hidden entrance.",
				null);

		// Ortiv Milquetoast is asked about an apothecary
		hintNPC3.add(ConversationStates.ATTENDING,
				"apothecary",
				null,
				ConversationStates.ATTENDING,
				"You must be speaking of my colleague, Jameson. He was forced to #hide out because of problems in Kalavan. He hasn't told me where, but he does bring the most delicious pears when he visits.",
				null);

		hintNPC3.add(ConversationStates.ATTENDING,
				Arrays.asList("hide", "hides", "hiding", "hidden"),
				null,
				ConversationStates.ATTENDING,
				"He hinted at a secret laboratory that he had built. Something about a hidden doorway.",
				null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Antivenom Ring",
				"As a favor to an old friend, Jameson the apothecary will strengthen the medicinal ring.",
				false);
		prepareHintNPCs();
		new ApothecaryStage(apothecary, QUEST_SLOT);
		new ZoologistStage(zoologist, QUEST_SLOT);
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "AntivenomRing";
	}

	public String getTitle() {
		return "AntivenomRing";
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_SURROUNDS;
	}

	@Override
	public String getNPCName() {
		return apothecary;
	}
}
