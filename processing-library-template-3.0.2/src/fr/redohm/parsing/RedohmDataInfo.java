package fr.redohm.parsing;

/**
 * Defines needed information to parse a Data (for instance sent by an Arduino unit).
 * 
 * @author <a href="mailto:redohm@bertrand-benoit.net">Bertrand BENOIT</a>
 */
public final class RedohmDataInfo {
	
	private final String name;

	/**
	 * The type of data, only float is currently supported.
	 */
	private final String type;
	private final float minValue;
	private final float maxValue;
	private final float mappedMinValue;
	private final float mappedMaxValue;
	
	private float value;
	
	public RedohmDataInfo(String name, String type, float minValue, float maxValue, float mappedMinValue, float mappedMaxValue) {
		super();
		this.name = name;
		this.type = type;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.mappedMinValue = mappedMinValue;
		this.mappedMaxValue = mappedMaxValue;
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

	void setValue(float value) {
		this.value = value;
	}
	
	void addValue(float value) {
		this.value += value;
	}

	public float getValue() {
		return value;
	}

	public float getMappedValue() {
		// Cf. https://forum.processing.org/one/topic/i-need-to-know-how-exactly-map-function-works#25080000001766343.html
		// For Processing map function source code.
  	    return mappedMinValue + (mappedMaxValue - mappedMinValue) * ((value - minValue) / (maxValue - minValue));
	}
}
