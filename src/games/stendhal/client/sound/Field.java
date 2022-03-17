/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.sound;

import java.lang.reflect.Array;

/**
 *
 * @author silvio
 */
public class Field {
	/**
	 * This method will return a new allocated array of size newSize if newSize
	 * is greater than field.length.
	 * It will return the same array otherwise
	 * Only if keepData is set to true, the data of the array will be copied to the
	 * new allocated array
	 *
	 * @param field original array (can be null)
	 * @param newSize
	 * @param keepData
	 * @return - new or old array
	 */
	public static byte[] expand(byte[] field, int newSize, boolean keepData) {
		if (field == null || field.length < newSize) {
			byte[] newArray = new byte[newSize];

			if (keepData && field != null) {
				System.arraycopy(field, 0, newArray, 0, field.length);
			}

			return newArray;
		}

		return field;
	}

	/**
	 * This method will return a new allocated array of size newSize if newSize
	 * is greater than field.length.
	 * It will return the same array otherwise
	 * Only if keepData is set to true, the data of the array will be copied to the
	 * new allocated array
	 *
	 * @param field original array (can be null)
	 * @param newSize
	 * @param keepData
	 * @return - new or old array
	 */
	public static int[] expand(int[] field, int newSize, boolean keepData) {
		if (field == null || field.length < newSize) {
			int[] newArray = new int[newSize];

			if (keepData && field != null) {
				System.arraycopy(field, 0, newArray, 0, field.length);
			}

			return newArray;
		}

		return field;
	}

	/**
	 * This method will return a new allocated array of size newSize if newSize
	 * is greater than field.length.
	 * It will return the same array otherwise
	 * Only if keepData is set to true, the data of the array will be copied to the
	 * new allocated array
	 *
	 * @param field original array (can be null)
	 * @param newSize
	 * @param keepData
	 * @return - new or old array
	 */
	public static float[] expand(float[] field, int newSize, boolean keepData) {
		if (field == null || field.length < newSize) {
			float[] newArray = new float[newSize];

			if (keepData && field != null) {
				System.arraycopy(field, 0, newArray, 0, field.length);
			}

			return newArray;
		}

		return field;
	}

	/**
	 * This method will return a new allocated array of size newSize if newSize
	 * is greater than field.length.
	 * It will return the same array otherwise
	 * Only if keepData is set to true, the data of the array will be copied to the
	 * new allocated array
	 *
	 * @param <T> any object
	 * @param field original array (must not be null)
	 * @param newSize
	 * @param keepData
	 * @return - new or old array
	 */
	public static <T> T[] expand(T[] field, int newSize, boolean keepData) {
		if (field == null) {
			throw new IllegalArgumentException("argument field must not be null");
		}

		if (field.length < newSize) {
			Class<?> componentType = field.getClass().getComponentType();

			@SuppressWarnings("unchecked")
			T[] newArray = (T[]) Array.newInstance(componentType, newSize);

			if (keepData) {
				System.arraycopy(field, 0, newArray, 0, field.length);
			}

			return newArray;
		}

		return field;
	}

	/**
	 * This method shifts a part of an array to the right
	 *
	 * @param field the whole array
	 * @param offset the starting offset of the data to be shifted
	 * @param size the size of the part to be shifted
	 * @param shiftCount how much entries the data is shifted
	 * @param expandIfNeeded if true the array will be expanted to match the shifted data
	 * otherwise the shifted data will be truncated to match the array size
	 * @return the array
	 */
	public static float[] rshift(float[] field, int offset, int size, int shiftCount, boolean expandIfNeeded) {
		if (field == null) {
			throw new IllegalArgumentException("the field argument must not be null");
		}
		if (offset < 0 || offset >= field.length) {
			throw new IllegalArgumentException(
					"the offset argument must be in range 0 >= offset < field.length");
		}

		size = Math.min(field.length - offset, size);

		int to = offset + size + shiftCount;
		int from = offset + size;

		if (to > field.length) {
			if (expandIfNeeded) {
				field = expand(field, to, true);
			} else {
				to = field.length;
				from = field.length - shiftCount;
			}
		}

		while (from > offset) {
			field[--to] = field[--from];
		}

		return field;
	}

	/**
	 * This method shifts a part of an array to the right
	 *
	 * @param field the whole array (must not be null)
	 * @param offset the starting offset of the data to be shifted
	 * @param size the size of the part to be shifted
	 * @param shiftCount how much entries the data is shifted
	 * @param expandIfNeeded if true the array will be expanded to match the shifted data
	 * otherwise the shifted data will be truncated to match the array size
	 * @return the array
	 */
	public static <T> T[] rshift(T[] field, int offset, int size, int shiftCount, boolean expandIfNeeded) {
		if (field == null) {
			throw new IllegalArgumentException("the field argument must not be null");
		}
		if (offset < 0 || offset >= field.length) {
			throw new IllegalArgumentException(
					"the offset argument must be in range 0 >= offset < field.length");
		}

		size = Math.min(field.length - offset, size);

		int to = offset + size + shiftCount;
		int from = offset + size;

		if (to > field.length) {
			if (expandIfNeeded) {
				field = expand(field, to, true);
			} else {
				to = field.length;
				from = field.length - shiftCount;
			}
		}

		while (from > offset) {
			field[--to] = field[--from];
		}

		return field;
	}

	/**
	 * Appends entries to an array. The array will be expanded if needed
	 *
	 * @param field the array that gets entries appended (must not be null)
	 * @param size indicates the size of the array (this can differ from field.length)
	 * @param values the entries to get appended
	 * @return the array
	 */
	public static <T> T[] append(T[] field, int size, T... values) {
		field = expand(field, (size + values.length), true);
		System.arraycopy(values, 0, field, size, values.length);
		return field;
	}

	/**
	 * Inserts entries into an array at a specific position. The array will be expanded if needed
	 *
	 * @param field the array where the data should be inserted
	 * @param index the position where the data should be inserted
	 * @param size indicates the size of the array (this can differ from field.length)
	 * @param values the entries to be inserted
	 * @return the array
	 */
	public static float[] insert(float[] field, int index, int size, float... values) {
		if (index < size) {
			field = rshift(field, index, (size - index), values.length, true);
		} else {
			field = expand(field, (size + values.length), true);
			index = size;
		}

		System.arraycopy(values, 0, field, index, values.length);
		return field;
	}

	/**
	 * Inserts entries into an array at a specific position. The array will be expanded if needed
	 *
	 * @param field the array where the data should be inserted (must not be null)
	 * @param index the position where the data should be inserted
	 * @param size indicates the size of the array (this can differ from field.length)
	 * @param values the entries to be inserted
	 * @return the array
	 */
	public static <T> T[] insert(T[] field, int index, int size, T... values) {
		if (index < size) {
			field = rshift(field, index, (size - index), values.length, true);
		} else {
			field = expand(field, (size + values.length), true);
			index = size;
		}

		System.arraycopy(values, 0, field, index, values.length);
		return field;
	}
}
