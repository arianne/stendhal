package games.stendhal.server.maps.quests.mithrilcloak;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

/*
 * @author kymara
 */

class InitialSteps {

	private MithrilCloakQuestInfo mithrilcloak;
	
	private final NPCList npcs = SingletonRepository.getNPCList();

	public InitialSteps(final MithrilCloakQuestInfo mithrilcloak) {
		this.mithrilcloak = mithrilcloak;
	}

	private void offerQuestStep() {
		final SpeakerNPC npc = npcs.get("Ida");
		

		// player asks about quest, they haven't started it yet
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new OrCondition(new QuestNotStartedCondition(mithrilcloak.getQuestSlot()), new QuestInStateCondition(mithrilcloak.getQuestSlot(), "rejected")),				
				ConversationStates.QUEST_OFFERED, 
				"My sewing machine is broken, will you help me fix it?",
				null);

		// Player says yes they want to help 
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING,
			null,			
				new ChatAction() {
							public void fire(final Player player,
									final Sentence sentence,
									final SpeakerNPC npc) {
								final List<String> PARTS_LIST = Arrays.asList("leather armor", "oil", "bobbin");
								final String parts = Rand.rand(PARTS_LIST);
								if ("leather armor".equals(parts)) {
									npc.say("Thank you! It needs a piece of leather to fix it. Please fetch me " 
											+ Grammar.a_noun(parts) + " and come back as soon as you can.");
								} else if ("oil".equals(parts)) {
									npc.say("Thank you! It isn't running smoothly and needs a can of #oil"
												+ ", I'm ever so grateful for your help.");
								} else {
										npc.say("Thank you! It needs a replacement #bobbin"
												+ ", I'm ever so grateful for your help.");
								}
								new SetQuestAndModifyKarmaAction(mithrilcloak.getQuestSlot(), "machine;" 
																 + parts, 15.0).fire(player, sentence, npc);
							}
				}
				);
		
		// player said no they didn't want to help
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.IDLE,
			"Oh dear, I don't know what I can do without a decent sewing machine. But don't worry I won't bother you any longer!",
			new SetQuestAndModifyKarmaAction(mithrilcloak.getQuestSlot(), "rejected", -5.0));


		// player asks for quest but they already did it	
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestCompletedCondition(mithrilcloak.getQuestSlot()),
				ConversationStates.ATTENDING, 
				"You've already completed the only quest that I have for you.",
				null);
		
		//player fixed the machine but hadn't got mithril shield. 
		// they return and ask for quest but they still haven't got mithril shield
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new AndCondition(new NotCondition(new QuestCompletedCondition(mithrilcloak.getShieldQuestSlot())),
								 new OrCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "need_mithril_shield"),
												 new QuestInStateCondition(mithrilcloak.getQuestSlot(), "fixed_machine"))
								 ),
				ConversationStates.ATTENDING, 
								 "I don't have anything for you until you have proved yourself worthy of carrying mithril items, by getting the mithril shield.",
				null);


		// player fixed the machine but hadn't got mithril shield at time or didn't ask to hear more about the cloak. 
		// when they have got it and return to ask for quest she offers the cloak
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new AndCondition(
								 new QuestCompletedCondition(mithrilcloak.getShieldQuestSlot()),
								 new OrCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "need_mithril_shield"),
												 new QuestInStateCondition(mithrilcloak.getQuestSlot(), "fixed_machine"))
								 ),
				ConversationStates.QUEST_2_OFFERED, 
				"Congratulations, you completed the quest for the mithril shield! Now, I have another quest for you, do you want to hear it?",
				null);

		npc.addReply("oil", "The only oil I have ever had is very fishy smelling. I expect a fisherman made it.");
		npc.addReply("bobbin", "Only dwarf smiths make bobbins, no-one else has nimble enough fingers. Try #Alrak.");
		npc.addReply("Alrak", "I thought you kids all knew Alrak, the only dwarf that kobolds have ever liked. Or maybe he's the only dwarf to ever like kobolds, I've never been sure which ...");

	}

	
	private void fixMachineStep() {

		final SpeakerNPC npc = npcs.get("Ida");

		// player returns who has agreed to help fix machine and prompts ida
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("sewing", "machine", "sewing machine", "task", "quest"),
				new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "machine"),
				ConversationStates.QUEST_ITEM_QUESTION,
				"My sewing machine is still broken, did you bring anything to fix it?",
				null);

			// we stored the needed part name as part of the quest slot
			npc.add(ConversationStates.QUEST_ITEM_QUESTION,
					ConversationPhrases.YES_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null,
					new ChatAction() {
						public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
							final String[] questslot = player.getQuest(mithrilcloak.getQuestSlot()).split(";");
							if (player.isEquipped(questslot[1])) {
								player.drop(questslot[1]);
								npc.say("Thank you so much! Listen, I must repay the favour, and I have a wonderful idea. Do you want to hear more?");
								player.addXP(100);
								player.setQuest(mithrilcloak.getQuestSlot(), "fixed_machine");
								player.notifyWorldAboutChanges();
								npc.setCurrentState(ConversationStates.QUEST_2_OFFERED);
							} else {
								npc.say("No, you don't have the " + Grammar.fullForm(questslot[1]) + " I need. What a shame.");
							}
						}
					});

			// player doesn't have the item to fix machine yet				
		   npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				   ConversationPhrases.NO_MESSAGES,
				   null,
				   ConversationStates.ATTENDING,
				   "Ok, well if there's anything else I can help you with just say. Don't forget about me though!",
				   null);

		   //offer cloak
		   npc.add(ConversationStates.QUEST_2_OFFERED,
				   ConversationPhrases.YES_MESSAGES,
				   new QuestCompletedCondition(mithrilcloak.getShieldQuestSlot()),
				   ConversationStates.ATTENDING,		   
				   "I will make you the most amazing cloak of mithril. You just need to get me the fabric and any tools I need! First please bring me a couple yards of " + mithrilcloak.getFabricName() + ". The expert on fabrics is the wizard #Kampusch.",
				   new SetQuestAndModifyKarmaAction(mithrilcloak.getQuestSlot(), "need_fabric", 10.0));
					

			// player asks for quest but they haven't completed mithril shield quest
			npc.add(ConversationStates.QUEST_2_OFFERED,
				ConversationPhrases.YES_MESSAGES, 
				new AndCondition(
								 new NotCondition(new QuestCompletedCondition(mithrilcloak.getShieldQuestSlot())),
								 new QuestStartedCondition(mithrilcloak.getShieldQuestSlot())
								 ),
				ConversationStates.ATTENDING, 
				"Oh, I see you are already on a quest to obtain a mithril shield. You see, I was going to offer you a mithril cloak. But you should finish that first. Come back when you've finished the mithril shield quest and we will speak again.",
				new SetQuestAction(mithrilcloak.getQuestSlot(), "need_mithril_shield"));
			
			// player asks for quest but they haven't completed mithril shield quest
			npc.add(ConversationStates.QUEST_2_OFFERED,
					ConversationPhrases.YES_MESSAGES,
					new QuestNotStartedCondition(mithrilcloak.getShieldQuestSlot()),
					ConversationStates.ATTENDING, 
					"There are legends of a wizard called Baldemar, in the famous underground magic city, who will forge a mithril shield for those who bring him what it needs. You should meet him and do what he asks. Once you have completed that quest, come back here and speak with me again. I will have another quest for you.",
					new SetQuestAction(mithrilcloak.getQuestSlot(), "need_mithril_shield"));

			// player refused to hear more about another quest after fixing machine
			npc.add(ConversationStates.QUEST_2_OFFERED,
					ConversationPhrases.NO_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"Ok then obviously you don't need any mithril items! Forgive me for offering to help...!",
					null);

			// where to find wizard
			npc.addReply("Kampusch", "He is obsessed with antiques so look for him in an antiques shop or a museum.");
	
	}

	public void addToWorld() {
		offerQuestStep();
		fixMachineStep();
	}

}
