package games.stendhal.server.maps.ados.bar;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.ExpressionType;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.npc.parser.SentenceImplementation;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class BarMaidNPCTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();
	}

	@Test
	public void testConfigureZone() {
		
		SingletonRepository.getRPWorld();
		final BarMaidNPC barmaidConfigurator = new BarMaidNPC();

		final StendhalRPZone zone = new StendhalRPZone("testzone");
		barmaidConfigurator.configureZone(zone, null);
		assertFalse(zone.getNPCList().isEmpty());
		final NPC barMaid = zone.getNPCList().get(0);
		assertThat(barMaid.getName(), is("Siandra"));
		assertThat(barMaid.getDescription(), is("You see a pretty young bar maid."));
	}

	@Test
	public void testHiandBye() throws Exception {
		SingletonRepository.getRPWorld();
		final BarMaidNPC barmaidConfigurator = new BarMaidNPC();
		final StendhalRPZone zone = new StendhalRPZone("testzone");
		barmaidConfigurator.configureZone(zone, null);
		final SpeakerNPC barMaid = (SpeakerNPC) zone.getNPCList().get(0);
		assertThat(barMaid.getName(), is("Siandra"));
		final Engine engine = barMaid.getEngine();
		engine.setCurrentState(ConversationStates.IDLE);

		Sentence sentence = new SentenceImplementation(new Expression("hi", ExpressionType.VERB));
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.ATTENDING));
		assertThat(getReply(barMaid), is("Hi!"));

		sentence = new SentenceImplementation(new Expression("bye", ExpressionType.VERB));
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.IDLE));
		assertThat(getReply(barMaid), is("Bye bye!"));
	}

	@Test
	public void testJobOfferQuest() throws Exception {
		SingletonRepository.getRPWorld();
		final BarMaidNPC barmaidConfigurator = new BarMaidNPC();
		final StendhalRPZone zone = new StendhalRPZone("testzone");
		barmaidConfigurator.configureZone(zone, null);
		final SpeakerNPC barMaid = (SpeakerNPC) zone.getNPCList().get(0);
		assertThat(barMaid.getName(), is("Siandra"));
		final Engine engine = barMaid.getEngine();
		engine.setCurrentState(ConversationStates.ATTENDING);

		Sentence sentence = new SentenceImplementation(new Expression("job", ExpressionType.VERB));
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.ATTENDING));
		assertThat("job text", getReply(barMaid),
				is("I'm a bar maid. But we've run out of food to feed our customers, can you #offer any?"));

		sentence = new SentenceImplementation(new Expression("help", ExpressionType.VERB));
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.ATTENDING));
		assertThat("help text", getReply(barMaid),
				is("If you could #offer any meat, ham or cheese to restock our larders I'd be grateful."));

		sentence = new SentenceImplementation(new Expression("quest", ExpressionType.VERB));
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.ATTENDING));
		assertThat("quest text", getReply(barMaid), is("Just #offers of food is enough, thank you."));
	}

	@Test
	public void testBuyerBehaviour() throws Exception {
		SingletonRepository.getRPWorld();

		final BarMaidNPC barmaidConfigurator = new BarMaidNPC();
		final StendhalRPZone zone = new StendhalRPZone("testzone");
		barmaidConfigurator.configureZone(zone, null);
		final SpeakerNPC barMaid = (SpeakerNPC) zone.getNPCList().get(0);
		assertThat(barMaid.getName(), is("Siandra"));
		final Engine engine = barMaid.getEngine();
		engine.setCurrentState(ConversationStates.ATTENDING);

		Sentence sentence = new SentenceImplementation(new Expression("offer", ExpressionType.VERB));
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.ATTENDING));
		assertThat("offer text", getReply(barMaid), is("I buy cheese, meat, spinach, ham, flour, and porcini."));

		final Expression sell = new Expression("sell", ExpressionType.VERB);

		sentence = new SentenceImplementation(sell);
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.ATTENDING));
		assertThat("offer text", getReply(barMaid), is("Please tell me what you want to sell."));

		sentence = new SentenceImplementation(sell, new Expression("cheese", ExpressionType.OBJECT));
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", getReply(barMaid), is("1 piece of cheese is worth 5. Do you want to sell it?"));
		engine.setCurrentState(ConversationStates.ATTENDING);

		sentence = new SentenceImplementation(sell, new Expression("meat", ExpressionType.OBJECT));
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", getReply(barMaid), is("1 piece of meat is worth 10. Do you want to sell it?"));

		engine.setCurrentState(ConversationStates.ATTENDING);
		sentence = new SentenceImplementation(sell, new Expression("spinach", ExpressionType.OBJECT));
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", getReply(barMaid), is("1 spinach is worth 15. Do you want to sell it?"));

		engine.setCurrentState(ConversationStates.ATTENDING);
		sentence = new SentenceImplementation(sell, new Expression("ham", ExpressionType.OBJECT));
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", getReply(barMaid), is("1 piece of ham is worth 20. Do you want to sell it?"));

		engine.setCurrentState(ConversationStates.ATTENDING);
		sentence = new SentenceImplementation(sell, new Expression("flour", ExpressionType.OBJECT));
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", getReply(barMaid), is("1 sack of flour is worth 25. Do you want to sell it?"));

		engine.setCurrentState(ConversationStates.ATTENDING);
		sentence = new SentenceImplementation(sell, new Expression("porcini", ExpressionType.OBJECT));
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", getReply(barMaid), is("1 porcini is worth 30. Do you want to sell it?"));

		engine.setCurrentState(ConversationStates.ATTENDING);
		final Expression porcini = new Expression("porcini", ExpressionType.OBJECT);
		porcini.setAmount(2);
		sentence = new SentenceImplementation(sell, porcini);
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", getReply(barMaid), is("2 porcini are worth 60. Do you want to sell them?"));

		engine.setCurrentState(ConversationStates.ATTENDING);
		final Expression flour = new Expression("flour", ExpressionType.OBJECT);
		flour.setAmount(2);
		sentence = new SentenceImplementation(sell, flour);
		engine.step(PlayerTestHelper.createPlayer("bob"), sentence);
		assertThat(engine.getCurrentState(), is(ConversationStates.SELL_PRICE_OFFERED));
		assertThat("offer text", getReply(barMaid), is("2 sacks of flour are worth 50. Do you want to sell them?"));
	}

}
