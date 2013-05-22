package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.nalwor.flowershop.FlowerGrowerNPC;
import marauroa.common.Log4J;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class RestockFlowerShopTest {
    
    private SpeakerNPC seremela;
    
    private RestockFlowerShop rfs;
    
    private final String QUEST_SLOT = "restock_flowershop";
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Log4J.init();
        
        MockStendhalRPRuleProcessor.get();

        MockStendlRPWorld.reset();
        MockStendlRPWorld.get();
    }
    
    @Before
    public void setup() {
        PlayerTestHelper.removeAllPlayers();
        StendhalRPZone zone = new StendhalRPZone("admin_test");
        new FlowerGrowerNPC().configureZone(zone, null);
        seremela = SingletonRepository.getNPCList().get("Seremela") ;

        rfs = new RestockFlowerShop();

        rfs.addToWorld();
    }
    
    @Test
    public void quest() {
        
        final Player player = PlayerTestHelper.createPlayer("player");
        
        final Engine en = seremela.getEngine();
        
        // Initialize conversation
        en.step(player, "hi");
        assertEquals("Hello.", getReply(seremela));
        assertTrue(seremela.isTalking());
        
        // Quest inactive replies
        assertTrue(!rfs.isStarted(player));
        Map<String, String> inactiveReplies = new HashMap<String, String>();
        inactiveReplies.put("flower", "Aren't flowers beautiful?");
        for (String help : ConversationPhrases.HELP_MESSAGES) {
            inactiveReplies.put(help, "Hmmmm, I don't think there is anything I can help with.");
        }
        
        // Ask for quest
        en.step(player, "quest");
        assertEquals("The flower shop is running low on flowers. Will you help me restock it?", getReply(seremela));
        en.step(player, "yes");
        //assertEquals(new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "Great! Here is what I need: [items]"), getReply(seremela));
    }
}
