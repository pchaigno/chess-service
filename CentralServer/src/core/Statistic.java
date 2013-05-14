package core;

/**
 * Enumeration of statistics used by StatsManager.
 * @author Clement Gautrais
 */
public enum Statistic {
	MEAN (0),
	VARIANCE (1),
	WEIGHT (2),
	NORMALIZATION_MEAN (3),
	NORMALIZATION_VARIANCE (4);
	
	private int value;
	
	/**
	 * Constructor
	 * @param value The numeric value corresponding to the type of statistic.
	 */
	Statistic(int value) {
		this.value = value;
	}
	
	/**
	 * @return The value corresponding to the type of statistic.
	 */
	public int getValue() {
		return this.value;
	}
	
}