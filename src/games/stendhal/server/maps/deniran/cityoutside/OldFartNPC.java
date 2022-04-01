/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityoutside;

import java.awt.Color;
import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.behaviour.impl.MonologueBehaviour;

/**
 * This old fart sits on a bench in Deniran. From eating cabbage soup he has
 * clearly audible flatulence. One can even smell it from afar. He's named after
 * Louis de Funès' character in the movie "The Cabbage Soup" and the French word
 * for storyteller. He's not interested in what one tells him and only drops
 * hints to stories if someone speaks to him. That's also the reason why he does
 * not insist in receiving a yes or no answer. His irritating behaviour is
 * intended. It's unclear if the old fart is somehow senile or is making fun of
 * players. Some stories he talks about are from the game others just made up or
 * old stories, songs, nursery rhymes or from pop culture.
 *
 * MonologueBehaviour could be combined with real fart sounds in the future.
 * There could be another old fart sitting near this NPC who somehow reacts on
 * the farts (and maybe the stories.) If that one is ever added, the stories
 * might move to a different NPC somewhere else.
 *
 * @author kribbel
 */
public class OldFartNPC implements ZoneConfigurator {
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final String[] farts = { "!me lets one off", "!me lets one rip so bad, it shakes the house", "!me breaks wind",
				"!me lets it rip", "!me cuts the cheese", "!me blows off a tiny stinker" };
		new MonologueBehaviour(buildNPC(zone), farts, 1);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC oldFart = new SpeakerNPC("Claude Conteur") {
			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Oh, hello there!");
				add(ConversationStates.ATTENDING,
						Arrays.asList("help", "job", "work", "offer", "task", "quest", "story", "stories"),
						ConversationStates.ATTENDING, null, new MultipleActions(
								new SayTextAction("Have you ever heard the story of "), new SayTextAction(stories)));
				add(ConversationStates.ATTENDING, ConversationPhrases.YES_MESSAGES, null, ConversationStates.IDLE, null,
						new MultipleActions(new SayTextAction(
								"Really!? That's great because I've forgotten it. You should go and write it down."),
								new SayTextAction("!me giggles and doesn't take notice of you any longer.")));
				add(ConversationStates.ATTENDING, ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE, null,
						new MultipleActions(new SayTextAction("Me neither."),
								new SayTextAction("!me giggles and doesn't take notice of you any longer.")));
				addGoodbye("!me doesn't take notice of you any longer.");
				// the following is just in case someone happens to ask
				add(ConversationStates.ATTENDING,
						Arrays.asList("unpleasant", "smell", "stench", "air", "fart", "farting", "magic", "magician",
								"sorcery", "sorcerer", "witch", "witchery", "witchcraft", "joke", "fun"),
						ConversationStates.ATTENDING, null,
						new MultipleActions(
								new SayTextAction("I'm a mighty sorcerer, you know. I can make the air smell."),
								new SayTextAction("!me giggles and clearly enjoys himself."),
								new SayTextAction("Feel the magic powers of cabbage #soup.")));
				addReply("cabbage",
						"Oh, you won't find any cabbage anywhere around. But I have a secret "
								+ "reliable supplier far in the north. Therefore I never have to be afraid of "
								+ "running out of cabbage #soup.");
				add(ConversationStates.ATTENDING, "soup", null, ConversationStates.ATTENDING, null,
						new MultipleActions(new SayTextAction("#Cabbage soup is so yummy."),
								new SayTextAction("!me licks his lips and rubs his tummy."),
								new SayTextAction("I eat it every day.")));
				add(ConversationStates.ATTENDING, Arrays.asList("French", "France", "français", "francais"),
						ConversationStates.ATTENDING, null, new SayTextAction("Je ne parle pas français!"));
			}

			// Make the NPC not turn to player when talking to show the lack of interest
			@Override
			public void say(final String text) {
				say(text, false);
			}
		};
		oldFart.setOutfit(0, 42, 0, 3, 5, 0, 39, 0, 0);
		oldFart.setOutfitColor("dress", Color.GREEN);
		oldFart.setDescription("You see an old Deniran citizen sitting on a bench.");
		oldFart.setPosition(46, 11);
		oldFart.initHP(100);
		oldFart.setIdleDirection(Direction.DOWN);
		zone.add(oldFart);
		return oldFart;
	}

	String[] stories = { "the dwarf that fell into the wishing well?", // made up
			"the ogre that found a fish pie in a hollow tree?", // made up
			"the gnome that begged the witch to turn him into a mighty bull elephant?", // made up
			"the pandas that planned to take over the world?", // made up
			"the leprechaun that forgot where he had hidden his pot of gold?", // made up
			"the brave chicken that killed the egg stealing hero?", // made up
			"the heroes who entered the Deniran Caves and found nothing?", // made up
			"the sheepmen that formed the Sheep Liberation Front of Faiumoni?", // made up
			"the rotten zombie that slept in Ados Swamp?", // made up
			"the rise and fall of the rat people's kingdom?", // made up
			"the assassins who secretly took over the whole Deniran Empire without anybody noticing?", // made up
			"the madaram archer who managed to shot himself an arrow into his backside?", // made up
			"the superogre that was permanently followed by a turtle that strived to bite him in his big toe?",
			// made up
			"the headless monster that fell in love with an elf sacerdotist's brain?", // made up
			"the extraordinary hero who succeded in making a tuna sandwich?",
			// made up, you can't make but only buy one
			"the band of orks from Kotoch that tried to cross the mountains of Ados in winter?", // made up
			"the fire elemental and the ice elemental that went on a school exchange programme to each others "
					+ "family?", // made up
			"the Great Ent–Beaver War started by some beavers damming a brook and setting the ents' roots under"
					+ " water?", // made up
			"the soldiers who entered the Semos Dungeon to be never seen again?", // Stendhal Kanmararn Soldiers
			"the barbarian who dug a tunnel under the ocean?", // Stendhal Lorenz
			"the hero who became an eternal prisoner to hell's demons?", // Stendhal Tomi
			"the little girl who was abducted on her way home from a party to be found two years later on a ship"
					+ " in Ados' harbour?", // Stendhal Susi
			"Lord Lichester who built Or'ril Castle?", // Stendhal
			"the vampire lord and how he came to Semos?", // Stendhal
			"the ship that sank near the shores of Athor?", // Stendhal
			"the cannibal who wore a tribal mask looking like a big lemon?", // Game: Monkey Island
			"the hero who learnt flying by reading a do-it-yourself book in the library?", // Game: Indiana Jones 3
			"the clownfish that searched together with a surgeonfish the whole ocean for his son?",
			// movie: Finding Nemo
			"the amulett that whoever it wore was able to understand the language of all animals?",
			// changed old story, variation of The White Snake
			"the beautiful girl who made friends with seven dwarves?", // old story: Snow White
			"the witch who lured children into her hut to fry and eat them?", // old story: Hansel & Gretel
			"the beautiful princess who slept for a hundred years?", // old story: Sleeping Beauty
			"the boy who was raised by wolves in the forest of Semos?", // old story: Jungle Book
			"the muscleman who became almost invulnerable after bathing in dragon blood?", // old story: Nibelungen
			"the mouse that pulled a thorne out of a lions paw?", // old story
			"the fox that failed to eat the grapes because they hung to high?", // old story
			"the sun and the wind that competed each other for who would be the strongest?", // old story
			"the monkeys that raced through the woods in the desperate search for a coconut?",
			// song, German: Die Affen rasen durch den Wald
			// https://en.wikipedia.org/wiki/List_of_nursery_rhymes
			"the old king who was a merry old soul and his fiddlers three?", // nursery rhyme: Old King Cole
			"the ninety nine bottles of beer on the wall?", // nursery rhyme: 99 Bottles of Beer
			"the three blind mice who lost their tails to a carving knife?", // nursery rhyme: Three Blind Mice
			"the three little kittens who lost their mittens?", // nursery rhyme: Three Little Kittens
			"the guy who went to market to buy a fat hog, jiggety-jog, and a plum cake?",
			// nursery rhyme: To Market, to Market
			"Tom, Tom, the piper's son, who stole a pig, and away he run?",
			// nursery rhyme: Tom, Tom, the Piper's Son
			"what little boys and little girls are made of?", // nursery rhyme: What Are Little Boys Made Of?
			"the old farmer who had a farm with a lot of loud animals?", // nursery rhyme: Old MacDonald
			"the kid who likes to eat, eat, eat apples and bananas?", // nursery rhyme: Apples and Bananas
			"the farmer who had a dog and Bingo was his name-o?", // nursery rhyme: Bingo
			"a-tisket a-tasket a green and yellow basket?", // nursery rhyme: A-Tisket, A-Tasket
			"the black sheep that had three bags full of wool?", // nursery rhyme: Baa, Baa, Black Sheep
			"the monk who let the right moment to ring the bells slip by because he overslept?"
			// nursery rhyme: Brother John, although the English version distorts the
			// original meaning
	};
}
