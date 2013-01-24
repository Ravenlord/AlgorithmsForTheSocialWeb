/**
 * Algorithms for the Social Web Task 7: Apache Hadoop M/R
 */
package at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach.prepare;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.datatype.UserIdPairWritable;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.datatype.UserRatingPairWritable;

/**
 * Apache hadoop mapper task that needs preparation of the Jeser Data Set for correlation calculation.
 *
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 * @version 1.0
 * @since 2012-19-12
 */
public class PreparationMapper extends MapReduceBase implements Mapper<LongWritable, Text, UserIdPairWritable, UserRatingPairWritable> {


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void map(LongWritable key, Text value, OutputCollector<UserIdPairWritable, UserRatingPairWritable> output, Reporter reporter) throws IOException {
		StringTokenizer tokenizer = new StringTokenizer(value.toString(), ";");
		Vector<Integer> vec = new Vector<Integer>();
		int i, j, x, y;

		// Loop over all users and add their ratings to our vector. We have to use a vector because we don't know how
		// many users we have.
		while (tokenizer.hasMoreTokens()) vec.add(Integer.valueOf(tokenizer.nextToken()));

		// Loop twice over our users to create user pairs for correlation computation in the reduce task.
		for (i = 0; i < vec.size(); i++) {
			for (j = 0; j < vec.size(); j++) {
				// Skip user pairs which we've already computed.
				if (j < i) continue;

				// Only add this pair if both have a valid rating for this joke. This replaces any intersection methods.
				if ((x = vec.get(i)) != 99 && (y = vec.get(j)) != 99) {
					output.collect(
							new UserIdPairWritable(i, j),		// User IDs of our user pair.
							new UserRatingPairWritable(x, y)	// Ratings for this joke of our user pair.
					);
				}
			}
		}
	}


}