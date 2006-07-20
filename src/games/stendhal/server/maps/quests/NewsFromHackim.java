package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: News from Hackim
 * PARTICIPANTS:
 * - Hackim
 * - Xin Blanca
 *
 * STEPS:
 * - Hackim asks you to give a message to Xin Blanca.
 * - Xin Blanca thanks you with a pair of leather_legs.
 *
 * REWARD:
 * - 10 XP
 * - a pair of leather_legs
 *
 * REPETITIONS:
 * - None.
 */
public class NewsFromHackim implements IQuest {
	private StendhalRPWorld world;

	private NPCList npcs;

	private void step_1() {
		SpeakerNPC npc = npcs.get("Hackim Easso");

		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted("news_hackim")) {
							engine.say("Shhh come here: Do me a favour and tell #Xin Blanca that the new supply of weapons is ready, will you?");
						} else {
							engine.say("Thanks, but I don't have any new message for #Xin. I can't smuggle so often and even now I think Xoderos is beginning to suspect something. Anyway, if I can help you somehow say it.");
							engine.setActualState(ConversationStates.ATTENDING);
						}
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				"Thanks. I'm sure that Xin will reward you generously. Now if I can help you in anything just ask.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						player.setQuest("news_hackim", "start");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Yes, now that I think about it, it isn't wise to involve anyone else in this small business. Forget it bud, I haven't told you anything... Now if I can help you just ask.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						player.setQuest("news_hackim", "rejected");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"Xin",
				null,
				ConversationStates.QUEST_OFFERED,
				"You don't know who Xin is? Everybody at the tavern knows Xin. He's the guy who owes money for beer to most people in Semus. So, will you do it?",
				null);
	}

	private void step_2() {

		SpeakerNPC npc = npcs.get("Xin Blanca");

		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest("news_hackim")
								&& player.getQuest("news_hackim").equals("start");
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						String answer;
						if (!player.isEquipped("leather_legs")) {
							answer = "Take this pair of shining leather_legs! Now if you need anything else just say it";
						} else {
							answer = "Yes, I know you have a pair of leather_legs already but it's the only cheap thing I've found for you. Now if you need anything else from me just say it";
						}
						//player.say("So... to make a long story short: I know your business with Hackim and I'm here to tell you that the next supply is ready");
						engine.say("So it is ready at last! Those are very good news! Let me give you an item for your service. "
										+ answer);
						player.setQuest("news_hackim", "done");
		
						Item item = world.getRuleManager().getEntityManager().getItem(
								"leather_legs");
						player.equip(item, true);
						player.addXP(10);
		
						world.modify(player);
					}
				});
	}

	public NewsFromHackim(StendhalRPWorld w, StendhalRPRuleProcessor rules) {
		this.npcs = NPCList.get();
		this.world = w;

		step_1();
		step_2();
	}
  }