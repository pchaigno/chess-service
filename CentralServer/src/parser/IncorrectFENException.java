package parser;

/**
 * Exception for when the FEN is incorrect.
 * @author Benoit Travers
 */
@SuppressWarnings("serial")
public class IncorrectFENException extends Exception {

	/**
	 * Constructor
	 * @param message The exception message.
	 */
	public IncorrectFENException(String message) {
		super(message);
	}
}