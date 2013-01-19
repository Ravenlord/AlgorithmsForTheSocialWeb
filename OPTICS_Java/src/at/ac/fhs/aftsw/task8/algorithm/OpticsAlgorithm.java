package at.ac.fhs.aftsw.task8.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import at.ac.fhs.aftsw.task8.entities.Seed;

/**
 * @author Markus Deutschl
 * OPTICS algorithm implementation.
 */
public class OpticsAlgorithm implements Algorithm {

	/*
	 * Properties.
	 * -------------------------------------------------------------------------
	 */
	private int minPoints;
	private double epsilon;
	
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
		SortedSet<Seed> orderedSeeds = new TreeSet<Seed>();
		for(Seed obj : seeds) {
			if(obj.isProcessed()) continue;
			//TODO
		}
	}

	private List<Seed> rangeQuery(Seed obj) {
		List<Seed> neighbors = new ArrayList<Seed>();
		//TODO
		return neighbors;
	}

}
