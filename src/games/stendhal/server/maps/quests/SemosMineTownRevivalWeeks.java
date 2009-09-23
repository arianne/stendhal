package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.maps.quests.revivalweeks.DadNPC;
import games.stendhal.server.maps.quests.revivalweeks.FoundGirl;

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

	// TODO: move this sign to the normal, non-quest map
	// This is actually a lie, the real is that we cannot tell apart
	// if someone is behind the top of the tower or in front of it.
	private void createSignToCloseTower() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		final Sign sign = new Sign();
		sign.setPosition(105, 114);
		sign.setText("Because of the missing guard rail it is too dangerous to enter the tower.");
		zone.add(sign);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		new FoundGirl().addToWorld();
		new DadNPC().addToWorld();
		createSignToCloseTower();
	}

	@Override
	public String getName() {
		return "SemosMineTownRevivalWeeks";
	}
}
