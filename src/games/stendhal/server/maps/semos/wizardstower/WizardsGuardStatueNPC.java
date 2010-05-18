package games.stendhal.server.maps.semos.wizardstower;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Arrays;
import java.util.Map;

/**
 * Zekiel, the guardian statue of the Wizards Tower (Zekiel in the basement)
 *
 * @see games.stendhal.server.maps.quests.ZekielsPracticalTestQuest
 * @see games.stendhal.server.maps.semos.WizardsGuardStatueSpireNPC
 */
public class WizardsGuardStatueNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildZekiel(zone);
	}

	private void buildZekiel(final StendhalRPZone zone) {
		final SpeakerNPC zekiel = new SpeakerNPC("Zekiel the guardian") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings Stranger! I am Zekiel the #guardian.");
				addHelp("I guess you want to explore this #tower. I am not just the #guardian, I am also here to receive visitors and accompany them through the practical #test.");
				addReply("guardian", "I watch and guard this #tower, the residence of the #wizards circle.");
				addReply("tower", "If you want to reach the spire, you have to stand the practical #test.");
				addGoodbye("So long!");
				addReply("test", "The practical test will be your #quest from me.");
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("wizard", "wizards"),
				        ConversationStates.ATTENDING,
				        "Seven wizards form the wizards circle. These are #Erastus, #Elana, #Ravashack, #Jaer, #Cassandra, #Silvanus and #Malleus",
				        null);
				addReply("erastus", "Erastus is the archmage of the wizards circle. He is the grandmaster of all magics and the wisest person that is known. He is the only one without a part in the practical test.");
				addReply("elana", "Elana is the warmest and friendliest enchantress. She is the protectress of all living creatures and uses divinely magic to save and heal them.");
				addReply("ravashack", "Ravashack is a very mighty necromancer. He studies the dark magic since ages. Ravashack is a mystery, using dark magic to gain the upper hand on his opponents, but fighting the evil liches, his archenemies.");
				addReply("jaer", "Jaer is the master of illusion. Charming and flighty like a breeze on a hot summerday. His domain is the air and he has many allies in the plains of mythical ghosts");
				addReply("cassandra", "Cassandra is a beautifull woman, but first of all a powerfull sorceress. Cassandras domain is the water and she can be cold like ice to achieve her aim.");
				addReply("silvanus", "Silvanus is a sage druid and perhaps the eldest of all elves. He is a friend of all animals, trees, fairy creatures and ents. His domain is the earth and nature.");
				addReply("malleus", "Malleus is the powerfull archetype of a magician and the master of destructive magics. His domain is the fire and he rambled the plains of demons for ages, to understand their ambitions.");

			} //remaining behaviour defined in maps.quests.ZekielsPracticalTestQuest
		};

		zekiel.setDescription("You see Zekiel, the guardian of this tower.");
		zekiel.setEntityClass("transparentnpc");
		zekiel.setPosition(15, 15);
		zekiel.initHP(100);
		zone.add(zekiel);
	}
}
