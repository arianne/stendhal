/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.common.math;

/**
 *
 * @author silvio
 */
public class Numeric
{
    public static int floatToInt(float value, float accuracy)
    {
        return (int)(value * accuracy);
    }

    public static float intToFloat(int value, float accuracy)
    {
        return (float)(value / accuracy);
    }
}
