// (Groovy script) <!-- Hide code from Dreamweaver
import groovy.xml.MarkupBuilder;
import groovy.xml.DOMBuilder;
import groovy.util.XmlParser;
import java.io.InputStream;
import java.io.FileInputStream;

// closure to extraxt and cut the text contents out of the message body
teaser = { x, l | xml = new XmlParser().parseText( x ); 
 t = xml.text().trim();
 if(t.length()<=l)
   l = t.length() - 1;
 while(true) {
  if(" ,.:;!?\n".indexOf(t.substring(l,l+1))>=0)
    break;
  if(--l < 20)
    break;
 }
 return( t.substring(0,l+1).trim() + "... "); 
}

// read the news.xml file
sb = new StringBuffer();
Reader xin = new File ("web/news.xml").newReader();
xin.eachLine { sb << it }
xin.close();
myNews = new XmlParser().parseText( sb.toString() );

// output the news
mb = new MarkupBuilder(new PrintWriter(out))
mb.div()  {
  int count = 0;
  myNews.item.each() {
    if(++count<=1) {
      String content = teaser (it.content.text(), 45);
      h3( it.title.text()); 
      p() {
        span( content );
        a(href : "/news", "read more")        
      }
    }
  }
}

//-->