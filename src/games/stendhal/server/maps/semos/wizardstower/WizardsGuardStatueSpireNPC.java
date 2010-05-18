package games.stendhal.server.maps.semos.wizardstower;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Arrays;
import java.util.Map;

/**
 * Zekiel, the guardian statue of the Wizards Tower (Zekiel in the spire)
 *
 * @see games.stendhal.server.maps.quests.ZekielsPracticalTestQuest
 * @see games.stendhal.server.maps.semos.WizardsGuardStatueNPC
 */
public class WizardsGuardStatueSpireNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildZekielSpire(zone);
	}

	private void buildZekielSpire(final StendhalRPZone zone) {
		final SpeakerNPC zekielspire = new SpeakerNPC("Zekiel") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings again, adventurer!");
				addHelp("");
				addGoodbye("So long!");
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

		zekielspire.setDescription("You see Zekiel, the guardian of this tower.");
		zekielspire.setEntityClass("transparentnpc");
		zekielspire.setPosition(15, 15);
		zekielspire.initHP(100);
		zone.add(zekielspire);
	}
}
