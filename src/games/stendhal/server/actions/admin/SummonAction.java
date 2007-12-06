package games.stendhal.server.actions.admin;

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.rule.EntityManager;
import marauroa.common.game.RPAction;

public class SummonAction extends AdministrationAction {
public static void register (){
	CommandCenter.register("summon", new SummonAction(), 800);
	
}
	@Override
	public void perform(Player player, RPAction action) {
	
		if (action.has("creature") && action.has("x") && action.has("y")) {
			StendhalRPZone zone = player.getZone();
			int x = action.getInt("x");
			int y = action.getInt("y");
	
			if (!zone.collides(player, x, y)) {
				EntityManager manager = StendhalRPWorld.get().getRuleManager()
						.getEntityManager();
				String type = action.get("creature");
	
				Entity entity = null;
				// Is the entity a creature
				if (manager.isCreature(type)) {
					StendhalRPRuleProcessor.get().addGameEvent(
							player.getName(), "summon", type);
					entity = new RaidCreature(manager.getCreature(type));
				} else if (manager.isItem(type)) {
					StendhalRPRuleProcessor.get().addGameEvent(
							player.getName(), "summon", type);
					entity = manager.getItem(type);
				}
	
				if (entity == null) {
					logger.info("onSummon: Entity \"" + type + "\" not found.");
					player.sendPrivateText("onSummon: Entity \"" + type
							+ "\" not found.");
					return;
				}
	
				StendhalRPAction.placeat(zone, entity, x, y);
			}
		}
	}

}
