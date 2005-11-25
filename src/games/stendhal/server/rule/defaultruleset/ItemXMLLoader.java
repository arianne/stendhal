package games.stendhal.server.rule.defaultruleset;

import java.io.*;

import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import games.stendhal.common.Pair;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;


public class ItemXMLLoader extends DefaultHandler
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(ItemXMLLoader.class);

  private String name;
  private String clazz;
  private String subclass;

  private double weight;
  
  private boolean stackable;

  /** slots where this item can be equiped */
  private List<String> slots;

  /** Attributes of the item */
  private List<Pair<String, String>> attributes;
  
  
  
  private List<DefaultItem> list;

  public static void main(String argv[])
    {
    if (argv.length != 1) 
      {
      System.err.println("Usage: cmd filename");
      System.exit(1);
      }
    try
    {
    List<DefaultItem> items=new ItemXMLLoader().load(argv[0]);
    for(DefaultItem item: items)
      {
      System.out.println (item.getItemName());
      }
    
    }
    catch(Throwable e)
    {
    e.printStackTrace();
    }
    
    System.exit(0);
    }
  
  private ItemXMLLoader()
    {
    }
  
  private static ItemXMLLoader instance;
  
  public static ItemXMLLoader get()
    {
    if(instance==null)
      {
      instance=new ItemXMLLoader();
      }
    
    return instance;    
    }
   
  public List<DefaultItem> load(String ref) throws SAXException
    {
    list=new LinkedList<DefaultItem>();
    // Use the default (non-validating) parser
    SAXParserFactory factory = SAXParserFactory.newInstance();
    try 
      {
      // Parse the input
      SAXParser saxParser = factory.newSAXParser();
      
      saxParser.parse(new File(ref), this); //getClass().getClassLoader().getResourceAsStream(ref)
      } 
    catch(ParserConfigurationException t) 
      {
      t.printStackTrace();
      }
    catch(IOException e)
      {
      e.printStackTrace();
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

  public void startElement(String namespaceURI, String lName, String qName, Attributes attrs)throws SAXException
    {
    if(qName.equals("item"))
      {
      name=attrs.getValue("name");
      attributes=new LinkedList<Pair<String,String>>();
      slots=new LinkedList<String>();
      stackable=false;
      }
    else if(qName.equals("type"))
      {
      clazz=attrs.getValue("class");
      subclass=attrs.getValue("subclass");
      }
    else if(qName.equals("stackable"))
      {
      stackable=true;
      }
    else if(qName.equals("weight"))
      {
      weight=Double.parseDouble(attrs.getValue("value"));
      }
    else if(qName.equals("slot"))
      {
      slots.add(attrs.getValue("name"));
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
      
      attributes.add(new Pair<String,String>(name,value));      
      }
    }

  public void endElement(String namespaceURI, String sName, String qName) throws SAXException
    {
    if(qName.equals("item"))
      {
      DefaultItem item=new DefaultItem(clazz,subclass,name,weight,slots,-1,attributes,stackable);
      list.add(item);
      }
    }

  public void characters(char buf[], int offset, int len) throws SAXException
    {
    String s = new String(buf, offset, len);
    }
  }