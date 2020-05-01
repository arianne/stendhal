package games.stendhal.server.entity.npc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import games.stendhal.server.core.config.ShopsXMLLoader;
import games.stendhal.server.core.config.zone.ConfiguratorDescriptor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.NpcLoader;
import utilities.PlayerTestHelper;

@RunWith(Parameterized.class)
public class SpeakerNpcTest {

	private static final String ZONES_PATH = "/data/conf/zones";

	private static final String ZONE_PATH_ADOS = ZONES_PATH + "/ados.xml";
	private static final String ZONE_PATH_AMAZON = ZONES_PATH + "/amazon.xml";
	private static final String ZONE_PATH_ATHOR = ZONES_PATH + "/athor.xml";
	private static final String ZONE_PATH_FADO = ZONES_PATH + "/fado.xml";
	private static final String ZONE_PATH_KALAVAN = ZONES_PATH + "/kalavan.xml";
	private static final String ZONE_PATH_KIKAREUKIN = ZONES_PATH
			+ "/kikareukin.xml";
	private static final String ZONE_PATH_KIRDNEH = ZONES_PATH + "/kirdneh.xml";
	private static final String ZONE_PATH_NALWOR = ZONES_PATH + "/nalwor.xml";
	private static final String ZONE_PATH_ORRIL = ZONES_PATH + "/orril.xml";
	private static final String ZONE_PATH_SEMOS = ZONES_PATH + "/semos.xml";

	private static final String[] ALL_ZONE_PATHS = { ZONE_PATH_ADOS,
			ZONE_PATH_AMAZON, ZONE_PATH_ATHOR, ZONE_PATH_FADO,
			ZONE_PATH_KALAVAN, ZONE_PATH_KIKAREUKIN, ZONE_PATH_KIRDNEH,
			ZONE_PATH_NALWOR, ZONE_PATH_ORRIL, ZONE_PATH_SEMOS };

	private static final String FIRST_PLAYER_NAME = "FirstPlayer";
	private static final String SECOND_PLAYER_NAME = "SecondPlayer";
	private static final String PLEASE_WAIT_MESSAGE = String.format(
			"Please wait, %s! I am still attending to %s.", SECOND_PLAYER_NAME,
			FIRST_PLAYER_NAME);

	private static StendhalRPZone zone;

	private SpeakerNPC npc;
	private Player firstPlayer;
	private Player secondPlayer;

	@Parameterized.Parameters
	public static Collection<SpeakerNPC[]> npcs() {
		ShopsXMLLoader.get().init(); // default shops are now configured in XML
		setUpZone();

		Collection<SpeakerNPC[]> npcArrays = new LinkedList<SpeakerNPC[]>();
		for (NPC npc : zone.getNPCList()) {
			if (npc instanceof SpeakerNPC) {
				npcArrays.add(new SpeakerNPC[] { (SpeakerNPC) npc });
			}
		}
		return npcArrays;
	}

	private static void setUpZone() {
		zone = new StendhalRPZone("int_semos_house");
		MockStendlRPWorld.get().addRPZone(zone);
		NpcLoader npcLoader = new NpcLoader();
		for (String zonePath : ALL_ZONE_PATHS) {
			Collection<ConfiguratorDescriptor> desc = npcLoader
					.loadNpcZoneConfiguratorDescriptors(zonePath);
			for (ConfiguratorDescriptor cd : desc) {
				cd.setup(zone);
			}
		}
	}

	public SpeakerNpcTest(SpeakerNPC npc) {
		this.npc = npc;
	}

	@Before
	public void setUp() {
		firstPlayer = PlayerTestHelper.createPlayer(FIRST_PLAYER_NAME);
		PlayerTestHelper.registerPlayer(firstPlayer, zone);

		secondPlayer = PlayerTestHelper.createPlayer(SECOND_PLAYER_NAME);
		PlayerTestHelper.registerPlayer(secondPlayer, zone);
	}

	@After
	public void tearDown() {
		PlayerTestHelper.removePlayer(secondPlayer);
		PlayerTestHelper.removePlayer(firstPlayer);
	}

	@Test
	public void testNpcHasWaitMessage() {
		npc.listenTo(firstPlayer, ConversationPhrases.GREETING_MESSAGES.get(0));
		if (npc.isTalking()) {
			npc.listenTo(secondPlayer, "hi");
			assertTrue(npc.isTalking());
			assertEquals(npc.getName() + "'s attending message is wrong;",
					PLEASE_WAIT_MESSAGE, getReply(npc));
		}
	}
}
