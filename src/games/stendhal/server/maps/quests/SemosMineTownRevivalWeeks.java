package games.stendhal.server.maps.quests;

import games.stendhal.server.maps.quests.revivalweeks.DadNPC;
import games.stendhal.server.maps.quests.revivalweeks.FoundGirl;
import games.stendhal.server.maps.quests.revivalweeks.NineSwitchesGame;
import games.stendhal.server.maps.quests.revivalweeks.OutfitLender2NPC;
import games.stendhal.server.maps.quests.revivalweeks.TicTacToeGame;
import games.stendhal.server.maps.quests.revivalweeks.TownerClosedSign;

/**
 * <p>Creates a special version of Susi by the semos mine town.
 * <p>Creates a special version of Susi's father in a nearby house.
 * <p>Puts a sign by the tower to say why it is shut.
 */
public class SemosMineTownRevivalWeeks extends AbstractQuest {

	private static final String QUEST_SLOT = "semos_mine_town_revival";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		new FoundGirl().addToWorld();
		new DadNPC().addToWorld();
		new OutfitLender2NPC().addToWorld();
		new TownerClosedSign().addToWorld();
		new TicTacToeGame().addToWorld();
		new NineSwitchesGame().addToWorld();
	}

	@Override
	public String getName() {
		return "SemosMineTownRevivalWeeks";
	}
}
