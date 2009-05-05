package games.stendhal.tools.playerUpdate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalPlayerDatabase;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.tools.modifer.PlayerModifier;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

public class UpdatePlayerEntitiesTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDoUpdate() throws SQLException, IOException {
		//updatePlayerEntities has to be initialized here to get rid of MockStendlRPWorld.get()
		UpdatePlayerEntities updatePlayerEntities = new UpdatePlayerEntities();
		StendhalPlayerDatabase spdb = (StendhalPlayerDatabase) SingletonRepository.getPlayerDatabase();
		
		PlayerModifier pm = new PlayerModifier();
		pm.setDatabase(spdb);
		Player loaded = pm.loadPlayer("george");
		assertNotNull("pm can only handle existing players, so if this fails first create a player in db by login", loaded);
		if (loaded.getSlot("bag").size() > 0) {
			loaded.getSlot("bag").remove(loaded.getSlot("bag").getFirst().getID());
		}
		assertEquals(null, loaded.getSlot("bag").getFirst());
		
		EntityManager em = SingletonRepository.getEntityManager();
		Item item = (Item) em.getItem("leather armor");
		item.put("name", "leather_armor_+1");
		loaded.equipToInventoryOnly(item);
		assertThat(loaded.getSlot("bag").getFirst().get("name"), is("leather_armor_+1"));

		assertTrue(pm.savePlayer(loaded));

		Player changing = updatePlayerEntities.createPlayerFromRPO(loaded);
		updatePlayerEntities.savePlayer(changing);
		
		
		
		
		Player secondLoaded = pm.loadPlayer("george");
		assertNotNull(secondLoaded);
		
		assertNotNull(secondLoaded.getSlot("bag"));
		assertNotNull(secondLoaded.getSlot("bag").getFirst());
		assertThat(secondLoaded.getSlot("bag").getFirst().get("name"), not(is("leather_armor_+1")));
	}

}
