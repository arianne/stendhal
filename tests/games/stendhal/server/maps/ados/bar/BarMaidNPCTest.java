package games.stendhal.server.maps.ados.bar;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.npc.parser.Sentence;


import marauroa.common.Log4J;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class BarMaidNPCTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConfigureZone() {
		SingletonRepository.getRPWorld();
		BarMaidNPC barmaidConfigurator = new BarMaidNPC();

		StendhalRPZone zone = new StendhalRPZone("testzone");
		barmaidConfigurator.configureZone(zone , null);
		assertFalse(zone.getNPCList().isEmpty());
		NPC barMaid =zone.getNPCList().get(0);
		assertThat(barMaid.getName(), is("Siandra"));
		assertThat(barMaid.getDescription(), is("You see a pretty young bar maid."));
	}

	@Test
	public void testHiandBye() throws Exception {
		SingletonRepository.getRPWorld();
		BarMaidNPC barmaidConfigurator = new BarMaidNPC();
		StendhalRPZone zone = new StendhalRPZone("testzone");
		barmaidConfigurator.configureZone(zone , null);
		SpeakerNPC barMaid =(SpeakerNPC) zone.getNPCList().get(0);
		assertThat(barMaid.getName(), is("Siandra"));
		Engine engine = barMaid.getEngine();
		engine.setCurrentState(ConversationStates.IDLE);
		Sentence sentence = ConversationParser.parse("hi");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.ATTENDING));
		assertThat(barMaid.getText(),is("Hi!"));
		sentence = ConversationParser.parse("bye");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.IDLE));
		assertThat(barMaid.getText(),is("Bye bye!"));
	}

	@Test
	public void testJobOfferQuest() throws Exception {
		SingletonRepository.getRPWorld();
		BarMaidNPC barmaidConfigurator = new BarMaidNPC();
		StendhalRPZone zone = new StendhalRPZone("testzone");
		barmaidConfigurator.configureZone(zone , null);
		SpeakerNPC barMaid =(SpeakerNPC) zone.getNPCList().get(0);
		assertThat(barMaid.getName(), is("Siandra"));
		Engine engine = barMaid.getEngine();
		engine.setCurrentState(ConversationStates.ATTENDING);

		Sentence sentence = ConversationParser.parse("job");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.ATTENDING));
		assertThat("job text", barMaid.getText(),is("I'm a bar maid. But we've run out of food to feed our customers, can you #offer any?"));

		sentence = ConversationParser.parse("help");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.ATTENDING));
		assertThat("help text", barMaid.getText(),is("If you could #offer any meat, ham or cheese to restock our larders I'd be grateful."));

		sentence = ConversationParser.parse("quest");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.ATTENDING));
		assertThat("quest text", barMaid.getText(),is("Just #offers of food is enough, thank you."));
	}

	@Test
	public void testBuyerBehaviour() throws Exception {
		SingletonRepository.getRPWorld();
		
		BarMaidNPC barmaidConfigurator = new BarMaidNPC();
		StendhalRPZone zone = new StendhalRPZone("testzone");
		barmaidConfigurator.configureZone(zone , null);
		SpeakerNPC barMaid =(SpeakerNPC) zone.getNPCList().get(0);
		assertThat(barMaid.getName(), is("Siandra"));
		Engine engine = barMaid.getEngine();
		engine.setCurrentState(ConversationStates.ATTENDING);

		Sentence sentence = ConversationParser.parse("offer");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.ATTENDING));
		assertThat("offer text", barMaid.getText(),is("I buy cheese, meat, spinach, ham, flour, and porcini."));

		sentence = ConversationParser.parse("sell");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.ATTENDING));
		assertThat("offer text", barMaid.getText(),is("Please tell me what you want to sell."));

		sentence = ConversationParser.parse("sell cheese");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", barMaid.getText(),is("1 piece of cheese is worth 5. Do you want to sell it?"));
		engine.setCurrentState(ConversationStates.ATTENDING);

		sentence = ConversationParser.parse("sell meat");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", barMaid.getText(),is("1 piece of meat is worth 10. Do you want to sell it?"));

		engine.setCurrentState(ConversationStates.ATTENDING);
		sentence = ConversationParser.parse("sell spinach");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", barMaid.getText(),is("1 spinach is worth 15. Do you want to sell it?"));

		engine.setCurrentState(ConversationStates.ATTENDING);
		sentence = ConversationParser.parse("sell ham");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", barMaid.getText(),is("1 piece of ham is worth 20. Do you want to sell it?"));

		engine.setCurrentState(ConversationStates.ATTENDING);
		sentence = ConversationParser.parse("sell flour");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", barMaid.getText(),is("1 sack of flour is worth 25. Do you want to sell it?"));

		engine.setCurrentState(ConversationStates.ATTENDING);
		sentence = ConversationParser.parse("sell porcini");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", barMaid.getText(),is("1 porcini is worth 30. Do you want to sell it?"));

		engine.setCurrentState(ConversationStates.ATTENDING);
		sentence = ConversationParser.parse("sell 2 porcini");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", barMaid.getText(),is("2 porcini are worth 60. Do you want to sell them?"));

		engine.setCurrentState(ConversationStates.ATTENDING);
		sentence = ConversationParser.parse("sell 2 sacks of flour");
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(),is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", barMaid.getText(),is("2 sacks of flour are worth 50. Do you want to sell them?"));

	}


}
