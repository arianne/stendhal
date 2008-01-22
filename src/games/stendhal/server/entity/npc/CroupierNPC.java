package games.stendhal.server.entity.npc;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Dice;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

import java.awt.Rectangle;
import java.util.List;

import marauroa.common.Pair;

public abstract class CroupierNPC extends SpeakerNPC {

	/**
	 * The time (in seconds) it takes before the NPC removes thrown dice from
	 * the table.
	 */
	private static final int CLEAR_PLAYING_AREA_TIME = 10;

	/**
	 * The area on which the dice have to be thrown.
	 */
	private Area playingArea;

	/**
	 * A list where each possible dice sum is the index of the element which is
	 * either the name of the prize for this dice sum and the congratulation
	 * text that should be said by the NPC, or null if the player doesn't win
	 * anything for this sum.
	 */
	private List<Pair<String, String>> prizes;

	public CroupierNPC(String name) {
		super(name);
	}

	public void setPrizes(List<Pair<String, String>> prizes) {
		this.prizes = prizes;
	}

	public void onThrown(Dice dice, Player player) {
		if (playingArea.contains(dice)) {
			int sum = dice.getSum();
			Pair<String, String> prizeAndText = prizes.get(sum);
			if (prizeAndText != null) {
				String prizeName = prizeAndText.first();
				String text = prizeAndText.second();
				Item prize = SingletonRepository.getEntityManager().getItem(
						prizeName);
				if (prizeName.equals("golden legs")) {
					prize.setBoundTo(player.getName());
				}

				say("Congratulations, " + player.getTitle() + ", you have "
						+ sum + " points. " + text);
				player.equip(prize, true);
			} else {
				say("Sorry, "
						+ player.getTitle()
						+ ", you only have "
						+ sum
						+ " points. You haven't won anything. Better luck next time!");
			}
			// The croupier takes the dice away from the table after some time.
			// This is simulated by shortening the degradation time of the dice.
			SingletonRepository.getTurnNotifier().dontNotify(dice);
			SingletonRepository.getTurnNotifier().notifyInSeconds(CLEAR_PLAYING_AREA_TIME, dice);
		}
	}

	/**
	 * Sets the playing area (a table or something like that).
	 * 
	 * @param playingArea
	 *            shape of the playing area (in the same zone as the NPC)
	 */
	public void setTableArea(Rectangle playingArea) {
		this.playingArea = new Area(getZone(), playingArea);
	}
}
