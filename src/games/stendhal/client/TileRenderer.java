package games.stendhal.client;

import java.io.*;
import java.awt.Graphics;
import java.util.*;

class TileRenderer
  {
  private int PLAIN_GRASS_TILE[]={1,8};

  private int LAND_TILE_NW[]={0,13};
  private int LAND_TILE_N[]={1,13};
  private int LAND_TILE_NE[]={2,13};

  private int LAND_TILE_W[]={0,14};
  private int LAND_TILE_C[]={1,14};
  private int LAND_TILE_E[]={2,14};

  private int LAND_TILE_SW[]={0,15};
  private int LAND_TILE_S[]={1,15};
  private int LAND_TILE_SE[]={2,15};

  private int SQUARE_TILE_NW[]={6,9};
  private int SQUARE_TILE_N[]={7,9};
  private int SQUARE_TILE_NE[]={8,9};

  private int SQUARE_TILE_W[]={6,10};
  private int SQUARE_TILE_C[]={7,10};
  private int SQUARE_TILE_E[]={8,10};

  private int SQUARE_TILE_SW[]={6,11};
  private int SQUARE_TILE_S[]={7,11};
  private int SQUARE_TILE_SE[]={8,11};

  private int WATER_TILE_NW[]={3,13};
  private int WATER_TILE_N[]={4,13};
  private int WATER_TILE_NE[]={5,13};

  private int WATER_TILE_W[]={3,14};
  private int WATER_TILE_C[]={4,14};
  private int WATER_TILE_E[]={5,14};

  private int WATER_TILE_SW[]={3,15};
  private int WATER_TILE_S[]={4,15};
  private int WATER_TILE_SE[]={5,15};

  private int DOUBLE_FLOWER_TILE[]={4,4};
  private int SINGLE_FLOWER_TILE[]={5,4};

  private int GRASS_TILE[]={17,7};
  private int DOUBLE_GRASS_TILE[]={16,7};

  private int PLANT_TILE[]={23,10};

  private int DOUBLE_FENCE_TILE[]={15,1};
  private int SINGLE_FENCE_TILE[]={15,0};
  private int FENCE_TILE_RIGHT[]={16,0};
  private int FENCE_TILE_LEFT[]={17,0};
  private int FENCE_TILE_CENTER[]={16,1};
  private int FENCE_TILE_LEFT_DOWN[]={17,1};
  private int FENCE_TILE_RIGHT_DOWN[]={16,2};
  private int FENCE_TILE_UP_DOWN[]={17,2};


  private TileStore tiles;
  private ArrayList<String> map;
  private int wx, wy;  
  
  public TileRenderer(TileStore tiles)
    {
    this.tiles=tiles;
    map=null;
    }
  
  public void setMapData(Reader reader) throws IOException
    {
    map=new ArrayList<String>();
        
    BufferedReader file=new BufferedReader(reader);
    String text;
    while((text=file.readLine())!=null)
      {
      map.add(text);
      }
    }

  public int getWidth()
    {
    return ((String)map.get(0)).length();
    }
  
  public int getHeight()
    {
    return map.size();
    }
  
  private char get(int x, int y)  
    {
    return ((String)(map.get(y))).charAt(x);
    }
  
  public void setScreenSize(int width, int height)
    {
    wx=width;
    wy=height;
    }
    

  private int[] getTypeOfLand(int x,int y)
    {
    boolean up=false, left=false, right=false, down=false;
    if(y>0 &&  get(x,y-1)!='l') up=true;
    if(y<getHeight()-1 && get(x,y+1)!='l') down=true;
    if(x>0 &&  get(x-1,y)!='l') left=true;
    if(x<getWidth()-1 && get(x+1,y)!='l') right=true;

    if(up && right)
      return LAND_TILE_NE;
    else if(up && left)
      return LAND_TILE_NW;
    else if(down && right)
      return LAND_TILE_SE;
    else if(down && left)
      return LAND_TILE_SW;
    else if(down)
      return LAND_TILE_S;
    else if(right)
      return LAND_TILE_E;
    else if(left)
      return LAND_TILE_W;
    else if(up)
      return LAND_TILE_N;
    else
      return LAND_TILE_C;
    }

  private int[] getTypeOfWater(int x,int y)
    {
    boolean up=false, left=false, right=false, down=false;
    if(y>0 &&  get(x,y-1)!='w') up=true;
    if(y<getHeight()-1 && get(x,y+1)!='w') down=true;
    if(x>0 &&  get(x-1,y)!='w') left=true;
    if(x<getWidth()-1 && get(x+1,y)!='w') right=true;

    if(up && right)
      return WATER_TILE_NE;
    else if(up && left)
      return WATER_TILE_NW;
    else if(down && right)
      return WATER_TILE_SE;
    else if(down && left)
      return WATER_TILE_SW;
    else if(down)
      return WATER_TILE_S;
    else if(right)
      return WATER_TILE_E;
    else if(left)
      return WATER_TILE_W;
    else if(up)
      return WATER_TILE_N;
    else
      return WATER_TILE_C;
    }

  private int[] getTypeOfSquare(int x,int y)
    {
    boolean up=false, left=false, right=false, down=false;
    if(y>0 &&  get(x,y-1)!='s') up=true;
    if(y<getHeight()-1 && get(x,y+1)!='s') down=true;
    if(x>0 &&  get(x-1,y)!='s') left=true;
    if(x<getWidth()-1 && get(x+1,y)!='s') right=true;

    if(up && right)
      return SQUARE_TILE_NE;
    else if(up && left)
      return SQUARE_TILE_NW;
    else if(down && right)
      return SQUARE_TILE_SE;
    else if(down && left)
      return SQUARE_TILE_SW;
    else if(down)
      return SQUARE_TILE_S;
    else if(right)
      return SQUARE_TILE_E;
    else if(left)
      return SQUARE_TILE_W;
    else if(up)
      return SQUARE_TILE_N;
    else
      return SQUARE_TILE_C;
    }

  private int[] getTypeOfFence(int x,int y)
    {
    boolean up=false, left=false, right=false, down=false;
    if(y>0 &&  get(x,y-1)=='F') up=true;
    if(y<getHeight()-1 && get(x,y+1)=='F') down=true;
    if(x>0 &&  get(x-1,y)=='F') left=true;
    if(x<getWidth()-1 && get(x+1,y)=='F') right=true;

    if(down && right)
      return FENCE_TILE_RIGHT_DOWN;
    else if(down && left)
      return FENCE_TILE_LEFT_DOWN;
    else if(down && up)
      return FENCE_TILE_UP_DOWN;
    else if(right && left)
      return FENCE_TILE_CENTER;
    else if(right)
      return FENCE_TILE_RIGHT;
    else if(left)
      return FENCE_TILE_LEFT;
    else if(down)
      return FENCE_TILE_UP_DOWN;
    else
      return SINGLE_FENCE_TILE;
    }
  
  public void render(Graphics g, int x, int y) 
    {
    for(int j=0;j<wy/32;j++)
      {
      for(int i=0;i<wx/32;i++)
        {
        if(map==null || !(i+x>=0 && i+x<getWidth() && j+y>=0 && j+y<getHeight()))
          {
          continue;
          }
          
        char item=get(i+x,y+j);
        switch(item)
          {
          case '*':
            tiles.getTile(PLAIN_GRASS_TILE).draw(g,i*32,j*32);
            break;
          case 'l':
            tiles.getTile(getTypeOfLand(x+i,y+j)).draw(g,i*32,j*32);
            break;    
          case 's':
            tiles.getTile(getTypeOfSquare(x+i,y+j)).draw(g,i*32,j*32);
            break;    
          case 'w':
            tiles.getTile(getTypeOfWater(x+i,y+j)).draw(g,i*32,j*32);
            break;    
          case 'F':
            tiles.getTile(getTypeOfFence(x+i,y+j)).draw(g,i*32,j*32);
            break;    
          case 'd':
            tiles.getTile(SINGLE_FLOWER_TILE).draw(g,i*32,j*32);
            break;
          case 'D':
            tiles.getTile(DOUBLE_FLOWER_TILE).draw(g,i*32,j*32);
            break;
          case 'g':
            tiles.getTile(GRASS_TILE).draw(g,i*32,j*32);
            break;
          case 'G':
            tiles.getTile(GRASS_TILE).draw(g,i*32,j*32);
            break;
          case 'p':
            tiles.getTile(PLANT_TILE).draw(g,i*32,j*32);
            break;
          case 'h':
            tiles.getTile(20,10).draw(g,i*32,j*32);
            tiles.getTile(21,10).draw(g,(i+1)*32,j*32);
            tiles.getTile(22,10).draw(g,(i+2)*32,j*32);
            break;
          case 'T':
            for(int k=0;k<5;k++)
              for(int m=0;m<4;m++)
                {
                tiles.getTile(24+m,k).draw(g,(i+m)*32,(j+k)*32);
                }
            break;
          case 't':
            for(int k=0;k<2;k++)
              for(int m=0;m<2;m++)
                {
                tiles.getTile(18+m,14+k).draw(g,(i+m)*32,(j+k)*32);
                }
            break;
          case 'H':
            tiles.getTile(17,3).draw(g,i*32,j*32);
            tiles.getTile(17,4).draw(g,i*32,(j+1)*32);
            tiles.getTile(17,4).draw(g,i*32,(j+2)*32);
            tiles.getTile(17,5).draw(g,i*32,(j+3)*32);
            tiles.getTile(17,6).draw(g,i*32,(j+4)*32);
            
            for(int k=0;k<5;k++)
              for(int m=1;m<7;m++)
                {
                tiles.getTile(11+m,9+k).draw(g,(i+m)*32,(j+k)*32);
                }
            break;
          }
        }
      }
    }
  }