package at.ac.fhs.aftsw.task8.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import at.ac.fhs.aftsw.task8.entities.Seed;

/**
 * 
 * @author Markus Deutschl File parser for retrieving seed data
 */
public class FileParser {

	/**
	 * Parses the specified file and returns valid seed data.
	 * 
	 * @param file
	 *            The file to parse
	 * @return Seed data from file
	 */
	public static List<Seed> parseSeedsFile(File file) {
		List<Seed> seeds = new ArrayList<Seed>();
		LineIterator lineIterator = null;
		try {
			// Iterate through all lines
			lineIterator = FileUtils.lineIterator(file);
			while (lineIterator.hasNext()) {
				String line = lineIterator.nextLine();
				// Strip all additional whitespace
				StringUtils.normalizeSpace(line);
				// Split data and fill seed.
				String[] seedData = line.split(" ");
				try {
					seeds.add(new Seed(seedData[2], seedData[0], seedData[1]));
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (lineIterator != null) {
				LineIterator.closeQuietly(lineIterator);
			}
		}
		return seeds;
	}
}
