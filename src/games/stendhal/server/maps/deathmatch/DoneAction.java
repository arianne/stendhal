package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Handles player claim of victory by giving reward after verifing the winning.
 *
 * @author hendrik
 */
public class DoneAction extends SpeakerNPC.ChatAction {

	/**
	 * Creates the player bound special trophy helmet and equips it.
	 *
	 * @param player Player object
	 * @return Helmet
	 */
	private Item createTrophyHelmet(Player player) {
		Item helmet = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("trophy_helmet");
		helmet.setBoundTo(player.getName());
		helmet.put("def", 1);
		helmet.setInfoString(player.getName());
		helmet.setPersistent(true);
		helmet.setDescription("This is " + player.getName()
		        + "'s grand prize for Deathmatch winners. Wear it with pride.");
		player.equip(helmet, true);
		return helmet;
	}

	/**
	 * Updates the player's points in the hall of fame for deathmatch.
	 *
	 * @param player Player
	 * @return new amount of points
	 */
	private int updatePoints(Player player) {
		StendhalRPRuleProcessor rules = StendhalRPRuleProcessor.get();
		DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));
		rules.addHallOfFamePoints(player.getName(), "D", deathmatchState.getQuestLevel());
		return rules.getHallOfFamePoints(player.getName(), "D");
	}

	@Override
	public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
		DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));
		if (deathmatchState.getLifecycleState() != DeathmatchLifecycle.VICTORY) {
			engine.say("C'm on, don't lie to me! All you can do now is #bail or win.");
			return;
		}

		int points = updatePoints(player);

		// We assume that the player only carries one trophy helmet.
		Item helmet = player.getFirstEquipped("trophy_helmet");
		if (helmet == null) {
			createTrophyHelmet(player);
			engine.say("Congratulations, your score is now " + points
			        + "! Here is your special trophy helmet. Enjoy it. Now, tell me if you want to #leave.");
		} else {
			int defense = 1;
			if (helmet.has("def")) {
				defense = helmet.getInt("def");
			}
			defense++;
			int maxdefense = 5 + (player.getLevel() / 5);
			if (defense > maxdefense) {
				helmet.put("def", maxdefense);
				engine.say("Congratulations, your score is now "
				                + points
				                + "! However, I'm sorry to inform you, the maximum defense for your helmet at your current level is "
				                + maxdefense);
			} else {
				helmet.put("def", defense);
				engine.say("Congratulations, your score is now " + points
				        + "! And your helmet has been magically strengthened. Now, tell me if you want to #leave.");
			}
		}
		player.updateItemAtkDef();
		player.setQuest("deathmatch", "done"); // without the additional information
	}
}
