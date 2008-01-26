package games.stendhal.common;

/**
 * ErrorDrain registers error messages while executing some algorithm like parsing command line texts.
 *
 * @author Martin Fuchs
 */
public interface ErrorDrain {

	public void setError(String error);
	public boolean hasError();
	public String getErrorString();
	
}
