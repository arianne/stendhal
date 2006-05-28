/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.sound;

import games.stendhal.common.Rand;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.UnsupportedAudioFileException;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 * Cliprunner encapsulates a sound clip. This clip consists of one or
 * more alternative samples.
 * 
 * @author Jane Hunt
 */
public class ClipRunner implements LineListener
{
  /** the logger */
  private static final Logger logger = Log4J.getLogger(ClipRunner.class);
  /** Base sound coordinator */
  private SoundSystem         system;
  /** name of this clip */
  private String              text;
  /** length */
  private long                maxLength;
  /** sound samples */
  private List<AudioClip>    samples;


  /**
   * Creates a ClipRunner instance by name. Volume setting is set to 100%.
   * 
   * @param name
   *          name of sound (docu)
   * @throws UnsupportedAudioFileException
   */
  public ClipRunner(SoundSystem system, String text)
  {
    this.text = text;
    this.system = system;
    samples = new ArrayList<AudioClip>();
  }

  /**
   * Adds another clip as an alternate sound to be run under this clip.
   * Alternative sounds are played by random and equal chance.
   * 
   * @param clip
   *          alternate sound clip
   * @throws UnsupportedAudioFileException
   */
  public void addSample(AudioClip clip)
  {
    samples.add(clip);
    maxLength = Math.max(maxLength, clip.getLength());
  }

  /**
   * The maximum play length of this clip in milliseconds.
   * 
   * @return long milliseconds, 0 if undefined
   */
  public long maxPlayLength()
  {
    return maxLength;
  }

  /**
   * Starts this clip to play with the given volume settings.
   * 
   * @param volume
   *          loudness in 0 .. 100
   * @param correctionDB
   *          decibel correction value from outward sources
   * @return the AudioSystem <code>DataLine</code> object that is being
   *         played, or <b>null</b> on error
   */
  public DataLine play(int volume, float correctionDB)
  {
    DataLine line = getAudioClip(volume, correctionDB);

    if (line != null)
    {
      line.start();
    }
    return line;
  }

  /**
   * Starts this clip to loop endlessly with the given start volume setting.
   * 
   * @param volume
   *          loudness in 0 .. 100
   * @return the AudioSystem <code>Clip</code> object that is being played, or
   *         <b>null</b> on error
   */
  public Clip loop(int volume, float correctionDB)
  {
    Clip line = getAudioClip(volume, correctionDB);

    if (line != null)
    {
      line.loop(Clip.LOOP_CONTINUOUSLY);
    }
    return line;
  }

  /**
   * Returns a runnable AudioSystem sound clip with the given volume settings.
   * 
   * @param volume
   *          loudness in 0 .. 100
   * @param correctionDB
   *          decibel correction value from outward sources
   * @return an AudioSystem sound <code>Clip</code> that represents this
   *         sound, or <b>null</b> on error
   */
  public Clip getAudioClip(int volume, float correctionDB)
  {
//    logger.error("getting audioclip for "+text+" ("+samples.size()+")");
    if (samples.size() > 0)
    {
     try
      {
        int index = Rand.rand(samples.size());
        AudioClip audioClip = samples.get(index);
//        logger.error("get audioclip "+audioClip);

        // Obtain and open the line.
        Clip line = audioClip.openLine();
//        logger.error("line = "+line);
        if (line == null)
        {
          // well...line not supported
          return null;
        }

        // set the volume
        FloatControl volCtrl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        if (volCtrl != null)
        {
          float dB = SoundSystem.dBValues[volume] + SoundSystem.dBValues[audioClip.getVolume()] + correctionDB;
          volCtrl.setValue(dB + system.getVolumeDelta());
        }
        else
        {
          logger.info("no master gain for line "+line.getLineInfo());
        }

        // run clip
        line.addLineListener(this);
        return line;
      } catch (Exception ex)
      {
        logger.error("** AudioSystem: clip line unavailable for: " + this.text, ex);
        return null;
      }
    }
    
    // no samples...
    return null;
  }

  /*
   * Overridden:
   * 
   * @see javax.sound.sampled.LineListener#update(javax.sound.sampled.LineEvent)
   */
  public void update(LineEvent event)
  {
    // this discards line resources when the sound has stopped
    if (event.getType() == LineEvent.Type.STOP)
    {
      ((Line) event.getSource()).close();
    }
  }

}