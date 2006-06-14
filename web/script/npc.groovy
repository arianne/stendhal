/* $Id$ */

import games.stendhal.server.entity.creature.Creature;
import groovy.xml.MarkupBuilder
import java.util.Collection;

sortednpcs = rules.getNPCs().sort { it.getName() };

mb = new MarkupBuilder(new PrintWriter(out))
mb.table() { 
  tr () {
    th(align : "left", "NPC")
    th(align : "left", "Description")
  }
  for (npc in sortednpcs) { 
    tr(valign : "bottom") {
      td() {
        img(src : "/sprite.groovy?sprite=npc/"+npc.get("class")+ ".png", alt: npc.getName())
      }
      td(npc.describe())
    }
  }
}

