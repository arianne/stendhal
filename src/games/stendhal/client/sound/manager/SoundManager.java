/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.manager;

import games.stendhal.client.sound.system.SignalProcessor;
import games.stendhal.client.sound.system.SoundSystem;
import games.stendhal.client.sound.system.SoundSystemException;
import games.stendhal.client.sound.system.Time;
import games.stendhal.client.sound.system.processors.DirectedSound;
import games.stendhal.client.sound.system.processors.Interruptor;
import games.stendhal.client.sound.system.processors.SoundLayers;
import games.stendhal.client.sound.system.processors.VolumeAdjustor;
import games.stendhal.common.math.Algebra;

import games.stendhal.common.resource.File;
import games.stendhal.common.resource.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.sound.sampled.AudioFormat;

import org.apache.log4j.Logger;

/**
 *
 * @author silvio
 */
public class SoundManager
{
    private static Logger logger = Logger.getLogger(SoundManager.class);

    private static SoundManager instance;

    private final static int                 OUTPUT_NUM_SAMPLES       = 256;
	private final static int                 USE_NUM_MIXER_LINES      = 15;
    private final static int                 DIMENSION                = 2;
    private final static float[]             HEARER_LOOKONG_DIRECTION = { 0.0f, 1.0f };
    private final static AudioFormat         AUDIO_FORMAT             = new AudioFormat(44100, 16, 2, true, false);
    public  final static InfiniteAudibleArea INFINITE_AUDIBLE_AREA    = new InfiniteAudibleArea();
    public  final static Time                ZERO_DURATION            = new Time();

    public final static class Sound implements Cloneable
    {
        final AtomicReference<SoundFile>    file    = new AtomicReference<SoundFile>(null);
        final AtomicReference<SoundChannel> channel = new AtomicReference<SoundChannel>(null);

		@Override
		public Sound clone()
		{
			Sound sound = new Sound();
			sound.file.set(file.get().clone());
			
			return sound;
		}

        public boolean isActive() { return channel.get() != null && channel.get().isActive(); }
    }

    private final class SoundChannel extends SignalProcessor
    {
        final float[]                      mSoundPosition = new float[DIMENSION];
        final AtomicBoolean                mAutoRepeat    = new AtomicBoolean(false);
        final AtomicBoolean                mIsActive      = new AtomicBoolean(false);
        final AtomicReference<AudibleArea> mAudibleArea   = new AtomicReference<AudibleArea>(INFINITE_AUDIBLE_AREA);
        final Interruptor                  mInterruptor   = new Interruptor();
        final DirectedSound                mDirectedSound = new DirectedSound();
        final VolumeAdjustor               mGlobalVolume  = new VolumeAdjustor();
        SoundLayers.VolumeAdjustor         mLayerVolume   = null;
        SoundSystem.Output                 mOutput        = null;
        Sound                              mSound         = null;

        SoundChannel()
        {
            mOutput      = mSoundSystem.openOutput(AUDIO_FORMAT);
            mLayerVolume = mSoundLayers.createVolumeAdjustor(0);

            SignalProcessor.createChain(mInterruptor, this, mLayerVolume, mGlobalVolume, mDirectedSound, mOutput);
        }

        boolean isActive      ()                            { return mIsActive.get();                      }
        void    setAutoRepeat (boolean repeat)              { mAutoRepeat.set(repeat);                     }
        void    setVolume     (float volume)                { mGlobalVolume.setVolume(volume);             }
        void    startFading   (float volume, Time duration) { mGlobalVolume.startFading(volume, duration); }
        void    setLayer      (int level)                   { mLayerVolume.setLayer(level);                }
        void    setAudibleArea(AudibleArea area)            { mAudibleArea.set(area);                      }
        void    resumePlayback()                            { mInterruptor.play();                         }

        void playSound(Sound newSound, Time time)
        {
            if(mSound != null)
            {
                mSound.file.get().disconnect();
                mSound.file.get().restart();
                mSound.channel.set(null);
                mLayerVolume.setIntensity(0.0f);
            }

            if(newSound != null)
            {
                if(time == null)
                    time = ZERO_DURATION;
                
                mInterruptor.play();
                mGlobalVolume.setVolume(0.0f);
                mGlobalVolume.startFading(1.0f, time);
                newSound.channel.set(this);
                newSound.file.get().connectTo(mInterruptor, true);
            }

            mSound = newSound;
            mIsActive.set(newSound != null);
        }

        void stopPlayback(Time time)
        {
            mAutoRepeat.set(false);
            mGlobalVolume.startFading(0.0f, time);
            mInterruptor.stop(time);
        }
        
        void update()
        {
            float intensity = mAudibleArea.get().getHearingIntensity(mHearerPosition);
            mAudibleArea.get().getClosestPoint(mSoundPosition, mHearerPosition);
            mDirectedSound.setPositions2D(mSoundPosition, mHearerPosition, HEARER_LOOKONG_DIRECTION, intensity);
            mLayerVolume.setIntensity(intensity);
        }

        @Override
        protected void finished()
        {
            if(mAutoRepeat.get())
            {
                mSound.file.get().restart();
            }
            else
            {
                playSound(null, null);
                super.quit();
            }
        }
    }
    
    private final HashMap<String,Sound>    mSounds         = new HashMap<String,Sound>();
    private final LinkedList<SoundChannel> mChannels       = new LinkedList<SoundChannel>();
    private final float[]                  mHearerPosition = new float[DIMENSION];
    private final SoundLayers              mSoundLayers    = new SoundLayers();
    private SoundSystem                    mSoundSystem    = null;

    /**
     * gets the sound manager
     *
     * @return SoundManager
     */
	@Deprecated
    public static SoundManager get()
    {
        if (instance == null) {
           instance = new SoundManager();
        }
        return instance;
    }

    protected SoundManager()
    {
        Algebra.mov_Vecf(mHearerPosition, 0.0f);

        try
        {
            mSoundSystem = new SoundSystem(AUDIO_FORMAT, new Time(70, Time.Unit.MILLI), USE_NUM_MIXER_LINES);
            mSoundSystem.setDaemon(true);
            mSoundSystem.start();
        }
        catch(SoundSystemException exception)
        {
            mSoundSystem = null;
            // TODO: write error message into logger
        }
    }

    public boolean isSoundSystemRunnig()
    {
        return mSoundSystem != null;
    }

	@Deprecated
    public void openSoundFile(String filePath, String soundName)
    {
        SoundFile.Type fileType = null;

        if(filePath.matches(".*\\.wav"))
            fileType = SoundFile.Type.WAV;
        else if(filePath.matches(".*\\.ogg"))
            fileType = SoundFile.Type.OGG;
        else
        {
            assert false: "could not open audio file - unknown file name extension";
            return;
        }

        try
        {
            SoundFile file  = new SoundFile(new File(filePath, true), fileType, OUTPUT_NUM_SAMPLES, true);
            Sound     sound = new Sound();
            sound.file.set(file);

            mSounds.put(soundName, sound);
        }
        catch(IOException exception)
        {
            logger.error(exception, exception);
        }
    }
	
	public Sound openSound(Resource resource, SoundFile.Type fileType)
    {
		Sound sound = null;

        try
        {
            SoundFile file = new SoundFile(resource, fileType, OUTPUT_NUM_SAMPLES, true);

            sound = new Sound();
            sound.file.set(file);
        }
        catch(IOException exception)
		{
			assert false: exception;
			return null;
		}

		return sound;
    }

	@Deprecated
    public void closeSoundFile(String soundName)
    {
        Sound sound = mSounds.get(soundName);

        if(sound != null)
        {
            if(sound.channel != null)
                sound.channel.get().stopPlayback(ZERO_DURATION);

            mSounds.remove(soundName);
        }
    }

    public void setHearerPosition(float[] position)
    {
        Algebra.mov_Vecf(mHearerPosition, position);
    }

    public void update()
    {
        for(SoundChannel channel: mChannels)
        {
            if(channel.isActive())
                channel.update();
        }
    }

	@Deprecated
    public void play(String soundName, int layerLevel, AudibleArea area, boolean autoRepeat, Time fadeInDuration)
    {
        Sound sound = mSounds.get(soundName);
        
        if(sound == null)
            return;

        if(sound.isActive())
        {
            SoundChannel channel = sound.channel.get();
            channel.setAutoRepeat(autoRepeat);
            channel.startFading(1.0f, fadeInDuration);
            channel.setLayer(layerLevel);
            channel.setAudibleArea(area);
            channel.resumePlayback();
            channel.update();
        }
        else
        {
            SoundChannel channel = findInactiveChannel();
            channel.setAutoRepeat(autoRepeat);
            channel.setLayer(layerLevel);
            channel.setAudibleArea(area);
            channel.playSound(sound, fadeInDuration);
            channel.update();
        }
    }

	@Deprecated
    public void stop(String soundName, Time fadeOutDuration)
    {
        Sound sound = mSounds.get(soundName);

        if(sound != null && sound.isActive())
            sound.channel.get().stopPlayback(fadeOutDuration);
    }

	@Deprecated
    public void changeVolume(String soundName, float volume)
    {
        Sound sound = mSounds.get(soundName);

        if(sound != null && sound.isActive())
            sound.channel.get().setVolume(volume);
    }

	@Deprecated
    public void changeLayer(String soundName, int layerLevel)
    {
        Sound sound = mSounds.get(soundName);

        if(sound != null && sound.isActive())
            sound.channel.get().setLayer(layerLevel);
    }

	@Deprecated
    public void changeAudibleArea(String soundName, AudibleArea area)
    {
        Sound sound = mSounds.get(soundName);

        if(sound != null && sound.isActive())
            sound.channel.get().setAudibleArea(area);
    }

    /**
     * checks whether the specified sound name is defined
     *
     * @param soundName name of sound
     * @return true, if it is defined; false otherwise
     */
	@Deprecated
    public boolean hasSoundName(String soundName) {
        return mSounds.get(soundName) != null;
    }

	public void play(Sound sound, float volume, int layerLevel, AudibleArea area, boolean autoRepeat, Time fadeInDuration)
    {
        if(sound == null)
            return;

        if(sound.isActive())
        {
            SoundChannel channel = sound.channel.get();
            channel.setAutoRepeat(autoRepeat);
            channel.startFading(1.0f, fadeInDuration);
			channel.setVolume(volume);
            channel.setLayer(layerLevel);
            channel.setAudibleArea(area);
            channel.resumePlayback();
			channel.update();
        }
        else
        {
            SoundChannel channel = findInactiveChannel();
            channel.setAutoRepeat(autoRepeat);
			channel.setVolume(volume);
            channel.setLayer(layerLevel);
            channel.setAudibleArea(area);
            channel.playSound(sound, fadeInDuration);
			channel.update();
        }
    }

    public void stop(Sound sound, Time fadeOutDuration)
    {
        if(sound != null && sound.isActive())
            sound.channel.get().stopPlayback(fadeOutDuration);
    }

    public void changeVolume(Sound sound, float volume)
    {
        if(sound != null && sound.isActive())
            sound.channel.get().setVolume(volume);
    }

    public void changeLayer(Sound sound, int layerLevel)
    {
        if(sound != null && sound.isActive())
            sound.channel.get().setLayer(layerLevel);
    }

    public void changeAudibleArea(Sound sound, AudibleArea area)
    {
        if(sound != null && sound.isActive())
            sound.channel.get().setAudibleArea(area);
    }
	
    public void close()
    {
        if(isSoundSystemRunnig())
        {
            mSoundSystem.close();

            try
            {
                mSoundSystem.join();
            }
            catch(InterruptedException exception) { }
        }
    }

    private SoundChannel findInactiveChannel()
    {
        SoundChannel foundChannel = null;

        for(SoundChannel channel: mChannels)
        {
            if(!channel.isActive())
			{
                foundChannel = channel;
				break;
			}
        }

        if(foundChannel == null)
		{
            mChannels.add(new SoundChannel());
            foundChannel = mChannels.getLast();
        }

        return foundChannel;
    }
}
