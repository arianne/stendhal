package games.stendhal.server.maps.semos.city;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.util.Collection;
import java.util.LinkedHashMap;

import marauroa.server.game.db.DatabaseFactory;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;


public class HealerNPCTest {
	
    private SpeakerNPC npc;
	private Player player;
	private Engine en;
	private ShopList sl;
	private LinkedHashMap<String, Integer> slh;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		StendhalRPZone zone = new StendhalRPZone("admin_test");
		new HealerNPC().configureZone(zone, null);
		new DatabaseFactory().initializeDatabase();
	}
	
	@Before
	public void setUp() {
		npc = SingletonRepository.getNPCList().get("Carmen");
		en = npc.getEngine();
		StendhalRPZone srpz = new StendhalRPZone("int_semos_guard_house",100,100);
		SingletonRepository.getRPWorld().addRPZone(srpz);
		player = PlayerTestHelper.createPlayer("bob");
		player.teleport(srpz, 10, 10, null, null);
		sl = ShopList.get();
        slh = (LinkedHashMap<String, Integer>) sl.get("healing");
	}
	
	@Test
	public void createDialogTest() {
		assertTrue(en.step(player, "hi"));
		assertEquals("Hi, if I can #help, just say.", getReply(npc));
		assertTrue(en.step(player, "job"));
        assertEquals("My special powers help me to heal wounded people. I also sell potions and antidotes.", getReply(npc));
        assertTrue(en.step(player, "help"));
        assertEquals("I can #heal you here for free, or you can take one of my prepared medicines with you on your travels; just ask for an #offer.", getReply(npc));

        final Collection<String> items = slh.keySet();
        final String key = items.iterator().next();
        final int price = slh.get(key);
        
        assertTrue(en.step(player, "offer"));       
        assertEquals("I sell "+ Grammar.enumerateCollection(items)
				+ ". "+"I can #heal you.", getReply(npc));
        
        player.setBaseHP(100);
        player.setHP(50);
        player.setATKXP(100);
        player.setDEFXP(100);        
        PlayerTestHelper.equipWithMoney(player, price);
        
        assertTrue(en.step(player, "heal"));
        assertEquals("There, you are healed. How else may I help you?", getReply(npc));
        assertEquals(player.getHP(),100);       
                
        //slh.get("antidote")
        
        assertTrue(en.step(player, "buy "+key));      
        final StringBuilder builder = new StringBuilder("");
		builder.append(Grammar.makeUpperCaseWord(Grammar.quantityplnoun(1, key)));	
	    builder.append(" will cost ");
	    builder.append(price);
	    builder.append(". Do you want to buy it?");	    
        assertEquals(builder.toString(), getReply(npc));        
        assertTrue(en.step(player, "no"));
        assertEquals("Ok, how else may I help you?", getReply(npc));
        
        assertTrue(en.step(player, "!me hugs Carmen"));
		assertEquals("!me hugs bob", getReply(npc));
		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", getReply(npc));		
	}
}
