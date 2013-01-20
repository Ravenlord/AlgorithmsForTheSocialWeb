package at.ac.fhs.aftsw.task8;

import java.io.File;
import java.util.List;

import at.ac.fhs.aftsw.task8.algorithm.Algorithm;
import at.ac.fhs.aftsw.task8.algorithm.OpticsAlgorithm;
import at.ac.fhs.aftsw.task8.distance.DistanceCalculator;
import at.ac.fhs.aftsw.task8.distance.EuclidianDistanceCalculator;
import at.ac.fhs.aftsw.task8.entities.OrderedSeedSet;
import at.ac.fhs.aftsw.task8.entities.Seed;
import at.ac.fhs.aftsw.task8.utils.FileParser;

/**
 * @author Markus Deutschl The main class for the OPTICS algorithm.
 */
public class Main {

	/*
	 * Properties.
	 * -------------------------------------------------------------------------
	 */
	private static List<Seed> objects;
	private static DistanceCalculator distanceCalculator;
	private static Algorithm algorithm;

	/*
	 * Methods.
	 * -------------------------------------------------------------------------
	 */
	private static void calculateDistances(int minPoints, double epsilon) {
		for (int i = 0; i < objects.size(); i++) {
			Seed lhs = objects.get(i);
			for (int j = 0; j < objects.size(); j++) {
				if (i == j)
					continue;
				// If the distance has already been calculated, just insert it.
				Seed rhs = objects.get(j);
				if (j < i) {
					lhs.addDistance(rhs.getName(),
							rhs.getDistance(lhs.getName()));
				} else {
					lhs.addDistance(rhs.getName(),
							distanceCalculator.calculateSeedDistance(lhs, rhs));
				}
			}
			lhs.setCoreDistance(minPoints, epsilon);
		}
	}

	private static void printUsage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Usage:\n");
		sb.append("\tjava -jar [this jar] [data location] [min pts] [epsilon]\n");
		sb.append("\tdata location:\tThe path to the data file. Format: x y name\n");
		sb.append("\tmin pts: The minimum amount of points within epsilon environment to use for a core point.\n");
		sb.append("\tepsilon: The epsilon environment to use.\n");
		System.out.println(sb.toString());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			printUsage();
		} else {
			String path = args[0];
			File file = new File(path);
			objects = FileParser.parseSeedsFile(file);
			int minPoints = Integer.valueOf(args[1]);
			double epsilon = Double.valueOf(args[2]);
			distanceCalculator = new EuclidianDistanceCalculator();
			calculateDistances(minPoints, epsilon);
			algorithm = new OpticsAlgorithm(minPoints, epsilon);
			algorithm.process(objects);
//			OrderedSeedSet seeds = new OrderedSeedSet();
//			seeds.add(objects.get(0));
//			seeds.add(objects.get(1));
//			Seed tmp = seeds.pop();
//			System.out.println(tmp);
		}
	}

}
