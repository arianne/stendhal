/* $Id$ */

import games.stendhal.server.entity.creature.Creature;
import groovy.xml.MarkupBuilder
import java.util.Collection;

creatures = world.getRuleManager().getEntityManager().getCreatures().toArray().toList().sort {it.level};
nameformatter = { name |  s = name.replace("_"," "); return (s.substring(0,1).toUpperCase() + s.substring(1));}

int result = 15 + new Random().nextInt(creatures.size()-16);

mb = new MarkupBuilder(new PrintWriter(out))
mb.div(id : "advert") {
  int i = 0;
  for (creature in creatures) { 
    if(++i==result) {
      h3("Are you ready?");
      p() {
        img(src : "/sprite.groovy?sprite=monsters/"+creature.get("class")+"/"+creature.getName()+".png", alt: nameformatter(creature.getName()), align:"left")
        span("Show your might and fight the " + nameformatter(creature.get("name")) + " today.")
        br();
        a(href : "/reference/bestiary.html", "more creatures");
      }
      break;
    }
  }
}
