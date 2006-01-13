package games.stendhal.server.rule.defaultruleset;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import games.stendhal.server.entity.creature.Creature;
import org.apache.log4j.Logger;
import marauroa.common.Log4J;



public class CreatureXMLLoader extends DefaultHandler
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(CreatureXMLLoader.class);
 
  private String name;
  private String clazz;
  private String subclass;
  private int tileid;
  
  private int atk;
  private int def;
  private int hp;
  private double speed;
  
  private int sizeWidth;
  private int sizeHeight;
  
  private int xp;
  private int level;
  
  private List<Creature.DropItem> dropsItems;
  private Map<String,String> aiProfiles;
  
  private List<DefaultCreature> list;

  public static void main(String argv[])
    {
    if (argv.length != 1) 
      {
      System.err.println("Usage: cmd filename");
      System.exit(1);
      }
        
    try{System.out.println (new CreatureXMLLoader().load(argv[0]).size());}catch(Throwable e){e.printStackTrace();}
    System.exit(0);
    }
  
  private CreatureXMLLoader()
    {
    }
  
  private static CreatureXMLLoader instance;
  
  public static CreatureXMLLoader get()
    {
    if(instance==null)
      {
      instance=new CreatureXMLLoader();
      }
    
    return instance;    
    }
   
  public List<DefaultCreature> load(String ref) throws SAXException
    {
    list=new LinkedList<DefaultCreature>();
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

      saxParser.parse(is, this);
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
    
    return list;
    }

  public void startDocument() throws SAXException
    {
    }

  public void endDocument() throws SAXException
    {
    }
  
  private boolean drops;

  public void startElement(String namespaceURI, String lName, String qName, Attributes attrs)throws SAXException
    {
    if(qName.equals("creature"))
      {
      name=attrs.getValue("name");
      drops=false;
      dropsItems=new LinkedList<Creature.DropItem>();
      aiProfiles=new HashMap<String,String>();
      }
    else if(qName.equals("type"))
      {
      clazz=attrs.getValue("class");
      subclass=attrs.getValue("subclass");
      
      String tileid_value=attrs.getValue("tileid");      
      tileid=(tileid_value!=null?Integer.parseInt(tileid_value):-1);
      }
    else if(qName.equals("level"))
      {
      level=Integer.parseInt(attrs.getValue("value"));
      }
    else if(qName.equals("experience"))
      {
      // XP rewarded is right now 5% of the creature real XP 
      xp=Integer.parseInt(attrs.getValue("value"))*20;
      }
    else if(qName.equals("drops"))
      {
      drops=true;
      }
    else if(qName.equals("item"))
      {
      if(drops)
        {
        String name=null;
        Double probability=null;
        String range=null;
        
        for(int i=0;i<attrs.getLength();i++)
          {
          if(attrs.getQName(i).equals("value"))
            {
            name=attrs.getValue(i);
            }
          else if(attrs.getQName(i).equals("probability"))
            {
            probability=Double.parseDouble(attrs.getValue(i));
            }
          else if(attrs.getQName(i).equals("quantity"))
            {
            range=attrs.getValue(i);
            }
          }
        
        if(name!=null && probability!=null && range!=null)
          {          
          if(range.contains("["))
            {
            range=range.replace("[","");
            range=range.replace("]","");
            String[] amount=range.split(",");
            
            dropsItems.add(new Creature.DropItem(name,probability,Integer.parseInt(amount[0]),Integer.parseInt(amount[1])));
            }
          else
            {
            dropsItems.add(new Creature.DropItem(name,probability,Integer.parseInt(range)));
            }            
          }
        }
      }
    else if(qName.equals("attribute"))
      {
      String name=null;
      String value=null;
      
      for(int i=0;i<attrs.getLength();i++)
        {
        if(attrs.getQName(i).equals("name"))
          {
          name=attrs.getValue(i);
          }
        else if(attrs.getQName(i).equals("value"))
          {
          value=attrs.getValue(i);
          }
        }
      
      if("atk".equals(name))
        {
        atk=Integer.parseInt(value);
        }
      if("def".equals(name))
        {
        def=Integer.parseInt(value);
        }
      if("hp".equals(name))
        {
        hp=Integer.parseInt(value);
        }
      if("speed".equals(name))
        {
        speed=Double.parseDouble(value);
        }
      if("size".equals(name))
        {
        String[] size=value.split(",");
        
        sizeWidth=Integer.parseInt(size[0]);
        sizeHeight=Integer.parseInt(size[1]);
        ;
        }
      }
    }

  public void endElement(String namespaceURI, String sName, String qName) throws SAXException
    {
    if(qName.equals("creature"))
      {
      DefaultCreature creature=new DefaultCreature(clazz,subclass,name,tileid,hp,atk,def,level,xp, sizeWidth, sizeHeight,speed,dropsItems, aiProfiles);
      list.add(creature);
      }
    else if(qName.equals("drops"))
      {
      drops=false;
      }
    }

  public void characters(char buf[], int offset, int len) throws SAXException
    {
//    String s = new String(buf, offset, len);
    }
  }