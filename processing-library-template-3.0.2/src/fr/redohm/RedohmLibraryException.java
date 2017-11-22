package fr.redohm;

public class RedohmLibraryException extends Exception {

	private static final long serialVersionUID = 5140284014797961802L;

	public RedohmLibraryException() {
		super();
	}

	public RedohmLibraryException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RedohmLibraryException(String message, Throwable cause) {
		super(message, cause);
	}

	public RedohmLibraryException(String message) {
		super(message);
	}

	public RedohmLibraryException(Throwable cause) {
		super(cause);
	}

}
