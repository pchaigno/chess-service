package parser;

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