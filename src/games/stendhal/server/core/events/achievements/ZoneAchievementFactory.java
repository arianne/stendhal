package games.stendhal.server.core.events.achievements;

import games.stendhal.server.entity.npc.condition.PlayerVisitedZonesCondition;
import games.stendhal.server.entity.npc.condition.PlayerVisitedZonesInRegionCondition;

import java.util.Collection;
import java.util.LinkedList;
/**
 * Factory for zone achievements
 *  
 * @author madmetzger
 */
public class ZoneAchievementFactory extends AchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.ZONE;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		Collection<Achievement> list = new LinkedList<Achievement>();
		//All outside zone achievements
		list.add(createAchievement("zone.outside.semos", "Junior Explorer", "Visit all outside zones in the Semos region", 
									Achievement.EASY_BASE_SCORE, 
									new PlayerVisitedZonesInRegionCondition("semos", Boolean.TRUE, Boolean.TRUE)));
		list.add(createAchievement("zone.outside.ados", "Big City Explorer", "Visit all outside zones in the Ados region", 
									Achievement.EASY_BASE_SCORE, 
									new PlayerVisitedZonesInRegionCondition("ados", Boolean.TRUE, Boolean.TRUE)));
		list.add(createAchievement("zone.outside.fado", "Far South", "Visit all outside zones in the Fado region", 
									Achievement.MEDIUM_BASE_SCORE, 
									new PlayerVisitedZonesInRegionCondition("fado", Boolean.TRUE, Boolean.TRUE)));
		list.add(createAchievement("zone.outside.orril", "Scout", "Visit all outside zones in the Fado region", 
									Achievement.MEDIUM_BASE_SCORE, 
									new PlayerVisitedZonesInRegionCondition("orril", Boolean.TRUE, Boolean.TRUE)));
		list.add(createAchievement("zone.outside.amazon", "Jungle Explorer", "Visit all outside zones in the Amazon region", 
									Achievement.HARD_BASE_SCORE, 
									new PlayerVisitedZonesInRegionCondition("amazon", Boolean.TRUE, Boolean.TRUE)));
		list.add(createAchievement("zone.outside.athor", "Tourist", "Visit all outside zones in the Athor region", 
									Achievement.EASY_BASE_SCORE, 
									new PlayerVisitedZonesInRegionCondition("athor", Boolean.TRUE, Boolean.TRUE)));
		list.add(createAchievement("zone.outside.kikareukin", "Sky Tower", "Visit all outside zones in the Kikareukin region", 
									Achievement.HARD_BASE_SCORE, 
									new PlayerVisitedZonesInRegionCondition("kikareukin", Boolean.TRUE, Boolean.TRUE)));
		//All below ground achievements
		list.add(createAchievement("zone.underground.semos", "Canary", "Visit all underground zones in the Semos region", 
									Achievement.MEDIUM_BASE_SCORE, 
									new PlayerVisitedZonesInRegionCondition("semos", null, Boolean.FALSE)));
		list.add(createAchievement("zone.underground.nalwor", "Fear not drows nor hell", "Visit all underground zones in the Nalwor region", 
									Achievement.MEDIUM_BASE_SCORE, 
									new PlayerVisitedZonesInRegionCondition("nalwor", null, Boolean.FALSE)));
		list.add(createAchievement("zone.underground.athor", "Labyrinth Solver", "Visit all underground zones in the Athor region", 
									Achievement.MEDIUM_BASE_SCORE, 
									new PlayerVisitedZonesInRegionCondition("athor", null, Boolean.FALSE)));
		list.add(createAchievement("zone.underground.amazon", "Human Mole", "Visit all underground zones in the Amazon region", 
									Achievement.MEDIUM_BASE_SCORE, 
									new PlayerVisitedZonesInRegionCondition("amazon", null, Boolean.FALSE)));
		//All interior zone achievements
		
		//Special zone achievements
		list.add(createAchievement("zone.special.bank", "Safe Deposit", "Visit all banks", 
									Achievement.MEDIUM_BASE_SCORE, 
									new PlayerVisitedZonesCondition("int_semos_bank", "int_nalwor_bank", "int_kirdneh_bank", 
																	"int_fado_bank", "int_magic_bank", "int_ados_bank")));
		return list;
	}

}
