package games.stendhal.server.script;

import java.util.ArrayList;
import java.util.List;

import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.PlayerList;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.Task;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
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
			StendhalRPZone irpZone2 = (StendhalRPZone) irpZone;
			if (!irpZone2.getName().startsWith("int")) {
				zones.add(irpZone2);				
			}
		}
	}

	@Override
	public void execute(final Player admin, List<String> args) {
		PlayerList onlinePlayers = SingletonRepository.getRuleProcessor().getOnlinePlayers();
		onlinePlayers.forAllPlayersExecute(new Task<Player>() {
			public void execute(Player player) {
				equipPlayer(player);
				player.setDEFXP(999999999);
				player.addXP(999999999);
				player.setImmune();
				StendhalRPZone zone = zones.get(Rand.rand(zones.size()));
				int x = Rand.rand(zone.getWidth() - 4) + 2;
				int y = Rand.rand(zone.getHeight() - 5) + 2;
				player.teleport(zone, x, y, Direction.DOWN, admin);
			}

			private void equipPlayer(Player player) {
				StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
				money.setQuantity(5000);
				player.equipToInventoryOnly(money);
				StackableItem potions = (StackableItem) SingletonRepository.getEntityManager().getItem("greater potion");
				potions.setQuantity(5000);
				player.equipToInventoryOnly(potions);
				if(!player.isEquipped("chaos dagger")) {
					Item first = (Item) player.getSlot("rhand").getFirst();
					player.drop(first);
					Item dagger = SingletonRepository.getEntityManager().getItem("chaos dagger");
					player.equip("rhand", dagger);
				}
				if(!player.isEquipped("chaos shield")) {
					Item first = (Item) player.getSlot("lhand").getFirst();
					player.drop(first);
					Item shield = SingletonRepository.getEntityManager().getItem("chaos shield");
					player.equip("lhand", shield);
				}
				if(!player.isEquipped("black helmet")) {
					Item first = (Item) player.getSlot("head").getFirst();
					player.drop(first);
					Item shield = SingletonRepository.getEntityManager().getItem("black helmet");
					player.equip("head", shield);
				}
				if(!player.isEquipped("elvish legs")) {
					Item first = (Item) player.getSlot("legs").getFirst();
					player.drop(first);
					Item shield = SingletonRepository.getEntityManager().getItem("elvish legs");
					player.equip("legs", shield);
				}
				if(!player.isEquipped("killer boots")) {
					Item first = (Item) player.getSlot("feet").getFirst();
					player.drop(first);
					Item shield = SingletonRepository.getEntityManager().getItem("killer boot");
					player.equip("feet", shield);
				}
				if(!player.isEquipped("green dragon cloak")) {
					Item first = (Item) player.getSlot("cloak").getFirst();
					player.drop(first);
					Item shield = SingletonRepository.getEntityManager().getItem("green dragon cloak");
					player.equip("cloak", shield);
				}
			}
		});
	}
	
	

}
