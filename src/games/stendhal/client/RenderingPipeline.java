package games.stendhal.client;


public class RenderingPipeline 
  {
  private static RenderingPipeline renderPipeline;
  private StaticGameLayers gameLayers;
  private GameObjects gameObjects;
  
  private RenderingPipeline()
    {    
    }
  
  public void addGameLayer(StaticGameLayers layer)
    {
    gameLayers=layer;
    }

  public void addGameObjects(GameObjects objects)
    {
    gameObjects=objects;
    }
  
  public static RenderingPipeline get()
    {
    if(renderPipeline==null)
      {
      renderPipeline=new RenderingPipeline();
      }
    
    return renderPipeline;
    }
    
  public void draw(GameScreen screen)
    {
    String set=gameLayers.getRPZoneLayerSet();
    gameLayers.draw(screen,set+"_0_floor");
    gameLayers.draw(screen,set+"_1_object");
    gameObjects.draw(screen);
    gameLayers.draw(screen,set+"_2_roof");
    }  
  }
