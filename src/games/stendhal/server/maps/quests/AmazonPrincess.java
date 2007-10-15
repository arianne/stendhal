package games.stendhal.server.maps.quests;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

/**
 * QUEST: The Amazon Princess
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Princess Esclare, the Amazon Princess in a Hut on Amazon Island</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>The princess asks you for an exotic drink</li>
 * <li>Find someone who serves exotic drinks</li>
 * <li>Take exotic drink back to princess</li>
 * <li>Princess gives you a reward</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>Karma +15</li>
 * <li>Some fish pie, random between 1 and 6.</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>You can repeat it ones an hour.</li>
 * </ul>
 */
public class AmazonPrincess extends AbstractQuest {

	private static final String QUEST_SLOT = "amazon_princess";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void offerQuestStep() {
		SpeakerNPC npc = npcs.get("Princess Esclara");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						if (!player.hasQuest(QUEST_SLOT)
								|| player.getQuest(QUEST_SLOT).equals(
										"rejected")) {
							npc
									.say("I'm looking for a drink, should be an exotic one. Can you bring me one?");
							npc
									.setCurrentState(ConversationStates.QUEST_OFFERED);
						} else if (player.isQuestCompleted(QUEST_SLOT)) { // shouldn't happen
							npc.say("I'm drunken now thank you!");
						} else if (player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals(
										"drink_brought")) {
							npc
									.say("The last Pina Colada you brought me was so lovely. Will you bring me another?");
							npc
									.setCurrentState(ConversationStates.QUEST_OFFERED);
						} else {
							npc
									.say("I like these exotic drinks, i lost the name of this special one.");
						}
					}
				});
		// Player agrees to get the drink
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						npc
								.say("Thank you! If you have found some, say #drink to me so I know you have it. I'll be sure to give you a nice reward.");
						player.setQuest(QUEST_SLOT, "start");
						player.addKarma(10.0);
					}
				});
		// Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
				"Oh, never mind. Bye then.", new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						player.addKarma(-10.0);
						player.setQuest(QUEST_SLOT, "rejected");
					}
				});
	}


	private void bringCocktailStep() {
		SpeakerNPC npc = npcs.get("Princess Esclara");
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("drink", "pina_colada", "cocktail", "cheers"),
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text,
							SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals(
										"start")
								&& player.isEquipped("pina_colada");
					}
				}, ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						player.drop("pina_colada");
						player.addKarma(15);
						StackableItem fishpies = (StackableItem) StendhalRPWorld
								.get().getRuleManager().getEntityManager()
								.getItem("fish_pie");
						int pieamount;
						pieamount = Rand.roll1D6();
						fishpies.setQuantity(pieamount);
						player.equip(fishpies, true);
						npc
								.say("Thank you!! Take these " + Integer.toString(pieamount) + " fish pies and this kiss from me.");
						player.setQuest(QUEST_SLOT, "drink_brought");
					}
				});
		npc
				.add(
						ConversationStates.ATTENDING,
						Arrays.asList("drink", "pina_colada", "cocktail", "cheers"),
						new SpeakerNPC.ChatCondition() {
							@Override
							public boolean fire(Player player, String text,
									SpeakerNPC npc) {
								return !player.isEquipped("pina_colada");
							}
						},
						ConversationStates.ATTENDING,
						"Don't lie to me. Go and you better get one!",
						null);

	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		offerQuestStep();
		bringCocktailStep();
	}

}
