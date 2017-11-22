package fr.redohm.utils;


/**
 * Utility methods to log message, warning and error.
 * 
 * @author <mailto:redohm@bertrand-benoit.net>Bertrand BENOIT</mailto>
 *
 */
public final class RedohmLogUtils {
	
	// TODO: uses a logger system like the Apache one.

	public static final void logLibraryInfo() {
		logMessage("##library.name## ##library.prettyVersion## by ##author##");
	}

	public static final void logMessage(final String message) {
		System.out.println(message);
	}

	public static void logDebug(final String message) {		
		// TODO: implement DEBUG system.
		logMessage(message);
	}

	public static final void logWarning(final String message) {
		System.err.println("[WARNING] " + message);
	}

	public static final void logError(final String message) {
		System.err.println("[ERROR] " + message);
	}

	public static final void logException(final String message, final Exception e) {
		logError(message + ": " + e.toString());
	}

}
