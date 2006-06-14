/* $Id$ */

import games.stendhal.server.entity.creature.Creature;
import groovy.xml.MarkupBuilder
import java.util.Collection;

//key to sort the creatures
sort = request.getParameter("sort");
if(sort == null)
  sort = "level";

sortedcreatures = world.getRuleManager().getEntityManager().getCreatures().toArray().toList();

if("level".equals(sort)) {
  sortedcreatures = sortedcreatures.sort { it.getLevel() };
}
else {
  sortedcreatures = sortedcreatures.sort { l, r | return l.get(sort) <=> r.get(sort) };
}

nameformatter = { name |  s = name.replace("_"," "); return (s.substring(0,1).toUpperCase() + s.substring(1));}

mb = new MarkupBuilder(new PrintWriter(out))
mb.html() {
  head() {
    title("Stendhal bestiary")
  }
  body() {
    table() { 
      tr () {
        th(align : "left") {
          a(href : "?sort=level", "Creature")
        }
        th(align : "left") {
          a(href : "?sort=class", "Class")
        }
        th(align : "left", "Description")
      }
      for (creature in sortedcreatures) { 
        tr(valign : "bottom") {
          td( align : "center") {
            img(src : "/sprite.groovy?sprite=monsters/"+creature.get("class")+"/"+creature.getName()+".png", alt: nameformatter(creature.getName()))
          }
          td(nameformatter(creature.get("class")))
          td(creature.describe())
        }
      }
    }
  }
}
