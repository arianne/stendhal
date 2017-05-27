/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.entity.item.scroll.TwilightMossScroll;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.mithrilcloak.MithrilCloakQuestChain;

/**
 * QUEST: Mithril Cloak
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li>Ida, a seamstress in Ados.</li>
 * <li>Imperial scientists, in kalavan basement</li>
 * <li>Mithrilbourgh wizards, in kirdneh and magic city</li>
 * <li>Hogart, a retired master dwarf smith, forgotten below the dwarf mines in
 * Orril.</li>
 * <li>Terry, the dragon hatcher in semos caves.</li>
 * <li>Ritati Dragontracker, odds and ends buyer in ados abandoned keep</li>
 * <li>Pdiddi, the dodgy dealer from Semos</li>
 * <li>Josephine, young woman from Fado</li>
 * <li>Pedinghaus, the mithril casting wizard in Ados</li>
 * </ul>
 * <p>
 * STEPS:
 * <ul>
 * <li>Ida needs sewing machine fixed, with one of three items from a list</li>
 * <li>Once machine fixed and if you have done mithril shield quest, Ida offers you cloak</li>
 * <li>Kampusch tells you to how to make the fabric</li>
 * <li>Imperial scientists take silk glands and make silk thread</li>
 * <li>Kampusch fuses mithril nuggets into the silk thread</li>
 * <li>Whiggins weaves mithril thread into mithril fabric</li>
 * <li>Ida takes fabric then asks for scissors</li>
 * <li>Hogart makes the scissors which need eggshells</li>
 * <li>Terry swaps eggshells for poisons</li>
 * <li>Ida takes the scissors then asks for needles</li>
 * <li>Needles come from Ritati Dragontracker</li>
 * <li>Ida breaks a random number of needles, meaning you need to get more each time</li>
 * <li>Ida pricks her finger on the last needle and goes to twilight zone</li>
 * <li>Pdiddi sells the moss to get to twilight zone</li>
 * <li>A creature in the twilight zone drops the elixir to heal lda</li>
 * <li>After being ill Ida asks you to take a blue striped cloak to Josephine</li>
 * <li>After taking cloak to Josephine and telling Ida she asks for mithril clasp</li>
 * <li>Pedinghaus makes mithril clasp</li>
 * <li>The clasp completes the cloak</li>
 * </ul>
 * <p>
 * REWARD:
 * <ul>
 * <li>Mithril Cloak</li>
 * <li> XP</li>
 * <li> Karma</li>
 * </ul>
 * <p>
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 *
 * @author kymara
 */
public class MithrilCloak extends AbstractQuest {
	private static final String QUEST_SLOT = "mithril_cloak";

	private static Logger logger = Logger.getLogger(MithrilCloak.class);

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Mithril Cloak",
				"A shiny and high defence cloak is available for those willing to complete a long list of tasks for the seamstress Ida.",
				false);

		// login notifier to teleport away players logging into the twilight zone.
		SingletonRepository.getLoginNotifier().addListener(new LoginListener() {
			@Override
			public void onLoggedIn(final Player player) {
				TwilightMossScroll scroll = (TwilightMossScroll) SingletonRepository.getEntityManager().getItem("twilight moss");
				scroll.teleportBack(player);
			}

		});

		MithrilCloakQuestChain mithrilcloak = new MithrilCloakQuestChain();
		mithrilcloak.addToWorld();
	}


	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		final String questState = player.getQuest(QUEST_SLOT);
		res.add("I met Ida in her sewing room in Ados.");
		if (questState.equals("rejected")) {
			res.add("I am not interested in helping Ida.");
			return res;
		}
		res.add("Ida's sewing machine is broken and she has asked me to find the missing part.");
		if (questState.startsWith("machine")) {
			res.add("I need to fetch Ida " + Grammar.a_noun(player.getRequiredItemName(QUEST_SLOT,1)) + ".");
			return res;
		}
		res.add("I brought the part to fix Ida's machine.");
		if (questState.equals("need_mithril_shield")) {
			res.add("I must earn my mithril shield before I can go further in my quest for the mithril cloak.");
			return res;
		}
		if (questState.equals("fixed_machine")) {
			return res;
		}
		res.add("My cloak needs mithril fabric, and Kampusch will help me with how to get that. He'd know what items I need.");
		if (questState.equals("need_fabric")) {
			return res;
		}
		res.add("Vincento Price is spinning thread from silk glands I took him.");
		if (questState.startsWith("makingthread;")) {
			// optionally could add if time is still remaining or if it's ready to collect (timestamp in index 1 of questslot)
			return res;
		}
		res.add("I got spools of silk thread to take Kampusch from Vincento's student, Boris Karlova.");
		if (questState.equals("got_thread")) {
			return res;
		}
		res.add("Kampusch is fusing mithril pieces onto the silk threads.");
		if (questState.startsWith("fusingthread;")) {
			// optionally could add if time is still remaining or if it's ready to collect (timestamp in index 1 of questslot)
			return res;
		}
		res.add("Whiggins will weave the mithril thread into fabric, I must find him.");
		if (questState.equals("got_mithril_thread")) {
			return res;
		}
		res.add("Before Whiggins will help me I must take a letter to Pedinghaus. Whiggins seemed really troubled.");
		if (questState.equals("taking_letter")) {
			return res;
		}
		res.add("I took the note to Pedinghaus and he read it. I better tell Whiggins that everything is okay so I can get my fabric.");
		if (questState.equals("took_letter")) {
			return res;
		}
		res.add("Whiggins is weaving mithril fabric for my cloak!.");
		if (questState.startsWith("weavingfabric;")) {
			// optionally could add if time is still remaining or if it's ready to collect (timestamp in index 1 of questslot)
			return res;
		}
		res.add("I collected mithril fabric from Whiggins and I need to take that to Ida next.");
		if (questState.equals("got_fabric")) {
			return res;
		}
		res.add("Ida cannot cut the fabric with normal scissors! I have to ask Hogart to make some magical scissors.");
		if (questState.equals("need_scissors")) {
			return res;
		}
		res.add("Hogart needs me to bring him an iron bar, a mithril bar, and a few magical eggshells.");
		if (questState.startsWith("need_eggshells;")) {
			// the quest slot knows how many eggshells were needed.
			return res;
		}
		res.add("Hogart is making magical scissors with the items I brought.");
		if (questState.startsWith("makingscissors;")) {
			// optionally could add if time is still remaining or if it's ready to collect (timestamp in index 1 of questslot)
			return res;
		}
		res.add("I should take the magical scissors to Ida.");
		if (questState.equals("got_scissors")) {
			return res;
		}
		res.add("Ida needs a magical needle to sew my cloak.");
		if (questState.startsWith("need_needle") || questState.startsWith("told_joke;")) {
			//  quest slot knows how many needles are still needed to take and which joke was told last
			return res;
		}
		res.add("Ida is sewing up my cloak!");
		if (questState.startsWith("sewing;")) {
			// optionally could add if time is still remaining or if it's ready to collect (timestamp in index 1 of questslot)
			// number of needles still remaining is in slot 2
			// don't bother with adding info about the looping (needle breaking and sewing again)
			return res;
		}
		res.add("Ida had a terrible accident and pricked her finger on the needle. She's hallucinating and I must try to visit her in the twilight zone.");
		if (questState.equals("twilight_zone")) {
			return res;
		}
		res.add("I gave Ida the twilight elixir to restore her health. But she got behind on her other jobs. Now I must go and find a blue striped cloak to take Josephine before Ida can work for me.");
		if (questState.equals("taking_striped_cloak")) {
			return res;
		}
		res.add("Jospehine was pretty happy to get her striped cloak. I should let Ida know.");
		if (questState.equals("gave_striped_cloak")) {
			return res;
		}
		res.add("My cloak is almost ready, all I need now is a clasp made of mithril, from Pedinghaus.");
		if (questState.equals("need_clasp")) {
			return res;
		}
		res.add("Pedinghaus is forging my mithril clasp. I can't wait!");
		if (questState.startsWith("forgingclasp;")) {
			// optionally could add if time is still remaining or if it's ready to collect (timestamp in index 1 of questslot)
			return res;
		}
		res.add("I got a mithril clasp to fasten my cloak. All I need to do is take that to Ida.");
		if (questState.equals("got_clasp")) {
			return res;
		}
		res.add("Finally I can wear my wonderful sparkly mithril cloak!");
		if (questState.equals("done")) {
			return res;
		}
		// if things have gone wrong and the quest state didn't match any of the above, debug a bit:
		final List<String> debug = new ArrayList<String>();
		debug.add("Quest state is: " + questState);
		logger.error("History doesn't have a matching quest state for " + questState);
		return debug;
	}

	@Override
	public String getName() {
		return "MithrilCloak";
	}

	// it's a long quest so they can always start it before they can necessarily finish all
	@Override
	public int getMinLevel() {
		return 100;
	}

	// Not sure about this one. it would make an achievement for all quests in ados city, quite hard
	@Override
	public String getRegion() {
		return Region.ADOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Ida";
	}
}
