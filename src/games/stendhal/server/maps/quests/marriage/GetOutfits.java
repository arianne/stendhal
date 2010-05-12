package games.stendhal.server.maps.quests.marriage;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

class GetOutfits {
	private final MarriageQuestInfo marriage;
	
	private final NPCList npcs = SingletonRepository.getNPCList();
	private SpeakerNPC tam;
	private SpeakerNPC tim;
	
	public GetOutfits(final MarriageQuestInfo marriage) {
		this.marriage = marriage;
	}
	
	private void getOutfitsStep() {
		tam = npcs.get("Tamara");
		tam.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new ChatCondition() {
					public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
						return marriage.isEngaged(player);
					}
				}, 
				ConversationStates.ATTENDING,
				"Welcome! If you're a bride-to-be I can #help you get ready for your wedding",
				null);
		tam.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new ChatCondition() {
					public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
						return !marriage.isEngaged(player);
					}
				}, 
				ConversationStates.IDLE,
				"Sorry, I can't help you, I am busy getting dresses ready for brides-to-be!",
				null);
		
		tim = npcs.get("Timothy");
		tim.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new ChatCondition() {
					public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
						return marriage.isEngaged(player);
					}
				}, 
				ConversationStates.ATTENDING,
				"Good day! If you're a prospective groom I can #help you prepare for your wedding.",
				null);
		tim.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new ChatCondition() {
					public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
						return !marriage.isEngaged(player);
					}
				}, 
				ConversationStates.IDLE,
				"Sorry, I can't help you, I am busy pressing suits.",
				null);
	}

	public void addToWorld() {
		getOutfitsStep();
	}
}
