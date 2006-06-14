/* $Id$ */

//import games.stendhal.server.entity.creature.Creature;
import groovy.xml.MarkupBuilder

// calculate the name of the sprite for the item using a closure
// i know it is not nice to repeat client display logig on server ;)
spritename = {item | switch(item.get("class")) {
case ["book", "herb", "key", "scroll"] :
  return "misc/" + item.get("class")+"/"+ item.get("class") + "_" + item.get("subclass");
break;
default:
  return item.get("class")+"/"+item.get("subclass");
}}

// key to sort the items
sort = request.getParameter("sort");
if(sort == null)
  sort = "name";

// get list of item instances and sort them by class
sorteditems = world.getRuleManager().getEntityManager().getItems().toArray().toList().sort { l, r | return l.get(sort) <=> r.get(sort) };

nameformatter = { name |  s = name.replace("_"," "); return (s.substring(0,1).toUpperCase() + s.substring(1));}

mb = new MarkupBuilder(new PrintWriter(out))
mb.table() { 
  tr () {
    th(align : "left") {
     a(href : "?sort=name", "Item")
    }
    th(align : "left") {
     a(href : "?sort=class", "Class")
    }
    th(align : "left", "Description")
  }
  for (item in sorteditems) { 
    tr(valign : "top") {
      td() {
      img(src : "/data/sprites/items/"+ spritename(item) +".png", alt: nameformatter(item.getName()))
      }
      td(nameformatter(item.get("class")))
      td(item.describe())
    }
  }
}
