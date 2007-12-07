package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ados.townhall.ClerkNPC;
import games.stendhal.server.maps.fado.church.PriestNPC;
import games.stendhal.server.maps.fado.church.VergerNPC;
import games.stendhal.server.maps.fado.city.NunNPC;
import games.stendhal.server.maps.fado.dressingrooms.BrideAssistantNPC;
import games.stendhal.server.maps.fado.hotel.GreeterNPC;
import games.stendhal.server.maps.fado.weaponshop.RingSmithNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class MarriageTest {

	private Player player = null;
	private Player player2 = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		StendhalRPZone zone = new StendhalRPZone("admin_test");
		new PriestNPC().configureZone(zone, null);
		new VergerNPC().configureZone(zone, null);
		new NunNPC().configureZone(zone, null);
		new RingSmithNPC().configureZone(zone, null);
		new BrideAssistantNPC().configureZone(zone, null);
		new GreeterNPC().configureZone(zone, null);
		new ClerkNPC().configureZone(zone, null);

		AbstractQuest quest = new Marriage();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("player");
		player2 = PlayerTestHelper.createPlayer("player2");
	}

	@Test
	@Ignore
	public void testQuest() {

		// -----------------------------------------------

		// **in front of church**
		npc = NPCList.get().get("Sister Benedicta");
		en = npc.getEngine();
		
		en.step(player, "hi");
		assertEquals("Welcome to this place of worship.", npc.get("text"));
		en.step(player, "help");
		assertEquals("I don't know what you need, dear child.", npc.get("text"));
		en.step(player, "engage");
		assertEquals("You have to tell me who you want to marry.", npc.get("text"));
		en.step(player, "engage player2");
		assertEquals("player, do you want to get engaged to player2?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("player2, do you want to get engaged to player?", npc.get("text"));
		en.step(player2, "yes");
		assertEquals("Congratulations, player and player2, you are now engaged! Please make sure you have got wedding rings made before you go to the church for the service. And here are some invitations you can give to your guests.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Goodbye, may peace be with you.", npc.get("text"));

		// -----------------------------------------------

		// **at ringsmith**
		npc = NPCList.get().get("Ognir");
		en = npc.getEngine();
		en.step(player, "hi");
		assertEquals("Hello. That's a rare emerald on your ring. If it gets broken, come to me to fix it.", npc.get("text"));
		en.step(player, "help");
		assertEquals("You can sell weapons to Yorphin Baos over there. I trade in precious items and I can also make a wedding ring as a special request.", npc.get("text"));
		en.step(player, "request");
		assertEquals("Just ask about the task if you want me to make a wedding ring for someone.", npc.get("text"));
		en.step(player, "task");
		assertEquals("I see you're on a life-long quest to get married! I find marriage more of a task, ha ha! Anyway, you'll need a wedding_ring.", npc.get("text"));
		en.step(player, "wedding_ring");
		assertEquals("I need 10 gold bars and a fee of 500 money, to make a wedding ring for your fiancee. Do you have it?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Good, come back in 10 minutes and it will be ready. Goodbye until then.", npc.get("text"));
		en.step(player, "bye");

		// -----------------------------------------------

		en.step(player2, "hi");
		assertEquals("Hello. That's a rare emerald on your ring. If it gets broken, come to me to fix it.", npc.get("text"));
		en.step(player2, "task");
		assertEquals("I see you're on a life-long quest to get married! I find marriage more of a task, ha ha! Anyway, you'll need a wedding_ring.", npc.get("text"));
		en.step(player2, "wedding_ring");
		assertEquals("I need 10 gold bars and a fee of 500 money, to make a wedding ring for your fiancee. Do you have it?", npc.get("text"));
		en.step(player2, "yes");
		assertEquals("Good, come back in 10 minutes and it will be ready. Goodbye until then.", npc.get("text"));
		en.step(player2, "bye");

		// -----------------------------------------------

		// **at hotel's dressing room**
		npc = NPCList.get().get("Timothy");
		en = npc.getEngine();
		en.step(player, "hi");
		assertEquals("Good day! If you're a prospective groom I can help you prepare for your wedding.", npc.get("text"));
		en.step(player, "help");
		assertEquals("Please tell me if you want to wear a suit for your wedding.", npc.get("text"));
		en.step(player, "wear");
		assertEquals("A suit will cost 50. Do you want to wear it?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thanks, and please don't forget to return it when you don't need it anymore!", npc.get("text"));
		en.step(player2, "bye");
		assertEquals("Good bye, I hope everything goes well for you.", npc.get("text"));

		// -----------------------------------------------

		// **at hotel's dressing room**
		npc = NPCList.get().get("Tamara");
		en = npc.getEngine();
		en.step(player2, "hi");
		assertEquals("Welcome! If you're a bride-to-be I can help you get ready for your wedding", npc.get("text"));
		en.step(player2, "help");
		assertEquals("Just tell me if you want to wear a gown for your wedding.", npc.get("text"));
		en.step(player2, "wear a gown");
		assertEquals("A gown will cost 100. Do you want to wear it?", npc.get("text"));
		en.step(player2, "yes");
		assertEquals("Thanks, and please don't forget to return it when you don't need it anymore!", npc.get("text"));

		// -----------------------------------------------

		// **at ringsmith**
		npc = NPCList.get().get("Ognir");
		en = npc.getEngine();
		en.step(player2, "hi");
		assertEquals("Hello. That's a rare emerald on your ring. If it gets broken, come to me to fix it.", npc.get("text"));
		en.step(player2, "bye");
		assertEquals("Bye, my friend.", npc.get("text"));
		en.step(player2, "hi");
		assertEquals("Hello. That's a rare emerald on your ring. If it gets broken, come to me to fix it.", npc.get("text"));
		en.step(player2, "bye");
		assertEquals("Bye, my friend.", npc.get("text"));
		en.step(player2, "hi");
		assertEquals("I haven't finished making the wedding ring. Please check back in less than a minute. Bye for now.", npc.get("text"));
		en.step(player2, "bye");

		en.step(player, "hi");
		assertEquals("Hello. That's a rare emerald on your ring. If it gets broken, come to me to fix it.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye, my friend.", npc.get("text"));
		en.step(player, "hi");
		assertEquals("I'm pleased to say, the wedding ring for your fiancee is finished! Make sure one is made for you, too! *psst* just a little hint for the wedding day ...", npc.get("text"));
		// [14:25] player earns 500 experience points.
		en.step(player, "hint");
		assertEquals("When my wife and I got married we went to Fado hotel and hired special clothes. The dressing rooms are on your right when you go in, look for the wooden door. Good luck!", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye, my friend.", npc.get("text"));

		en.step(player2, "hi");
		assertEquals("I haven't finished making the wedding ring. Please check back in less than a minute. Bye for now.", npc.get("text"));
		en.step(player2, "hi");
		assertEquals("I'm pleased to say, the wedding ring for your fiancee is finished! Make sure one is made for you, too! *psst* just a little hint for the wedding day ...", npc.get("text"));
		// [14:26] player2 earns 500 experience points.
		en.step(player2, "bye");
		assertEquals("Bye, my friend.", npc.get("text"));

		en.step(player2, "hi");
		assertEquals("Hello. That's a rare emerald on your ring. If it gets broken, come to me to fix it.", npc.get("text"));
		en.step(player2, "bye");
		assertEquals("Bye, my friend.", npc.get("text"));
		en.step(player2, "hi");
		assertEquals("Hello. That's a rare emerald on your ring. If it gets broken, come to me to fix it.", npc.get("text"));
		en.step(player2, "hint");
		en.step(player2, "wedding_ring");
		en.step(player2, "bye");
		assertEquals("Bye, my friend.", npc.get("text"));

		// -----------------------------------------------

		// **player drops ring of life**
		en.step(player, "hi");
		assertEquals("Hi, can I help you?", npc.get("text"));
		en.step(player, "help");
		assertEquals("You can sell weapons to Yorphin Baos over there. I trade in precious items and I can also make a wedding ring as a special request.", npc.get("text"));
		en.step(player, "request");
		assertEquals("Just ask about the task if you want me to make a wedding ring for someone.", npc.get("text"));
		en.step(player, "task");
		assertEquals("Looking forward to your wedding? Make sure your fiancee gets a wedding ring made for you, too! Oh and remember to get dressed up for the big day.", npc.get("text"));
		en.step(player, "dressed");
		assertEquals("When my wife and I got married we went to Fado hotel and hired special clothes. The dressing rooms are on your right when you go in, look for the wooden door. Good luck!", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye, my friend.", npc.get("text"));

		// -----------------------------------------------

		// **inside church**
		npc = NPCList.get().get("Lukas");
		en = npc.getEngine();
		en.step(player2, "hi");
		assertEquals("Welcome to this place of worship. Are you here to be married?", npc.get("text"));
		en.step(player2, "married");
		assertEquals("If you want to be engaged, speak to Sister Benedicta. She'll make sure the priest knows about your plans.", npc.get("text"));
		en.step(player2, "task");
		assertEquals("I have eveything I need. But it does bring me pleasure to see people married.", npc.get("text"));

		// -----------------------------------------------

		npc = NPCList.get().get("Priest");
		en = npc.getEngine();
		en.step(player, "hi");
		assertEquals("Welcome to the church!", npc.get("text"));
		en.step(player, "help");
		assertEquals("I can help you marry your loved one. But you must be engaged under the supervision of Sister Benedicta, and have a ring to give your partner.", npc.get("text"));
		en.step(player, "ring");
		assertEquals("Once you are engaged, you can go to Ognir who works here in Fado to get your wedding rings made. I believe he also sells engagement rings, but they are purely for decoration. How wanton!", npc.get("text"));
		en.step(player, "marry");
		assertEquals("You have to tell me who you want to marry.", npc.get("text"));
		en.step(player, "marry player2");
		assertEquals("player, do you really want to marry player2?", npc.get("text"));
		en.step(player, "no");
		assertEquals("What a pity! Goodbye!", npc.get("text"));
		en.step(player, "hi");
		assertEquals("Welcome to the church!", npc.get("text"));
		en.step(player, "marry player2");
		assertEquals("player, do you really want to marry player2?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("player2, do you really want to marry player?", npc.get("text"));
		en.step(player2, "yes");
		assertEquals("Congratulations, player and player2, you are now married! I don't really approve of this, but if you would like a honeymoon, go ask Linda in the hotel. Just say 'honeymoon' to her and she will understand.", npc.get("text"));

		en.step(player2, "hi");
		assertEquals("Welcome to the church!", npc.get("text"));
		en.step(player2, "marry");
		en.step(player2, "bye");
		assertEquals("May the force be with you.", npc.get("text"));

		en.step(player, "hi");
		assertEquals("Welcome to the church!", npc.get("text"));
		en.step(player, "help");
		assertEquals("I can help you marry your loved one. But you must be engaged under the supervision of Sister Benedicta, and have a ring to give your partner.", npc.get("text"));
		en.step(player, "marry");
		en.step(player, "bye");

		// -----------------------------------------------

		// **inside hotel**
		npc = NPCList.get().get("Linda");
		en = npc.getEngine();
		en.step(player, "hi");
		assertEquals("Hello! Welcome to the Fado City Hotel! Can I help you?", npc.get("text"));
		en.step(player, "help");
		assertEquals("When the building work on the hotel rooms is complete you will be able to reserve one.", npc.get("text"));
		en.step(player, "honeymoon");
		assertEquals("How lovely! Please read our catalogue here and tell me the room number that you would like.", npc.get("text"));
		// [14:34] You read:
		// "0. Blue Paradise - with a flaming bed
		// 1. Windy Love - be blown away
		// 2. Literary Haven - bathe, read and relax
		// 3. Heart of Flowers - a floral masterpiece
		// 4. A Gothic Romance - the room for mystics
		// 5. Gourmet Delight - a room for food lovers
		// 6. Forest Fantasy - for natural lovers
		// 7. The Cold Love - feel the chill
		// 8. Waterfall Wonder - splash, or admire
		// 9. Wooden Delicacy - go back to your roots
		// 10. Simple Serenity - a room of calm
		// 11. Water of Love - wine flows freely
		// 12. Stone Hearted - an architect's delight
		// 13. Blue For You - azure, lapis, cobalt and cornflower
		// 14. Rhapsody in pink - romantic yet regal
		// 15. Femme Fatale - feminine and frivolous

		// -----------------------------------------------

		// If you're looking for a honeymoon room, just say the room number you desire
		// For example say:  11  if you want the room called Water of Love."
		en.step(player, "2");

	}
}
