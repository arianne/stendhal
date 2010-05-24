package games.stendhal.server.maps.semos.wizardstower;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;

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
				addHelp("You are located in the #store. You can enter the spire by the teleporter in front of me. The one behind me teleports you back to the tower entrance.");
				addJob("I am the guardian and #storekeeper of the #wizards tower.");
				addGoodbye("So long!");
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("store", "storekeeper"),
				        ConversationStates.ATTENDING,
				        "I can create #special items with the materials from the store. Just tell me what you want, but for the most items I will need extra ingredients.",
				        null);
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("special"),
				        ConversationStates.ATTENDING,
				        "For example I can create a #riftcloak. I could read in your mind, adventurer. But it is not allowed to me here. So you have to tell me which special item you want and I will tell you, if I can help you.",
				        null);
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

	//behavior on special item SCROLL
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("scroll", "scrolls"),
				        ConversationStates.ATTENDING,
				        "I will create a magic scroll for you, but I need eight pieces of wood for that. If you want the scroll and got the wood, then just tell me to #create #a #scroll. The magic scroll is empty and can be enchanted by wizards.",
				        null);
		add(ConversationStates.ATTENDING, Arrays.asList("create a scroll"),
			new NotCondition(new PlayerHasItemWithHimCondition("wood", 8)),
			ConversationStates.ATTENDING,
			"You don't have enough wood, I will need eight pieces.", null);
		add(ConversationStates.ATTENDING, Arrays.asList("create a scroll"),
			new PlayerHasItemWithHimCondition("wood", 8),
			ConversationStates.ATTENDING,
			"There is your magic scroll.",
			new MultipleActions(new DropItemAction("wood", 8),
			new EquipItemAction("scroll", 1, true),
			new IncreaseXPAction(250)));

	//behavior on special item RIFTCLOAK
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("riftcloak"),
				        ConversationStates.ATTENDING,
				        "I will create a riftcloak for you, but I have to spine a carbuncle and an emerald in the magic. When you have both gems then just tell me to #create #a #riftcloak. But remember! The cloak will protect you only one time"+
					" entering a magical rift. The rift disintegrates the cloak instead of you. There is no way to get the cloak back. If you want to enter the rift again, you will need a new riftcloak.",
				        null);
		add(ConversationStates.ATTENDING, Arrays.asList("create a riftcloak"),
			new AndCondition(
			new NotCondition(new PlayerHasItemWithHimCondition("carbuncle", 1)),
			new PlayerHasItemWithHimCondition("emerald", 1)),
			ConversationStates.ATTENDING,
			"You don't have a carbuncle, I will need an emerald and a carbuncle.", null);
		add(ConversationStates.ATTENDING, Arrays.asList("create a riftcloak"),
			new AndCondition(
			new NotCondition(new PlayerHasItemWithHimCondition("emerald", 1)),
			new PlayerHasItemWithHimCondition("carbuncle", 1)),
			ConversationStates.ATTENDING,
			"You don't have an emerald, I will need a carbuncle and an emerald.", null);
		add(ConversationStates.ATTENDING, Arrays.asList("create a riftcloak"),
			new AndCondition(
			new PlayerHasItemWithHimCondition("emerald", 1),
			new PlayerHasItemWithHimCondition("carbuncle", 1)),
			ConversationStates.ATTENDING,
			"There is your riftcloak. Don't forget that it protects you only one time, befor it will be destroyed. So be sure that you are ready for what awaits you in the rift.",
			new MultipleActions(new DropItemAction("carbuncle", 1),
			new DropItemAction("emerald", 1),
			new EquipItemAction("riftcloak", 1, true),
			new IncreaseXPAction(5000)));

	//behavior on special item XARUHWAIYZ PHIAL
			} //remaining behavior defined in maps.quests.ZekielsPracticalTestQuest
		};

		zekielspire.setDescription("You see Zekiel, the guardian of this tower.");
		zekielspire.setEntityClass("transparentnpc");
		zekielspire.setPosition(15, 15);
		zekielspire.initHP(100);
		zone.add(zekielspire);
	}
}
