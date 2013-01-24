/**
 * Algorithms for the Social Web Task 7: Apache Hadoop M/R
 */
package at.ac.fhsalzburg.algorithmsforthesocialweb.task7.datatype;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.DoubleWritable;

/**
 * Custom data type for a single user with all ratings.
 *
 * @see <a href="http://stackoverflow.com/a/4390928">StackOverflow</a>
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 * @version 1.0
 * @since 2013-24-01
 */
public class DoubleArrayWritable extends ArrayWritable {


	// ----------------------------------------------------------------------------------------------------------------- Properties


	/**
	 * To ensure perfect type safety we re-declare the private values property of type DoubleWritable[] instead of
	 * Writable[].
	 */
	private DoubleWritable[] values;


	// ----------------------------------------------------------------------------------------------------------------- Constructors


	/**
	 * Create new instance of DoubleArrayWritable class.
	 */
	public DoubleArrayWritable() {
		super(DoubleWritable.class);
	}

	/**
	 * Create new instance of DoubleArrayWritable class and directly pass along it's values.
	 *
	 * @param values
	 *   The values of this DoubleWritable[]
	 */
	public DoubleArrayWritable(DoubleWritable[] values) {
		super(DoubleWritable.class);
		this.set(values);
	}


	// ----------------------------------------------------------------------------------------------------------------- Public Methods


	/**
	 * Set the values of this instance.
	 *
	 * @param values
	 *   The values to set.
	 */
	public void set(DoubleWritable[] values) {
		this.values = values;
	}

	/**
	 * Get the values of this instance.
	 *
	 * @return
	 *   The values of this instance.
	 */
	@Override
	public DoubleWritable[] get() {
		return this.values;
	}

	/**
	 * Read the array back in after it was written.
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		DoubleWritable value;
		this.values = new DoubleWritable[in.readInt()];
		for (int i = 0; i < this.values.length; i++) {
			value = new DoubleWritable();
			value.readFields(in);
			this.values[i] = value;
		}
	}

	/**
	 * Write the array to file.
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(this.values.length);
		for (int i = 0; i < this.values.length; i++) {
			this.values[i].write(out);
		}
	}


}