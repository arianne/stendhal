package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
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
    
    private String QUEST_SLOT;
    
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
        
        QUEST_SLOT = rfs.getSlotName();
    }
    
    @Test
    public void quest() {
        
        final Player player = PlayerTestHelper.createPlayer("player");
        
        final Engine en = seremela.getEngine();
        
        // Test that NPCs name is correct
        assertEquals(seremela.getName(), rfs.getNPCName());
        
        // QUEST_SLOT is correct
        assertEquals(QUEST_SLOT, "restock_flowershop");
        
        // Initialize conversation
        en.step(player, "hi");
        assertEquals("Hello.", getReply(seremela));
        assertTrue(seremela.isTalking());
        
        // Quest inactive replies
        assertTrue(!rfs.isStarted(player));
        en.step(player, "flower");
        assertEquals("Aren't flowers beautiful?", getReply(seremela));
        en.step(player, "help");
        assertEquals("Hmmmm, I don't think there is anything I can help with.", getReply(seremela));
        
        // Ask for quest
        en.step(player, "quest");
        assertEquals("The flower shop is running low on flowers. Will you help me restock it?", getReply(seremela));
        en.step(player, "yes");
        //assertEquals(new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "Great! Here is what I need: [items]"), getReply(seremela));
        
        // Confirm quest is activated
        assertTrue(rfs.isStarted(player));
        
        // Request quest after active
        en.step(player, "quest");
        assertEquals("You still haven't brought me the #flowers I asked for.", getReply(seremela));
        
        // Quest active replies
        en.step(player, "help");
        assertEquals("I can #remind you of which #flowers I need. I might also be able help you figure out #where you can find some.", getReply(seremela));
        en.step(player, "remind");
        //assertEquals(new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "I still need [items]. Did you bring any of those?"), getReply(seremela));
        en.step(player, "daisies");
        en.step(player, "lilia");
        en.step(player, "pansies");
        en.step(player, "rose");
        en.step(player, "zantedechia");
        en.step(player, "water");
        assertEquals("I need water to keep the #flowers fresh. You'll need to find a water source and fill up some #flasks. Maybe there is someone who sells water.", getReply(seremela));
        en.step(player, "who");
        assertEquals("#Jenny knows a lot about flowers. You may be able to talk with #Fleur as well.", getReply(seremela));
        en.step(player, "jenny");
        assertEquals("You can find Jenny around the windmill near Semos where she mills flour.", getReply(seremela));
        en.step(player, "fleur");
        assertEquals("Fleur works at the market in Kirdneh.", getReply(seremela));
        en.step(player, "flask");
        assertEquals("Ask the barmaid in Semos.", getReply(seremela));
        
        // End conversation
        en.step(player, "bye");
        assertEquals("Goodbye!", getReply(seremela));
        
        // Restart conversation with no required items
        en.step(player, "hi");
        assertEquals("Hello.", getReply(seremela));
        en.step(player, "bye");
        
        // Restart conversation with required items
        PlayerTestHelper.equipWithStackableItem(player, "daisies", 100);
        en.step(player, "hi");
        assertEquals("Did you bring anything for the shop?", getReply(seremela));
        en.step(player, "yes");
        assertEquals("What did you bring?", getReply(seremela));
        // Undesired item
        en.step(player, "cauliflower");
        assertEquals("I don't think that would look good in the shop.", getReply(seremela));
        // Not carrying desired item
        en.step(player, "rose");
        //assertEquals("You're not carrying any of that.", getReply(seremela));
        // Carrying desired item
        en.step(player, "daisies");
        assertEquals("Thank you! What else did you bring?", getReply(seremela));
        // Already brought item
        en.step(player, "daisies");
        assertEquals("I don't need any more of those.", getReply(seremela));
        
        // End conversation while listing items
        en.step(player, "bye");
        assertEquals("Please come back when you have found some flowers.", getReply(seremela));
        
        // Respond that did not bring items
        PlayerTestHelper.equipWithStackableItem(player, "water", 100);
        PlayerTestHelper.equipWithStackableItem(player, "rose", 100);
        PlayerTestHelper.equipWithStackableItem(player, "lilia", 100);
        PlayerTestHelper.equipWithStackableItem(player, "zantedeschia", 100);
        PlayerTestHelper.equipWithStackableItem(player, "pansy", 100);
        en.step(player, "hi");
        en.step(player, "no");
        assertEquals("Don't stop to smell the roses yet. Orders are backing up. I can #remind you of what to bring.", getReply(seremela));
        en.step(player, "remind");
        // FIXME: Code here to show items that Seremela wants
        // Has brought items
        en.step(player, "yes");
        assertEquals("What did you bring?", getReply(seremela));
        en.step(player, "water");
        en.step(player, "rose");
        en.step(player, "lilia");
        en.step(player, "zantedeschia");
        en.step(player, "pansies");
        assertEquals("Thank you so much! Now I can fill all of my orders.", getReply(seremela));
        
        // Quest is complete
        assertTrue(player.isQuestCompleted(QUEST_SLOT));
        en.step(player, "bye");
        
        // Initialize conversation after quest is completed
        en.step(player, "hi");
        assertEquals("Hello.", getReply(seremela));
        
        // Request quest before wait time is completed
        en.step(player, "quest");
        //assertEquals("The flowers you brought are selling quickly. I may need your help again in 3 days", getReply(seremela));
        
        // Reject the quest
        player.setQuest(QUEST_SLOT, null);
        en.step(player, "quest");
        en.step(player, "no");
        assertEquals("I am sorry to hear that.", getReply(seremela));
    }
}
