package fr.redohm.net;

import fr.redohm.RedohmLibraryException;

public interface IRedohmSerialPort {

	/**
	 * Initialises the serial port.
	 * @throws RedohmLibraryException
	 */
	void initialize() throws RedohmLibraryException;

	/**
	 * Checks if the serial port is available.
	 * 
	 * @return <code>true</code> if the port is available, <code>false</code> otherwise.
	 */
	boolean available();

	
	/**
	 * Reads next line.
	 * @return the next read line, <code>null</code> if there is nothing more to read, or if connection has been cut off.
	 * @throws RedohmLibraryException
	 */
	String readNextLine() throws RedohmLibraryException ;
}
