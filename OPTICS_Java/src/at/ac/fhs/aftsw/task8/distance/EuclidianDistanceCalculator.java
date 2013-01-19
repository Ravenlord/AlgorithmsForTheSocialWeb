package at.ac.fhs.aftsw.task8.distance;

import org.apache.commons.math3.util.MathArrays;

import at.ac.fhs.aftsw.task8.entities.Seed;

public class EuclidianDistanceCalculator implements DistanceCalculator {

	@Override
	public double calculateSeedDistance(Seed lhs, Seed rhs) {
		double[] lhsCoordinates = {lhs.getX(), lhs.getY()};
		double[] rhsCoordinates = {rhs.getX(), rhs.getY()};
		return Math.abs(MathArrays.distance(lhsCoordinates, rhsCoordinates));
	}

}
