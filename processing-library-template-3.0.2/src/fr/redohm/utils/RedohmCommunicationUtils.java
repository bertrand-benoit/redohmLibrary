package fr.redohm.utils;

import processing.serial.Serial;

public class RedohmCommunicationUtils {

	// Fonction permettant de verifier l'existence du port cible souhaite.
	public final boolean checkSerialPort(final String targetPort) {
		// Checks if emulation is required, in which case, check will always be OK.
		if (RedohmUtils.emulateSamplingRequired())
			return true;

		// Recuperation de la liste des ports disponibles.
		final String[] serialPortList = Serial.list();
		// Iteration sur chacun de ces ports.
		for (int i = 0; i < serialPortList.length; i++)
			// Comparaison du nom du port (en minuscules), avec le port cible.
			if (serialPortList[i].toLowerCase().equals(targetPort))
				return true;

		// Le port n'a pas ete trouve !
		return false;
	}

}
