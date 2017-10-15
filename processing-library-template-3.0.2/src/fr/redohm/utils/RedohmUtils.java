package fr.redohm.utils;

public final class RedohmUtils {

	public static final float map(float value, float start1, float stop1, float start2, float stop2) {
		// Cf.
		// https://github.com/processing/processing/blob/master/core%2Fsrc%2Fprocessing%2Fcore%2FPApplet.java#L4915
		float outgoing = start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
		String badness = null;
		if (outgoing != outgoing) {
			badness = "NaN (not a number)";

		} else if (outgoing == Float.NEGATIVE_INFINITY || outgoing == Float.POSITIVE_INFINITY) {
			badness = "infinity";
		}
		if (badness != null) {
			final String msg = String.format("map(%s, %s, %s, %s, %s) called, which returns %s", nf(value), nf(start1),
					nf(stop1), nf(start2), nf(stop2), badness);
			RedohmLogUtils.logWarning(msg);
		}
		return outgoing;
	}

	static public String nf(float num) {
		// Cf.
		// https://github.com/processing/processing/blob/master/core%2Fsrc%2Fprocessing%2Fcore%2FPApplet.java#L9716
		int inum = (int) num;
		if (num == inum) {
			return String.valueOf(inum);
		}
		return String.valueOf(num);
	}

}
