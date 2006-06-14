// (Groovy script) <!-- Hide code from Dreamweaver
import marauroa.server.game.Statistics;
import groovy.xml.MarkupBuilder;
import groovy.sql.Sql
import marauroa.common.Configuration;

Configuration conf=Configuration.getConfiguration();
String jdbc_url=conf.get("jdbc_url");
String jdbc_user=conf.get("jdbc_user");
String jdbc_pwd=conf.get("jdbc_pwd");



mb = new MarkupBuilder(new PrintWriter(out))
mb.table()  {
  tr() {
    th(align : "left", colspan : "2", "Name"); 
    th(align : "right", "Level"); 
    th(align : "right", "Experience"); 
  }
  sql = Sql.newInstance(jdbc_url, jdbc_user, jdbc_pwd,
      "org.gjt.mm.mysql.Driver");
  sql.eachRow("select name, level, xp, outfit from avatars order by xp desc limit 10"){ row |
    tr( valign : "bottom") {
      td() {
      img(src : "/outfit.groovy?outfit="+ row.outfit, alt: row.name)
      }
      td(row.name); 
      td(align : "right",row.level); 
      td(align : "right",row.xp); 
    }
  }
}
//-->