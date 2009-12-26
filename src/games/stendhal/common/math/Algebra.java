/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.common.math;

import java.util.Arrays;

/**
 *
 * @author silvio
 */
public final class Algebra
{
    public static float EPSILON = 0.0000001f;

    public static float[] vecf(float ...args)
    {
        return args;
    }

    public static boolean isEqual_Scalf(float a, float b)
    {
        return Math.abs(a - b) <= EPSILON;
    }

    public static boolean isEqual_Vecf(float[] a, float[] b)
    {
        assert a.length == b.length;

        for(int i=0; i<a.length; ++i)
        {
            if(Math.abs(a[i] - b[i]) > EPSILON)
                return false;
        }

        return true;
    }

    public static void mov_Vecf(float[] result, float[] vec)
    {
        assert result.length == vec.length;

        for(int i=0; i<vec.length; ++i)
            result[i] = vec[i];
    }

    public static void mov_Vecf(float[] result, float a)
    {
        Arrays.fill(result, a);
    }

    public static void add_Vecf(float[] result, float[] a, float[] b)
    {
        assert result.length == a.length && a.length == b.length;

        for(int i=0; i<a.length; ++i)
            result[i] = a[i] + b[i];
    }

    public static void add_Vecf(float[] result, float[] a, float b)
    {
        assert result.length == a.length;

        for(int i=0; i<a.length; ++i)
            result[i] = a[i] + b;
    }

    public static void sub_Vecf(float[] result, float[] a, float[] b)
    {
        assert result.length == a.length && a.length == b.length;

        for(int i=0; i<a.length; ++i)
            result[i] = a[i] - b[i];
    }

    public static void sub_Vecf(float[] result, float[] a, float b)
    {
        assert result.length == a.length;

        for(int i=0; i<a.length; ++i)
            result[i] = a[i] - b;
    }

    public static void mul_Vecf(float[] result, float[] a, float[] b)
    {
        assert result.length == a.length && a.length == b.length;

        for(int i=0; i<a.length; ++i)
            result[i] = a[i] * b[i];
    }

    public static void mul_Vecf(float[] result, float[] a, float b)
    {
        assert result.length == a.length;

        for(int i=0; i<a.length; ++i)
            result[i] = a[i] * b;
    }

    public static void div_Vecf(float[] result, float[] a, float[] b)
    {
        assert result.length == a.length && a.length == b.length;

        for(int i=0; i<a.length; ++i)
            result[i] = a[i] / b[i];
    }

    public static void div_Vecf(float[] result, float[] a, float b)
    {
        assert result.length == a.length;

        for(int i=0; i<a.length; ++i)
            result[i] = a[i] / b;
    }

    public static float dot_Vecf(float[] a, float[] b)
    {
        assert a.length == b.length;

        float result = 0.0f;

        for(int i=0; i<a.length; ++i)
            result += a[i] * b[i];

        return result;
    }

    public static void cross_Vec3f(float[] result, float[] a, float[] b)
    {
        assert result.length == 3 && a.length == 3 && b.length == 3;

        float x =  a[1] * b[2] - b[1] * a[2];
        float y = -a[0] * b[2] + b[0] * a[2];
        float z =  a[0] * b[1] - b[0] * a[1];

        result[0] = x;
        result[1] = y;
        result[2] = z;
    }

    public static void cross_Vec2f(float[] result, float[] a)
    {
        assert result.length == 2 && a.length == 2;

        float x = a[0];
        float y = a[1];
        
        result[0] =  y;
        result[1] = -x;
    }

    public static float lengthSqrt_Vecf(float[] v)
    {
        return dot_Vecf(v,v);
    }

    public static float length_Vecf(float[] v)
    {
        return (float)Math.sqrt(dot_Vecf(v,v));
    }

    public static void normalize_Vecf(float[] result, float[] v)
    {
        assert result.length == v.length;

        float length = 1.0f / length_Vecf(v);

        for(int i=0; i<v.length; ++i)
            result[i] = v[i] * length;
    }

    public static float distanceSqrt_Vecf(float[] a, float[] b)
    {
        assert a.length == b.length;

        float temp;
        float result = 0.0f;

        for(int i=0; i<a.length; ++i)
        {
            temp    = a[i] - b[i];
            result += temp * temp;
        }

        return result;
    }

    public static float distance_Vecf(float[] a, float[] b)
    {
        return (float)Math.sqrt(distanceSqrt_Vecf(a, b));
    }
}
