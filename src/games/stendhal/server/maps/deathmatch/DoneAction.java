package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * Handles player claim of victory by giving reward after verifing the winning.
 */
public class DoneAction implements ChatAction {

	/**
	 * Creates the player bound special trophy helmet and equips it.
	 *
	 * @param player Player object
	 * @return Helmet
	 */
	private Item createTrophyHelmet(final Player player) {
		final Item helmet = SingletonRepository.getEntityManager().getItem("trophy helmet");
		helmet.setBoundTo(player.getName());
		helmet.put("def", 1);
		helmet.setInfoString(player.getName());
		helmet.setPersistent(true);
		helmet.setDescription("This is " + player.getName()
		        + "'s grand prize for Deathmatch winners. Wear it with pride.");
		player.equipOrPutOnGround(helmet);
		return helmet;
	}

	/**
	 * Updates the player's points in the hall of fame for deathmatch.
	 *
	 * @param player Player
	 * @return new amount of points
	 */
	private int updatePoints(final Player player) {
		final StendhalRPRuleProcessor rules = SingletonRepository.getRuleProcessor();
		final DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));
		rules.addHallOfFamePoints(player.getName(), "D", deathmatchState.getPoints());
		return rules.getHallOfFamePoints(player.getName(), "D");
	}

	public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		final DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));
		if (deathmatchState.getLifecycleState() != DeathmatchLifecycle.VICTORY) {
			engine.say("C'm on, don't lie to me! All you can do now is #bail or win.");
			return;
		}

		final int points = updatePoints(player);

		// We assume that the player only carries one trophy helmet.
		final Item helmet = player.getFirstEquipped("trophy helmet");
		if (helmet == null) {
			createTrophyHelmet(player);
			engine.say("Congratulations, your score is now " + points
			        + "! Here is your special trophy helmet. Keep it, as the defense will increase by 1 "
				+ " for every deathmatch you complete. Now, tell me if you want to #leave.");
		} else {
			int defense = 1;
			if (helmet.has("def")) {
				defense = helmet.getInt("def");
			}
			defense++;
			final int maxdefense = 5 + (player.getLevel() / 5);
			if (defense > maxdefense) {
				helmet.put("def", maxdefense);
				engine.say("Congratulations, your score is now "
				                + points
				                + "! However, I'm sorry to inform you, the maximum defense for your helmet at your current level is "
				                + maxdefense);
			} else {
				helmet.put("def", defense);
				String message;
				if (defense == maxdefense) {
					message = "And your helmet has been magically strengthened to the maximum defense for your level, " + defense;
				} else {
					message = "And your helmet has been magically strengthened to a defense of " + defense;
				}
				engine.say("Congratulations, your score is now " + points
				        + "! " + message + ". Now, tell me if you want to #leave.");
			}
		}
		player.updateItemAtkDef();
		
		// without the additional information
		player.setQuest("deathmatch", "done"); 
	}
}
