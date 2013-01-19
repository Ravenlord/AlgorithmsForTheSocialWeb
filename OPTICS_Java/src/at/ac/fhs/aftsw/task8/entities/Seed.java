package at.ac.fhs.aftsw.task8.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
		this(name, Double.parseDouble(x), Double.parseDouble(y), INFINITY, INFINITY);
	}

	public Seed(String name, double x, double y, double coreDistance,
			double reachabilityDistance) {
		this.setName(name);
		this.setX(x);
		this.setY(y);
		this.setCoreDistance(coreDistance);
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

	public void setCoreDistance(double coreDistance) {
		this.coreDistance = coreDistance;
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

	public double getDistance(String seedName) {
		return this.distances.get(seedName);
	}
	
	public void addDistance(String seedName, double distance) {
		if(this.distances.containsKey(seedName)) return;
		this.distances.put(seedName, distance);
	}
	/*
	 * Required comparison methods.
	 * -------------------------------------------------------------------------
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		return prime * result + ((name == null) ? 0 : name.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj || this.getName().equals(((Seed) obj).getName())) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Seed: name='");
		sb.append(this.getName());
		sb.append("', x=");
		sb.append(this.getX());
		sb.append(", y=");
		sb.append(this.getY());
		sb.append(", core distance=");
		if(this.getCoreDistance() == INFINITY){
			sb.append("inf");
		} else {
			sb.append(this.getCoreDistance());			
		}
		sb.append(", reachability distance=");
		if(this.getReachabilityDistance() == INFINITY) {
			sb.append("inf");
		} else {
			sb.append(this.getReachabilityDistance());			
		}
		sb.append("distances=[\n");
		for(Entry<String, Double> entry : this.distances.entrySet()){
			sb.append(entry.getKey());
			sb.append(" => ");
			sb.append(entry.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public int compareTo(Seed otherSeed) {
		if (this == otherSeed){
			return 0;
		}
		if (this.getReachabilityDistance() == otherSeed.getReachabilityDistance()) {
			return this.getName().compareTo(otherSeed.getName());
		} else {
			return (int) (this.getReachabilityDistance() - otherSeed
					.getReachabilityDistance());
		}
	}

}
