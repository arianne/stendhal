/**
 *Created by L. Stevens thegeneral@users.sourceforge.net
 *This is used to serve as deterrent, to hopfully prevent someone from looking at the saved pword
 */
package games.stendhal.client.gui;

import java.util.BitSet;

public class Encoder {

	private BitSet key;

	public Encoder() {
		// BitSet key =
		// createBitSet(stringToBinary(String.valueOf(System.identityHashCode(test.this))));
		key = createBitSet("KLGFJoiU)(#%lKJT#)(I@(_)*)({%T:KLGJEJ*%)(Y*EKLJHROY*");
	}

	public String decode(String str) {
		try {
			BitSet nameSet = createBitSet(str); // create a BitSet based on the
												// binary representation

			nameSet.xor(key); // xor the BitSet with the key

			// turn the xor'd BitSet back into a String
			StringBuilder strBuff = new StringBuilder(str.length() * 7);
			for (int i = 0; i < nameSet.size(); i++) {
				if (nameSet.get(i)) {
					strBuff.append('1');
				} else {
					strBuff.append('0');
				}
			}
			strBuff.reverse();

			// read in the first two numbers of the stream and cut the string
			// down to the size specified by these numbers
			String decodedString = binaryToString(strBuff.toString());
			String stringLen = decodedString.substring(0, 2);
			int len = Integer.parseInt(stringLen);
			return decodedString.substring(2, len + 2);
		} catch (Exception e) {
			return "";
		}
	}

	public String encode(String str) {
		String binaryString = stringToBinary(str); // create binary
													// representationn of input
													// string

		// add the length (in binary number format) of entire encoded string to
		// the begging of the encoded string.
		String sizeOfEncodedString = String.valueOf(binaryString.length() / 7); // the
																				// size
																				// of
																				// total
																				// binaryString
		String stringSizeBinary = "";
		// if the size of the encoded string isnt two digits in length then add
		// a zero as padding
		if (sizeOfEncodedString.length() < 2) {
			sizeOfEncodedString = "0".concat(sizeOfEncodedString);
		}

		for (int i = 2; i > 0; i--) {
			stringSizeBinary = stringToBinary(sizeOfEncodedString.substring(
					i - 1, i));
			binaryString = stringSizeBinary.concat(binaryString);
		}

		BitSet nameSet = createBitSet(binaryString); // create a BitSet based
														// on the binary
														// representation
		nameSet.xor(key); // xor the BitSet with the key

		// turn the xor'd BitSet back into a String so it can be written to file
		StringBuilder strBuff = new StringBuilder(str.length() * 7);
		for (int i = 0; i < nameSet.size(); i++) {
			if (nameSet.get(i)) {
				strBuff.append('1');
			} else {
				strBuff.append('0');
			}
		}

		strBuff.reverse();
		return strBuff.toString();
	}

	private String binaryToString(String binaryString) {
		char letter;
		StringBuilder strBuff = new StringBuilder(binaryString.length() / 7);
		for (int i = 0, k = 0; i <= binaryString.length() - 1; i++, k++) {
			// break down into groups of seven
			if (k == 7) {
				k = 0;
				letter = (char) Integer.parseInt(binaryString.substring(i - 7,
						i), 2);
				strBuff.append(letter);
			}
		}
		return strBuff.toString();
	}

	/** creates a BitSet based on a string representation of binary digits. */
	private BitSet createBitSet(String binaryString) {
		BitSet bset = new BitSet(binaryString.length());
		boolean bitTrue = false;
		for (int i = 0; i < binaryString.length(); i++) {
			if (binaryString.charAt(i) == '1') {
				bitTrue = true;
			} else {
				bitTrue = false;
			}
			bset.set(i, bitTrue);
		}
		return bset;
	}

	private String stringToBinary(String theString) {
		StringBuilder strBuff = new StringBuilder();
		String binary = "";
		String padding = "";
		int paddingNeededSize = 0;
		int len = 0;
		for (int i = 0; i < theString.length(); i++) {
			// convert string into a char then convert in to binnaryString then
			// add to buffer
			binary = Integer.toBinaryString(theString.charAt(i));
			len = binary.length();
			if (len < 7) { // somtimes integers do not take up the total 7
							// bits. So padding is necessary
				paddingNeededSize = 7 - len; // how many binary digits are
												// missing to be complete
				for (int k = 0; k < paddingNeededSize; k++) {
					padding = padding.concat("0");
				}
				binary = padding.concat(binary);
				padding = "";
			}

			strBuff.append(binary);
		}
		return strBuff.toString();
	}
}
