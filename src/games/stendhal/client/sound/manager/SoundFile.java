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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import games.stendhal.client.sound.facade.SoundFileType;
import games.stendhal.client.sound.system.SignalProcessor;
import games.stendhal.client.sound.system.processors.OggVorbisDecoder;
import games.stendhal.client.sound.system.processors.PCMStreamConverter;
import games.stendhal.client.sound.system.processors.Recorder;

/**
 *
 * @author silvio
 */
public class SoundFile extends SignalProcessor implements Cloneable {

    private int                   mNumChannels;
    private int                   mSampleRate;
	private SignalProcessor       mGenerator = null;
	private Recorder              mRecorder  = null;
	private final AudioResource        mAudioResource;
	private final boolean         mEnableStreaming;
    private final int             mOutputNumSamples;
    private final SoundFileType            mFileType;

	private final SignalProcessor mPropagator = new SignalProcessor()
	{
		@Override
		protected void modify(float[] data, int frames, int channels, int rate)
		{
			SoundFile.this.propagate(data, frames, channels, rate);
		}

		@Override
		protected void finished()
		{
			SoundFile.this.quit();
		}
	};

    public SoundFile(AudioResource audioResource, SoundFileType fileType, int outputNumSamplesPerChannel, boolean enableStreaming) throws IOException
    {

    	InputStream stream = audioResource.getInputStream();
		if(stream == null) {
            throw new IOException("audio AudioResource doesn't exist: " + audioResource.getName());
		}

		mAudioResource         = audioResource;
        mFileType         = fileType;
        mEnableStreaming  = enableStreaming;
        mOutputNumSamples = outputNumSamplesPerChannel;

        SignalProcessor decoder = chooseDecoder(stream, fileType, outputNumSamplesPerChannel);

        if(decoder == null) {
			throw new IOException("could not load audio AudioResource: " + audioResource.getName());
		}

        if(enableStreaming)
        {
            decoder.connectTo(mPropagator, true);
            mGenerator = decoder;
        }
        else
        {
            mRecorder = new Recorder();
            mRecorder.connectTo(decoder, false);

            while (mRecorder.request()) {
    			// sleep(1) causes freezes, see #arianne log of 2012-07-03
    			/* try {
    				Thread.sleep(1);
    			} catch (InterruptedException e) {
    				logger.error(e, e);
    			} */
            }

            mRecorder.disconnect();
            mRecorder.trim();

			Recorder.Player player = mRecorder.createPlayer(outputNumSamplesPerChannel);
			player.connectTo(mPropagator, true);

            mGenerator = player;
        }
    }

	private SoundFile(Recorder recorder, AudioResource audioResource, SoundFileType fileType, int outputNumSamplesPerChannel)
	{
		mAudioResource         = audioResource;
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
			file = new SoundFile(mRecorder, mAudioResource, mFileType, mOutputNumSamples);
		}
		else
		{
			try
			{
				file = new SoundFile(mAudioResource, mFileType, mOutputNumSamples, mEnableStreaming);
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
                decoder.open(mAudioResource.getInputStream(), 256, mOutputNumSamples);
            }
            catch(IOException exception) { }

        }
        else if(mGenerator instanceof PCMStreamConverter)
        {
            PCMStreamConverter converter = (PCMStreamConverter)mGenerator;
            converter.close();

            try
            {
                AudioInputStream stream = AudioSystem.getAudioInputStream(mAudioResource.getInputStream());
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

	private SignalProcessor chooseDecoder(InputStream stream, SoundFileType fileType, int outputNumSamplesPerChannel)
    {
        SignalProcessor decoder = null;

        switch(fileType)
        {
        case OGG:
            try
            {
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
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(stream));
                decoder                 = new PCMStreamConverter(stream, audioStream.getFormat(), outputNumSamplesPerChannel);

                mNumChannels = audioStream.getFormat().getChannels();
                mSampleRate  = (int)audioStream.getFormat().getSampleRate();
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
