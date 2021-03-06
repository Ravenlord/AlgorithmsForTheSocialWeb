package at.ac.fhs.aftsw.task8.algorithm;

import java.util.List;

import at.ac.fhs.aftsw.task8.entities.Seed;

/**
 * @author Markus Deutschl
 * Generic Algorithm interface.
 */
public interface Algorithm {
	
	public void process(List<Seed> seeds);
}
