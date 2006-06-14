// (Groovy script) <!-- Hide code from Dreamweaver
import groovy.xml.MarkupBuilder;
import groovy.xml.DOMBuilder;
import groovy.util.XmlParser;
import java.io.InputStream;
import java.io.FileInputStream;


sb = new StringBuffer();
Reader xin = new File ("web/news.xml").newReader();
xin.eachLine { sb << it }
xin.close();
myNews = new XmlParser().parseText( sb.toString() );

mb = new MarkupBuilder(new PrintWriter(out))
mb.div(class : "feature")  {
  int count = 0;
  myNews.item.each() {
    if(++count==1) {
      img (src : "", alt : "",  width : "250",  height : "250");
      h3( it.title.text()); 
      // begin hack to preserve possible html codes in the news body
      toState(2, "div");
      getPrinter().print(">");
      getPrinter().print(it.content.text());
      toState(3, "div")
      // done hack. if you don't use html in news.xml use the following line
      // div( it.content.text());
    }
  }
}

mb.div(class : "story")  {
  int count = 0;
  myNews.item.each() {
    if(++count>1) {    
      h3( it.title.text()); 
      p(it.content.text());
    }
  }
}


//-->