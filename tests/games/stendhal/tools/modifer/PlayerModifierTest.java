package games.stendhal.tools.modifer;


import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.player.Player;

import org.junit.Test;

public class PlayerModifierTest {

	@Test
	public void testLoadPlayer() {
		StendhalRPWorld.get();
		String characterName = "modifyme";
		PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(SingletonRepository.getPlayerDatabase());
		Player player = mod.loadPlayer("");
		assertThat(player, nullValue());
		
		player = mod.loadPlayer(characterName);
		assertThat(player, not(nullValue()));
		assertThat(player.getName(), is(characterName));
	}
	
	@Test
	public void testmodifyPlayer() {
		StendhalRPWorld.get();
		String characterName = "modifyme";
		PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(SingletonRepository.getPlayerDatabase());
		Player player = mod.loadPlayer(characterName);
		assertThat(player, not(nullValue()));
		assertThat(player.getName(), is(characterName));
		int adminlevel;
		if (player.getAdminLevel() == 100) {
			adminlevel = 0;
			
		} else {
			adminlevel = 100;
		}
		assertThat(player.getAdminLevel(), not(is(adminlevel)));
		player.setAdminLevel(adminlevel);
		
		assertThat(mod.savePlayer(player), is(true));
		
		player = mod.loadPlayer(characterName);
		assertThat(player, not(nullValue()));
		assertThat(player.getName(), is(characterName));
		assertThat(player.getAdminLevel(), is(100));
	}
}
