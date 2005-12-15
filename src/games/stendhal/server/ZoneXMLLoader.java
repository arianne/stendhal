package games.stendhal.server;

import java.io.*;

import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.apache.log4j.Logger;
import marauroa.common.Log4J;

public class ZoneXMLLoader extends DefaultHandler
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(ZoneXMLLoader.class);

  public static class XMLZone
    {
    public String name;
    
    public int x;
    public int y;
    public int width;
    public int height;
    
    public boolean interior;
    public int level;
    
    public Map<String,String> layers;
    
    public XMLZone()
      {
      layers=new HashMap<String,String>();
      }
    
    public String getLayer(String key)
      {
      return layers.get(key);
      }
    
    public int getx()
      {
      return x;
      }
     
    public int gety()
      {
      return y;
      }

    public int getLevel()
      {
      return level;
      }
    
    public boolean isInterior()
      {
      return interior;
      }
    }

  public static void main(String argv[])
    {
    if (argv.length != 1) 
      {
      System.err.println("Usage: cmd filename");
      System.exit(1);
      }
    try
    {
    XMLZone zone=new ZoneXMLLoader().load(argv[0]);
    System.out.println("zone "+zone.name+" loaded successfully");
    }
    catch(Throwable e)
    {
    e.printStackTrace();
    }
    
    System.exit(0);
    }
  
  private ZoneXMLLoader()
    {
    }
  
  private static ZoneXMLLoader instance;

  public static ZoneXMLLoader get()
    {
    if(instance==null)
      {
      instance=new ZoneXMLLoader();
      }
    
    return instance;    
    }
   
  private XMLZone actualZone;
  
  public XMLZone load(String ref) throws SAXException
    {
    // Use the default (non-validating) parser
    SAXParserFactory factory = SAXParserFactory.newInstance();
    try 
      {
      // Parse the input
      SAXParser saxParser = factory.newSAXParser();
      
      InputStream is = getClass().getClassLoader().getResourceAsStream(ref);
      if (is == null)
        {
        throw new FileNotFoundException("cannot find resource '"+ref+"' in classpath");
        }
      
      saxParser.parse(new java.util.zip.InflaterInputStream(is), this);
      } 
    catch(ParserConfigurationException t) 
      {
      logger.error(t);
      }
    catch(IOException e)
      {
      logger.error(e);
      throw new SAXException(e);
      }
    
    return actualZone;
    }

  public void startDocument() throws SAXException
    {
    actualZone=new XMLZone();
    }

  public void endDocument() throws SAXException
    {
    }
  
  private StringBuffer st;
  private String layerName;

  public void startElement(String namespaceURI, String lName, String qName, Attributes attrs)throws SAXException
    {
    if(qName.equals("map"))
      {
      actualZone.name=attrs.getValue("name");
      }
    else if(qName.equals("location"))
      {
      String level=attrs.getValue("level");
      
      if(level.equals("int")==false)
        {
        actualZone.interior=false;
        actualZone.level=Integer.parseInt(level);
        actualZone.x=Integer.parseInt(attrs.getValue("x"));
        actualZone.y=Integer.parseInt(attrs.getValue("y"));
        }
      else
        {
        actualZone.interior=true;
        }
      }
    else if(qName.equals("size"))
      {
      actualZone.width=Integer.parseInt(attrs.getValue("width"));
      actualZone.height=Integer.parseInt(attrs.getValue("height"));
      }
    else if(qName.equals("layer"))
      {
      layerName=attrs.getValue("name");
      st=new StringBuffer();
      st.append(actualZone.width+" "+actualZone.height);
      }
    }

  public void endElement(String namespaceURI, String sName, String qName) throws SAXException
    {
    if(qName.equals("map"))
      {
//      System.out.println (actualZone.name+"\t"+actualZone.x+"\t"+actualZone.y+"\t"+actualZone.level+"\t"+actualZone.width+"\t"+actualZone.height);
//      for(String entry: actualZone.layers.keySet())
//        {
//        System.out.println (entry);
//        System.out.println (actualZone.layers.get(entry));
//        }
      }
    else if(qName.equals("layer"))
      {
      actualZone.layers.put(layerName,st.toString());
      st=null;
      }
    }

  public void characters(char buf[], int offset, int len) throws SAXException
    {
    if(st!=null)
      {
      st.append(buf,offset,len);
      }
    }
  }