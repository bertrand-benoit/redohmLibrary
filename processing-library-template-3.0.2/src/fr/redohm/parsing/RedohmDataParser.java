package fr.redohm.parsing;

import java.util.Iterator;

import fr.redohm.utils.RedohmLogUtils;
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

		// Requests sampling initialization.
		Iterator<RedohmDataInfo> initializationIterator = dataModel.getIterator();
		while (initializationIterator.hasNext()) {
			final RedohmDataInfo dataInfo = initializationIterator.next();
			dataInfo.initializeSampling();
		}

		while (true) {
			// Lecture du prochain message disponible.
			message = myPort.readStringUntil('\n');
			// Vérification : on s'assure qu'un message AVEC fin de ligne est bien
			// disponible sur le port,
			// sinon on ne fait rien de plus dans cette exécution de draw().
			// L'exécution suivante prendra en charge les messages en attente.
			if (message == null)
				continue;

			// TODO: log debug message println(message);

			// Décodage du message reçu; il doit impérativement respecter le format :
			// x;y;z
			final String[] values = message.split(separator);
			if (values.length != awaitedDataCount) {
				RedohmLogUtils.logMessage("Le message suivant n'a pas le bon format (" + awaitedDataCount
						+ " valeur(s) sont attendue(s), separee(s) par '" + separator + "'): " + message);
				continue;
			}

			// TODO: find a better way to update the temp value ...

			// Convertion des valeurs reeues en Float.
			try {
				int valueIndex = 0;
				Iterator<RedohmDataInfo> iterator = dataModel.getIterator();
				while (iterator.hasNext()) {
					final RedohmDataInfo dataInfo = iterator.next();
					final float value = Float.parseFloat(values[valueIndex++].trim());
					// System.out.println("Parsed new value : " + value);
					dataInfo.addSamplingValue(value);
				}
			} catch (NumberFormatException e) {
				// Affichage d'un message d'erreur.
				RedohmLogUtils.logException(
						"Impossible de convertir l'une des valeurs en entier ... le message suivant est donc totalement ignore : "
								+ message,
						e);
				continue;
			}

			// Decrements iteration count only HERE to ensure all "safe-guard" are OK.
			if (--iterationCount <= 0)
				break;
		}

		// Lissage des valeurs, en redivisant par le nombre de mesures ...
		Iterator<RedohmDataInfo> finalizationIterator = dataModel.getIterator();
		while (finalizationIterator.hasNext()) {
			final RedohmDataInfo dataInfo = finalizationIterator.next();
			dataInfo.finalizeSampling(measureCount);
		}
	}

	public final float getDataValue(final String name) {
		return dataModel.getDataValue(name);
	}

	public final float getMappedValue(final String name) {
		return dataModel.getMappedValue(name);
	}

	public final float getMinSampledValue(final String name) {
		return dataModel.getMinSampledValue(name);
	}

	public final float getMaxSampledValue(final String name) {
		return dataModel.getMaxSampledValue(name);
	}

}
