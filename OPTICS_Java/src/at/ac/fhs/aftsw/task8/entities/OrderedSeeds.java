package at.ac.fhs.aftsw.task8.entities;

import org.apache.commons.collections.buffer.PriorityBuffer;

public class OrderedSeeds extends PriorityBuffer {

	private static final long serialVersionUID = 6354647072220174153L;

	public boolean insertOrUpdate(Seed seedToInsert) {
		if(this.contains(seedToInsert)) {
			this.remove(seedToInsert);
		}
		return this.add(seedToInsert);
	}
	
	public Seed pop() {
		return (Seed) this.remove();
	}
}
