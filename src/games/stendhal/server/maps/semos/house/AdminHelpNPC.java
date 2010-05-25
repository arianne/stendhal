package games.stendhal.server.maps.semos.house;


import games.stendhal.common.Direction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.npc.behaviour.adder.HealerAdder;
import games.stendhal.server.entity.npc.condition.AdminCondition;

/**
 * A young lady (original name: Skye) who is lovely to admin.
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class AdminHelpNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Hello! You're looking particularly good today. In fact, you look great every day!");
		npc.addJob("I'm here to make you feel happy. And you can come here easily if you #/teleportto me. Also, I can explain the #portals here.");
		npc.addHelp("I can #heal you if you like. Or I can just say #nice #things. If you need to know about the #portals, just ask.");
		npc.addOffer("I can send you to a #playground to play in!");
		npc.addReply("nice", "Did you know how many players think you're lovely for helping? Well I can tell you, loads of them do.");
		npc.addReply("things", "So you're one of the people who tests all the #blue #words, aren't you? Now wonder you have responsibility!");
		npc.addReply("blue", "Aw, don't be sad :( Put some nice music on, perhaps ... ");
		npc.addReply("words", "Roses are red, violets are blue, Stendhal is great, and so are you!");
		npc.addReply("portals", "The one with the Sun goes to semos city. It shows you where this house really is. The rest are clear, I hope. There is a door to the bank, the jail, and the Death Match in Ados. Of course they are all one way portals so you will not be disturbed by unexpected visitors.");
		npc.addQuest("Now you're really testing how much thought went into making me!");
		npc.add(ConversationStates.ATTENDING,
				"playground",
				new AdminCondition(500),
				ConversationStates.IDLE,
				"Have fun!",
				new TeleportAction("int_admin_playground", 20, 20, Direction.DOWN));
		new HealerAdder().addHealer(npc, 0);
		npc.addGoodbye("Bye, remember to take care of yourself.");
	}
}
