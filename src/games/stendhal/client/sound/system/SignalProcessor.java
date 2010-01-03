/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.system;


/**
 * Every class that either wants to modify or generate a stream of
 * pcm audio data should derive from this class
 * <p>
 * Each SignalProcessor is a chain link in a doubly linked list, a processing chain.
 * The first SignalProcessor in such a chain should always generate audio data
 * otherwise no data will arrive at the quit of the chain
 *
 * @author silvio
 */
public abstract class SignalProcessor
{
/*    private static class ChainIterator implements ListIterator<SignalProcessor>
    {
        SignalProcessor mCurrent;
        SignalProcessor mLastReturned;
        int             mIndex;

        private ChainIterator(SignalProcessor begin, int index)
        {
            mLastReturned = null;
            mCurrent      = begin;
            mIndex        = index;
        }

        public boolean hasNext      () { return mCurrent != null; }
        public boolean hasPrevious  () { return mCurrent != null; }
        public int     nextIndex    () { return mIndex;           }
        public int     previousIndex() { return mIndex - 1;       }

        public SignalProcessor next()
        {
            assert mCurrent != null;
            
            mLastReturned = mCurrent;
            mCurrent      = mCurrent.mNext;
            ++mIndex;

            return mLastReturned;
        }

        public SignalProcessor previous()
        {
            assert mCurrent != null;

            mLastReturned = mCurrent;
            mCurrent      = mCurrent.mPrev;
            --mIndex;
            
            return mLastReturned;
        }

        public void remove()
        {
            assert mLastReturned != null;
            mLastReturned.disconnect();
        }

        public void set(SignalProcessor processor)
        {
            assert mLastReturned != null;
            mLastReturned.replace(processor);
        }

        public void add(SignalProcessor processor)
        {
            assert mLastReturned != null;
            mLastReturned.insert(processor, true);
        }
    }

    public static class Chain extends AbstractSequentialList<SignalProcessor>
    {
        private SignalProcessor mFirst = null;

        public Chain(SignalProcessor first) { mFirst = first; }

        public Chain(SignalProcessor ...processors)
        {
            int l = processors.length;

            if(l >= 1)
                mFirst = processors[0];

            if(l >= 2)
            {
                for(int i=1; i<(l - 1); ++i)
                    processors[i].insert(processors[i-1], false);

                processors[0  ].connectTo(processors[1  ], true);
                processors[l-1].connectTo(processors[l-2], false);
            }
        }

        @Override
        public ListIterator<SignalProcessor> listIterator(int startIndex)
        {
            int             index = 0;
            SignalProcessor begin = mFirst;

            for(int i=0; i<startIndex && (begin != null); ++i)
            {
                begin = begin.mNext;
                ++index;
            }
            
            return new ChainIterator(begin, index);
        }

        @Override
        public int size()
        {
            int             size  = 0;
            SignalProcessor begin = mFirst;

            while(begin != null)
            {
                begin = begin.mNext;
                ++size;
            }
            
            return size;
        }
    }//*/

    private SignalProcessor mNext = null;
    private SignalProcessor mPrev = null;

    /**
     * Connects this SignalProcessor to the next one
     * (it is inserted in the processing chain before nextProcessor)
     *
     * @param processor nextProcessor
     */
    public final synchronized void insert(SignalProcessor processor, boolean before)
    {
        assert processor != this;

        if(mNext != null) { mNext.mPrev = mPrev; }
        if(mPrev != null) { mPrev.mNext = mNext; }

        if(processor != null)
        {
            if(before)
            {
                mNext = processor;
                mPrev = processor.mPrev;

                if(processor.mPrev != null)
                    processor.mPrev.mNext = this;

                processor.mPrev = this;
            }
            else
            {
                mNext = processor.mNext;
                mPrev = processor;

                if(processor.mNext != null)
                    processor.mNext.mPrev = this;

                processor.mNext = this;
            }
        }
        else
        {
            mNext = null;
            mPrev = null;
        }
    }

    public final synchronized void connectTo(SignalProcessor processor, boolean before)
    {
        if(before)
        {
            if(mNext == processor)
                return;

            if(mNext           != null) { mNext.mPrev           = null; }
            if(processor.mPrev != null) { processor.mPrev.mNext = null; }

            mNext           = processor;
            processor.mPrev = this;
        }
        else
        {
            if(mPrev == processor)
                return;

            if(mPrev           != null) { mPrev.mNext           = null; }
            if(processor.mNext != null) { processor.mNext.mPrev = null; }

            mPrev           = processor;
            processor.mNext = this;
        }
    }

    public final synchronized void split(boolean before)
    {
        if(before)
        {
            if(mPrev != null)
                mPrev.mNext = null;

            mPrev = null;
        }
        else
        {
            if(mNext != null)
                mNext.mPrev = null;

            mNext = null;
        }
    }

    /**
     * Replaces this SignalProcessor with "processor" in the processing chain
     * it is a member of
     * 
     * @param processor
     */
    public final synchronized void replace(SignalProcessor processor)
    {
        if(processor == this)
            return;

        if(processor != null)
        {
            processor.disconnect();
            processor.mNext = mNext;
            processor.mPrev = mPrev;
        }
        else
        {
            disconnect();
        }
    }

    /**
     * Removes this SignalProcessor fom the processing chain
     */
    public final void disconnect()
    {
        insert(null, true);
    }
    
    /**
     * This function should be called from a derived class
     * to propagate the modified audio data to the next SignalProcessor in the chain
     *
     * @param data
     * @param samples
     * @param channels
     * @param rate
     */
    protected final synchronized void propagate(float[] data, int samples, int channels, int rate)
    {
        if(mNext != null)
            mNext.modify(data, samples, channels, rate);
    }

    public final synchronized void quit()
    {
        if(mNext != null)
            mNext.finished();
    }

    /**
     * This will call the generate() method of the first
     * SignalProcessor in the processing chain
     */
    public synchronized boolean request()
    {
        if(mPrev != null)
            return mPrev.request();

        return generate();
    }
    
    /**
     * This function should be overwritten by all classes that want to
     * modify an pcm audio stream. The audio data is uniform and interleaved
     * uniform:     Each sample has a value between -1.0 and 1.0
     * interleaved: The channels are not sepperated. They are bundled in frames
     *              e.g. if we stereo pcm data:
     *              data[0] and data[1] are the left and right channels of sample 0
     *              data[2] and data[3] are the left and right channels of sample 1
     *              data[4] and data[5] are the left and right channels of sample 2
     *              and so on ...
     *
     * @param data     the audio data
     * @param samples  the number of samples per channel contained in data
     * @param channels number of channels
     * @param rate     the sample rate
     */
    protected void modify(float[] data, int samples, int channels, int rate)
    {
        propagate(data, samples, channels, rate);
    }

    /**
     * This function should be overwritten by all classes that want to
     * generate an pcm audio stream e.g. a mp3 decoder, a frequency generator, ...
     */
    protected boolean generate() { return false; }

    protected void finished() { quit(); }

    /**
     * This function will create a processing chain by connecting any number
     * of SignalProcessors together
     * 
     * @param processors any number of SignalProcessors to insert together
     */
    public static void/*Chain*/ createChain(SignalProcessor ...processors)
    {
        int l = processors.length;

        if(l >= 2)
        {
            for(int i=1; i<(l - 1); ++i)
                processors[i].insert(processors[i-1], false);

            processors[0  ].connectTo(processors[1  ], true);
            processors[l-1].connectTo(processors[l-2], false);
        }

        //if(l >= 1)
        //    return new Chain(processors[0]);

        //return null;
    }
}
