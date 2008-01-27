package games.stendhal.common;

/**
 * ErrorDrain registers error messages while executing some algorithm like parsing command line texts.
 *
 * @author Martin Fuchs
 */
public interface ErrorDrain {

	void setError(String error);
	boolean hasError();
	String getErrorString();
	
}
