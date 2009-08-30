package games.stendhal.server.maps.quests.marriage;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

class Divorce {
	private final NPCList npcs = SingletonRepository.getNPCList();
	private SpeakerNPC clerk;
	private MarriageQuestInfo marriage;

	public Divorce(final MarriageQuestInfo marriage) {
		this.marriage = marriage;
	}

	private void divorceStep() {

		/**
		 * Creates a clerk NPC who can divorce couples.
		 * 
		 * Note: in this class, the Player variables are called husband and
		 * wife. However, the game doesn't know the concept of genders. The
		 * player who initiates the divorce is just called husband, the other
		 * wife.
		 * 
		 * @author immibis
		 * 
		 */

		clerk = npcs.get("Wilfred");

		clerk.add(ConversationStates.ATTENDING, 
				"divorce",
				new ChatCondition() {
					public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						return (player.isQuestCompleted(marriage.getQuestSlot()))
								&& player.isEquipped("wedding ring") && player.isEquipped("money",200*player.getLevel());
					}
				}, 
				ConversationStates.QUESTION_3,
				null,
			   	new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						Player husband;
						Player wife;
						String partnerName;
						String additional = "";
						husband = player;
						partnerName = husband.getQuest(marriage.getSpouseQuestSlot());
						wife = SingletonRepository.getRuleProcessor().getPlayer(
								partnerName);
						if ((wife != null)
								&& wife.hasQuest(marriage.getQuestSlot())
								&& wife.getQuest(marriage.getSpouseQuestSlot()).equals(
										husband.getName())) {
							if (wife.isEquipped("money", 200*wife.getLevel())) {
								additional = partnerName + " has their fee of " + 200*wife.getLevel() + " and will also be charged.";
							} else {
								additional = partnerName + " doesn't have their fee of " + 200*wife.getLevel() + " and will lose 3% xp instead.";
							}
						}
						npc.say("There's an offer currently, you can pay to divorce instead of losing xp. It will cost you " + 200* player.getLevel() + " money. " + additional + " Do you want to divorce, and pay the money instead of losing xp?");
					}
				});

		clerk.add(ConversationStates.ATTENDING, 
				  "divorce",
				  new ChatCondition() {
					  public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						  return (player.isQuestCompleted(marriage.getQuestSlot()))
							  && player.isEquipped("wedding ring") && !player.isEquipped("money",200*player.getLevel());
					  }
				  }, 
				  ConversationStates.QUESTION_3,
				  null,
				  new ChatAction() {
					  public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						  Player husband;
						  Player wife;
						  String partnerName;
						  String additional = "";
						  husband = player;
						  partnerName = husband.getQuest(marriage.getSpouseQuestSlot());
						  wife = SingletonRepository.getRuleProcessor().getPlayer(
																				  partnerName);
						  if ((wife != null)
							  && wife.hasQuest(marriage.getQuestSlot())
							  && wife.getQuest(marriage.getSpouseQuestSlot()).equals(
																					 husband.getName())) {
							  if (wife.isEquipped("money", 200*wife.getLevel())) {
								  additional = partnerName + " has their fee of " + 200*wife.getLevel() + " and will also be charged.";
							  } else {
								  additional = partnerName + " doesn't have their fee of " + 200*wife.getLevel() + " and will lose 3% xp instead.";
							  }
						  }
						  npc.say("There's an offer currently, you can pay to divorce instead of losing xp. It would cost you " + 200* player.getLevel() + " money, but you do not have sufficent money here with you. " + additional + " You can take the penalty of losing 3% of your xp. Do you want to divorce, and lose the xp?");
					  }
				  });
		
		clerk.add(ConversationStates.ATTENDING,
					"divorce",
					new ChatCondition() {
						public boolean fire(final Player player, final Sentence sentence,
								final SpeakerNPC npc) {
							return (player.hasQuest(marriage.getQuestSlot()) && player.getQuest(
									marriage.getQuestSlot()).equals("just_married"))
									&& player.isEquipped("wedding ring");
						}
					},
					ConversationStates.QUESTION_3,
					"I see you haven't been on your honeymoon yet. Are you sure you want to divorce so soon?",
					null);

		clerk.add(ConversationStates.ATTENDING,
				"divorce",
				new ChatCondition() {
					public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						return !(player.isQuestCompleted(marriage.getQuestSlot()) || (player.hasQuest(marriage.getQuestSlot()) && player.getQuest(
								marriage.getQuestSlot()).equals("just_married")));
					}
				}, ConversationStates.ATTENDING,
				"You're not even married. Stop wasting my time!",
				null);

		clerk.add(ConversationStates.ATTENDING,
				"divorce",
				new NotCondition(new PlayerHasItemWithHimCondition("wedding ring")),
				ConversationStates.ATTENDING,
				"I apologise, but I need your wedding ring in order to divorce you. If you have lost yours, you can go to Ognir to make another.",
				null);

		// If they say no
		clerk.add(ConversationStates.QUESTION_3,
				ConversationPhrases.NO_MESSAGES, 
				null,
				ConversationStates.ATTENDING,
				"I hope you have a happy marriage, then.", 
				null);

		// If they say yes
		clerk.add(ConversationStates.QUESTION_3,
				ConversationPhrases.YES_MESSAGES, 
				null,
				ConversationStates.ATTENDING, 
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						Player husband;
						Player wife;
						String partnerName;
						husband = player;
						partnerName = husband.getQuest(marriage.getSpouseQuestSlot());
						wife = SingletonRepository.getRuleProcessor().getPlayer(
								partnerName);
						// check wife is online and check that they're still
						// married to the current husband
						if ((wife != null)
								&& wife.hasQuest(marriage.getQuestSlot())
								&& wife.getQuest(marriage.getSpouseQuestSlot()).equals(
										husband.getName())) {
							if (wife.isEquipped("wedding ring")) {
								wife.drop("wedding ring");
							}
							if (wife.isEquipped("money", 200*wife.getLevel())) {
								wife.drop("money", 200*wife.getLevel());
							} else {
								final int xp = (int) (wife.getXP() * 0.03);
								wife.subXP(xp);
							}
							wife.removeQuest(marriage.getQuestSlot());
							wife.removeQuest(marriage.getSpouseQuestSlot());
							wife.sendPrivateText(husband.getName()
									+ " has divorced from you.");
							npc.say("What a pity...what a pity...and you two were married so happily, too...");
						} else {
							final Player postman = SingletonRepository.getRuleProcessor().getPlayer(
									"postman");
							if (postman != null) {
								postman.sendPrivateText("Wilfred tells you: msg "
										+ partnerName
										+ " "
										+ husband.getName()
										+ " has divorced from you!");
							}
						}
						if (husband.isEquipped("money", 200*husband.getLevel())) {
							husband.drop("money", 200*husband.getLevel());
						} else {
							final int xp = (int) (husband.getXP() * 0.03);
							husband.subXP(xp);
						}
						husband.drop("wedding ring");
						husband.removeQuest(marriage.getQuestSlot());
						husband.removeQuest(marriage.getSpouseQuestSlot());
						npc.say("What a pity...what a pity...and you two were married so happily, too...");
					}
				});

	}

	public void addToWorld() {
		divorceStep();
	}

}
