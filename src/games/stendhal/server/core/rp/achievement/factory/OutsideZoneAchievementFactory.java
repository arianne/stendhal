package games.stendhal.server.core.rp.achievement.factory;

import java.util.Collection;
import java.util.LinkedList;

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.PlayerVisitedZonesCondition;
import games.stendhal.server.entity.npc.condition.PlayerVisitedZonesInRegionCondition;
/**
 * Factory for zone achievements
 *
 * @author madmetzger
 */
public class OutsideZoneAchievementFactory extends AbstractAchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.OUTSIDE_ZONE;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		Collection<Achievement> list = new LinkedList<Achievement>();
		//All outside zone achievements
		list.add(createAchievement("zone.outside.semos", "Junior Explorer", "Visit all outside zones in the Semos region",
									Achievement.EASY_BASE_SCORE, true,
									new PlayerVisitedZonesInRegionCondition("semos", Boolean.TRUE, Boolean.TRUE)));
		list.add(createAchievement("zone.outside.ados", "Big City Explorer", "Visit all outside zones in the Ados region",
									Achievement.EASY_BASE_SCORE, true,
									new PlayerVisitedZonesInRegionCondition("ados", Boolean.TRUE, Boolean.TRUE)));
		list.add(createAchievement("zone.outside.fado", "Far South", "Visit all outside zones in the Fado region",
									Achievement.MEDIUM_BASE_SCORE, true,
									new PlayerVisitedZonesInRegionCondition("fado", Boolean.TRUE, Boolean.TRUE)));
		list.add(createAchievement("zone.outside.orril", "Scout", "Visit all outside zones in the Orril region",
									Achievement.MEDIUM_BASE_SCORE, true,
									new PlayerVisitedZonesInRegionCondition("orril", Boolean.TRUE, Boolean.TRUE)));
		list.add(createAchievement("zone.outside.amazon", "Jungle Explorer", "Visit all outside zones in the Amazon region",
									Achievement.HARD_BASE_SCORE, true,
									new PlayerVisitedZonesInRegionCondition("amazon", Boolean.TRUE, Boolean.TRUE)));
		list.add(createAchievement("zone.outside.athor", "Tourist", "Visit all outside zones in the Athor region",
									Achievement.EASY_BASE_SCORE, true,
									new PlayerVisitedZonesInRegionCondition("athor", Boolean.TRUE, Boolean.TRUE)));
		list.add(createAchievement("zone.outside.kikareukin", "Sky Tower", "Visit all outside zones in the Kikareukin region",
									Achievement.HARD_BASE_SCORE, true,
									new PlayerVisitedZonesInRegionCondition("kikareukin", Boolean.TRUE, Boolean.TRUE)));
		list.add(createAchievement("zone.outside.deniran", "Westerner", "Visit all outside zones in the Deniran region",
									Achievement.EASY_BASE_SCORE, true,
									new PlayerVisitedZonesInRegionCondition("deniran", true, true)));

		//All interior zone achievements

		//Special zone achievements
		list.add(createAchievement("zone.special.bank", "Safe Deposit", "Visit all banks",
									Achievement.MEDIUM_BASE_SCORE, true,
									new PlayerVisitedZonesCondition("int_semos_bank", "int_nalwor_bank", "int_kirdneh_bank",
																	"int_fado_bank", "int_magic_bank", "int_ados_bank")));
		return list;
	}

}
