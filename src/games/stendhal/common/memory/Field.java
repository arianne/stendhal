/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.common.memory;

/**
 *
 * @author silvio
 */
public class Field
{
	/**
	 * This method will return a new allocated array of size newSize if newSize
	 * is greater than array.length.
	 * Tt will return the same array otherwise
	 * Only if keepData is set to true, the data of the array will be copied to the
	 * new allocated array
	 *
	 * @param array
	 * @param newSize
	 * @param keepData
	 * @return
	 */
	public static byte[] expand(byte[] array, int newSize, boolean keepData)
	{
		if(array == null || array.length < newSize)
		{
			byte[] newArray = new byte[newSize];

			if(keepData && array != null)
				System.arraycopy(array, 0, newArray, 0, array.length);

			return newArray;
		}

		return array;
	}

	/**
	 * This method will return a new allocated array of size newSize if newSize
	 * is greater than array.length.
	 * Tt will return the same array otherwise
	 * Only if keepData is set to true, the data of the array will be copied to the
	 * new allocated array
	 *
	 * @param array
	 * @param newSize
	 * @param keepData
	 * @return
	 */
	public static int[] expand(int[] array, int newSize, boolean keepData)
	{
		if(array == null || array.length < newSize)
		{
			int[] newArray = new int[newSize];

			if(keepData && array != null)
				System.arraycopy(array, 0, newArray, 0, array.length);

			return newArray;
		}

		return array;
	}

	/**
	 * This method will return a new allocated array of size newSize if newSize
	 * is greater than array.length.
	 * Tt will return the same array otherwise
	 * Only if keepData is set to true, the data of the array will be copied to the
	 * new allocated array
	 * 
	 * @param array
	 * @param newSize
	 * @param keepData
	 * @return
	 */
	public static float[] expand(float[] array, int newSize, boolean keepData)
	{
		if(array == null || array.length < newSize)
		{
			float[] newArray = new float[newSize];

			if(keepData && array != null)
				System.arraycopy(array, 0, newArray, 0, array.length);

			return newArray;
		}

		return array;
	}
}
