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

/**
 * Geometry algorithms.
 * @author silvio
 */
public class Geometry
{
    public static float halfSpaceTest_PlanePointf(float[] planeNormal, float planeDist, float[] point)
    {
        return Algebra.dot_Vecf(point, planeNormal) + planeDist;
    }

    public static float halfSpaceTest_PlanePointf(float[] planeNormal, float[] planePoint, float[] point)
    {
        float[] temp = new float[point.length];

        Algebra.sub_Vecf(temp, point, planePoint);
        return Algebra.dot_Vecf(temp, planeNormal);
    }

    public static boolean closestPoint_RayPointf(float[] result, float[] rayOrigin, float[] rayDirection, float[] point)
    {
        Algebra.sub_Vecf(result, point, rayOrigin);
        float t = Algebra.dot_Vecf(rayDirection, result) / Algebra.dot_Vecf(rayDirection, rayDirection);
        Algebra.mul_Vecf(result, rayDirection, t);
        Algebra.add_Vecf(result, result, rayOrigin);

        // if the closest point is equal to "vec"
        if(Algebra.isEqual_Vecf(result, point)) {
			return false;
		}

        return true;
    }

    public static boolean closestPoint_SpherePointf(float[] result, float[] sphereCenter, float sphereRadius, float[] point)
    {
        // if "vec" is equal to the sphere center, we are not able to calculate the closest point
        if(Algebra.isEqual_Vecf(sphereCenter, point)) {
			return false;
		}

        Algebra.sub_Vecf(result, point, sphereCenter);
        Algebra.normalize_Vecf(result, result);
        Algebra.mul_Vecf(result, result, sphereRadius);
        Algebra.add_Vecf(result, result, sphereCenter);
        return true;
    }

    public static boolean closestPoint_CapsulePointf(float[] result, float[] capsulePt1, float[] capsulePt2, float capsuleRadius, float[] point)
    {
        float[] normal = new float[point.length];
        Algebra.sub_Vecf(normal, capsulePt2, capsulePt1);

        if(halfSpaceTest_PlanePointf(normal, capsulePt1, point) > 0.0f)
        {
            if(halfSpaceTest_PlanePointf(normal, capsulePt2, point) < 0.0f)
            {
                // if "vec" lies directly on the ray, we cannot calculate the closest point to the capsule
                if(!closestPoint_RayPointf(result, capsulePt1, normal, point)) {
					return false;
				}

                Algebra.sub_Vecf(normal, point, result);
                Algebra.normalize_Vecf(normal, normal);
                Algebra.mul_Vecf(normal, normal, capsuleRadius);
                Algebra.add_Vecf(result, result, normal);
                return true;
            }

            return closestPoint_SpherePointf(result, capsulePt2, capsuleRadius, point);
        }

        return closestPoint_SpherePointf(result, capsulePt1, capsuleRadius, point);
    }

    public static float distanceSqrt_LinePointf(float[] linePt1, float[] linePt2, float[] point)
    {
        float[] lineDir = new float[point.length];
        Algebra.sub_Vecf(lineDir, linePt2, linePt1);

        if(halfSpaceTest_PlanePointf(lineDir, linePt1, point) > 0.0f)
        {
            if(halfSpaceTest_PlanePointf(lineDir, linePt2, point) < 0.0f)
            {
                float[] closestPoint = new float[point.length];
                closestPoint_RayPointf(closestPoint, linePt1, lineDir, point);
                return Algebra.distanceSqrt_Vecf(point, closestPoint);
            }

            return Algebra.distanceSqrt_Vecf(point, linePt2);
        }

        return Algebra.distanceSqrt_Vecf(linePt1, point);
    }

    public static float distance_LinePointf(float[] linePt1, float[] linePt2, float[] point)
    {
        return (float)Math.sqrt(distanceSqrt_LinePointf(linePt1, linePt2, point));
    }
}
