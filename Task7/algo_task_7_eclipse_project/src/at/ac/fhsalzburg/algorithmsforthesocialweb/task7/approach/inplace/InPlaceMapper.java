/**
 * Algorithms for the Social Web Task 7: Apache Hadoop M/R
 */
package at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach.inplace;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.Coefficient;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.datatype.DoubleArrayWritable;

/**
 * Apache hadoop mapper task that doesn't need preparation of the data.
 *
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 * @version 1.0
 * @since 2012-19-12
 */
public class InPlaceMapper extends MapReduceBase implements Mapper<LongWritable, Text, LongWritable, DoubleArrayWritable> {


	// ----------------------------------------------------------------------------------------------------------------- Properties


	/**
	 * The total amount of users we have to compute.
	 */
	private int maxUsers;


	// ----------------------------------------------------------------------------------------------------------------- Public Methods


	/**
	 * Used to check job configuration and configure the map task.
	 *
	 * @param job
	 *   The job configuration of this hadoop job.
	 */
	public void configure(JobConf job) {
		this.maxUsers = job.getInt(Coefficient.HADOOP_VARIABLE_MAX_USERS, 0);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void map(LongWritable key, Text value, OutputCollector<LongWritable, DoubleArrayWritable> output, Reporter reporter) throws IOException {
		StringTokenizer tokenizer = new StringTokenizer(value.toString(), ";");
		DoubleWritable[] ratings = new DoubleWritable[Coefficient.MAX_RATINGS];
		String[] jokeRating;
		String rating;
		int i = 0;

		// Initialize the ratings array with not rated for each joke.
		for (i = 0; i < Coefficient.MAX_RATINGS; i++) ratings[i] = new DoubleWritable(99);

		// Loop over all ratings and collect them in our ratings array.
		while (tokenizer.hasMoreTokens()) {
			// Split the tokenized string at "," and check if we have two offsets. We also check if the
			// extracted rating contains whitespaces, is empty ("") or null, as these are illegal values for a
			// rating.
			if ((jokeRating = tokenizer.nextToken().split(",")).length == 2 && !(StringUtils.isBlank(rating = StringUtils.trim(jokeRating[0])))) {
				// If the rating is -0 insert 0, otherwise we'll get a NumberFormatException later on.
				ratings[Integer.valueOf(jokeRating[1]) - 1] = new DoubleWritable(rating.equals("-0") ? 0 : Double.valueOf(rating));
			}
		}

		// Export ...
		for (i = 0; i < this.maxUsers; i++) output.collect(new LongWritable(i), new DoubleArrayWritable(ratings));
	}


}