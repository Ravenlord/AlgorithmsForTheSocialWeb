/**
 * Algorithms for the Social Web Task 7: Apache Hadoop M/R
 */
package at.ac.fhsalzburg.algorithmsforthesocialweb.task7;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach.Approaches;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach.JobConfFactory;

/**
 * Main class for our Apache hadoop M/R task. At first we check if all necessary arguments for the job were provided and
 * throw an exception if something is missing. If everything is alright the correct M/R task will be executed.
 *
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 * @version 1.0
 * @since 2012-19-12
 */
public class Coefficient extends Configured implements Tool {


	// ----------------------------------------------------------------------------------------------------------------- Properties


	/**
	 * The name of our hadoop M/R task.
	 */
	public static final String HADOOP_JOB_NAME = "Algorithms for the Social Web Task 7: Apache Hadoop M/R";

	/**
	 * Platform independent line separator.
	 */
	public static final String EOL = System.getProperty("line.separator");

	/**
	 * The maximum amount of ratings of one user.
	 */
	public static final int MAX_RATINGS = 100;

	/**
	 * Expected character set of the incoming file.
	 */
	public static final String CHARSET = "ASCII";

	/**
	 * Desired buffer size for reading and writing files. Java default is 8192 (ASCII characters; 8k).
	 */
	public static final int BUFFER_SIZE = 262144;

	/**
	 * The name of the variable we set for the total user count.
	 */
	public static final String HADOOP_VARIABLE_MAX_USERS = "max_users";

	/**
	 * The name of the variable we set for the kind of algorithm to be used in the reduce task.
	 */
	public static final String HADOOP_VARIABLE_ALGORITHMS_KEY = "correlation_algorithms";

	/**
	 * The default approach, use the faster one!
	 */
	public static final Approaches DEFAULT_APPROACH = Approaches.inplace;


	// ----------------------------------------------------------------------------------------------------------------- Public Methods


	/**
	 * {@inheritDoc}
	 */
	public int run(String[] args) throws Exception {
		// Get desired job configuration.
		JobConf job = JobConfFactory.getJobConf(getConf(), args, Coefficient.class);

		// Start our micro benchmark and execute the hadoop job. After the job finished print the micor benchmark output
		// to the console.
		Benchmark benchmark = new Benchmark();
		JobClient.runJob(job);
		System.out.println(EOL + "Apache hadoop job duration: " + benchmark.stop());

		return 0;
	}


	// ----------------------------------------------------------------------------------------------------------------- Main


	/**
	 * Main class invoked by Apache hadoop.
	 *
	 * @param args
	 *   Arguments passed in via console.
	 * @throws Exception
	 *   Various exception can be thrown from hadoop or our M/R implementation.
	 */
	public static void main(String[] args) throws Exception {
		// If the third console argument was omitted throw an illegal argument exception and explain the user what kind
		// of options he has.
		if (args.length < 3 && args.length > 4) {
			throw new IllegalArgumentException(
					EOL +
					"You must provide a third argument which states what kind of algorithm should be used. Usage:" +
					EOL +
					EOL +
					"[p|pearson] = Pearson algorithm will be used." +
					EOL +
					"[s|spearman] = Spearman algorithm will be used." +
					EOL +
					EOL +
					"You can provide an optional fourth argument to decide with approach should be used. Usage:" +
					EOL +
					EOL +
					"[i|inplace] = In place approach where everything is computed with M/R." +
					EOL +
					"[p|preparation] = Preparation approach, where the input file is pre-processed." +
					EOL +
					EOL +
					"Default approach is the " + DEFAULT_APPROACH.toString() + " approach because it's faster." +
					EOL +
					EOL
			);
		}

		// Start the haddop job via the ToolRunner utility, otherwise we get a warning.
		System.exit(ToolRunner.run(new Configuration(), new Coefficient(), args));
	}


}