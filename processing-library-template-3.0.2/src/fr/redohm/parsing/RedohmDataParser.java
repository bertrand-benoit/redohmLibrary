package fr.redohm.parsing;

import java.util.Iterator;

import processing.serial.Serial;

public class RedohmDataParser {

	private Serial myPort;
	private RedohmDataModel dataModel;
	private String separator;

	public RedohmDataParser(RedohmDataModel dataModel, String separator, Serial myPort) {
		this.dataModel = dataModel;
		this.separator = separator;
		this.myPort = myPort;
	}

	public final void readNextValues(int measureCount) {
		String message;
		int iterationCount = measureCount;
		int awaitedDataCount = dataModel.getDataCount();

		while (true) {
			// Lecture du prochain message disponible.
			message = myPort.readStringUntil('\n');
			// VÃ©rification : on s'assure qu'un message AVEC fin de ligne est bien
			// disponible sur le port,
			// sinon on ne fait rien de plus dans cette exÃ©cution de draw().
			// L'exÃ©cution suivante prendra en charge les messages en attente.
			if (message == null)
				continue;
			
			// TODO: log debug message println(message);

			// DÃ©codage du message reÃ§u; il doit impÃ©rativement respecter le format :
			// x;y;z
			final String[] values = message.split(separator);
			if (values.length != awaitedDataCount) {
				System.err.println("Le message suivant n'a pas le bon format (" + awaitedDataCount
						+ " valeur(s) sont attendue(s), séparée(s) par '" + separator + "'): " + message);
				continue;
			}

			// TODO: find a better way to update the temp value ...
			
			// Convertion des valeurs reçues en Float.
			try {
				int valueIndex = 0;
				Iterator<RedohmDataInfo> iterator = dataModel.getIterator();
				while (iterator.hasNext()) {
					final RedohmDataInfo dataInfo = iterator.next();
					final float value = Float.parseFloat(values[valueIndex++].trim());
					//System.out.println("Parsed new value : " + value);
					dataInfo.addValue(value);
				}
			} catch (NumberFormatException e) {
				// Affichage d'un message d'erreur.
				System.err.println(
						"Impossible de convertir l'une des valeurs en entier ... le message suivant est donc totalement ignoré : "
								+ message);
				e.printStackTrace();
				continue;
			}
			
			// Decrements iteration count only HERE to ensure all "safe-guard" are OK.
			if (iterationCount-- <= 0)
				break;
		}
		
		// Lissage des valeurs, en redivisant par le nombre de mesures ...
		Iterator<RedohmDataInfo> iterator = dataModel.getIterator();
		while (iterator.hasNext()) {
			final RedohmDataInfo dataInfo = iterator.next();
			dataInfo.setValue(dataInfo.getValue()/measureCount);
		}
	}

	public final float getDataValue(final String name) {
		return dataModel.getDataValue(name);
	}

	public final float getMappedValue(final String name) {
		return dataModel.getMappedValue(name);
	}
	
}
