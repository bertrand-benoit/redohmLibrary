package fr.redohm.utils;

import processing.serial.Serial;

public class RedohmCommunicationUtils {
	public float[] getAverageValues(Serial myPort, int measureCount) {
		String message;
	    int iterationCount = measureCount;
	    float valeurBrute1 = 0.0f, valeurBrute2 = 0.0f, etatBouton = 0.0f;
	    
	    // Boucle jusuq'à ce que l'on obtienne le nombre de mesures demandées (en ne prenant pas en compte tous les problèmes de format, de convertion en entiers ...).
	    while (true) {
	      // Lecture du prochain message disponible.
	      message = myPort.readStringUntil('\n');
	      // Vérification : on s'assure qu'un message AVEC fin de ligne est bien disponible sur le port, 
	      // sinon on ne fait rien de plus dans cette exécution de draw().
	      // L'exécution suivante prendra en charge les messages en attente.
	      if (message == null)
	        continue;
	      
	      //println(message);
	      
	      // Décodage du message reçu; il doit impérativement respecter le format : x;y;z
	      final String[] values = message.split(";");
	      if (values.length != 3) {
	       System.out.println("Le message suivant ne respecte pas le format attendu (x;y;z): " + message);
	       continue;
	      }
	  
	      // Convertion des valeurs reçues en entiers (en gérant le cas où l'une des valeurs n'est pas un entier).
	      try {
	        float prochaineValeurBrute1 = Float.parseFloat(values[0].trim());
	        float prochaineValeurBrute2 = Float.parseFloat(values[1].trim());
	        float nextetatBouton = Float.parseFloat(values[2].trim());
	      
	        // Prise en compte de ces nouvelles valeurs (pour le moment, on somme toutes les valeurs).
	        valeurBrute1 += prochaineValeurBrute1;
	        valeurBrute2 += prochaineValeurBrute2;
	        etatBouton += nextetatBouton;
	      } catch(NumberFormatException e) {
	         // Affichage d'un message d'erreur. 
	    	  System.out.println("Impossible de convertir l'une des valeurs en entier ... le message suivant est donc totalement ignoré : " + message);
	         e.printStackTrace();
	         continue; 
	      }
	  
	      // Arrivé ici, on a la certitude d'avoir bien reçu et pris en compte une nouvelle valeur, au bon format.
	      // C'est ici que l'on gère le nombre d'itération, et la condition d'arrêt de la boucle infinie (while(true)). 
	      if (--iterationCount <= 0)
	        break;
	    }
	    
	    // Envoie des 3 valeurs lissées, sous la forme d'un tableau.
	    return new float[]{valeurBrute1/measureCount, valeurBrute2/measureCount, etatBouton/measureCount};
	  }
}
