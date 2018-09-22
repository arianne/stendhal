package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.nalwor.flowershop.FlowerGrowerNPC;
import games.stendhal.server.util.ItemCollection;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;

public class RestockFlowerShopTest {

    private SpeakerNPC seremela;

    private RestockFlowerShop rfs;

    private String questSlot;

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

        questSlot = rfs.getSlotName();
    }

    /**
	 * Returns all items that the given player still has to bring to complete the quest.
	 *
	 * @param player The player doing the quest
	 * @return A list of item names
	 */
	private ItemCollection getMissingItems(final Player player) {
		final ItemCollection missingItems = new ItemCollection();

		missingItems.addFromQuestStateString(player.getQuest(questSlot));

		return missingItems;
	}

	/**
	 * Try bringing a new item that the player is carrying.
	 *
	 * @param player player engine
	 * @param en
	 * @param item brought item
	 */
	private void checkNeeded(Player player, Engine en, String item) {
		ItemCollection needed = getMissingItems(player);
		if (!player.isQuestCompleted(questSlot)) {
			if (needed.containsKey(item)) {
				boolean last = needed.size() == 1;
				en.step(player, item);
				if (last) {
					assertEquals("Thank you so much! Now I can fill all of my orders. Here are some Nalwor City scrolls to show my appreciation.", getReply(seremela));
				} else {
					assertEquals("Thank you! What else did you bring?", getReply(seremela));
				}
			} else {
				en.step(player, item);
				assertEquals("I don't need any more of those.", getReply(seremela));
			}
		}
	}

    @Test
    public void quest() {
        final Player player = PlayerTestHelper.createPlayer("player");

        final Engine en = seremela.getEngine();

        // Test that NPCs name is correct
        assertEquals(seremela.getName(), rfs.getNPCName());

        // QUEST_SLOT is correct
        assertEquals(questSlot, "restock_flowershop");

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
        assertEquals("You don't have a bottle of water with you!", getReply(seremela));
        en.step(player, "nothing");
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
        assertEquals("Did you bring #something for the shop?", getReply(seremela));
        en.step(player, "something");
        assertFalse("I don't think that would look good in the shop.".equals(getReply(seremela)));
        en.step(player, "bye");

        // Restart conversation with required items
        PlayerTestHelper.equipWithStackableItem(player, "daisies", 100);
        en.step(player, "hi");
        assertEquals("Did you bring #something for the shop?", getReply(seremela));
        en.step(player, "yes");
        assertEquals("What did you bring?", getReply(seremela));
        // Undesired item
        en.step(player, "cauliflower");
        assertEquals("I don't think that would look good in the shop.", getReply(seremela));
        ItemCollection needed = getMissingItems(player);
        // Not carrying desired item
        en.step(player, "rose");
        if (needed.containsKey("rose")) {
        	assertEquals("You don't have a rose with you!", getReply(seremela));
        } else {
        	assertEquals("I don't need any more of those.", getReply(seremela));
        }
        // Carrying desired item
        checkNeeded(player, en, "daisies");

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
        // Has brought items
        en.step(player, "yes");
        assertEquals("What did you bring?", getReply(seremela));
        checkNeeded(player, en, "water");
        checkNeeded(player, en, "rose");
        checkNeeded(player, en, "lilia");
        checkNeeded(player, en, "zantedeschia");
        checkNeeded(player, en, "pansy");

        // Quest is complete
        assertTrue(player.isQuestCompleted(questSlot));
        en.step(player, "bye");

        // Initialize conversation after quest is completed
        en.step(player, "hi");
        assertEquals("Hello.", getReply(seremela));

        // Request quest before wait time is completed
        en.step(player, "quest");
        //assertEquals("The flowers you brought are selling quickly. I may need your help again in 3 days", getReply(seremela));

        // Reject the quest
        player.setQuest(questSlot, null);
        en.step(player, "quest");
        en.step(player, "no");
        assertEquals("I am sorry to hear that.", getReply(seremela));
    }
}
