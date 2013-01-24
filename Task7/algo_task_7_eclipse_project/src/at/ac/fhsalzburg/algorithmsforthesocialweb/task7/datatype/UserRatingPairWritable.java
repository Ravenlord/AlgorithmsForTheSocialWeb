/**
 * Algorithms for the Social Web Task 7: Apache Hadoop M/R
 */
package at.ac.fhsalzburg.algorithmsforthesocialweb.task7.datatype;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * Custom data type for rating pairs in our Apache hadoop M/R task.
 *
 * @see <a href="http://vangjee.wordpress.com/2012/02/29/computing-pearson-correlation-using-hadoops-mapreduce-mr-paradigm/">Jee’s Blog</a>
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 * @version 1.0
 * @since 2012-19-12
 */
public class UserRatingPairWritable implements Writable {


	// ----------------------------------------------------------------------------------------------------------------- Properties


	/**
	 * The rating of the first user for the current joke.
	 */
	private double x;

	/**
	 * The rating of the second user for the current joke.
	 */
	private double y;


	// ----------------------------------------------------------------------------------------------------------------- Constructor


	/**
	 * Constructor without parameters.
	 */
	public UserRatingPairWritable() {
		this(0.0d, 0.0d);
	}

	/**
	 * Constructor with parameters.
	 *
	 * @param x
	 *   First user ID in the pair.
	 * @param y
	 *   Second user ID in the pair.
	 */
	public UserRatingPairWritable(double x, double y) {
		this.x = x;
		this.y = y;
	}


	// ----------------------------------------------------------------------------------------------------------------- Public Methods


	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return 42 + (new Double(x)).hashCode() + (new Double(y)).hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return (new StringBuilder().append(this.getX()).append(",").append(this.getY())).toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		this.x = in.readDouble();
		this.y = in.readDouble();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeDouble(this.getX());
		out.writeDouble(this.getY());
	}


	// ----------------------------------------------------------------------------------------------------------------- Public Getter


	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}


}