package core;

/**
 * Contain the statistics used by StatsManager.
 * Contain the enumeration of all Statistics accessible.
 * @author Clement Gautrais
 */
public class Statistic {
	
	enum Stat {
		WEIGHT,
		MEAN,
		VARIANCE,
		NORMALIZATION_MEAN,
		NORMALIZATION_VARIANCE;
	}
	
	double mean;
	double variance;
	double normalizationMean;
	double normalizationVariance;
	
	public Statistic() {
		this.mean = 0;
		this.variance = 0;
		this.normalizationMean = 0;
		this.normalizationVariance = 0;
	}
	
	public Statistic(double mean, double variance, double normalizationMean, double normalizationVariance) {
		this.mean = mean;
		this.variance = variance;
		this.normalizationMean = normalizationMean;
		this.normalizationVariance = normalizationVariance;
	}
}