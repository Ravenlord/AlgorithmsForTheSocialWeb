/**
 * Algorithms for the Social Web Task 7: Apache Hadoop M/R
 */
package at.ac.fhsalzburg.algorithmsforthesocialweb.task7.datatype;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

/**
 * Custom data type for user IDs in our Apache hadoop M/R task.
 *
 * @see <a href="http://vangjee.wordpress.com/2012/02/29/computing-pearson-correlation-using-hadoops-mapreduce-mr-paradigm/">Jee’s Blog</a>
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 * @version 1.0
 * @since 2012-19-12
 */
public class UserIdPairWritable implements WritableComparable<UserIdPairWritable> {


	// ----------------------------------------------------------------------------------------------------------------- Properties


	/**
	 * First user ID in the pair.
	 */
	private long x;

	/**
	 * Second user ID in the pair.
	 */
	private long y;


	// ----------------------------------------------------------------------------------------------------------------- Constructor


	/**
	 * Constructor without parameters.
	 */
	public UserIdPairWritable() {
		this(0, 0);
	}

	/**
	 * Constructor with parameters.
	 *
	 * @param x
	 *   First user ID in the pair.
	 * @param y
	 *   Second user ID in the pair.
	 */
	public UserIdPairWritable(long x, long y) {
		this.x = x;
		this.y = y;
	}


	// ----------------------------------------------------------------------------------------------------------------- Public Methods


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (!(object instanceof UserIdPairWritable)) return false;
		UserIdPairWritable indexPairs = (UserIdPairWritable) object;
		return this.getX() == indexPairs.getX() && this.getY() == indexPairs.getY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return 42 + (new Long(x)).hashCode() + (new Long(y)).hashCode();
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
		this.x = in.readLong();
		this.y = in.readLong();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(this.getX());
		out.writeLong(this.getY());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(UserIdPairWritable object) {
		int result;
		return (result = ((Long) this.getX()).compareTo(object.getX())) == 0 ? ((Long) this.getY()).compareTo(object.getY()) : result;
	}


	// ----------------------------------------------------------------------------------------------------------------- Public Getter


	/**
	 * @return the x
	 */
	public long getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public long getY() {
		return y;
	}


}