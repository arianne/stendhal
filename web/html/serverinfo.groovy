// (Groovy script) <!-- Hide code from Dreamweaver
import marauroa.server.game.Statistics;
import groovy.xml.MarkupBuilder;

statistics = Statistics.getStatistics();

mb = new MarkupBuilder(new PrintWriter(out))
mb.div()  {
 h3( "Server info");
 p() {
   span("Players online: " + statistics.get("Players online"));   
   br();
   span("Objects now: " + statistics.get("Objects now"));  
   br();
   a(href : "/stats", "more statistics")        
 }
}
//-->