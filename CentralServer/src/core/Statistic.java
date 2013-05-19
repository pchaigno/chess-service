package core;

/**
 * Contain the statistics used by StatsManager.
 * Contain the enumeration of all Statistics accessible.
 * @author Clement Gautrais
 */
public class Statistic {
	
	public enum Stat{
		WEIGHT,
		MEAN,
		VARIANCE,
		NORMALIZATION_MEAN,
		NORMALIZATION_VARIANCE;
	}
	
	private double mean;
	private double variance;
	private double normalization_mean;
	private double normalization_variance;
	
	public Statistic(){
		init();
	}
	
	
	public Statistic(double mean, double variance, double normalization_mean, double normalization_variance) {
		super();
		this.mean = mean;
		this.variance = variance;
		this.normalization_mean = normalization_mean;
		this.normalization_variance = normalization_variance;
	}


	public void init(){
		mean = 0;
		variance = 0;
		normalization_mean = 0;
		normalization_variance = 0;
	}

	public double getMean() {
		return mean;
	}

	public double getVariance() {
		return variance;
	}

	public double getNormalization_mean() {
		return normalization_mean;
	}

	public double getNormalization_variance() {
		return normalization_variance;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public void setVariance(double variance) {
		this.variance = variance;
	}

	public void setNormalization_mean(double normalization_mean) {
		this.normalization_mean = normalization_mean;
	}

	public void setNormalization_variance(double normalization_variance) {
		this.normalization_variance = normalization_variance;
	}
	
}