package games.stendhal.tools.playerUpdate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalPlayerDatabase;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.tools.modifer.PlayerModifier;

import java.io.IOException;
import java.sql.SQLException;

import marauroa.common.game.RPObject;

import org.junit.Before;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class UpdatePlayerEntitiesTest {

	@Before
	public void setUp() throws Exception {
		MockStendlRPWorld.get();
	}

	@Test
	public void testDoUpdate() throws SQLException, IOException {
		StendhalPlayerDatabase spdb = (StendhalPlayerDatabase) SingletonRepository.getPlayerDatabase();
		EntityManager em = SingletonRepository.getEntityManager();
		PlayerModifier pm = new PlayerModifier();
		pm.setDatabase(spdb);
		Player p = PlayerTestHelper.createPlayer("madmetzger");
		p.equip((Item) em.getEntity("leather armor"), 1);
		p.getSlot("bag").getFirst().put("name", "leather_armor_+1");
		pm.savePlayer(p);
		UpdatePlayerEntities updatePlayerEntities = new UpdatePlayerEntities();
		updatePlayerEntities.createPlayerFromRPO((RPObject) p);
		updatePlayerEntities.savePlayer(p);
		Player loaded = pm.loadPlayer("madmetzger");
		assertThat(loaded.getSlot("bag").getFirst().get("name"), not(is("leather_armor_+1")));
	}

}
