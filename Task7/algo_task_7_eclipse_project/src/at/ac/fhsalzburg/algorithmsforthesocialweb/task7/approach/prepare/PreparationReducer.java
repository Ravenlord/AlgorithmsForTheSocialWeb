/**
 * Algorithms for the Social Web Task 7: Apache Hadoop M/R
 */
package at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach.prepare;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.Algorithms;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.Coefficient;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.datatype.UserIdPairWritable;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.datatype.UserRatingPairWritable;

/**
 * Apache hadoop reduce task that needs preparation of the Jeser Data Set for correlation calculation.
 *
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 * @version 1.0
 * @since 2012-19-12
 */
public class PreparationReducer extends MapReduceBase implements Reducer<UserIdPairWritable, UserRatingPairWritable, Text, Text> {


	// ----------------------------------------------------------------------------------------------------------------- Properties


	/**
	 * The kind of algorithm to compute.
	 */
	private Algorithms algorithm;


	// ----------------------------------------------------------------------------------------------------------------- Public Methods


	/**
	 * Used to check job configuration and configure the reduce task.
	 *
	 * @param job
	 *   The job configuration of this hadoop job.
	 */
	public void configure(JobConf job) {
		this.algorithm = job.getEnum(Coefficient.HADOOP_VARIABLE_ALGORITHMS_KEY, Algorithms.pearson);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reduce(UserIdPairWritable key, Iterator<UserRatingPairWritable> it, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		double correlation = 0;

		// If this is the same user and not a pair, simply use 1 as correlation. We know for a fact that our algorithm
		// implementations are correct because we use Apache commons, therefor it's not a problem to simply use the
		// magic number 1 at this place. We still include it, because we want to have a complete output at the end of
		// our M/R job.
		if (key.getX() == key.getY()) {
			correlation = 1;
		}
		// If we have a user pair, prepare their ratings and pass it along to the chosen algorithm.
		else {
			UserRatingPairWritable valuePair;
			double[] xArray = new double[Coefficient.MAX_RATINGS];
			double[] yArray = new double[Coefficient.MAX_RATINGS];
			int i = 0;
	
			// Loop all ratings of the user pair and prepare the arrays for the correlation computation.
			while (it.hasNext()) {
				valuePair = it.next();
				xArray[i] = valuePair.getX();
				yArray[i] = valuePair.getY();
				i++;
			}
	
			switch (this.algorithm) {
			case p:
			case pearson:
				correlation = new PearsonsCorrelation().correlation(xArray, yArray);
				break;
			case s:
			case spearman:
				correlation = new SpearmansCorrelation().correlation(xArray, yArray);
				break;
			}
		}

		output.collect(new Text(key.toString()), new Text(String.valueOf(correlation)));
	}


}