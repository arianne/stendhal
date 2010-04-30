package games.stendhal.server.script;

import java.util.ArrayList;
import java.util.List;

import marauroa.common.game.IRPZone;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.PlayerList;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.Task;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
/**
 * Script to make all players stronger and immune to poison before randomly distributing them 
 * over all zones of the running server
 *  
 * @author madmetzger
 */
public class MoveAndStrengthenOnlinePlayers extends ScriptImpl {
	
	private List<StendhalRPZone> zones = new ArrayList<StendhalRPZone>();
	
	/**
	 * Create the script and initialize the list of zones
	 */
	public MoveAndStrengthenOnlinePlayers() {
		StendhalRPWorld rpWorld = SingletonRepository.getRPWorld();
		for (IRPZone irpZone : rpWorld) {
			zones.add((StendhalRPZone) irpZone);
		}
	}

	@Override
	public void execute(final Player admin, List<String> args) {
		PlayerList onlinePlayers = SingletonRepository.getRuleProcessor().getOnlinePlayers();
		onlinePlayers.forAllPlayersExecute(new Task<Player>() {
			public void execute(Player player) {
				player.setDEFXP(99999999);
				player.addXP(99999999);
				player.setImmune();
				StendhalRPZone zone = zones.get(Rand.rand(zones.size()));
				int x = Rand.rand(zone.getWidth() - 4) + 2;
				int y = Rand.rand(zone.getHeight() - 5) + 2;
				player.teleport(zone, x, y, Direction.DOWN, admin);
			}
		});
	}
	
	

}
