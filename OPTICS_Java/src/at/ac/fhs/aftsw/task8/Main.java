package at.ac.fhs.aftsw.task8;

import java.io.File;
import java.util.List;

import at.ac.fhs.aftsw.task8.algorithm.Algorithm;
import at.ac.fhs.aftsw.task8.algorithm.OpticsAlgorithm;
import at.ac.fhs.aftsw.task8.distance.DistanceCalculator;
import at.ac.fhs.aftsw.task8.distance.EuclidianDistanceCalculator;
import at.ac.fhs.aftsw.task8.entities.Seed;
import at.ac.fhs.aftsw.task8.utils.FileParser;

/**
 * @author Markus Deutschl
 * The main class for the OPTICS algorithm.
 */
public class Main {

	/*
	 * Properties.
	 * -------------------------------------------------------------------------
	 */
	private static List<Seed> objects;
	private static DistanceCalculator distanceCalculator;
	
	/*
	 * Methods.
	 * -------------------------------------------------------------------------
	 */
	private static void calculateDistances() {
		for(int i=0; i < objects.size(); i++) {
			Seed lhs = objects.get(i);
			for(int j=0; j < objects.size(); j++) {
				if(i==j) continue;
				// If the distance has already been calculated, just insert it.
				Seed rhs = objects.get(j);
				if(j < i) {
					lhs.addDistance(rhs.getName(), rhs.getDistance(lhs.getName()));
				} else {
					lhs.addDistance(rhs.getName(), distanceCalculator.calculateSeedDistance(lhs, rhs));
				}
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = System.getProperty("user.home") + File.separator + "data.txt";
		File file = new File(path);
		objects = FileParser.parseSeedsFile(file);
		distanceCalculator = new EuclidianDistanceCalculator();
		calculateDistances();
		Algorithm algorithm = new OpticsAlgorithm(3, 100);
		
		System.out.println(objects);
	}

}
