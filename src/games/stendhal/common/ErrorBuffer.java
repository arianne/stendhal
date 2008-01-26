package games.stendhal.common;

/**
 * ErrorBuffer stores and concatenates multiple error messages.
 *
 * @author Martin Fuchs
 */
public class ErrorBuffer implements ErrorDrain {

	// start with no errors
	protected String errorBuffer = null;

	/**
	 * Store error message.
	 * 
	 * @param error message
	 */
	public void setError(final String error) {
		if (errorBuffer == null) {
			errorBuffer = error;
		} else {
			errorBuffer += '\n';
			errorBuffer += error;
		}
	}

	/**
	 * Return whether some error has been registered.
	 * 
	 * @return error flag
	 */
	public boolean hasError() {
		return errorBuffer != null;
	}

	/**
	 * Return the concatenated error message.
	 * 
	 * @return error string
	 */
	public String getErrorString() {
		return errorBuffer;
	}

}
