package at.ac.fhs.aftsw.task8.entities;

import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

public class OrderedSeedSet extends TreeSet<Seed> {

	/*
	 * Properties.
	 * -------------------------------------------------------------------------
	 */
	/**
	 * Generated serial UID.
	 */
	private static final long serialVersionUID = 4254139903373207623L;

	/*
	 * Methods.
	 * -------------------------------------------------------------------------
	 */
	/**
	 * Inserts the seed if it doesn't exist yet. Update is not necessary,
	 * because we only have references.
	 * 
	 * @param seedToInsert
	 *            The seed to insert or update.
	 */
	public void insertOrUpdate(Seed seedToInsert) {
		for (Seed s : this) {

			if (StringUtils.equalsIgnoreCase(s.getName(),
					seedToInsert.getName()))
				return;
		}
		this.add(seedToInsert);
	}

	/**
	 * Removes the first element from the set and returns it.
	 * 
	 * @return The first element of the set.
	 */
	public Seed pop() {
		Seed firstSeed = this.first();
		this.remove(firstSeed);
		return firstSeed;
	}
}
