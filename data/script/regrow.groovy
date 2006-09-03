// Sets all plant growers to full growth.

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.PlantGrower;

for (PlantGrower grower in StendhalRPRuleProcessor.get().getPlantGrowers()) {
	grower.setToFullGrowth();
	grower.notifyWorldAboutChanges();
}