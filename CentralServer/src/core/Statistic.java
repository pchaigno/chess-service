package core;

/**
 * Enumeration of the statistics used by StatsManager.
 * @author Clement Gautrais
 */
public enum Statistic {
	MEAN (0),
	VARIANCE (1),
	WEIGHT (2),
	NORMALIZATION_MEAN (3),
	NORMALIZATION_VARIANCE (4);
	
	private int value;
	
	Statistic(int value){
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
	
}