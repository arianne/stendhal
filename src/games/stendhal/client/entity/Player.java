package games.stendhal.client.entity;

import marauroa.common.*;
import marauroa.common.game.*;
import games.stendhal.client.*;

import java.awt.*;

/** A Player entity */
public class Player extends AnimatedGameEntity 
  {
  private final static int TEXT_PERSISTENCE_TIME=10000;
  private String name;
  private String text;
  private Sprite nameImage;
  private java.util.List<Sprite> textImages;
  private java.util.List<Long> textImagesTimes;
  private long wasTextWritten;
  
  public Player(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    name="";
    text="";
    textImages=new java.util.LinkedList<Sprite>();
    textImagesTimes=new java.util.LinkedList<Long>();
    nameImage=null;
    }
    
  protected void buildAnimations(String type)
    {
    SpriteStore store=SpriteStore.get();  

    sprites.put("move_up", store.getAnimatedSprite(translate(type),0,4,64,48));      
    sprites.put("move_right", store.getAnimatedSprite(translate(type),1,4,64,48));      
    sprites.put("move_down", store.getAnimatedSprite(translate(type),2,4,64,48));      
    sprites.put("move_left", store.getAnimatedSprite(translate(type),3,4,64,48));      
    }
  
  protected Sprite defaultAnimation()
    {
    animation="move_up";
    return sprites.get("move_up")[0];
    }
  

  public void modify(RPObject object) throws AttributeNotFoundException
    {
    super.modify(object);
    
    if(stopped && object.has("dir"))
      {
      int value=object.getInt("dir");
      switch(value)
        {
        case 0:
          animation="move_left";
          break;
        case 1:
          animation="move_right";
          break;
        case 2:
          animation="move_up";
          break;
        case 3:
          animation="move_down";
          break;
        }
      }
    
    if(name!=null && !name.equals(object.get("name")))
      {
      name=object.get("name");
      
      GameScreen screen=GameScreen.get();      
      Graphics g2d=screen.expose();

      GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      Image image = gc.createCompatibleImage(g2d.getFontMetrics().stringWidth(name),16,Transparency.BITMASK);    
      Graphics g=image.getGraphics();
      g.setColor(Color.white);
      g.drawString(name,0,10);
      nameImage=new Sprite(image);      
      }

    if(text!=null && object.has("text") && !text.equals(object.get("text")))    
      {
      text=object.get("text");
      
      GameScreen screen=GameScreen.get();      
      Graphics g2d=screen.expose();

      int lineLengthPixels=g2d.getFontMetrics().stringWidth(text);
      int numLines=(lineLengthPixels/240)+1;
      
      GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      Image image = gc.createCompatibleImage(((lineLengthPixels<240)?lineLengthPixels:240),16*numLines,Transparency.BITMASK);
      
      Graphics g=image.getGraphics();
      g.setColor(Color.yellow);
      int lineLength=text.length()/numLines;
      for(int i=0;i<numLines;i++)
        {
        String line=text.substring(i*lineLength,(i+1)*lineLength);
        g.drawString(line,0,i*16+10);
        }
        
      textImages.add(new Sprite(image));      
      textImagesTimes.add(new Long(System.currentTimeMillis()));
      }  
    }

  public void draw(GameScreen screen)
    {
    if(nameImage!=null) screen.draw(nameImage,x,y-0.3);
    if(textImages!=null) 
      {
      int j=0;
      for(Sprite textImage: textImages)
        {
        screen.draw(textImage,x+0.7-(textImage.getWidth()/(32.0f*2.0f)),y+j*0.5+2.05);
        j++;
        }
      
      if(textImages.size()>0 && (System.currentTimeMillis()-textImagesTimes.get(0)>TEXT_PERSISTENCE_TIME))
        {
        textImages.remove(0);
        textImagesTimes.remove(0);        
        }
      }
      
    super.draw(screen);
    }
  }
