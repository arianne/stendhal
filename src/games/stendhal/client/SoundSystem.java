/*
 *  SoundSystem in games.stendhal.client
 *  file: SoundSystem.java
 * 
 *  Project stendhal
 *  @author Janet Hunt
 *  Created 25.12.2005
 * 
 *  Copyright (c) 2005 by Jane Hunt
 * 
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 Place - Suite 330, Boston, MA 02111-1307, USA, or go to
 http://www.gnu.org/copyleft/gpl.html.
 */

package games.stendhal.client;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.SoundObject;
import games.stendhal.common.Rand;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;

import org.apache.log4j.Logger;

/**
 * This sound system makes available a library of sounds which can be performed
 * under their library sound names. Sounds can be played as one-time occurrences
 * under various operation modi. (Not all combinations of modi are implemented.)
 * Sound volume can be set globally for all played sounds (system level) and per
 * particular sound performance or sound cycle schedule. The sound system also 
 * has a global Mute switch. 
 * 
 * <p>Operation Modi
 * <br>Sounds can be played GLOBAL or MAP-LOCALIZED (attributed with a map location)
 * <br>Sound can be played SINGULAR or in a CYCLIC pattern (which is governed by some
 * random pattern).
 * <br>Sounds can be played CERTAIN or PROBABLE (definable chances).
 * <br>Furthermore, there are the concepts of AUDIBILITY of map sounds and 
 * HEARING RANGE of the player, which are both mutable settings.
 * 
 * <p>Nature of Library Sounds
 * <br>Library sounds may be multifold and by this consist of a series of singular 
 * sound samples. When such a sound is called to perform, one of the alternative
 * samples is randomly selected to play. The definition file determines the inner
 * structure of library sounds, including possible equalizing volume settings.
 * 
 * <p>External Files
 * <br>This sound system requires a definition file and a sound database. The
 * definition file is a properties file located in <code>STORE_PROPERTYFILE</code>.
 * The sound database is a ZIP file containing PCM formatted sound samples stored
 * under their sample names. Library sound names, as used in the interface, are 
 * related to sample names in the definition file. The location of the database may
 * be also defined in the definition file under property "soundbase".      
 * 
 */
public class SoundSystem implements WorldObjects.WorldListener
{
   /** the logger instance. */
   static final Logger logger = Log4J.getLogger( SoundSystem.class );
   /** expected location of the sound definition file (classloader). */ 
   private static final String STORE_PROPERTYFILE = "data/sounds/stensounds.properties";  
   
   private static SoundSystem singleton;
   private static float[] dBValues = new float[101];
//   private static Timer timer = new Timer();
   
   static {
      // init our volume -> decibel map
      for ( int i = 0; i < 101; i++ )
      {
         double level = ((double)i) / 100;
         dBValues[i] = (float)(Math.log( level )/Math.log(10.0)*20.0);
      }
   }
   
   private HashMap<String,Object> sfxmap = new HashMap<String,Object>( 256 );
   private HashMap<byte[],SoundCycle> cycleMap = new HashMap<byte[],SoundCycle>();
   private ArrayList<AmbientSound> ambientList = new ArrayList<AmbientSound>();
   private JarFile soundFile;
   
   private Mixer mixer;
   private FloatControl volumeCtrl;
   private int volumeSetting = 100;
   private float volumeDelta;
   private boolean muteSetting;
   
   private boolean operative;
   
   /**
    * Plays a sound of the given name from the library of this sound system.
    * 
    * @param name token of sound
    * @param volBot relative bottom volume in percent, ranges 0..100
    * @param volTop relative top volume in percent, ranges 0..100
    * @param correctionDB loudness correction value (decibel)
    * @return the sound <code>DataLine</code> that is being played,
    *         or <b>null</b> on error
    */
   protected DataLine playSoundIntern ( String name, int volBot, int volTop,
         float correctionDB )
   {
      ClipRunner clip;
      int volume;
      
      // verify start conditions
      if ( name == null | volBot == 0 | !operative | muteSetting )
         return null;
      if ( volBot < 0 | volBot > 100 | volTop < 0 | volTop > 100 | volTop < volBot )
         throw new IllegalArgumentException("bad volume setting");
      
      // check/fetch sound 
      if ( (clip = getSoundClip( name )) == null )
         return null;
      
      volume = volBot + Rand.rand( volTop - volBot +1 );
      return clip.play( volume, correctionDB );
   }  // playSound
   
   /**
    * Plays a sound of the given name from the library of this sound system.
    * 
    * @param name token of sound
    * @param volume relative sound amplitude request in percent, ranges 0..100
    * @return the sound <code>DataLine</code> that is being played,
    *         or <b>null</b> on error  
    */
   public static DataLine playSound ( String name, int volume )
   {
      return get().playSoundIntern( name, volume, volume, (float)0.0 );
   }
   
   /**
    * Plays a sound of the given name from the library of this sound system
    * by setting volume to a random value between volBot and volTop.
    * 
    * @param name token of sound
    * @param volBot relative bottom volume in percent, ranges 0..100
    * @param volTop relative top volume in percent, ranges 0..100
    * @return the sound <code>DataLine</code> that is being played,
    *         or <b>null</b> on error
    */
   public static DataLine playSound ( String name, int volBot, int volTop )
   {
      return get().playSoundIntern( name, volBot, volTop, (float)0.0 );
   }

   /**
    * Plays a sound subject to a random performance chance.
    * 
    * @param chance 0..100 % chance value
    * @param name token of sound
    * @param volBot relative bottom volume in percent, ranges 0..100
    * @param volTop relative top volume in percent, ranges 0..100
    * 
    * @return the sound <code>DataLine</code> that is being played,
    *         or <b>null</b> on error or if performance is bailed 
    */
   public static DataLine probablePlaySound ( int chance, String name, int volBot, int volTop )
   {
      if ( Rand.rand( 100 ) < chance )
         return get().playSoundIntern( name, volBot, volTop, (float)0.0 );
      return null;
   }
   
   /**
    * Plays a sound bound to a given map position, possibly restricted by an
    * audibility area confinement. The sound volume is automatically adjusted 
    * to reflect the player's map position and hearing range.
    * 
    * @param where map position expressed in zone's coordinate system
    * @param audibility rectangel area of the coordinate system where
    *        this sound is audible; if <b>null</b> it is audible everywhere
    * @param name library sound token
    * @param volBot relative bottom volume
    * @param volTop relative top volume
    * @param chance percent chance of performance
    * 
    * @return <code>javax.sound.sampled.DataLine</code> the sound line that is
    *         being performed or <b>null</b> if no performance takes place
    */
   public static DataLine playMapSound ( 
         Point2D where, 
         Rectangle2D audibility,  // may be null
         String name,
         int volBot,
         int volTop,
         int chance
         )
   {
      Player player;
      RPObject playerObj; 
      Point2D playerPosition;
      Rectangle2D playerHearing;
      double distance, maxDist;
      int fogVolume;
      
      // broken cases
      if ( where == null | chance < 0 )
         throw new IllegalArgumentException();
      
      // lost chance cases (random)
      if ( chance < 100 && Rand.rand(100) >= chance )
         return null;
      
      // obtain player character's position and hearing range
      if ( (playerObj = StendhalClient.get().getPlayer()) == null )
         return null;
      player = (Player) StendhalClient.get().getGameObjects().get(playerObj.getID());
      if ( player == null )
         return null;
      playerPosition = player.getPosition();
      playerHearing = player.getHearingArea();
      
      // exclusion cases
      if ( !playerHearing.contains( where ) || 
           (audibility != null && !audibility.contains( playerPosition )) )
         return null;
      
      logger.debug( "SoundSystem: playing map sound (" + name + ") at pos " + (int)where.getX() + ", " + (int)where.getY() );
      
      // determine sound volume cutoff due to distance (fog value)
      distance = where.distance( playerPosition );
      maxDist = playerHearing.getWidth()/2;
      fogVolume = Math.max(0, (int)(95 * (maxDist - distance) / maxDist + 5) );
      
      return get().playSoundIntern( name, volBot, volTop, dBValues[fogVolume] );
   }  // playMapSound
   
   
   
   public static void playAmbientSound ( AmbientSound ambient )
   {
      SoundSystem sys;
      
      sys = get();
      ambient.play();
      
      synchronized ( sys.ambientList )
      {
         sys.ambientList.add( ambient );
      }
   }  // playAmbientSound
   
   /** Returns a <code>ClipRunner</code> object ready to play a sound of the 
    *  specified library sound name.
    *  
    * @param name token of library sound
    * @return <code>ClipRunner</code> or <b>null</b> if the sound is undefined
    */ 
   private ClipRunner getSoundClip ( String name )
   {
      ZipEntry zipEntry;
      Object o;
      String path, hstr;
      
      if ( (o = sfxmap.get( name )) instanceof ClipRunner )
         return (ClipRunner)o;
      
      if ( o != null )
      {
         // load sounddata from soundfile
         path = (String)o;
         hstr = name + "@" + path;
         logger.debug( "- loading from external SOUND ZIP: " + hstr );         
         zipEntry = soundFile.getEntry( path );
         if ( zipEntry != null )
            try {
               return new ClipRunner( hstr, getZipData( zipEntry ) );
            }
            catch ( Exception e )
            {}
      }
      return null;
   } // getSoundClip
   
   /**
    * Starts cyclic performance of a given library sound, attributed to a
    * specific entity on the map. There can only be one sound cycle for an
    * entity at a given time. If an sound cycle is started while a previous
    * cycle is defined for the entity, the previous cycle is discarded
    * and any ongoing sound performance stopped.
    * 
    * @param entity the game object that makes the sound 
    * @param token the library sound
    * @param period maximum time period for one sound occurrence
    * @param volBot bottom volume
    * @param volTop top volume
    * @param chance percent chance of performance 
    */
   public static SoundCycle startSoundCycle ( 
         Entity entity, 
         String token, 
         int period,
         int volBot,
         int volTop,
         int chance
         )
   {
      SoundSystem sys;
      SoundCycle cycle, c1;
      byte[] entity_token;
      
      if ( !(sys = get()).isOperative() )
         return null;
      
      entity_token = entity.get_IDToken();
      cycle = null;
      synchronized( sys.cycleMap )
      {
         try {
            cycle = new SoundCycle(entity, token, period, volBot, volTop, chance );
            cycle.play();
            
            c1 = sys.cycleMap.get( entity_token );
            if ( c1 != null )
               c1.terminate();
               
            sys.cycleMap.put( entity_token, cycle );
         }
         catch ( IllegalStateException e )
         {
            logger.error( "*** Undefined sound sample: " + token, e );
         }
      }
      return cycle;
   }  // startSoundCycle
   
   /**
    * Stops execution of the sound cycle for a specific map entity. 
    * This will interrupt any ongoing sound performance immediately.
    *  
    * @param entity_ID byte[] identity token of the map entity 
    */
   public static void stopSoundCycle ( byte[] entity_ID )
   {
      SoundCycle cycle;
      SoundSystem sys;
      
      sys = get(); 
      if ( (cycle = sys.cycleMap.get(entity_ID)) != null )
      synchronized( sys.cycleMap )
      {
         sys.cycleMap.remove(entity_ID);
         cycle.terminate();
      }
   }
   
/*   
   private ClipRunner getSoundClip ( String name, ZipEntry entry )
      throws IOException, UnsupportedAudioFileException
   {
      return new ClipRunner( name, getZipData( entry ) );
   }
*/ 
   
   /** Loads a junk of data from the jar soundfile and returns it as
    *  a byte array.
    *  
    * @param entry
    * @return
    * @throws IOException
    */
   private byte[] getZipData ( ZipEntry entry ) throws IOException
   {
      InputStream in;
      ByteArrayOutputStream bout;

      in = soundFile.getInputStream( entry );
      bout = new ByteArrayOutputStream( (int)entry.getSize() );
      transferData( in, bout, 4096 );
      in.close();
      return bout.toByteArray();
   }
/*   
   private String getSoundFileName ( String name )
   {
      Object o;
      
      if ( (o = sfxmap.get( name )) instanceof String )
         return (String)o;
      return null;
   }
*/   
   /** Whether the parameter sound is available in this sound system.
    * @param name token of sound
    */
   public boolean contains ( String name )
   {
      return name != null && sfxmap.containsKey( name );
   }
   
   /** Obtains a resource input stream. 
    *  Fetches currently from the main program's classloader. 
    * 
    * @param name 
    * @return InputStream
    * @throws IOException
    */
   private InputStream getResourceStream( String name ) throws IOException
   {
      InputStream in = Log4J.class.getClassLoader().getResourceAsStream( name );
      if ( in == null )
         throw new FileNotFoundException( name );
      return in;
   }
   
   private void init ()
   {
      Properties prop;
      HashMap<String,byte[]> dataList = new HashMap<String,byte[]>();
      ZipEntry zipEntry;
      File file;
      InputStream in;
      OutputStream out;
      String path, key, value, name, hstr;
      ClipRunner clip, sound;
      int loaded, failed, count, pos, i, loudness;
      byte[] soundData;
      Iterator it;
      Map.Entry entry;
      boolean load;
      
      if ( !initJavaSound() )
      {
         logger.error( "*** SOUNDSYSTEM JAVA INIT ERROR" );
         return;
      }
      
      try {
         // load sound properties 
         prop = new Properties();
         in = getResourceStream(STORE_PROPERTYFILE);
         prop.load( in );
         in.close();
      
         // get sound library file
         path = prop.getProperty( "soundbase", "sounds/stensounds0.jar" );
         
         // make a temporary copy of sound resource
         file = File.createTempFile( "stendhal-", ".tmp" );
         in = getResourceStream( path );
         out = new FileOutputStream( file );
         transferData( in, out, 4096 );
         in.close();
         out.close();
         
         // open the sound file 
         soundFile = new JarFile( file );
         file.deleteOnExit();
         
         // read all load-permitted sounds listed in properties 
         // from soundfile into cache map
         failed = loaded = count = 0;
         for ( it = prop.entrySet().iterator(); it.hasNext(); )
         {
            entry = (Map.Entry)it.next();
            key = (String)entry.getKey();
            if ( !key.startsWith( "sfx." ) )
               continue;
            
            // name and declaraction of sound data
            name = key.substring( 4 );
            value = (String)entry.getValue();
            
            logger.debug("- sound definition: " + key + " = " + value );
            
            // decide on loading
            // (do not load when ",x" trailing path; always load when "." in name)
            if ( (pos = value.indexOf( ',' )) > -1 )
            {
               path = value.substring( 0, pos );
               load = value.substring( pos+1 ).charAt(0) != 'x';
            }
            else
            {
               path = value;
               load = true;
            }
            load |= name.indexOf('.') != -1;
            
            // look if sound data is already stored internally
            if ( (soundData = dataList.get( path )) == null )
            {
               // else load sounddata from jar file
               zipEntry = soundFile.getEntry( path );
               if ( zipEntry == null )
               {
                  hstr = "*** MISSING SOUND: " + name + "=" + path;
                  logger.error( hstr );
                  failed++;
                  continue;
               }
               soundData = getZipData( zipEntry );
            }
            
            // construct sound clip from sample data
            // (we always do that to verify sound sample format) 
            try {
               // determine equalizing loudness setting
               loudness = 100;
               if ( (pos = value.lastIndexOf( ',' )) != -1 )
               {
                  try { 
                     loudness = Integer.parseInt( value.substring( pos+1 ) ); 
                  }
                  catch ( Exception e )
                  {}
               }
               
               // investigate sample status
               if ( (i = name.indexOf('.')) != -1 )
                  name = name.substring( 0, i );
               
               sound = new ClipRunner( name + "@" + path, soundData, loudness );
               count++;
            }
            catch ( Exception e )
            {
               // could not validate sound file content
               hstr = "*** CORRUPTED SOUND: " + name + "=" + path;
               logger.error( hstr, e );
               failed++;
               continue;
            }
            
            // store new sound object into soundsystem library map if opted
            if ( load )
            {
               logger.debug("- storing mem-library soundclip: " + name );

               // stores the clip sound in memory
               if ( (clip = getSoundClip( name )) != null )
                  clip.addSample( sound );
               else
                  sfxmap.put( name, sound );

               // memorizes the sound data (only for init purposes)
               dataList.put( path, soundData );
               loaded++;
            }
            else
            {
               // or stores just the sample data name
               logger.debug("- storing external sound ref: " + name );
               sfxmap.put( name, path );
            }
         }  // for
      
         // report to startup console
         
         hstr = "Stendhal Soundsystem OK: " + count + " samples approved / " 
               + loaded + " loaded / " + sfxmap.size() + " library sounds";
         logger.info( hstr );

         if ( failed != 0 )
         {
            hstr = "missing or corrupted sounds: " + failed;
            logger.info( hstr );
         }
         
         // register listeners
         WorldObjects.addWorldListener( this );
         operative = true;
      }

      catch ( IOException e )
      {
         hstr = "*** SOUNDSYSTEM LOAD ERROR";
         logger.error( hstr, e );
         return;
      }
   }  // init
   
   private boolean initJavaSound ()
   {
      Mixer.Info info, mixInfos[];
      String hstr;
      double level;
      int i;
      
      if ( (mixInfos = AudioSystem.getMixerInfo()) == null ||
            mixInfos.length == 0 )
      { 
         logger.error( "*** SoundSystem: no sound driver available!" );
         return false;
      }

      mixer = AudioSystem.getMixer( null ); //mixInfos[4] );
      info = mixer.getMixerInfo();
      hstr = "Sound driver: " + info.getName() + "(" + info.getDescription() + ")";
      logger.info( hstr );
      
      // try a master volume control 
      try {
         volumeCtrl = (FloatControl) mixer.getControl( FloatControl.Type.MASTER_GAIN );
         volumeCtrl.setValue( (float)0.0 );
      }
      catch ( Exception e )
      {
         logger.debug( "SoundSystem: no master volume controls" );
      }
      
      return true;
   }  // initJavaSound
   
   /** Stops and removes all ambient sounds. */
   public void clearAmbientSounds ()
   {
      List list;
      Iterator it;
      
      synchronized ( ambientList )
      {
         list = (List)ambientList.clone();
         for ( it = list.iterator(); it.hasNext(); )
         {
            ((AmbientSound)it.next()).terminate();
         }
      }
   }
   
   /** Sets the global Mute switch of this sound system. Does nothing
    *  on duplicate call. */
   public void setMute ( boolean v )
   {
      AmbientSound ambient;
      Iterator it;

      if ( v == muteSetting )
         return;
      
      logger.info( "- sound system setting mute = " + (v ? "ON" : "OFF") );           
      muteSetting = v;

      synchronized ( ambientList )
      {
         for ( it = ambientList.iterator(); it.hasNext(); )
         {
            ambient = (AmbientSound)it.next();
            if ( v )
               ambient.stop();
            else
               ambient.play();
         }
      }
   }  // setMute

   /** Returns the actual state of the global Mute switch of this sound system. 
    *  @return <b>true</b> if and only if Mute is ON (silent)
    */
   public boolean getMute ()
   {
      return muteSetting;
   }
   
   /** Sets a global volume level for all sounds played with this sound system. 
    *  The volume value ranges between 0 (silent) and 100 (loudest).
    *  
    * @param volume 0 .. 100
    */ 
   public void setVolume ( int volume )
   {
      AmbientSound ambient;
      Iterator it;
      float dB;
      
      if ( volume < 0 )
         volume = 0;
      if ( volume > 100 )
         volume = 100;
      
      dB = dBValues[ volume ];
      logger.info( "- sound system setting volume dB = " + dB 
            + "  (gain " + volume + ")" );           

      volumeSetting = volume;
      if ( volumeCtrl != null )
      {
         volumeCtrl.setValue( dB );
      }
      else
      {
         volumeDelta = dB;

         // update ambient sounds
         synchronized ( ambientList )
         {
            for ( it = ambientList.iterator(); it.hasNext(); )
            {
               ((AmbientSound)it.next()).updateVolume();
            }
         }
      }
   }  // setVolume
   
   /** Returns the current value of this sound system's voume setting. 
    * 
    *  @return volume ranging 0 (silent) .. 100 (loudest)
    */  
   public int getVolume ()
   {
      return volumeSetting;
   }
   
   /** Whether the sound system has been initialized and is ready to operate. */
   public boolean isOperative ()
   {
      return operative;
   }
   
   /** Returns the singleton instance of the Stendhal sound system. */
   public static SoundSystem get()
   {
      if ( singleton == null )
         singleton = new SoundSystem();
      return singleton;
   }

   /** Releases any resources associated with this sound system. The system is
    * rendered inoperative.
    */
   public void exit ()
   {
      clearAmbientSounds();
      if ( soundFile != null )
         try { 
            soundFile.close();
            operative = false;
            }
         catch ( Exception e ) 
         {}
      logger.info( "sound system exit performed, inactive" );
   }
   
   private SoundSystem()
   {
      init();
   }

   /**
    * Transfers the contents of the input stream to the output stream
    * until the end of input stream is reached.
    * 
    * @param input
    * @param output
    * @param bufferSize
    * @throws java.io.IOException
    */
   public static void transferData ( InputStream input, OutputStream output,
         int bufferSize  ) throws java.io.IOException
   {
      byte[] buffer = new byte[ bufferSize ];
      int len;

      while ( (len = input.read( buffer )) > 0 )
         output.write( buffer, 0, len );
   }  // transferData

   
   
//  *************  INNER CLASSES  ***********************
   
private class ClipRunner implements LineListener
{
   private String text;
   private byte[] data;
   private int loudness;
   private long maxLength;
   private AudioFileFormat format;
   private List <ClipRunner>samples;
   
   /**
    * Creates a ClipRunner instance by name and raw audio data.
    * Loudness setting is at 100%.
    * 
    * @param text info about sound (docu)
    * @param audioData raw audio data
    * @throws UnsupportedAudioFileException
    */   
   public ClipRunner ( String text, byte[] audioData )
      throws UnsupportedAudioFileException
   {
      this( text, audioData, 100 );
   }

   /**
    * Creates a ClipRunner instance by name, raw audio data and a relative
    * loudness setting.
    * 
    * @param text info about sound (docu)
    * @param audioData raw audio data
    * @param volume standard loudness of sound 0 .. 100
    * @throws UnsupportedAudioFileException
    */   
public ClipRunner ( String text, byte[] audioData, int volume )
   throws UnsupportedAudioFileException
{
   ByteArrayInputStream in;
   float frameRate, frames;

   if ( text == null )
      throw new NullPointerException();
   if ( volume < 0 | volume > 100 )
      throw new IllegalArgumentException("illegal loudness value");
   
   this.text = text;
   data = audioData;
   loudness = volume;

   // detect/control digestible sample format
   in = new ByteArrayInputStream( audioData );
   try { 
      format = AudioSystem.getAudioFileFormat( in );
      
      frameRate = format.getFormat().getFrameRate();
      frames = format.getFrameLength();
      if ( frameRate != AudioSystem.NOT_SPECIFIED & frames != AudioSystem.NOT_SPECIFIED )
      {
         maxLength = (int)(frames / frameRate * 1000);
      }
   }
   catch ( IOException e )
   {
      logger.error( "- IO-Error reading sound data: ", e );
   }
}  // constructor

/** Adds another clip as an alternate sound to be run under this clip. 
 *  Alternative sounds are played by random and equal chance.
 * 
 * @param clip alternate sound clip 
 * @throws UnsupportedAudioFileException
 */
public void addSample ( ClipRunner clip )
{
   if ( samples == null )
      samples = new ArrayList<ClipRunner>();

   samples.add( clip );
   maxLength = Math.max( maxLength, clip.maxPlayLength() );
}  // addSample

/** The maximum play length of this clip in milliseconds. 
 * 
 * @return long milliseconds, 0 if undefined
 */
public long maxPlayLength ()
{
   return maxLength;
}

/** Starts this clip to play with the given volume settings.
 * 
 *  @param volume loudness in 0 .. 100
 *  @param correctionDB decibel correction value from outward sources
 *  @return the AudioSystem <code>DataLine</code> object that is being played,
 *          or <b>null</b> on error
 */
public DataLine play ( int volume, float correctionDB )
{
   DataLine line;
   
   if ( (line = getAudioClip( volume, correctionDB )) != null )
   {
      line.start();
   }
   return line;
}

/** Starts this clip to loop endlessly with the given start volume setting.
 * 
 *  @param volume loudness in 0 .. 100
 *  @return the AudioSystem <code>Clip</code> object that is being played,
 *          or <b>null</b> on error
 */
public Clip loop ( int volume, float correctionDB )
{
   Clip line;
   
   if ( (line = getAudioClip( volume, correctionDB )) != null )
   {
      line.loop( Clip.LOOP_CONTINUOUSLY );
   }
   return line;
}

/** Returns a runnable AudioSystem sound clip with the given volume settings.
 * 
 *  @param volume loudness in 0 .. 100
 *  @param correctionDB decibel correction value from outward sources
 *  @return an AudioSystem sound <code>Clip</code> that represents this sound,
 *          or <b>null</b> on error
 */
public Clip getAudioClip ( int volume, float correctionDB )
{
   Clip clip;
   DataLine.Info info;
   AudioFormat fo;
   FloatControl volCtrl;
   float dB;
   int index;
   ByteArrayInputStream input;
   String hstr;
   
   fo = format.getFormat(); 
   info = new DataLine.Info( Clip.class, fo );
   if ( !AudioSystem.isLineSupported(info) ) 
   {
      logger.error( "** AudioSystem: unsupported line format for: " + text );
      return null;
   }

   try {
      index = 0;
      
      // if multiple samples then roll dice
      if ( samples != null )
         index = Rand.rand( samples.size()+1 );

      // if available choice is for this object's sample ("data")
      if ( index == 0 )
      {
         // Obtain and open the line.
         clip = (Clip) mixer.getLine(info);
   //      clip.open( fo, data, 0, data.length );
         input = new ByteArrayInputStream( data );
         clip.open( AudioSystem.getAudioInputStream( input ) );
         
         // set the volume
         try { 
            volCtrl = (FloatControl) clip.getControl( FloatControl.Type.MASTER_GAIN );
            dB = dBValues[ volume ] + dBValues[ loudness ] + correctionDB;
            volCtrl.setValue( dB + volumeDelta );
         }
         catch ( Exception e )
         { 
            hstr = "** AudioSystem: no master_gain controls for: " + this.text;
            logger.error( hstr, e );
         }

         // run clip
         clip.addLineListener( this );
         return clip;
      }
      
      // if choice is for other sample
      else
      {
         return samples.get( index-1 ).getAudioClip( volume, correctionDB );
      }
   } 
   catch (Exception ex) 
   {
      hstr = "** AudioSystem: clip line unavailable for: " + this.text;
      logger.error( hstr, ex );
      return null;
   }   
}  // getAudioClip

/* 
 * Overridden: @see javax.sound.sampled.LineListener#update(javax.sound.sampled.LineEvent)
 */
public void update ( LineEvent event )
{
   // this discards line resources when the sound has stopped
   if ( event.getType() == LineEvent.Type.STOP )
   {
      ((Line)event.getSource()).close();
   }
}

}  // class ClipRunner
   
/**
 * A sound cycle loops on performing a library sound. After each termination
 * of a sound performance it chooses a timepoint of the next performance 
 * at random within the range of the PEROID setting (milliseconds). It can
 * be furthermore subject to probability. 
 * 
 */
private static class SoundCycle extends Thread  implements Cloneable
{
   private byte[] ID_Token;
   private WeakReference<Entity> entityRef;
   private String token; 
   private int period;
   private int volBot;
   private int volTop;
   private int chance;

   private DataLine dataline;
   private long waitTime;
   private int playMax;
   private boolean executing;
   private boolean stopped;
   
   /**
    * Creates a sound cycle for a game entity.
    * 
    * @param entity game entity to which this cycle is bound  
    * @param token library sound token
    * @param period milliseconds of maximum delay time between singular
    *        sound performances
    * @param volBot relative bottom volume in percent, ranges 0..100
    * @param volTop relative top volume in percent, ranges 0..100
    * @param chance percent chance of performance for singular performances
    */
   public SoundCycle ( 
         Entity entity, 
         String token, 
         int period,
         int volBot,
         int volTop,
         int chance
         )
   {
      super( "Stendhal.CycleSound." + token );
      
      ClipRunner clip;
      
      if ( entity == null | token == null )
         throw new NullPointerException();

      if ( period < 1000 )
         throw new IllegalArgumentException("illegal sound period");
      if ( volBot < 0 | volBot > 100 | volTop < 0 | volTop > 100 | volTop < volBot )
         throw new IllegalArgumentException("bad volume setting");

      if ( (clip = get().getSoundClip( token )) == null )
         throw new IllegalStateException( "undefined sound sample: " + token );
      
      this.ID_Token = entity.get_IDToken();
      this.entityRef = new WeakReference<Entity>( entity );
      this.token = token;
      this.period = period;
      this.volBot = volBot;
      this.volTop = volTop;
      this.chance = chance;
      
      // calculate period minimum
      playMax = (int)clip.maxPlayLength();

      stopped = true;
   }  // constructor

   public void terminate ()
   {
      Entity o;
      ID oid;
      String hstr;
      
      o = entityRef.get();
      if ( o != null )
      {
         oid = o.getID();
         hstr = oid == null ? "VOID" : oid.toString(); 
      }
      else
         hstr = "VOID"; 
      hstr = "  ** terminating cycle sound: " + token + " / entity=" + hstr;
      logger.debug( hstr );
      
      if ( dataline != null )
      {
         dataline.stop();
         dataline = null;
      }
      executing = false;
   }
   
   /** Temporarily ceases to perform sound playing. (May be resumed through
    *  method <code>play()</code>.) */  
   public void stopPlaying ()
   {
      stopped = true;
   }
   
   /** Starts or resumes playing this sound cycle.
    */
   public void play ()
   {
      String hstr; 
      
      stopped = false;
      if ( !isAlive() )
      {
         hstr = "  ** starting cycle sound: " + token + " / entity=?";
         logger.debug( hstr );
         executing = true;
         start();
      }
   }
   
   public void run ()
   {
      Entity o;
      
      while ( executing )
      {
         waitTime = Math.max( playMax, Rand.rand( period ) );
         try { sleep( waitTime ); }
         catch ( InterruptedException e )
         {}
         
         if ( !executing )
            return;
         
         if ( stopped )
            continue;
         
         if ( (o = entityRef.get()) != null  )
         {
            logger.debug( "- start cyclic sound for entity: " + o.getType() + " / " + o.getSubType() );
            dataline = o.playSound( token, volBot, volTop, chance );
         }
         else
         {
            stopSoundCycle( ID_Token );
            terminate();
         }
      }
   }
   
   /** Returns a full copy of this SoundCycle, which is not running.
     */
   public SoundCycle clone ()
   {
      SoundCycle c;
      
      c = new SoundCycle( entityRef.get(), token, period, volBot, volTop, chance );
      return c;
   }
}  // class SoundCycle

/**
 * An ambient sound is a compound sound consisting of any number of loop sounds 
 * and cycle sounds. Loop sounds play continuously without interruption, cycle
 * sounds work as described in class SoundCycle. The ambient sound can be played 
 * global or fixed to a map location.  
 */
public static class AmbientSound 
{
   private List<LoopSoundInfo> loopSounds = new ArrayList<LoopSoundInfo>(); 
   private List<SoundCycle> cycleList = new ArrayList<SoundCycle>();

   private String name;
   private SoundObject soundObject;
   private Point2D soundPos;
   private Point2D playerPos;
   private Rectangle2D playerHearing;
   private float loudnessDB;
   private boolean playing; 
   
   /** The LoopSoundInfo stores information which is required to start the 
    *  continuously looping sound elements of this ambient sound. 
    */
   private static class LoopSoundInfo implements Cloneable
   {
      String name; 
      float loudnessDB;
      int delay;
      Clip clip;
      
      public LoopSoundInfo ( String sound, int volume, int delay )
      {
         name = sound;
         loudnessDB = dBValues[ volume ];
         this.delay = delay;
      }
      
      /** Returns a copy of this LoopSoundInfo with <code>clip</code> set
       *  to <b>null</b> (clip not playing).
       */   
      public LoopSoundInfo clone ()
      {
         LoopSoundInfo si;
         
         try { 
            si = (LoopSoundInfo)super.clone();
            si.clip = null;
         }
         catch ( CloneNotSupportedException e )
         { logger.warn("#### bad clone",e); return null; }
         
         return si;
      }
      
      public synchronized void stopClip ()
      {
         if ( clip != null )
         {
            clip.stop();
            clip = null;
         }
      }
   }
   
   private class SoundStarter extends Thread
   {
      LoopSoundInfo soundInfo;
      float correctionDB;
   
      /** Starts a one-time sound.     
      public SoundStarter ( String sound, float loudnessDB, int delay )
      {
         this.sound = sound;
         this.delay = delay;
         this.loudnessDB = loudnessDB;
      }
   */
      /** Starts a looping sound. */   
      public SoundStarter ( LoopSoundInfo loopInfo, float correctionDB )
      {
         this.soundInfo = loopInfo;
         this.correctionDB = correctionDB;
      }
      
      public void run ()
      {
         ClipRunner libClip;
         
         // get the library sound clip
         if ( (libClip = get().getSoundClip( soundInfo.name )) == null )
            throw new IllegalArgumentException( "sound unknown: " + soundInfo.name );
         
         // handle delay phase request on sample start
         if ( soundInfo.delay > 0 )
            try { sleep( soundInfo.delay ); }
            catch ( InterruptedException e )
            {}
         
         synchronized ( soundInfo )
         {
            // terminate an existing sound 
            soundInfo.stopClip();
               
            // start playing
            soundInfo.clip = libClip.getAudioClip( get().volumeSetting,  
                  loudnessDB + soundInfo.loudnessDB + correctionDB );  
            soundInfo.clip.loop( Clip.LOOP_CONTINUOUSLY );
            playing = true;
         }
      }
   }  // SoundStarter
   
   /** 
    * Creates an unlocalized ambient sound (plays everywhere) with the
    * given overall volume setting. 
    * 
    * @param volume int 0..100 loudness of ambient sound in total
    */
   public AmbientSound ( String name, int volume )
   {
      this( name, null, 0, volume );
   }

   /** 
    * Creates a map-localized ambient sound with the
    * given overall volume setting. 
    * 
    * @param name ambient sound name
    * @param point <code>Point2D</code> map location expressed in coordinate units
    * @param radius audibility radius of sound object in coordinate units
    * @param volume int 0..100 loudness of ambient sound in total
    */
   public AmbientSound ( String name, Point2D point, int radius, int volume )
   {
      if ( name == null )
         throw new NullPointerException();
      
      this.name = name;
      soundPos = point;
      loudnessDB = dBValues[ volume ];
      
      if ( soundPos != null )
      {
         soundObject = new SoundObject();
         soundObject.setLocation( soundPos );
         soundObject.setAudibleRange( radius );
      }
   }  // constructor

   /** 
    * Creates a map-localized ambient sound with the
    * given settings and the sound composition taken from the parameter
    * ambient sound. (The paradigm presets content equivalent to <code>addCycle()</code>
    * and <code>addLoop()</code> calls.) 
    * 
    * @param sound <code>AmbientSound</code> as paradigm for sound composition 
    * @param name ambient sound name
    * @param point <code>Point2D</code> map location expressed in coordinate units
    * @param radius audibility radius of sound object in coordinate units
    * @param volume int 0..100 loudness of ambient sound in total
    */
   public AmbientSound ( AmbientSound sound, String name, 
         Point2D point, int radius, int volume )
   {
      this( name, point, radius, volume );
      
      for( LoopSoundInfo c : sound.loopSounds )
         loopSounds.add( c.clone() );
      
      for( SoundCycle c : sound.cycleList )
      {
         SoundCycle cycle = c.clone();
         cycle.entityRef = new WeakReference<Entity>( soundObject );
         cycleList.add( cycle );
      }
      
      logger.debug( "-- created AMBIENT: " + name + " " + loopSounds.size() + 
            " loops, " + cycleList.size() + " cycles" ); 
   }  // constructor

   /** This adds a loop sound to the ambient sound definition.
    *  
    * @param sound library sound name
    * @param volume relative play volume of the added sound
    * @param delay milliseconds of start delay for playing the sound 
    */
   public void addLoop ( String sound, int volume, int delay )
   {
      SoundSystem sys;
      LoopSoundInfo info;
      
      sys = get();
      if ( !sys.contains( sound ) )
      { 
         logger.error( "** Ambient Sound: missing sound definition (" + sound + ")" );
         return;
      }
      
      info = new LoopSoundInfo( sound, volume, delay );
      loopSounds.add( info );
   }  // addLoop
   
   public void addCycle ( 
         String token, 
         int period,
         int volBot,
         int volTop,
         int chance
         )
   {
      SoundCycle cycle;
      
      // sorry, no cycles if not localized
      if ( soundObject == null )
         return;
      
      cycle = new SoundCycle( soundObject, token, period, volBot, volTop, chance );
      cycleList.add( cycle );
   }  // addCycle
   
   private boolean canPlay ()
   {
      return soundPos == null || (playerHearing.contains( soundPos ) && 
             soundObject.getAudibleArea().contains( playerPos ));
   }
   
   /** Starts playing this ambient sound. This will take required actions, 
    * if this ambient sound is not yet playing, to make it audible, global or
    * relative to the player's position depending on this sound's initializer. 
    * This does nothing if this sound is already playing.  Playing is suppressed
    * if sound position is outside the hearing range of the player.  
    */
   protected void play ()
   {
      play( getPlayer() );
   }
   
   /** Starts playing this ambient sound with the given player's
    * hearing parameters. This will take required actions, 
    * if this ambient sound is not yet playing, to make it audible, global or
    * relative to the player's position depending on this sound's initializer. 
    * This does nothing if this sound is already playing. Playing is suppressed
    * if sound position is outside the hearing range of the player.  
    * 
    * @param player the client player object
    */
   protected void play ( Player player )
   {
      LoopSoundInfo soundInfo;
      Iterator it;
      float fogDB;
      
      if ( playing )
         return;
      
      stop();

      // if map-localized
      if ( soundPos != null )
      {
         // adjust to player settings
         if ( player != null )
         {
            playerPos = player.getPosition();
            playerHearing = player.getHearingArea();
            
            // return if sound object is out of range
            if ( !canPlay() )
               return;
         }
         // return undone if no player 
         else 
            return;
      }         
      
      // create and start loop sounds
      synchronized( loopSounds )
      {
         fogDB = getPlayerVolume();
         for ( it = loopSounds.iterator(); it.hasNext(); )
         {
            soundInfo = (LoopSoundInfo)it.next();
            new SoundStarter( soundInfo, fogDB ).start();
         }
      }
      
      // start cycle sounds
      for ( SoundCycle c : cycleList )
      {
         c.play();
      }

      playing = true;
logger.debug( "- playing ambient: " + name );          
   }  // play

   /** (Temporarily) stops playing this ambient sound. */
   protected void stop ()
   {
      Iterator it;
      
      if ( !playing )
         return;
      
      // terminate loop sounds
      synchronized( loopSounds )
      {
         for ( it = loopSounds.iterator(); it.hasNext(); )
         {
            ((LoopSoundInfo)it.next()).stopClip();
         }
      }
      
      // stop cycle sounds
      for ( it = cycleList.iterator(); it.hasNext(); )
      {
         ((SoundCycle)it.next()).stopPlaying();
      }

      playing = false;
logger.debug( "- stopped ambient: " + name );          
   }  // stop
   
   /** Unrevokably terminates this ambient sound. */
   public void terminate ()
   {
      SoundSystem sys;
      Iterator it;
      
      sys = get();
      stop();

      // terminate cycle sounds
      for ( it = cycleList.iterator(); it.hasNext(); )
      {
         ((SoundCycle)it.next()).terminate();
      }
      
      // clear internal sound lists
      loopSounds.clear();
      cycleList.clear();
      
      // remove this object from sound system
      synchronized( sys.ambientList )
      {
         sys.ambientList.remove( this );
      }
      
logger.debug( "- terminated ambient: " + name );          
   }  // terminate
   
   /** Returns the sound volume for this ambient sound relative to the 
    *  current player position (fog correction value). Returns 0.0 if
    *  this sound is not map-localized.
    * 
    *  @return float dB correction of loudness
    */ 
   private float getPlayerVolume ()
   {
      double distance, maxDist;
      int fogVolume;
      
      // if the sound is global (no position)
      if ( soundPos == null )
         return 0;

      // if the sound is map localized
      else
      {
         // maximum fog if no player infos available 
         if ( playerPos == null | playerHearing == null )
         {
            logger.debug( "ambient (" + name + ") fog volume: 0 (player unavailable)" );       
            return dBValues[ 0 ];
         }
         
         // determine sound volume cutoff due to distance (fog value)
         distance = soundPos.distance( playerPos );
         maxDist = playerHearing.getWidth()/2;
         fogVolume = Math.max(0, (int)(95 * (maxDist - distance) / maxDist + 5) );
         return dBValues[ fogVolume ];
      }
   }  // getPlayerVolume
   
   /** 
    * Informs this ambient sound about the actual player's position
    * and hearing parameters. Does nothing if player is <b>null</b>
    * or the sound is not map-localized.
    * (Otherwise this will adjust sound fog loudness.)
    * 
    * @param player client player object (may be <b>null</b>
    */
   public void performPlayerMoved ( Player player )
   {
      SoundSystem sys;
      
      // operation control
      sys = SoundSystem.get();
      if ( !sys.isOperative() | sys.getMute() | player == null | soundPos == null )
         return;
      
      // if not yet playing, start playing 
      if ( !playing )
         play( player );
      
      // if playing, correct loudness/status of clips
      else
      {
         // set new player parameters
         playerPos = player.getPosition();
         playerHearing = player.getHearingArea();
         
         // decide on stopping to play (when sound object has moved out of range)
         if ( !canPlay() )
         {
            stop();
         }
         // or updating sound loudness
         else
         {
            updateVolume();
         }
      }
   }  // performPlayerPosition
   
   public void updateVolume ()
   {
      FloatControl volCtrl;
      LoopSoundInfo info;
      Iterator it;
      float fogDB;
      
      // detect player loudness fog value
      fogDB = getPlayerVolume();
      
      // set corrected volume to all running clips
      synchronized( loopSounds )
      {
         for ( it = loopSounds.iterator(); it.hasNext(); )
         {
            info = ((LoopSoundInfo)it.next());
            if ( info.clip != null )
            {
               volCtrl = (FloatControl) info.clip.getControl( FloatControl.Type.MASTER_GAIN );
               volCtrl.setValue( get().volumeDelta + loudnessDB + info.loudnessDB + fogDB );
            }
         }
      }
   }  // updateVolume

   /** Returns the game Player object or <b>null</b> if unavailable or
    *  the sound is not map-localized.
    *   
    *  @return Player object
    */
   private Player getPlayer ()
   {
      Player player = null;
      RPObject playerObj; 
      
      // try to obtain player parameters if relative playing is ordered
      if ( soundPos != null )
      {
         if ( (playerObj = StendhalClient.get().getPlayer()) != null )
            player = (Player) StendhalClient.get().getGameObjects().get(playerObj.getID());
      }
      return player;
   }
   
   /* 
    * Overridden: @see javax.sound.sampled.LineListener#update(javax.sound.sampled.LineEvent)
    */
/*   
   public void update ( LineEvent event )
   {
      Line clip;
      
      // this discards line resources when the sound has stopped
      if ( event.getType() == LineEvent.Type.STOP )
      {
         clip = ((Line)event.getSource()); 
         clip.close();
      }
   }
*/

}  // class AmbientSound

private static class AmbientStore
{
   static Point2D soundPos = new Point2D.Double( 0, 0 ); 
 
   
   public static AmbientSound getAmbient ( String name )
   {
      AmbientSound ambient = null;
      
      if ( name.equals( "wind-tree-1" ) )
      {
         ambient = new SoundSystem.AmbientSound( 
               name, soundPos, 100, 100 );
         ambient.addLoop( "wind-loop-1", 25, 0 );
         ambient.addLoop( "wind-loop-1", 25, 500 );
         ambient.addLoop( "wind-loop-2", 50, 0 );
         ambient.addCycle( "treecreak-1", 25000, 10, 30, 100 );
      }
      
      else if ( name.equals( "water-beach-1" ) )
      {
         ambient = new SoundSystem.AmbientSound( 
               name, soundPos, 100, 100 );
         ambient.addCycle( "water-splash-1", 20000, 10, 35, 75 );
         ambient.addCycle( "water-splash-2", 60000, 10, 50, 80 );
         ambient.addCycle( "water-wave-1", 60000, 10, 30, 80 );
      }
      
      else if ( name.equals( "meadow-larks-1" ) )
      {
         ambient = new SoundSystem.AmbientSound( 
               name, soundPos, 100, 100 );
         ambient.addCycle( "lark-1", 120000, 10, 40, 80 );
         ambient.addCycle( "lark-2", 120000, 10, 40, 80 );
      }
      
      else if ( name.equals( "water-flow-1" ) )
      {
         ambient = new SoundSystem.AmbientSound( 
               name, soundPos, 100, 100 );
         ambient.addLoop( "water-flow-1", 50, 0 );
      }
      

      return ambient;
   }
}  // class AmbientStore

private String actualZone = "";

/* 
 * Overridden: @see games.stendhal.client.WorldObjects.WorldListener#zoneEntered(java.lang.String)
 */
public void zoneEntered ( String zone )
{
   AmbientSound baseAmb, ambient;
   Point2D soundPos;
   
   logger.debug( "-- SoundSys: ZONE ENTERED: " + zone ); 
   actualZone = zone;
   
   if ( zone.equals( "0_semos_village" ) )
   {
      // creaking tree and wind
      ambient = AmbientStore.getAmbient( "wind-tree-1" );
      
      soundPos = new Point2D.Double( 13, 42 ); 
      ambient = new SoundSystem.AmbientSound( ambient, 
            "semos-village-tree", soundPos, 30, 25 );
      SoundSystem.playAmbientSound( ambient );
   
      // larks
      baseAmb = AmbientStore.getAmbient( "meadow-larks-1" );

      soundPos = new Point2D.Double( 50, 16 ); 
      ambient = new SoundSystem.AmbientSound( baseAmb, 
            "semos-village-larks-1", soundPos, 30, 50 );
      SoundSystem.playAmbientSound( ambient );
      
   }
   else if ( zone.equals( "int_semos_blacksmith" ) )
   {
      
      soundPos = new Point2D.Double( 11, 3 ); 
      ambient = new SoundSystem.AmbientSound( 
            "blacksmith-forgefire-main", soundPos, 30, 50 );
      ambient.addLoop( "forgefire-1", 50, 0 );
//      ambient.addLoop( "wind-loop-1", 25, 0 );
      ambient.addCycle( "firesparks-1", 60000, 10, 50, 80 );
      SoundSystem.playAmbientSound( ambient );
      
      soundPos = new Point2D.Double( 3, 3 );
      ambient = new SoundSystem.AmbientSound( 
            "blacksmith-forgefire-side", soundPos, 6, 50 );
      ambient.addLoop( "forgefire-2", 50, 0 );
      ambient.addLoop( "forgefire-3", 50, 0 );
      SoundSystem.playAmbientSound( ambient );
      
   }
   else if ( zone.equals( "0_semos_road_ados" ) )
   {
      // creaking tree and wind
      baseAmb = AmbientStore.getAmbient( "wind-tree-1" );

      soundPos = new Point2D.Double( 10, 45 ); 
      ambient = new SoundSystem.AmbientSound( baseAmb, 
            "road-ados-tree-1", soundPos, 30, 30 );
      SoundSystem.playAmbientSound( ambient );

      soundPos = new Point2D.Double( 54, 59 ); 
      ambient = new SoundSystem.AmbientSound( baseAmb, 
            "road-ados-tree-2", soundPos, 100, 50 );
      SoundSystem.playAmbientSound( ambient );

      soundPos = new Point2D.Double( 65, 31 ); 
      ambient = new SoundSystem.AmbientSound( baseAmb, 
            "road-ados-tree-3", soundPos, 100, 30 );
      SoundSystem.playAmbientSound( ambient );

      // beach water
      baseAmb = AmbientStore.getAmbient( "water-beach-1" );

      soundPos = new Point2D.Double( 32, 46 ); 
      ambient = new SoundSystem.AmbientSound( baseAmb, 
            "road-ados-beachwater-1", soundPos, 7, 25 );
      SoundSystem.playAmbientSound( ambient );

      soundPos = new Point2D.Double( 43, 47 ); 
      ambient = new SoundSystem.AmbientSound( baseAmb, 
            "road-ados-beachwater-2", soundPos, 7, 25 );
      SoundSystem.playAmbientSound( ambient );

      soundPos = new Point2D.Double( 32, 55 ); 
      ambient = new SoundSystem.AmbientSound( baseAmb, 
            "road-ados-beachwater-3", soundPos, 12, 35 );
      SoundSystem.playAmbientSound( ambient );

      // larks
      baseAmb = AmbientStore.getAmbient( "meadow-larks-1" );

      soundPos = new Point2D.Double( 15, 15 ); 
      ambient = new SoundSystem.AmbientSound( baseAmb, 
            "road-ados-larks-1", soundPos, 30, 50 );
      SoundSystem.playAmbientSound( ambient );
      
      soundPos = new Point2D.Double( 32, 33 ); 
      ambient = new SoundSystem.AmbientSound( baseAmb, 
            "road-ados-larks-2", soundPos, 30, 50 );
      SoundSystem.playAmbientSound( ambient );

      // water at bridge
      baseAmb = AmbientStore.getAmbient( "water-flow-1" );

      soundPos = new Point2D.Double( 47, 47 ); 
      ambient = new SoundSystem.AmbientSound( baseAmb, 
            "road-ados-bridge-1", soundPos, 3, 50 );
      SoundSystem.playAmbientSound( ambient );
      
   }
}

/* 
 * Overridden: @see games.stendhal.client.WorldObjects.WorldListener#zoneLeft(java.lang.String)
 */
public void zoneLeft ( String zone )
{
   logger.debug( "-- SoundSys: ZONE LEFT: " + zone ); 
   if ( zone.equals( actualZone ) )
      clearAmbientSounds();
}

/* 
 * Overridden: @see games.stendhal.client.WorldObjects.WorldListener#playerMoved(games.stendhal.client.entity.Player)
 */
public void playerMoved ( Player player )
{
   // update ambient sounds about player position
   synchronized ( ambientList )
   {
      for ( AmbientSound a : ambientList )
      {
         a.performPlayerMoved( player );
      }
   }
}

}
