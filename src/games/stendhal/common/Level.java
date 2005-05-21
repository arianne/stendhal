package games.stendhal.common;

import java.util.*;
import marauroa.common.*;

public class Level
  {
  static private int LEVELS=100;
  
  static Vector<Integer> table;
  static
    {
    table=new Vector<Integer>();
    table.add(0);
    table.add(100);
    
    for(int i=2;i<LEVELS;i++)
      {
      int exp=((i*10+i*i*5+i*i*i*10+80)>>7)<<7;
      Logger.trace("Level::(static)","D","Level "+i+": "+exp);
      table.add(exp);
      }
    }
  
  public static int getLevel(int exp)
    {
    for(int i=0;i<LEVELS;i++)
      {
      if(exp<table.get(i))
        {
        return i-1;
        }
      }
    
    return LEVELS;
    }
  
  public static int changeLevel(int exp, int added)
    {
    int i;
    for(i=0;i<LEVELS;i++)
      {
      if(exp<table.get(i))
        {
        break;
        }
      }
    
    for(int j=i;j<LEVELS;j++)
      {
      if(exp+added<table.get(j))
        {
        return j-i;
        }
      }

    return 0;
    }
  }

