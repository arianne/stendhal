package games.stendhal.server.maps.semos.jail;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.player.Player;

/**
 * The prison guard (original name: Marcus) who's patrolling along the cells.
 * 
 * @author hendrik
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class GuardNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Greetings! How may I #help you?");
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.JOB_MESSAGES,
				new NotInJailCondition(),
		        ConversationStates.ATTENDING,
		        "I am the jail keeper.",
		        null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.JOB_MESSAGES,
				new InJailCondition(),
		        ConversationStates.ATTENDING,
		        "I am the jail keeper. You have been confined here because of your bad behaviour.",
		        null);
		
		npc.add(ConversationStates.ATTENDING,
		        ConversationPhrases.HELP_MESSAGES,
		        new InJailCondition(),
		        ConversationStates.ATTENDING,
		        "Please wait for an administrator to come here and decide what to do with you. In the meantime, there is no escape for you. If you logout, your jail sentence will simply be restarted.",
		        null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.HELP_MESSAGES,
				new NotInJailCondition(),
		        ConversationStates.ATTENDING,
		        "Be careful with the criminals in the cells.",
		        null);
		
		npc.addGoodbye();
	}

	/**
	 * Is the player speaking to us in jail?
	 */
	public static class InJailCondition implements ChatCondition {

		public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
			return Jail.isInJail(player);
		}
	}

	/**
	 * Is the player speaking to us not in jail?
	 */
	public static class NotInJailCondition implements ChatCondition {

		public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
			return !Jail.isInJail(player);
		}
	}
}
