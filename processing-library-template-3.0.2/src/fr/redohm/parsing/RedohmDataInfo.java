package fr.redohm.parsing;

import fr.redohm.utils.RedohmUtils;

/**
 * Defines needed information to parse a Data (for instance sent by an Arduino
 * unit).
 * 
 * @author <a href="mailto:redohm@bertrand-benoit.net">Bertrand BENOIT</a>
 */
public final class RedohmDataInfo {

	public static final String TYPE_FLOAT = "float";
	public static final String TYPE_BOOLEAN = "boolean";

	private final String name;

	/**
	 * The type of data, only float is currently supported.
	 */
	private final String type;
	private final float minValue;
	private final float maxValue;
	private final float mappedMinValue;
	private final float mappedMaxValue;

	/**
	 * The current value of the data.
	 */
	private float value;

	/**
	 * A buffer style value allowing sampling initialization, update, and
	 * finalization.
	 */
	private float samplingValueBuffer;

	public RedohmDataInfo(String name, String type, float minValue, float maxValue, float mappedMinValue,
			float mappedMaxValue) {
		this.name = name;
		this.type = type;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.mappedMinValue = mappedMinValue;
		this.mappedMaxValue = mappedMaxValue;

		this.value = 0.0f;
		this.samplingValueBuffer = 0.0f;
	}

	public final String getName() {
		return name;
	}

	public final String getType() {
		return type;
	}

	public final float getMinValue() {
		return minValue;
	}

	public final float getMaxValue() {
		return maxValue;
	}

	public float getMappedMinValue() {
		return mappedMinValue;
	}

	public float getMappedMaxValue() {
		return mappedMaxValue;
	}

	public float getValue() {
		return value;
	}

	final void initializeSampling() {
		samplingValueBuffer = 0.0f;
	}

	final void addSamplingValue(float value) {
		// Adapts management, according to type.
		if (type == TYPE_BOOLEAN) {
			// For boolean, we only need to keep the last value, there is no sampling.
			this.samplingValueBuffer = value;
			return;
		}

		this.samplingValueBuffer += value;
	}

	final void finalizeSampling(final int samplingMeasureCount) {
		// Adapts management, according to type.
		// N.B.: for boolean, we only need to keep the last value, there is no sampling;
		// so no need to divide anything.
		if (type != TYPE_BOOLEAN) {
			this.samplingValueBuffer /= samplingMeasureCount;
		}

		setValue(samplingValueBuffer);
	}

	private void setValue(float value) {
		this.value = value;
	}

	public float getMappedValue() {
		return RedohmUtils.map(value, minValue, maxValue, mappedMinValue, mappedMaxValue);
	}

	@Override
	public final String toString() {
		return "RedohmDataInfo [name=" + name + ", type=" + type + ", minValue=" + minValue + ", maxValue=" + maxValue
				+ ", mappedMinValue=" + mappedMinValue + ", mappedMaxValue=" + mappedMaxValue + "]";
	}

}
