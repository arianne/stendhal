package games.stendhal.server.maps.semos.house;


import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

/**
 * A young lady (original name: Skye) who is lovely to admin. 
 */
public class AdminHelpNPC extends SpeakerNPCFactory {

	@Override
	protected void createDialog(SpeakerNPC npc) {
		npc.addGreeting("Hello! You're looking particularly good today. In fact, you look great every day!");
		npc.addJob("I'm here to make you feel happy. And you can come here easily if you #/teleportto me. Also, the portals here take you to useful places!");
		npc.addHelp("I can #heal you if you like. Or I can just say #nice #things.");
		npc.addReply("nice","Did you know how many players think you're lovely for helping? Well I can tell you, loads of them do.");
		npc.addReply("things","So you're one of the people who tests all the #blue #words, aren't you? Now wonder you have responsibility!");
		npc.addReply("blue","Aw, don't be sad :( Put some nice music on, perhaps ... ");
		npc.addReply("words","Roses are red, violets are blue, Stendhal is great, and so are you!");
		npc.addQuest("Now you're really testing how much thought went into making me!");
		npc.addHealer(0);
		npc.addGoodbye("Bye, remember to take care of yourself.");
	}
}
