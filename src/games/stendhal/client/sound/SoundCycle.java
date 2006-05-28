package games.stendhal.client.sound;

import games.stendhal.client.entity.Entity;
import games.stendhal.common.Rand;
import java.lang.ref.WeakReference;
import javax.sound.sampled.DataLine;
import org.apache.log4j.Logger;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject.ID;

/**
 * A sound cycle loops on performing a library sound. After each termination
 * of a sound performance it chooses a timepoint of the next performance at
 * random within the range of the PEROID setting (milliseconds). It can be
 * furthermore subject to probability, and a range for playing volume can be
 * defined.
 * <p>
 * A sound cycle can be GLOBAL or OBJECT-BOUND, depending on whether a game
 * entity has been supplied during creation. Global sounds always play
 * independent from player position.
 */
class SoundCycle extends Thread implements Cloneable
{
  /** the logger */
  private static final Logger logger = Log4J.getLogger(ClipRunner.class);

  private byte[]                ID_Token;

  WeakReference<Entity> entityRef;

  private String                token;

  private int                   period;

  private int                   volBot;

  private int                   volTop;

  private int                   chance;

  private DataLine              dataline;

  private long                  waitTime;

  private int                   playMax;

  private boolean               executing;

  private boolean               stopped;

  /**
   * Creates a sound cycle for a game entity. Depending on whether <code>
   * entity</code>
   * is void, this cycle is global or object-bound.
   * 
   * @param entity
   *          the game entity to which this cycle is bound; if <b>null</b>
   *          then a global cycle is created
   * @param token
   *          library sound token
   * @param period
   *          milliseconds of maximum delay time between singular sound
   *          performances
   * @param volBot
   *          relative bottom volume in percent, ranges 0..100
   * @param volTop
   *          relative top volume in percent, ranges 0..100
   * @param chance
   *          percent chance of performance for singular performances
   */
  public SoundCycle(Entity entity, String token, int period, int volBot, int volTop, int chance)
  {
    super("Stendhal.CycleSound." + token);

    ClipRunner clip;

    if (token == null)
      throw new NullPointerException();

    if (period < 1000)
      throw new IllegalArgumentException("illegal sound period");
    if (volBot < 0 | volBot > 100 | volTop < 0 | volTop > 100 | volTop < volBot)
      throw new IllegalArgumentException("bad volume setting");

    if ((clip = SoundSystem.get().getSoundClip(token)) == null)
      throw new IllegalStateException("undefined sound sample: " + token);

    if (entity != null)
    {
      this.ID_Token = entity.get_IDToken();
      this.entityRef = new WeakReference<Entity>(entity);
    }
    this.token = token;
    this.period = period;
    this.volBot = volBot;
    this.volTop = volTop;
    this.chance = chance;

    // calculate period minimum
    playMax = (int) clip.maxPlayLength();

    stopped = true;
  } // constructor

  public void terminate()
  {
    Entity o;
    ID oid;
    String hstr;

    o = null;
    if (entityRef != null)
      o = entityRef.get();

    if (o != null)
    {
      oid = o.getID();
      hstr = oid == null ? "VOID" : oid.toString();
    } else
      hstr = "VOID";

    hstr = "  ** terminating cycle sound: " + token + " / entity=" + hstr;
    logger.debug(hstr);
    // System.out.println( hstr );

    if (dataline != null)
    {
      dataline.stop();
      dataline = null;
    }
    executing = false;
  } // terminate

  /**
   * Temporarily ceases to perform sound playing. (May be resumed through
   * method <code>play()</code>.)
   */
  public void stopPlaying()
  {
    stopped = true;
  }

  /**
   * Starts or resumes playing this sound cycle.
   */
  public void play()
  {
    String hstr;

    stopped = false;
    if (!isAlive())
    {
      hstr = "  ** starting cycle sound: " + token + " / entity=?";
      logger.debug(hstr);
      // System.out.println( hstr );
      executing = true;
      start();
    }
  }

  public void run()
  {
    Entity o;

    while (executing)
    {
      waitTime = Math.max(playMax, Rand.rand(period));
      try
      {
        sleep(waitTime);
      } catch (InterruptedException e)
      {
      }

      if (!executing)
        return;

      if (stopped)
        continue;

      // if object bound sound cycle
      if (entityRef != null)
        if ((o = entityRef.get()) != null)
        {
          logger.debug("- start cyclic sound for entity: " + o.getType());
          dataline = o.playSound(token, volBot, volTop, chance);
        } else
        {
          SoundSystem.stopSoundCycle(ID_Token);
          terminate();
        }

      // if global sound cycle
      else
        SoundSystem.probablePlaySound(chance, token, volBot, volTop);
    }
  } // run

  /**
   * Returns a full copy of this SoundCycle, which is not running.
   */
  public SoundCycle clone()
  {
    Entity entity;
    SoundCycle c;

    entity = null;
    if (entityRef != null)
      entity = entityRef.get();

    c = new SoundCycle(entity, token, period, volBot, volTop, chance);
    return c;
  }
}