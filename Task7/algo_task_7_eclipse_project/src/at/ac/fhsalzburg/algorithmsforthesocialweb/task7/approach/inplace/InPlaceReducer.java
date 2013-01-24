/**
 * Algorithms for the Social Web Task 7: Apache Hadoop M/R
 */
package at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach.inplace;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.Algorithms;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.Coefficient;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.datatype.DoubleArrayWritable;

/**
 * Apache hadoop reducer task that doesn't need preparation of the data.
 *
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 * @version 1.0
 * @since 2012-19-12
 */
public class InPlaceReducer extends MapReduceBase implements Reducer<LongWritable, DoubleArrayWritable, Text, Text> {


	// ----------------------------------------------------------------------------------------------------------------- Properties


	/**
	 * The kind of algorithm to compute.
	 */
	private Algorithms algorithm;


	// ----------------------------------------------------------------------------------------------------------------- Public Methods


	/**
	 * Used to check job configuration and configure the map task.
	 *
	 * @param job
	 *   The job configuration of this hadoop job.
	 */
	public void configure(JobConf job) {
		// Export algorithm type to class scope.
		this.algorithm = job.getEnum(
				Coefficient.HADOOP_VARIABLE_ALGORITHMS_KEY,	// Name of the enumerable.
				Algorithms.pearson							// Default value of the enumerable.
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reduce(LongWritable key, Iterator<DoubleArrayWritable> it, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		DoubleWritable[] caster = new DoubleWritable[Coefficient.MAX_RATINGS];
		double[] currentUserRatings = new double[Coefficient.MAX_RATINGS];
		double[] otherUserRatings = new double[Coefficient.MAX_RATINGS];
		double correlation = 0;
		int i = 0;

		while (it.hasNext()) {
			// The current user we want to compute.
			if (key.get() == i) {
				// Get the DoubleWritable[] and cast each value to a primitive double value.
				caster = it.next().get();
				for (int j = 0; j < caster.length; j++) {
					currentUserRatings[j] = caster[j].get();
				}

				// Correlation for ourself is always 1.
				output.collect(new Text(key.toString() + "," + key.toString()), new Text("1"));
			}
			// The users after the current users we want to compute. We've already computed users before the current
			// user in a preceding reduce task.
			else if (i > key.get()) {
				caster = it.next().get();
				for (int j = 0; j < caster.length; j++) {
					otherUserRatings[j] = caster[j].get();
				}

				switch (this.algorithm) {
				case p:
				case pearson:
					correlation = new PearsonsCorrelation().correlation(currentUserRatings, otherUserRatings);
					break;
				case s:
				case spearman:
					correlation = new SpearmansCorrelation().correlation(currentUserRatings, otherUserRatings);
					break;
				}

				output.collect(new Text(key.toString() + "," + i), new Text(String.valueOf(correlation)));
			}

			i++;
		}
	}


}