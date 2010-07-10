package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

/**
 * QUEST: The Jailed Barbarian
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Lorenz, the jailed barbarian in a hut on Amazon Island</li>
 * <li>Esclara the Amazon Princess</li>
 * <li>Ylflia the Princess of Kalavan</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>1. Lorenz ask you for a scythe to bring him</li>
 * <li>2. You have to ask Princess Esclara for a 'reason'</li>
 * <li>3. You have to bring him an egg</li>
 * <li>4. You have to inform Princess Ylflia</li>
 * <li>5. You have to bring him a barbarian armor</li>
 * <li>6. You get a reward.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>You get 20 gold bars</li>
 * <li>Karma: 15</li>
 * <li>You get 52,000 experience points in all</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Not repeatable.</li>
 * </ul>
 */
 
 public class JailedBarbarian extends AbstractQuest {
 
 	private static final String QUEST_SLOT = "jailedbarb";
 	
 
 	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void step1() {
		final SpeakerNPC npc = npcs.get("Lorenz");	
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, 
				"I need some help to escape from this prison. These ugly Amazonesses! Can you help me please?",
				null);
							
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thank you for your help! Now I can escape!",
				null);
		

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Thank you! First I need a #scythe to cut down these ugly flowers. And beware of bringing me an old one! Let me know if you have one!",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 10.0));

		// Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
				"So go away and someone who can help me!",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));
	}
	
	private void step2() {
	final SpeakerNPC npc = npcs.get("Lorenz");	
	
		npc.add(ConversationStates.ATTENDING, "scythe",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"),
				new PlayerHasItemWithHimCondition("scythe")),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					player.drop("scythe");
					player.addKarma(10);
					player.addXP(1000);
					npc.say("Thank you!! First part is done! Now I can cut all flowers down! Now please ask Princess Esclara why I am here! I think saying my name should tell her something...");
					player.setQuest(QUEST_SLOT, "capture");
				};
		});

		npc.add(
			ConversationStates.ATTENDING, "scythe",
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new PlayerHasItemWithHimCondition("scythe"))),
			ConversationStates.ATTENDING,
			"You don't have a scythe yet! Go and get one and hurry up!",
			null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"I already asked you to bring me a #scythe to cut the flowers down!",
				null);
	}
	
	private void step3() {
	final SpeakerNPC npc = npcs.get("Princess Esclara");
	
		npc.add(ConversationStates.ATTENDING, "Lorenz",
				new QuestInStateCondition(QUEST_SLOT, "capture"),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					npc.say("You want to know why he is in there? He and his ugly friends dug the #tunnel to our sweet Island! That's why he got jailed!");
					player.setQuest(QUEST_SLOT, "princess");
				};
		});
		npc.add(ConversationStates.ATTENDING, "tunnel",
				new QuestInStateCondition(QUEST_SLOT, "princess"),
				ConversationStates.ATTENDING, "I am angry now and won't speak any more of it! If you want to learn more you'll have to ask him about the #tunnel!",
				null);	

	}
	
	private void step4() {
	final SpeakerNPC npc = npcs.get("Lorenz");
	
		npc.add(ConversationStates.ATTENDING, "tunnel",
				new QuestInStateCondition(QUEST_SLOT, "princess"),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					npc.say("What she drives me nuts, like all the flowers! This makes me hungry, go and get an #egg for me! Just let me know, you got one.");
					player.setQuest(QUEST_SLOT, "egg");
				};
		});	
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestInStateCondition(QUEST_SLOT, "capture"),
				ConversationStates.ATTENDING,
				"Please go ask Princess Esclara why I am here! I think saying my name should prompt her to tell you",
				null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestInStateCondition(QUEST_SLOT, "princess"),
				ConversationStates.ATTENDING,
				"I bet Princess Esclara said I was imprisoned because of the #tunnel ... ",
				null);
	}
	
	private void step5() {
	final SpeakerNPC npc = npcs.get("Lorenz");	
	
		npc.add(ConversationStates.ATTENDING, "egg",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "egg"),
				new PlayerHasItemWithHimCondition("egg")),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					player.drop("egg");
					player.addKarma(10);
					player.addXP(1000);
					npc.say("Thank you again my friend. Now you have to tell Princess Ylflia, in Kalavan Castle, that I am #jailed here. Please hurry up!");
					player.setQuest(QUEST_SLOT, "jailed");
				};
		});

		npc.add(
			ConversationStates.ATTENDING, "egg",
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "egg"), new NotCondition(new PlayerHasItemWithHimCondition("egg"))),
			ConversationStates.ATTENDING,
			"I cannot see an egg!",
			null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestInStateCondition(QUEST_SLOT, "egg"),
				ConversationStates.ATTENDING,
				"I asked you to fetch an #egg for me!",
				null);
		
		npc.add(ConversationStates.ATTENDING, "jailed",
				new QuestInStateCondition(QUEST_SLOT, "jailed"),
				ConversationStates.ATTENDING, "I know that *I'm* jailed! I need you to go tell Princess Ylflia that I am here!",
				null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestInStateCondition(QUEST_SLOT, "jailed"),
				ConversationStates.ATTENDING,
				"I need you to go tell Princess Ylflia that I am #jailed here.",
				null);
	}
	
	private void step6() {
	final SpeakerNPC npc = npcs.get("Princess Ylflia");
	
		npc.add(ConversationStates.ATTENDING, Arrays.asList("jailed", "Lorenz"),
				new QuestInStateCondition(QUEST_SLOT, "jailed"),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					npc.say("Oh my dear. My father should not know it. Hope he is fine! Thanks for this message! Send him #greetings! You better return to him, he could need more help.");
					player.setQuest(QUEST_SLOT, "spoken");
				};
		});

		npc.add(ConversationStates.ATTENDING, "greetings",
				new QuestInStateCondition(QUEST_SLOT, "spoken"),
				ConversationStates.ATTENDING, "Please, go and give Lorenz my #greetings.",
				null);

	}

	private void step7() {
	final SpeakerNPC npc = npcs.get("Lorenz");
	
		npc.add(ConversationStates.ATTENDING, "greetings",
				new QuestInStateCondition(QUEST_SLOT, "spoken"),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					npc.say("Thanks my friend. Now a final task for you! Bring me a barbarian armor. Without this I cannot escape from here! Go! Go! And let me know when you have the #armor !");
					player.setQuest(QUEST_SLOT, "armor");
				};
		});
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestInStateCondition(QUEST_SLOT, "greetings"),
				ConversationStates.ATTENDING,
				"I suppose you must have spoken with Princess Ylflia by now ... I do hope she sent her warmest #greetings to me...",
				null);
	}
	
	private void step8() {
	final SpeakerNPC npc = npcs.get("Lorenz");	
	
		npc.add(ConversationStates.ATTENDING, "armor",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "armor"),
				new PlayerHasItemWithHimCondition("barbarian armor")),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					player.drop("barbarian armor");
					 final StackableItem gold = (StackableItem) SingletonRepository.getEntityManager().getItem("gold bar");
					final int goldamount = 20;
					gold.setQuantity(goldamount);
					player.equipOrPutOnGround(gold);
					player.addKarma(15);
					player.addXP(50000);
					npc.say("Thats all! Now I am prepared for my escape! Here is something I have stolen from Princess Esclara! Do not let her know. And now leave me!");
					player.setQuest(QUEST_SLOT, "done");
				};
		});

		npc.add(
			ConversationStates.ATTENDING, "armor",
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "armor"), new NotCondition(new PlayerHasItemWithHimCondition("barbarian armor"))),
			ConversationStates.ATTENDING,
			"You have no barbarian armor with you! Go get one!",
			null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestInStateCondition(QUEST_SLOT, "armor"),
				ConversationStates.ATTENDING,
				"I am waiting for you to bring me a barbarian #armor so I am strong enough to escape.",
				null);
	}
	
	@Override
	public void addToWorld() {
		super.addToWorld();

		step1();
		step2();
		step3();
		step4();
		step5();
		step6();
		step7();
		step8();
	}
	@Override
	public String getName() {
		return "JailedBarbarian";
	}
	
	// Amazon is dangerous below this level - don't hint to go there
	@Override
	public int getMinLevel() {
		return 70;
	}
}
 
