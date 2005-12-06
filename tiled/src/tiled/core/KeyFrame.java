/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 */
 
package tiled.core;

public class KeyFrame
{
    public static final int KEY_LOOP    = 0x01;
    public static final int KEY_STOP    = 0x02;
    public static final int KEY_AUTO    = 0x04;
    public static final int KEY_REVERSE = 0x08;

    public static final int KEY_NAME_LENGTH_MAX = 32;

    private String name=null;
    private int firstFrame=0,lastFrame=0,id=-1;
    private long flags=KEY_LOOP;
    private float frameRate=0;


    public KeyFrame() {
        flags=KEY_LOOP;
    }

    public KeyFrame(String name) {
        this.name=name;
        flags=KEY_LOOP;
    }

    public void setStartFinish(int s,int f) {
        firstFrame=s;
        lastFrame=f;
    }

    public void setName(String name) {
        this.name=name;
    }

    public void setFrameRate(float r) {
        frameRate=r;
    }

    public void setId(int id) {
        this.id=id;
    }

    public int getId() {
        return id;
    }

    public void setFlags(long f) {
        flags=f;
    }

    public int getStartFrame() {
        return firstFrame;
    }

    public int getFinishFrame() {
        return lastFrame;
    }

    public long getFlags() {
        return flags;
    }

    public String getName() {
        return name;
    }

    public float getFrameRate() {
        return frameRate;
    }

    public boolean equalsIgnoreCase(String n) {
        if (name!=null)
            return(name.equalsIgnoreCase(n));
        return(false);
    }

    public String toString() {
        return "("+name+")"+id+": "+firstFrame+"->"+lastFrame+" @ "+frameRate;
    }
}
