package games.stendhal.client.entity;

import marauroa.common.game.*;
import games.stendhal.client.*;

import java.awt.*;

/** A Player entity */
public class Player extends AnimatedGameEntity 
  {
  private String name;
  private String text;
  private Sprite nameImage;
  private Sprite textImage;
  private long wasTextWritten;
  
  public Player(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    name="";
    text="";
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
      
    if(!object.has("text"))
      {
      textImage=null;
      //text=null;
      }
      
    if(text!=null && !text.equals(object.get("text")))    
      {
      wasTextWritten=System.currentTimeMillis();
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
      textImage=new Sprite(image);      
      }
    }

  public void draw(GameScreen screen)
    {
    if(nameImage!=null) screen.draw(nameImage,x,y-0.3);
    if(System.currentTimeMillis()-wasTextWritten<3000 && textImage!=null) 
      {      
      screen.draw(textImage,x+1-((textImage.getWidth()/32.0f)/2),y+2.05);
      }
      
    super.draw(screen);
    }
  }
