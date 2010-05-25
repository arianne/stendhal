package games.stendhal.server.maps.semos.bank;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
//TODO: take NPC definition elements which are currently in XML and include here
public class CustomerAdvisorNPC extends SpeakerNPCFactory {

	private final class VaultChatAction implements ChatAction {
		
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
			final StendhalRPZone vaultzone = (StendhalRPZone) SingletonRepository
					.getRPWorld().getRPZone("int_vault");
			String zoneName = player.getName() + "_vault";
			
			final StendhalRPZone zone = new Vault(zoneName, vaultzone, player);
			
			
			SingletonRepository.getRPWorld().addRPZone(zone);
			player.teleport(zone, 4, 5, Direction.UP, player);
			
			
		}
	}

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Welcome to the bank of Semos! I am here to #help you manage your personal chest.");
		npc.addHelp("Follow the corridor to the right, and you will find the magic chests. You can store your belongings in any of them, and nobody else will be able to touch them! A number of spells have been cast on the chest areas to ensure #safety.");
		npc.addReply("safety", "When you are standing at a chest to organise your items, any other people or animals will not be able to come near you. A magical aura stops others from using scrolls to arrive near you. You will need to walk out. Lastly let me tell you about safe #trading.");
		npc.addReply("trading", "There is a large table in the top right hand corner of this bank. It is designed so that trading can be done safely. Here is how to use it: Each take a chair and sit at opposite sides of the table. Once you have agreed to a trade, place up to 3 items at once on the 3 tiles directly adjacent to you on the table. Wait until the other person has done the same. Make sure you can see exactly what they have placed and how much of each item. Next is the #exchange.");
		npc.addReply("exchange", "When you are both ready, swap places. The narrow corridors are designed so that no-one else can take the items you have placed. If someone gets in the way you can just go back and remove your items from the table until the area is clear again. If you don't understand anything, try asking another player for a demonstration. Oh, and by the way, we also have #security at the table.");
		npc.addReply("security", "Yes, there is a spell to make sure no-one can return to this world next to the table. If they exit to the astral plane when standing by the table, and then attempt to return there, they are magically moved to a safer place. Good luck with your trading!");
		npc.addJob("I'm the Customer Advisor here at Semos Bank.");
		npc.addOffer("If you wish to access your personal chest in solitude, I can give you access to a private #vault. A guidebook inside will explain how it works.");		
		npc.addGoodbye("It was a pleasure to serve you.");
		npc.add(ConversationStates.ANY, "vault", new QuestCompletedCondition("armor_dagobert"), ConversationStates.IDLE, null, 
				new VaultChatAction());
		
		npc.add(ConversationStates.ANY, "vault", new QuestNotCompletedCondition("armor_dagobert"), ConversationStates.ATTENDING, "Perhaps you could do a #favour for me, and then I will tell you more about the private banking vaults.", null);
		
		// remaining behaviour defined in games.stendhal.server.maps.quests.ArmorForDagobert	
	}

	
}
