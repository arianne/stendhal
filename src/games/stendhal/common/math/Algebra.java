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
package games.stendhal.common.math;

import java.util.Arrays;

/**
 * Algebraic helper functions-
 * @author silvio
 */
public final class Algebra
{
    public static final float EPSILON = 0.0000001f;

    public static float[] vecf(float ...args)
    {
		assert args.length > 0;
        return args;
    }

	public static float[] homogenousf(float ...args)
    {
		assert args.length > 0;

		int   l = args.length - 1;
		float w = args[l];

		for(int i=0; i<l; ++i) {
			args[i] *= w;
		}

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
            if(Math.abs(a[i] - b[i]) > EPSILON) {
				return false;
			}
        }

        return true;
    }

    public static void mov_Vecf(float[] result, float[] vec)
    {
		assert result != null: "method argument 1 is null";
		assert vec    != null: "method argument 2 is null";
		assert result.length == vec.length;

        for(int i=0; i<vec.length; ++i) {
			result[i] = vec[i];
		}
    }

    public static void mov_Vecf(float[] result, float a)
    {
		assert result != null: "method argument 1 is null";
        Arrays.fill(result, a);
    }

    public static void add_Vecf(float[] result, float[] a, float[] b)
    {
		assert result != null: "method argument 1 is null";
		assert a      != null: "method argument 2 is null";
		assert b      != null: "method argument 3 is null";
		assert result.length == a.length && a.length == b.length;

        for(int i=0; i<a.length; ++i) {
			result[i] = a[i] + b[i];
		}
    }

    public static void add_Vecf(float[] result, float[] a, float b)
    {
		assert result != null: "method argument 1 is null";
		assert a      != null: "method argument 2 is null";
		assert result.length == a.length;

        for(int i=0; i<a.length; ++i) {
			result[i] = a[i] + b;
		}
    }

    public static void sub_Vecf(float[] result, float[] a, float[] b)
    {
		assert result != null: "method argument 1 is null";
		assert a      != null: "method argument 2 is null";
		assert b      != null: "method argument 3 is null";
        assert result.length == a.length && a.length == b.length;

        for(int i=0; i<a.length; ++i) {
			result[i] = a[i] - b[i];
		}
    }

    public static void sub_Vecf(float[] result, float[] a, float b)
    {
		assert result != null: "method argument 1 is null";
		assert a      != null: "method argument 2 is null";
        assert result.length == a.length;

        for(int i=0; i<a.length; ++i) {
			result[i] = a[i] - b;
		}
    }

    public static void mul_Vecf(float[] result, float[] a, float[] b)
    {
		assert result != null: "method argument 1 is null";
		assert a      != null: "method argument 2 is null";
		assert b      != null: "method argument 3 is null";
        assert result.length == a.length && a.length == b.length;

        for(int i=0; i<a.length; ++i) {
			result[i] = a[i] * b[i];
		}
    }

    public static void mul_Vecf(float[] result, float[] a, float b)
    {
		assert result != null: "method argument 1 is null";
		assert a      != null: "method argument 2 is null";
        assert result.length == a.length;

        for(int i=0; i<a.length; ++i) {
			result[i] = a[i] * b;
		}
    }

    public static void div_Vecf(float[] result, float[] a, float[] b)
    {
		assert result != null: "method argument 1 is null";
		assert a      != null: "method argument 2 is null";
		assert b      != null: "method argument 3 is null";
        assert result.length == a.length && a.length == b.length;

        for(int i=0; i<a.length; ++i) {
			result[i] = a[i] / b[i];
		}
    }

    public static void div_Vecf(float[] result, float[] a, float b)
    {
		assert result != null: "method argument 1 is null";
		assert a      != null: "method argument 2 is null";
        assert result.length == a.length;

        for(int i=0; i<a.length; ++i) {
			result[i] = a[i] / b;
		}
    }

    public static float dot_Vecf(float[] a, float[] b)
    {
		assert a      != null: "method argument 1 is null";
		assert b      != null: "method argument 2 is null";
        assert a.length == b.length;

        float result = 0.0f;

        for(int i=0; i<a.length; ++i) {
			result += a[i] * b[i];
		}

        return result;
    }

    public static void cross_Vec3f(float[] result, float[] a, float[] b)
    {
		assert result != null: "method argument 1 is null";
		assert a      != null: "method argument 2 is null";
		assert b      != null: "method argument 3 is null";
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
		assert result != null: "method argument 1 is null";
		assert a      != null: "method argument 2 is null";
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


    public static void normalize_Vecf(float[] result, float[] a)
    {
		assert result != null: "method argument 1 is null";
		assert a      != null: "method argument 2 is null";
        assert result.length == a.length;

        float length = 1.0f / length_Vecf(a);

        for(int i=0; i<a.length; ++i) {
			result[i] = a[i] * length;
		}
    }

    public static float distanceSqrt_Vecf(float[] a, float[] b)
    {
		assert a      != null: "method argument 1 is null";
		assert b      != null: "method argument 2 is null";
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

	// ------------------------------------------------------------- //

	public static float[] mat(boolean horizontalVectors, float[] ...vectors)
	{
		assert vectors.length    != 0;
		assert vectors[0]        != null;
		assert vectors[0].length != 0;

		int     vecLength = vectors[0].length;
		int     numVecs   = vectors.length;
		float[] result    = new float[numVecs * vecLength];
		int     index     = 0;

		if(horizontalVectors)
		{
			for(float[] vec: vectors)
			{
				assert vec.length == vecLength: "all vectors must have equal length";

				System.arraycopy(vec, 0, result, index, vecLength);
				index += vecLength;
			}
		}
		else
		{
			for(float[] vec: vectors)
			{
				assert vec.length == vecLength: "all vectors must have equal length";

				int row = index;

				for(float val: vec)
				{
					result[row] = val;
					row += numVecs;
				}

				++index;
			}
		}

		return result;
	}

	public static float[] mul_mat(float[] a, int rowsA, int colsA, float[] b, int rowsB, int colsB)
	{
		assert colsA == rowsB;
		assert (rowsA * colsA) != 0;
		assert (rowsB * colsB) != 0;

		int     resultSize = rowsA * colsB;
		float[] result     = new float[resultSize];
		int     ra         = 0; // current row for array a
		int     rr         = 0; // current row for array result

		Arrays.fill(result, 0.0f);

		while(ra < resultSize)
		{
			for(int cb=0; cb<colsB; ++cb)
			{
				int ib = cb; // current index for array b

				for(int d=0; d<colsA; ++d)
				{
					result[rr + cb] += a[ra + d] * b[ib];
					ib += colsB;
				}
			}

			ra += colsA;
			rr += colsB;
		}

		return result;
	}
}
