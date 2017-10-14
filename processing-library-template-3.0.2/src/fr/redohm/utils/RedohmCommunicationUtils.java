package fr.redohm.utils;

import processing.serial.Serial;

public class RedohmCommunicationUtils {

	// Fonction permettant de v�rifier l'existence du port cible souhait�.
	public final boolean checkSerialPort(final String targetPort) {
	  // R�cup�ration de la liste des ports disponibles.
	  final String[] serialPortList = Serial.list();
	  // It�ration sur chacun de ces ports.
	  for (int i=0; i < serialPortList.length; i++)
	    // Comparaison du nom du port (en minuscules), avec le port cible. 
	    if (serialPortList[i].toLowerCase().equals(targetPort))
	      return true;

	  // Le port n'a pas �t� trouv� !
	  return false;
	}

}
