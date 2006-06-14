// <div id="navBar"><h3>Groovy script</h3></div> <!-- Hide code from Dreamweaver
import groovy.xml.MarkupBuilder;

isActive = { menu | if (request.getPath().getPath().startsWith(menu)) return ("active"); return ("inactive");}

mb = new MarkupBuilder(new PrintWriter(out))
mb.div(id : "navBar") {
/*
  div ( id : "search") {
    form ( action : "#") {
      label ("search")
      input ( name : "searchfor", type : "text", size : "10")
      input ( name : "searchfor", type : "submit", value : "go")
     }
  }
*/  
  div ( id: "sectionlinks") {
    h3 ( "Sections ")
    ul(class : "navigation") {
      li ( class : isActive("/index.html") ) {
        a(href : "/index.html", "Home")
      }
      li ( class : isActive("/news") ) {
        a(href : "/news", "News")
      }
      li ( class : isActive("/stats") ) {
        a(href : "/stats", "Statistics")
      }
      li ( class : "inactive" ) {
        a(href : "/client.jnlp", "Play")
      }
    }
   }
  div ( class: "relatedlinks") {
    h3 ( "Reference ")
    ul(class : "navigation") {
      li ( class : isActive("/reference/item") ) {
        a(href : "/reference/item.html?sort=name", "Items")
      }
      li ( class : isActive("/reference/npc") ) {
        a(href : "/reference/npc.html", "NPCs")
      }
      li ( class : isActive("/reference/bestiary") ) {
        a(href : "/reference/bestiary.html", "Bestiary")
      }
      li ( class : isActive("/reference/level") ) {
        a(href : "/reference/level.html", "Levels")
      }
    }
  }
  div ( class: "relatedlinks") {
    h3 ( "External Links ")
    ul(class : "navigation") {
      li ( ) {
        a(href : "http://sourceforge.net/projects/arianne/", target: "_blank", "Sourceforge")
      }
      li ( ) {
        a(href : "http://arianne.sourceforge.net/wiki/index.php?title=Main_Page", target: "_blank", "Wiki")
      }
    }
  }
  div ( class: "relatedlinks") {
    h3 ( "Stendhal sites")
    ul(class : "navigation") {
      li ( ) {
        a(href : "http://arianne.murpe.com/", target: "_blank", "arianne.murpe.com")
      }
    }
    ul(class : "navigation") {
      li ( ) {
        a(href : "http://stendhal.ath.cx/", target: "_blank", "stendhal.ath.cx")
      }
    }
  }
}

// -->


