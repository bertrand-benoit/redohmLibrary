package fr.redohm.net;

import processing.core.PApplet;
import processing.serial.Serial;


// TODO: update all documentation, with author & co ...

/**
 * Concrete implementation of IRedohmSerialPort with a real Processing Serial instance embeded.
 * 
 * @author bsquare
 *
 */
public final class RedohmSerialPortImpl implements IRedohmSerialPort {

	private final Serial myPort;

	public RedohmSerialPortImpl(PApplet processingApplet, String portName, int baudRate) {
		myPort = new Serial(processingApplet, portName, baudRate);
	}

	@Override
	public void initialize() {
		myPort.bufferUntil('\n');
	}

	@Override
	public boolean available() {
		return myPort.available() > 0;
	}

	@Override
	public String readNextLine() {
		return myPort.readStringUntil('\n');
	}
	
	
	
}
