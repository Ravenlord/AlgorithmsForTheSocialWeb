package at.ac.fhs.aftsw.task8.distance;

import at.ac.fhs.aftsw.task8.entities.Seed;

public interface DistanceCalculator {

	/**
	 * Calculate the distance between two seeds.
	 * @param lhs The first seed.
	 * @param rhs The second seed.
	 * @return The distance
	 */
	public double calculateSeedDistance(Seed lhs, Seed rhs);
}
