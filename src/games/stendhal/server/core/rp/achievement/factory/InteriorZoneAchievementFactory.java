package games.stendhal.server.core.rp.achievement.factory;

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.PlayerVisitedZonesInRegionCondition;

import java.util.Collection;
import java.util.LinkedList;
/**
 * Factory for interior zone achievements
 *
 * @author kymara
 */
public class InteriorZoneAchievementFactory extends AbstractAchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.INTERIOR_ZONE;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		Collection<Achievement> list = new LinkedList<Achievement>();
		//All below ground achievements
		list.add(createAchievement("zone.interior.semos", "Home maker", "Visit all interior zones in the Semos region",
									Achievement.MEDIUM_BASE_SCORE, true,
									new PlayerVisitedZonesInRegionCondition("semos", Boolean.FALSE, Boolean.FALSE)));
		list.add(createAchievement("zone.interior.nalwor", "Elf visitor", "Visit all interior zones in the Nalwor region",
									Achievement.MEDIUM_BASE_SCORE, true,
									new PlayerVisitedZonesInRegionCondition("nalwor", Boolean.FALSE, Boolean.FALSE)));
		list.add(createAchievement("zone.interior.ados", "Up town guy", "Visit all accessible interior zones in the Ados region",
									Achievement.MEDIUM_BASE_SCORE, true,
									new PlayerVisitedZonesInRegionCondition("ados", Boolean.FALSE, Boolean.FALSE)));
		list.add(createAchievement("zone.interior.wofolcity", "Kobold City", "Visit all interior zones in Wo'fol",
									Achievement.MEDIUM_BASE_SCORE, true,
									new PlayerVisitedZonesInRegionCondition("wofol city", Boolean.FALSE, Boolean.FALSE)));
		list.add(createAchievement("zone.interior.magiccity", "Magic City", "Visit all interior zones in the underground Magic city",
									Achievement.MEDIUM_BASE_SCORE, true,
									new PlayerVisitedZonesInRegionCondition("magic city", Boolean.FALSE, Boolean.FALSE)));
		return list;
	}

}
