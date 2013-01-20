package at.ac.fhs.aftsw.task8.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import at.ac.fhs.aftsw.task8.entities.OrderedSeedSet;
import at.ac.fhs.aftsw.task8.entities.Seed;

/**
 * @author Markus Deutschl OPTICS algorithm implementation.
 */
public class OpticsAlgorithm implements Algorithm {

	/*
	 * Properties.
	 * -------------------------------------------------------------------------
	 */
	private int minPoints;
	private double epsilon;
	private List<Seed> seeds;

	/*
	 * Constructor.
	 * -------------------------------------------------------------------------
	 */
	public OpticsAlgorithm(int minPoints, double epsilon) {
		this.minPoints = minPoints;
		this.epsilon = epsilon;
	}

	/*
	 * Methods.
	 * -------------------------------------------------------------------------
	 */
	@Override
	public void process(List<Seed> seeds) {
		this.seeds = seeds;
		OrderedSeedSet orderedSeeds = new OrderedSeedSet();
		List<Seed> neighbors = null;
		for (Seed obj : seeds) {
			if (obj.isProcessed()) {
				continue;
			}
			neighbors = this.rangeQuery(obj);
			obj.setProcessed(true);
			Map<String, Double> objDistances = obj.getDistances();
			System.out.println(obj.toPrintableString());
			// Set the core distance of the current object.
			// Also stop computing, if the seed is no core point.
			if (obj.getCoreDistance() != Seed.INFINITY) {
				for (Seed neighbor : neighbors) {
					double tempReachabilityDistance = Math.max(
							obj.getCoreDistance(),
							objDistances.get(neighbor.getName()).doubleValue());
					neighbor.setReachabilityDistance(Math.min(
							neighbor.getReachabilityDistance(),
							tempReachabilityDistance));
					orderedSeeds.insertOrUpdate(neighbor);
				}
				while (!orderedSeeds.isEmpty()) {
					Seed poppedSeed = orderedSeeds.pop();
					neighbors = rangeQuery(poppedSeed);
					// Set the processed state of the popped element.
					poppedSeed.setProcessed(true);
					Map<String, Double> poppedSeedDistances = poppedSeed
							.getDistances();
					System.out.println(poppedSeed.toPrintableString());
					// Set the core distance of the current ordered seed
					// element.
					// Also stop computing, if the seed is no core point.
					if (poppedSeed.getCoreDistance() != Seed.INFINITY) {
						for (Seed neighbor : neighbors) {
							double tempReachabilityDistance = Math.max(
									poppedSeed.getCoreDistance(),
									poppedSeedDistances.get(neighbor.getName())
											.doubleValue());
							neighbor.setReachabilityDistance(Math.min(
									neighbor.getReachabilityDistance(),
									tempReachabilityDistance));
							orderedSeeds.insertOrUpdate(neighbor);
						}
					}
				}
			}
		}
	}

	private List<Seed> rangeQuery(Seed obj) {
		List<Seed> neighbors = new ArrayList<Seed>();
		Map<String, Double> distances = obj.getDistances();
		for (Seed neighbor : this.seeds) {
			if (obj.getName().equals(neighbor.getName()))
				continue;
			if (!neighbor.isProcessed()
					&& distances.get(neighbor.getName()) <= this.epsilon) {
				neighbors.add(neighbor);
			}
		}
		return neighbors;
	}

}
