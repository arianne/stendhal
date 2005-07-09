package games.stendhal.common;

import java.util.Random;

public class Rand 
  {
  static Random rand;
  
  static
    {
    rand=new Random();
    }
    
  public static int roll1D6()
    {
    return rand.nextInt(6)+1;
    }

  public static int roll1D20()
    {
    return rand.nextInt(6)+1;
    }
  
  public static int rand(int max)
    {
    return rand.nextInt(max);
    }
  }
