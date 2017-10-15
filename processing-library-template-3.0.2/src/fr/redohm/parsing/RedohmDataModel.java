package fr.redohm.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Defines complete Data Model needed to parse information.
 * 
 * @author <a href="mailto:redohm@bertrand-benoit.net">Bertrand BENOIT</a>
 */
public class RedohmDataModel {

	public static final float INVALID_VALUE = Float.MIN_VALUE;

	private final List<RedohmDataInfo> dataInfoList;

	public RedohmDataModel(RedohmDataInfo... dataInfos) {
		this.dataInfoList = new ArrayList<RedohmDataInfo>();
		dataInfoList.addAll(Arrays.asList(dataInfos));
	}

	public int getDataCount() {
		return dataInfoList.size();
	}

	private final RedohmDataInfo getDataInfo(final String name) {
		final Optional<RedohmDataInfo> dataInfoIfExists = dataInfoList.stream().filter(p -> p.getName() == name)
				.findFirst();
		if (!dataInfoIfExists.isPresent()) {
			// TODO: log error/warning
			return null;
		}

		// Returns the RedohmDataInfo.
		return dataInfoIfExists.get();
	}

	public final float getDataValue(final String name) {
		RedohmDataInfo dataInfo = getDataInfo(name);
		if (dataInfo == null)
			return INVALID_VALUE;

		return dataInfo.getValue();
	}

	public final float getMappedValue(final String name) {
		RedohmDataInfo dataInfo = getDataInfo(name);
		if (dataInfo == null)
			return INVALID_VALUE;

		return dataInfo.getMappedValue();
	}

	public final Iterator<RedohmDataInfo> getIterator() {
		return dataInfoList.iterator();
	}

}
