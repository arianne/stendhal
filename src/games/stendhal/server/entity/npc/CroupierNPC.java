package games.stendhal.server.entity.npc;

import games.stendhal.common.Pair;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Dice;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.util.Area;

import java.awt.Rectangle;
import java.util.List;

import marauroa.common.game.IRPZone;

public abstract class CroupierNPC extends SpeakerNPC {
	
	/**
	 * The time (in turns) it takes before the NPC removes
	 * thrown dice from the table.
	 */
	private static final int CLEAR_PLAYING_AREA_TIME = 3 * 10;
	
	/**
	 * The area on which the dice have to be thrown.  
	 */
	private Area playingArea;
	
	/**
	 * A list where each possible dice sum is the index of the element
	 * which is either the name of the prize for this dice sum and
	 * the congratulation text that should be said by the NPC, or null
	 * if the player doesn't win anything for this sum.
	 */
	private List<Pair<String, String>> prizes;
	
	public CroupierNPC(String name, Rectangle playingArea) {
		super(name);
		IRPZone npcZone = StendhalRPWorld.get().getRPZone(this.getID());
		this.playingArea = new Area(npcZone, playingArea);
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
				Item prize = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(prizeName);
				say("Congratulations, "
						+ player.getName()
						+ ", you have "
						+ sum
						+ " points. "
						+ text);
				player.equip(prize, true);
			} else {
				say("Sorry, "
						+ player.getName()
						+ ", you only have "
						+ sum
						+ " points. You haven't won anything. Better luck next time!");
			}
			// The croupier takes the dice away from the table after some time.
			// This is simulated by shortening the degradation time of the dice.
			TurnNotifier.get().dontNotify(dice, null);
			TurnNotifier.get().notifyInTurns(CLEAR_PLAYING_AREA_TIME, dice, null);
		}
	}
}
