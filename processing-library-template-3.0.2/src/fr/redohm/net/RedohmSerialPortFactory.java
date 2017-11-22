package fr.redohm.net;

import fr.redohm.utils.RedohmUtils;
import processing.core.PApplet;

public final class RedohmSerialPortFactory {

	public static final IRedohmSerialPort newRedohmSerialPort(PApplet processingApplet, String portName, int baudRate) {
		if (RedohmUtils.emulateSamplingRequired())
			return new RedohmSerialPortEmulatorImpl();
		else
			return new RedohmSerialPortImpl(processingApplet, portName, baudRate);
	}

}
