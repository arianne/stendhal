package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Susi's father during the Semos Mine Town Revival Weeks
 */
public class DadNPC {
	private void createDadNPC() {
		final StendhalRPZone zone2 = SingletonRepository.getRPWorld().getZone("int_semos_frank_house");
		final SpeakerNPC npc2 = new SpeakerNPC("Mr Ross") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi there.");
				addJob("I'm on vacation now for the Semos Mine Town Revival Weeks. But I still need to finish some work before my daughter #Susi and I can enjoy the party.");
				addHelp("My daughter Susi is all excited about the Semos Mine Town Revival Weeks. But I am really concerned that something bad may happen to her again. So she will have to wait until I finish my work.");
				addReply("susi", "My daughter Susi is all excited about the Semos Mine Town Revival Weeks. But I am really concerned that something bad may happen to her again. So she will have to wait until I finish my work.");
				addOffer("Sorry I do not have anything to offer you. I have to walk #Susi home to Ados once the party is over.");
				addQuest("Go meet my daughter #Susi, she'd love to make friends.");
				addGoodbye("Bye, nice to meet you.");
			}
		};

		npc2.setOutfit(new Outfit(27, 07, 34, 01));
		npc2.setPosition(21, 10);
		npc2.setDirection(Direction.LEFT);
		npc2.initHP(100);
		zone2.add(npc2);
	}

	public void addToWorld() {
		createDadNPC();
	}
}
