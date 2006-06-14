// (Groovy script) <!-- Hide code from Dreamweaver
import groovy.xml.MarkupBuilder;
import groovy.xml.DOMBuilder;
import java.io.InputStream;
import java.io.FileInputStream;
import marauroa.common.Configuration;

Configuration conf=Configuration.getConfiguration();
String webfolder=conf.get("server_stats_directory");

sb = new StringBuffer();
Reader xin = new File (webfolder + "server_stats.xml").newReader();
xin.eachLine { sb << it }
xin.close();

stats = new groovy.util.XmlParser().parseText( sb.toString() );

nameformatter = { name |  s = name.replace("_"," "); return (s.substring(0,1).toUpperCase() + s.substring(1));}

class KillRecord { 
 public String name; 
 public int number;
}

kills = [];

stats.attrib.findAll( ) {
  name= it ['@name'];
  if(name.startsWith("Killed ")) {
    mynumber = new Integer(it ['@value']);
    myname   = nameformatter(name.substring(7));
    kills.add new KillRecord(name : myname, number : mynumber);
  }
}

sort = request.getParameter("sort");
if(sort == null)
  sort = "name";


if("name".equals(sort)) {
  kills.sort{it.name};
}
else {
  kills.sort{it.number};
}



mb = new MarkupBuilder(new PrintWriter(out))
mb.table()  {
  tr() {
  th(align : "left") {
    a(href : "?sort=name", "Creature")
   }
   th(align : "right") {
    a(href : "?sort=number", "Deaths")
   }
  }
  for ( kill in kills) {
    tr() {
      td(align: "left", kill.name);
      td(align: "right", kill.number); 
    }
  }
}
//-->