package core;

/**
 * Container for the statistics used by StatsManager.
 * Contain the enumeration of all Statistics accessible.
 * @author Clement Gautrais
 */
public class Statistic {
	
	/**
	 * Represents all the statistics accessible via StatsManager
	 */
	enum Stat {
		/**
		 * The Weight (number of values).
		 */
		WEIGHT,
		/**
		 * The mean on values.
		 */
		MEAN,
		/**
		 * The variance on values.
		 */
		VARIANCE,
		/**
		 * The normalization mean (on score).
		 */
		NORMALIZATION_MEAN,
		/**
		 * The normalization variance (on score).
		 */
		NORMALIZATION_VARIANCE;
	}
	
	double mean;
	double variance;
	double normalizationMean;
	double normalizationVariance;
	
	/**
	 * Constructor.
	 * Initialize all values to zero.
	 */
	public Statistic() {
		this.mean = 0;
		this.variance = 0;
		this.normalizationMean = 0;
		this.normalizationVariance = 0;
	}
	
	/**
	 * Constructor.
	 * @param mean The mean on values.
	 * @param variance The variance on values.
	 * @param normalizationMean The normalization mean (mean on score).
	 * @param normalizationVariance The normalization variance (variance on score).
	 */
	public Statistic(double mean, double variance, double normalizationMean, double normalizationVariance) {
		this.mean = mean;
		this.variance = variance;
		this.normalizationMean = normalizationMean;
		this.normalizationVariance = normalizationVariance;
	}
}