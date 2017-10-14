package fr.redohm.utils;

import processing.serial.Serial;

public class RedohmCommunicationUtils {

	// Fonction permettant de vérifier l'existence du port cible souhaité.
	public final boolean checkSerialPort(final String targetPort) {
	  // Récupération de la liste des ports disponibles.
	  final String[] serialPortList = Serial.list();
	  // Itération sur chacun de ces ports.
	  for (int i=0; i < serialPortList.length; i++)
	    // Comparaison du nom du port (en minuscules), avec le port cible. 
	    if (serialPortList[i].toLowerCase().equals(targetPort))
	      return true;

	  // Le port n'a pas été trouvé !
	  return false;
	}

}
