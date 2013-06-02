package parser;

/**
 * Exception for incorrect algebraic notations.
 * This exception is raised if the algebraic notation is incorrect.
 * Since we don't check the validity of the notation, it can be raised at any moment.
 * It happens when the length is incorrect or when there is a letter in place of a digit.
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