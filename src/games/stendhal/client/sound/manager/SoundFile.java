/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.sound.manager;

import games.stendhal.client.sound.system.processors.Recorder;
import games.stendhal.client.sound.system.processors.OggVorbisDecoder;
import games.stendhal.client.sound.system.processors.PCMStreamConverter;
import games.stendhal.client.sound.system.SignalProcessor;

import games.stendhal.common.resource.Resource;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author silvio
 */
public class SoundFile extends SignalProcessor implements Cloneable
{
    public static enum Type { OGG, WAV }

	private int                   mNumChannels;
    private int                   mSampleRate;
	private SignalProcessor       mGenerator = null;
	private Recorder              mRecorder  = null;
	private final Resource        mResource;
	private final boolean         mEnableStreaming;
    private final int             mOutputNumSamples;
    private final Type            mFileType;
	private final SignalProcessor mPropagator = new SignalProcessor()
	{
		@Override
		protected void modify(float[] data, int samples, int channels, int rate)
		{
			SoundFile.this.propagate(data, samples, channels, rate);
		}

		@Override
		protected void finished()
		{
			SoundFile.this.quit();
		}
	};

	@SuppressWarnings("empty-statement")
    public SoundFile(Resource resource, Type fileType, int outputNumSamplesPerChannel, boolean enableStreaming) throws IOException
    {
		assert resource != null;
		assert fileType != null;

		if(!resource.exists())
            throw new IOException("audio resource doesn't exist: " + resource.getURI());

		mResource         = resource;
        mFileType         = fileType;
        mEnableStreaming  = enableStreaming;
        mOutputNumSamples = outputNumSamplesPerChannel;

        SignalProcessor decoder = chooseDecoder(resource, fileType, outputNumSamplesPerChannel);

        if(decoder == null)
            throw new IOException("could not load audio resource: " + resource.getURI());

        if(enableStreaming)
        {
            decoder.connectTo(mPropagator, true);
            mGenerator = decoder;
        }
        else
        {
            mRecorder = new Recorder();
            mRecorder.connectTo(decoder, false);

            while(mRecorder.request())
                /* pass */;

            mRecorder.disconnect();
            mRecorder.trim();

			Recorder.Player player = mRecorder.createPlayer(outputNumSamplesPerChannel);
			player.connectTo(mPropagator, true);
			
            mGenerator = player;
        }
    }

	private SoundFile(Recorder recorder, Resource resource, Type fileType, int outputNumSamplesPerChannel)
	{
		mResource         = resource;
		mFileType         = fileType;
		mOutputNumSamples = outputNumSamplesPerChannel;
		mEnableStreaming  = false;
		mRecorder         = recorder;
		mGenerator        = recorder.createPlayer(outputNumSamplesPerChannel);
		
		mGenerator.connectTo(mPropagator, true);
	}

    public int getNumChannels() { return mNumChannels; }
    public int getSampleRate () { return mSampleRate;  }

	public void close()
	{
		if(mGenerator instanceof Recorder.Player)
		{
			mRecorder.clear();
		}
		else if(mGenerator instanceof OggVorbisDecoder)
		{
			OggVorbisDecoder decoder = (OggVorbisDecoder)mGenerator;
			decoder.close();
		}
		else if(mGenerator instanceof PCMStreamConverter)
		{
			PCMStreamConverter converter = (PCMStreamConverter)mGenerator;
			converter.close();
		}
	}

    @Override
    public SoundFile clone()
    {
        SoundFile file = null;

		if(mGenerator instanceof Recorder.Player)
		{
			file = new SoundFile(mRecorder, mResource, mFileType, mOutputNumSamples);
		}
		else
		{
			try
			{
				file = new SoundFile(mResource, mFileType, mOutputNumSamples, mEnableStreaming);
			}
			catch(IOException exception)
			{
				assert false: "catched exception - " + exception;
			}
		}
        return file;
    }

    public void restart()
    {
        if(mGenerator instanceof OggVorbisDecoder)
        {
            OggVorbisDecoder decoder = (OggVorbisDecoder)mGenerator;
            decoder.close();

            try
            {
                decoder.open(mResource.getInputStream(), 256, mOutputNumSamples);
            }
            catch(IOException exception) { }
            
        }
        else if(mGenerator instanceof PCMStreamConverter)
        {
            PCMStreamConverter converter = (PCMStreamConverter)mGenerator;
            converter.close();

            try
            {
                AudioInputStream stream = AudioSystem.getAudioInputStream(mResource.getInputStream());
                converter.open(stream, stream.getFormat(), mOutputNumSamples);
            }
            catch(IOException                   exception) { }
            catch(UnsupportedAudioFileException exception) { }
        }
		else if(mGenerator instanceof Recorder.Player)
		{
			Recorder.Player player = (Recorder.Player)mGenerator;
			player.restart();
		}
    }

	private SignalProcessor chooseDecoder(Resource resource, Type fileType, int outputNumSamplesPerChannel)
    {
        SignalProcessor decoder = null;

        switch(fileType)
        {
        case OGG:
            try
            {
                InputStream stream = resource.getInputStream();

				if(stream != null)
				{
					OggVorbisDecoder oggdec = new OggVorbisDecoder(stream, 256, outputNumSamplesPerChannel);

					mNumChannels = oggdec.getNumChannels();
					mSampleRate  = oggdec.getSampleRate();
					decoder      = oggdec;
				}
            }
            catch(Exception exception) { decoder = null; }
            break;

        case WAV:
            try
            {
                AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(resource.getInputStream()));
                decoder                 = new PCMStreamConverter(stream, stream.getFormat(), outputNumSamplesPerChannel);

                mNumChannels = stream.getFormat().getChannels();
                mSampleRate  = (int)stream.getFormat().getSampleRate();
            }
            catch(Exception exception) { decoder = null; }
            break;
        }

        return decoder;
    }

    @Override
    protected boolean generate()
    {
        return mPropagator.request();
    }
}