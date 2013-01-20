package at.ac.fhs.aftsw.task8.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Markus Deutschl
 * 
 */
public class Seed implements Comparable<Seed> {
	/*
	 * Constants.
	 * -------------------------------------------------------------------------
	 */
	public static final double INFINITY = Double.MAX_VALUE;
	/*
	 * Properties.
	 * -------------------------------------------------------------------------
	 */
	/**
	 * The name of the seed.
	 */
	private String name;
	/**
	 * The x-position of the seed.
	 */
	private double x;
	/**
	 * The y-position of the seed.
	 */
	private double y;
	/**
	 * The seed's core distance.
	 */
	private double coreDistance;
	/**
	 * The seed's reachability distance.
	 */
	private double reachabilityDistance;
	/**
	 * The seed's processed state.
	 */
	private boolean processed;
	/**
	 * The distances to the other seeds.
	 */
	private Map<String, Double> distances;

	/*
	 * Constructors.
	 * -------------------------------------------------------------------------
	 */
	public Seed(String name) {
		this(name, 0.0, 0.0, INFINITY, INFINITY);
	}

	public Seed(String name, String x, String y) throws NumberFormatException {
		this(name, Double.valueOf(x), Double.valueOf(y), INFINITY, INFINITY);
	}

	public Seed(String name, double x, double y, double coreDistance,
			double reachabilityDistance) {
		this.setName(name);
		this.setX(x);
		this.setY(y);
		this.coreDistance = coreDistance;
		this.setReachabilityDistance(reachabilityDistance);
		this.distances = new HashMap<String, Double>();
	}

	/*
	 * Getters and setters.
	 * -------------------------------------------------------------------------
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getCoreDistance() {
		return coreDistance;
	}

	public void setCoreDistance(int minPoints, double epsilon) {
		List<Double> distanceValues = new ArrayList<Double>(
				this.distances.values());
		Collections.sort(distanceValues);
		double tempCoreDistance = distanceValues.get(minPoints - 1);
		this.coreDistance = tempCoreDistance > epsilon ? INFINITY
				: tempCoreDistance;
	}

	public double getReachabilityDistance() {
		return reachabilityDistance;
	}

	public void setReachabilityDistance(double reachabilityDistance) {
		this.reachabilityDistance = reachabilityDistance;
	}

	public boolean isProcessed() {
		return this.processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public Map<String, Double> getDistances() {
		return Collections.unmodifiableMap(this.distances);
	}

	public double getDistance(String seedName) {
		return this.distances.get(seedName);
	}

	public void addDistance(String seedName, double distance) {
		if (this.distances.containsKey(seedName))
			return;
		this.distances.put(seedName, distance);
	}

	/*
	 * Required comparison methods.
	 * -------------------------------------------------------------------------
	 */

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.name);
		return builder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		EqualsBuilder builder = new EqualsBuilder();
		if (obj == null || !(obj instanceof Seed)) {
			return false;
		}
		if(this == obj){
			return true;
		}
		Seed other = (Seed) obj;
		builder.append(this.name, other.getName());
		return builder.isEquals();
	}

	@Override
	public int compareTo(Seed otherSeed) {
		if (this == otherSeed) {
			return 0;
		}
		if (this.getReachabilityDistance() == otherSeed
				.getReachabilityDistance()) {
			return this.getName().compareTo(otherSeed.getName());
		} else {
			return (int) (this.getReachabilityDistance() - otherSeed
					.getReachabilityDistance());
		}
	}

	/*
	 * Printing methods.
	 * -------------------------------------------------------------------------
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Seed: name='");
		sb.append(this.getName());
//		sb.append("', x=");
//		sb.append(this.getX());
//		sb.append(", y=");
//		sb.append(this.getY());
//		sb.append(", core distance=");
//		sb.append(this.getCoreDistance() == INFINITY ? "inf" : this
//				.getCoreDistance());
		sb.append(", reachability distance=");
		sb.append(this.getReachabilityDistance() == INFINITY ? "inf" : this
				.getReachabilityDistance());
		sb.append("\n");
//		sb.append(" processed=");
//		sb.append(this.isProcessed());
//		sb.append("\ndistances=[\n");
//		for (Entry<String, Double> entry : this.distances.entrySet()) {
//			sb.append(entry.getKey());
//			sb.append(" => ");
//			sb.append(entry.getValue());
//			sb.append("\n");
//		}
//		sb.append("]\n");
		return sb.toString();
	}

	public String toPrintableString() {
		StringBuilder sb = new StringBuilder(this.getName());
		sb.append("\t");
		sb.append(this.getReachabilityDistance() == INFINITY ? "inf" : this
				.getReachabilityDistance());
		sb.append("\t");
		sb.append(this.getCoreDistance() == INFINITY ? "inf" : this
				.getCoreDistance());
		sb.append("\n");
		return sb.toString();
	}
}
