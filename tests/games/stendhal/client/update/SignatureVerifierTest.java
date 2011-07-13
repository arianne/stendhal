/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.update;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

/**
 * Tests for SignatureVerifier
 *
 * @author hendrik
 */
public class SignatureVerifierTest {

	/**
	 * Tests for hexStringToByteArray
	 */
	@Test
	public void testHexStringToByteArray() {
		// test uneven number of hex digits because a leading 0 was stripped.
		String data = "3bf02bccaedb1a01407f4acd6aa52b821055dcf86cf5d9c35161ee9560c37fc651ca8e2e0c54b735069eef5ff08a960b37ccd5ae02eef0b1c4721229f2f6e3d0ca7b6517487357a307b6b7bdc2ca39602dfe9442c732c794c2037a8ef91e834b276c27631d5ea6b894be6637e27295f44f93416e8bf1122e4f45f91615c6b434a7403ac836b5bb1f2f12f3080450803309d9425efe8a87f1e1eae63e243305b1c793d2b4cd360b959352f45f331e1820ab088be4a84c5475eda02ee49157c6cf3d98c81ab4b5b4b02a85e98f7fb90c6b0072b246670bf30ee97ba226796f88e0e564a06f31a16577db04dc9d76fa6f1cad480553d73f21d058a80f0874b886c";
		byte[] res = SignatureVerifier.hexStringToByteArray(data);
		assertThat(Arrays.toString(res), equalTo("[3, -65, 2, -68, -54, -19, -79, -96, 20, 7, -12, -84, -42, -86, 82, -72, 33, 5, 93, -49, -122, -49, 93, -100, 53, 22, 30, -23, 86, 12, 55, -4, 101, 28, -88, -30, -32, -59, 75, 115, 80, 105, -18, -11, -1, 8, -87, 96, -77, 124, -51, 90, -32, 46, -17, 11, 28, 71, 33, 34, -97, 47, 110, 61, 12, -89, -74, 81, 116, -121, 53, 122, 48, 123, 107, 123, -36, 44, -93, -106, 2, -33, -23, 68, 44, 115, 44, 121, 76, 32, 55, -88, -17, -111, -24, 52, -78, 118, -62, 118, 49, -43, -22, 107, -119, 75, -26, 99, 126, 39, 41, 95, 68, -7, 52, 22, -24, -65, 17, 34, -28, -12, 95, -111, 97, 92, 107, 67, 74, 116, 3, -84, -125, 107, 91, -79, -14, -15, 47, 48, -128, 69, 8, 3, 48, -99, -108, 37, -17, -24, -88, 127, 30, 30, -82, 99, -30, 67, 48, 91, 28, 121, 61, 43, 76, -45, 96, -71, 89, 53, 47, 69, -13, 49, -31, -126, 10, -80, -120, -66, 74, -124, -59, 71, 94, -38, 2, -18, 73, 21, 124, 108, -13, -39, -116, -127, -85, 75, 91, 75, 2, -88, 94, -104, -9, -5, -112, -58, -80, 7, 43, 36, 102, 112, -65, 48, -18, -105, -70, 34, 103, -106, -8, -114, 14, 86, 74, 6, -13, 26, 22, 87, 125, -80, 77, -55, -41, 111, -90, -15, -54, -44, -128, 85, 61, 115, -14, 29, 5, -118, -128, -16, -121, 75, -120, 108]"));
	}

}
