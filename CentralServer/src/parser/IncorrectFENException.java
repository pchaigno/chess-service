package parser;

/**
 * Exception for when the FEN is incorrect.
 * This exception is raised when the FEN is detect as incorrect.
 * We check some caracteristics of the FEN at the beginning but since we don't check everything
 * (no regular expression) it can happen at any moment.
 * It can happen when the length is incorrect or when there is a letter in place of a digit.
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