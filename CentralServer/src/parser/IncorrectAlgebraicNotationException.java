package parser;

/**
 * Exception for incorrect algebraic notations.
 * @author Paul Chaignon
 */
@SuppressWarnings("serial")
public class IncorrectAlgebraicNotationException extends Exception {

	/**
	 * Constructor
	 * @param message The exception message.
	 */
	public IncorrectAlgebraicNotationException(String message) {
		super(message);
	}
}
