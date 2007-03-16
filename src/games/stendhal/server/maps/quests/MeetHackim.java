package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Speak with Hackim
 * PARTICIPANTS:
 * - Hackim
 *
 * STEPS:
 * - Talk to Hackim to activate the quest and keep speaking with Hackim.
 *
 * REWARD:
 * - 10 XP (check that user's level is lower than 15)
 * - 5 gold coins
 *
 * REPETITIONS:
 * - As much as wanted.
 */
public class MeetHackim extends AbstractQuest {

	private void step_1() {

		SpeakerNPC npc = npcs.get("Hackim Easso");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.INFORMATION_1,
				"We aren't allowed to sell weapons to adventurers nowadays; we're working flat-out to produce equipment for the glorious Imperial Deniran Army as they fight against Blordrough's dark legions in the south. (Sssh... can you come here so I can whisper?)",
				null);

		npc.add(ConversationStates.INFORMATION_1,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.INFORMATION_2,
				"*whisper* Go to the tavern and talk to a man called #Xin #Blanca... he buys and sells equipment that might interest you. Do you want to hear more?",
				null);

		npc.add(ConversationStates.INFORMATION_2,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.INFORMATION_3,
				"Ask him what he has to #offer, and look at what he will let you #buy and #sell. For instance, if you had a studded shield which you didn't want, you could #sell #studded_shield.",
				null);

		npc.add(ConversationStates.INFORMATION_3,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						int level = player.getLevel();
						String answer;
						if (level < 15) {
							StackableItem money = (StackableItem) StendhalRPWorld.get()
									.getRuleManager().getEntityManager().getItem(
											"money");
							money.setQuantity(5);
							player.equip(money);
		
							player.addXP(10);
		
							player.notifyWorldAboutChanges();
		
							answer = "If anybody asks, you don't know me!";
						} else {
							answer = "Where did you get those weapons? A toy shop?";
						}
						engine.say("Guessed who supplies Xin Blanca with the weapons he sells? Well, it's me! I have to avoid raising suspicion, though, so I can only smuggle him small weapons. If you want something more powerful, you'll have to venture into the dungeons and kill some of the creatures there for items.\n" + answer);
					}
				});

		npc.add(new int[] { ConversationStates.ATTENDING,
				            ConversationStates.INFORMATION_1,
				            ConversationStates.INFORMATION_2,
				            ConversationStates.INFORMATION_3 },
				"no",
				null,
				ConversationStates.ATTENDING,
				"Remember, all the weapons are counted; best to leave them alone.",
				null);

	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
	}
}
